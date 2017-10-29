package house.mcintosh.mahjong.scoring;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import house.mcintosh.mahjong.exception.InvalidHandException;
import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.SetComparator;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.scoring.ScoringScheme.ScoreElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * A hand that is scored.  A scored hand can change. As sets are added to it the score and
 * Mahjong status of the hand change.
 */

public class ScoredHand implements Iterable<ScoredGroup>
{
	private final ScoringScheme					m_scheme;
	private final ObservableList<ScoredGroup>	m_backingGroups;
	private final SortedList<ScoredGroup>		m_sortedGroups;
	
	private ScoreList							m_scores;
	
	private boolean	m_requirePairConcealedInfo	= false;
	private boolean	m_mahjongPairConcealed		= false;
	private boolean	m_mahjongByLooseTile		= false;
	private boolean	m_mahjongByWallTile			= false;
	private boolean	m_mahjongByLastWallTile		= false;
	private boolean	m_mahjongByLastDiscard		= false;
	private boolean	m_mahjongByRobbingKong		= false;
	private boolean	m_mahjongByOnlyPossibleTile	= false;
	private boolean	m_mahjongByOriginalCall		= false;
	private boolean	m_nonMahjongByOriginalCall	= false;
	
	
	private boolean	m_isMahjong					= false;
	private int		m_totalScoreUnlimited		= 0;
	private int		m_totalScoreLimited			= 0;
	
	public ScoredHand(ScoringScheme scheme)
	{
		m_scheme		= scheme;
		m_backingGroups	= FXCollections.observableArrayList();
		m_sortedGroups	= new SortedList<ScoredGroup>(m_backingGroups, new SetComparator());
	}
	
	public void add(ScoredGroup group)
	{
		m_backingGroups.add(group);
		updateScore();
	}
	
	public int getTotalScore()
	{
		return m_totalScoreLimited;
	}
	
	public int getTotalScoreUnlimited()
	{
		return m_totalScoreUnlimited;
	}
	
	public boolean isMahjong()
	{
		return m_isMahjong;
	}
	
	public void setMahjongByWallTile(boolean fromWall)
	{
		m_mahjongByWallTile = fromWall;
		updateScore();
	}
	
	public boolean isMahjongByWallTile()
	{
		return m_mahjongByWallTile;
	}

	public void setMahjongByLastWallTile(boolean isLast)
	{
		m_mahjongByLastWallTile = isLast;
		updateScore();
	}
	
	public boolean isMahjongByLastWallTile()
	{
		return m_mahjongByLastWallTile;
	}

	public void setMahjongByOnlyPossibleTile(boolean only)
	{
		m_mahjongByOnlyPossibleTile = only;
		updateScore();
	}
	
	public boolean isMahjongByOnlyPossibleTile()
	{
		return m_mahjongByOnlyPossibleTile;
	}
	
	/**
	 * Evaluation of the Mahjong hand should call this to determine whether additional info
	 * is required about concealment of the pair.  setMahjongPairConcealed can then be called
	 * if necessary.
	 */
	public boolean requiresPairConcealedInfo()
	{
		return m_requirePairConcealedInfo;
	}
	
	public void setMahjongPairConcealed(boolean concealed)
	{
		m_mahjongPairConcealed = concealed;
		updateScore();
	}

	public boolean isMahjongByLooseTile()
	{
		return m_mahjongByLooseTile;
	}

	public void setMahjongByLooseTile(boolean mahjongByLooseTile)
	{
		this.m_mahjongByLooseTile = mahjongByLooseTile;
		updateScore();
	}

	public boolean isMahjongByLastDiscard()
	{
		return m_mahjongByLastDiscard;
	}

	public void setMahjongByLastDiscard(boolean mahjongByLastDiscard)
	{
		this.m_mahjongByLastDiscard = mahjongByLastDiscard;
		updateScore();
	}

	public boolean isMahjongByRobbingKong()
	{
		return m_mahjongByRobbingKong;
	}

	public void setMahjongByRobbingKong(boolean mahjongByRobbingKong)
	{
		this.m_mahjongByRobbingKong = mahjongByRobbingKong;
		updateScore();
	}

	public boolean isMahjongByOriginalCall()
	{
		return m_mahjongByOriginalCall;
	}

	public void setMahjongByOriginalCall(boolean mahjongByOriginalCall)
	{
		this.m_mahjongByOriginalCall = mahjongByOriginalCall;
		updateScore();
	}

	public boolean isNonMahjongByOriginalCall()
	{
		return m_nonMahjongByOriginalCall;
	}

	public void setNonMahjongByOriginalCall(boolean nonMahjongByOriginalCall)
	{
		this.m_nonMahjongByOriginalCall = nonMahjongByOriginalCall;
		updateScore();
	}

	@Override
	public Iterator<ScoredGroup> iterator()
	{
		return m_sortedGroups.iterator();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[\n");
		
		for (Group set : this)
		{
			sb.append("  ");
			sb.append(set);
			sb.append('\n');
		}
		
		sb.append(']');
		
		return sb.toString();
	}
	
	/**
	 * Recalculate the score of the hand, based on the current sets.  Also performs some
	 * sanity checking on the hand, and calculates whether it is a mahjong hand.
	 */
	private void updateScore()
	{
		// Zero score in case we exit early.
		m_totalScoreLimited = m_totalScoreUnlimited = 0;
		
		ScoreList scores = new ScoreList();
		
		int effectiveHandTiles	= 0;
		int pairCount			= 0;
		
		for (ScoredGroup group : this.m_backingGroups)
		{
			scores.append(group.getScore());
			
			effectiveHandTiles += group.getType().getHandSize();
			
			if (group.getType() == Group.Type.PAIR)
				pairCount++;
		}
		
		if (m_nonMahjongByOriginalCall)
			scores.append(m_scheme.getScoreContribution(ScoreElement.OriginalCallHandScore));
		
		if (effectiveHandTiles == m_scheme.MahjongHandSize && pairCount == 1)
			m_isMahjong = true;
		else if (effectiveHandTiles >= m_scheme.MahjongHandSize)
		{
			m_isMahjong = false;
			throw new InvalidHandException("Too many tiles for non-mahjong hand");
		}
		else
			m_isMahjong = false;
		
		if (m_isMahjong)
		{
			// Additional scoring that applies to mahjong hand only.
			scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongHandScore));
			
			// Look for all major and no chows
			
			boolean			allMajor				= true;
			boolean			noChow					= true;
			Set<Tile.Suit>	suits					= new HashSet<>();
			boolean			allNonPairsConcealed	= true;
			
			for (ScoredGroup group : this)
			{
				Tile firstTile = group.getFirstTile();
				
				if (group.getType() == Group.Type.CHOW)
				{
					noChow		= false;
					allMajor	= false;
				}
				
				if (!firstTile.isMajor())
					allMajor	= false;
				
				if (firstTile.getType() == Tile.Type.SUIT)
					suits.add(firstTile.getSuit());
				
				if (group.getType() != Group.Type.PAIR && !group.isConcealed())
					allNonPairsConcealed = false;
			}
			
			if (allMajor)
				scores.append(m_scheme.getScoreContribution(ScoreElement.AllMajorHandScore));
			
			if (noChow)
				scores.append(m_scheme.getScoreContribution(ScoreElement.NoChowsHandScore));
			
			if (suits.size() == 1)
				scores.append(m_scheme.getScoreContribution(ScoreElement.SingleSuitHandScore));
			
			if (allNonPairsConcealed)
				m_requirePairConcealedInfo = true;
			
			if (allNonPairsConcealed && m_mahjongPairConcealed)
				scores.append(m_scheme.getScoreContribution(ScoreElement.AllConcealedHandScore));
			
			if (m_mahjongByWallTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByWallTileHandScore));
			
			if (m_mahjongByLastWallTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByLastWallTileHandScore));
			
			if (m_mahjongByOnlyPossibleTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByOnlyPossibleTileHandScore));
			
			if (m_mahjongByLooseTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByLooseTileHandScore));
			
			if (m_mahjongByLastDiscard)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByLastDiscardHandScore));
			
			if (m_mahjongByRobbingKong)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByRobbingKongHandScore));

			if (m_mahjongByOriginalCall)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByOriginalCallHandScore));
		}
		
		
		m_totalScoreUnlimited	= scores.getTotal();
		m_totalScoreLimited		= Math.min(m_totalScoreUnlimited, m_scheme.LimitScore);
		m_scores				= scores;
	}
}

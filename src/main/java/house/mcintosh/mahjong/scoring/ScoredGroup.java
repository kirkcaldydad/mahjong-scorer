package house.mcintosh.mahjong.scoring;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.scoring.ScoringScheme.ScoreElement;

/**
 * A Set that has been scored.  Immutable because the Set is is constructed from is immutable, and
 * the score is calculated during construction.
 */
public class ScoredGroup extends Group
{
	private final ScoreList	m_score;
	
	public ScoredGroup(Group set, ScoringScheme scheme, Tile.Wind ownWind, Tile.Wind prevailingWind)
	{
		super(set);
		
		switch (getType())
		{
		case PAIR:
			m_score = scorePair(scheme, ownWind, prevailingWind);
			break;
		case CHOW:
			m_score = scoreChow(scheme, ownWind, prevailingWind);
			break;
		case PUNG:
			m_score = scorePung(scheme, ownWind, prevailingWind);
			break;
		case KONG:
			m_score = scoreKong(scheme, ownWind, prevailingWind);
			break;
			
		default:
			// Never get here.
			m_score = ScoreList.EMPTY;
		}
	}
	
	public ScoreList getScore()
	{
		return m_score;
	}
	
	private ScoreList scorePair(ScoringScheme scheme, Tile.Wind ownWind, Tile.Wind prevailingWind)
	{
		switch (getTileType())
		{
		case SUIT:
			return scheme.getScoreContribution(ScoreElement.PairSuitScore);
		case WIND:
			{
				Tile.Wind wind = getFirstTile().getWind();
				if (wind == ownWind)
					return scheme.getScoreContribution(ScoreElement.PairOwnWindScore);
				if (wind == prevailingWind)
					return scheme.getScoreContribution(ScoreElement.PairPrevailingWindScore);
				return scheme.getScoreContribution(ScoreElement.PairWindScore);
			}
		case DRAGON:
			return scheme.getScoreContribution(ScoreElement.PairDragonScore);
		
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}
	
	private ScoreList scoreChow(ScoringScheme scheme, Tile.Wind ownWind, Tile.Wind prevailingWind)
	{
		switch (getTileType())
		{
		case SUIT:
			return scheme.getScoreContribution(ScoreElement.ChowSuitScore);
		
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}
	
	private ScoreList scorePung(ScoringScheme scheme, Tile.Wind ownWind, Tile.Wind prevailingWind)
	{
		Tile	tile		= getFirstTile();
		boolean concealed	= isConcealed();
		
		switch (getTileType())
		{
		case SUIT:
			{
				if (tile.isMajor())
					return concealed ? scheme.getScoreContribution(ScoreElement.PungConcealedMajorSuitScore) : scheme.getScoreContribution(ScoreElement.PungExposedMajorSuitScore);
				else
					return concealed ? scheme.getScoreContribution(ScoreElement.PungConcealedMinorSuitScore) : scheme.getScoreContribution(ScoreElement.PungExposedMinorSuitScore);
			}
			
		case WIND:
			{
				Tile.Wind wind = tile.getWind();
				if (wind == ownWind && wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.PungConcealedPrevailingOwnWindScore)	: scheme.getScoreContribution(ScoreElement.PungExposedPrevailingOwnWindScore);
				if (wind == ownWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.PungConcealedOwnWindScore)			: scheme.getScoreContribution(ScoreElement.PungExposedOwnWindScore);
				if (wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.PungConcealedPrevailingWindScore)	: scheme.getScoreContribution(ScoreElement.PungExposedPrevailingWindScore);
				
				return concealed		? scheme.getScoreContribution(ScoreElement.PungConcealedWindScore)				: scheme.getScoreContribution(ScoreElement.PungExposedWindScore);
			}
			
		case DRAGON:
			return concealed ? scheme.getScoreContribution(ScoreElement.PungConcealedDragonScore) : scheme.getScoreContribution(ScoreElement.PungExposedDragonScore);
			
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}
	
	private ScoreList scoreKong(ScoringScheme scheme, Tile.Wind ownWind, Tile.Wind prevailingWind)
	{
		Tile	tile		= getFirstTile();
		boolean concealed	= isConcealed();
		
		switch (getTileType())
		{
		case SUIT:
			{
				if (tile.isMajor())
					return concealed ? scheme.getScoreContribution(ScoreElement.KongConcealedMajorSuitScore) : scheme.getScoreContribution(ScoreElement.KongExposedMajorSuitScore);
				else
					return concealed ? scheme.getScoreContribution(ScoreElement.KongConcealedMinorSuitScore) : scheme.getScoreContribution(ScoreElement.KongExposedMinorSuitScore);
			}
			
		case WIND:
			{
				Tile.Wind wind = tile.getWind();
				if (wind == ownWind && wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.KongConcealedPrevailingOwnWindScore)	: scheme.getScoreContribution(ScoreElement.KongExposedPrevailingOwnWindScore);
				if (wind == ownWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.KongConcealedOwnWindScore)			: scheme.getScoreContribution(ScoreElement.KongExposedOwnWindScore);
				if (wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.KongConcealedPrevailingWindScore)	: scheme.getScoreContribution(ScoreElement.KongExposedPrevailingWindScore);
				
				return concealed		? scheme.getScoreContribution(ScoreElement.KongConcealedWindScore)				: scheme.getScoreContribution(ScoreElement.KongExposedWindScore);
			}
			
		case DRAGON:
			return concealed ? scheme.getScoreContribution(ScoreElement.KongConcealedDragonScore) : scheme.getScoreContribution(ScoreElement.KongExposedDragonScore);
			
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}

}

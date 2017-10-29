package house.mcintosh.mahjong.model;

import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.scoring.ScoredHand;

/**
 * A Round is a set of hands - one for each player.
 */
public class Round
{
	private final Map<Player, Entry>	m_entries			= new HashMap<>();
	private final Wind					m_prevailingWind;
	private Player						m_mahjongPlayer;
	
	public Round(Wind prevailingWind)
	{
		m_prevailingWind = prevailingWind;
	}
	
	public void addHand(Player player, ScoredHand hand, Wind playerWind)
	{
		if (m_entries.containsKey(player))
			throw new InvalidModelException("Duplicate player");
		
		if (hand.isMahjong())
		{
			if (m_mahjongPlayer != null)
				throw new InvalidModelException("Already got mahjong hand in round");
			
			m_mahjongPlayer = player;
		}
		
		m_entries.put(player, new Entry(player, hand, playerWind));
	}

	public Wind getPrevailingWind()
	{
		return m_prevailingWind;
	}
	
	public Wind getPlayerWind(Player player)
	{
		return m_entries.get(player).playerWind;
	}
	
	public ScoredHand getHand(Player player)
	{
		return m_entries.get(player).hand;
	}
	
	public int getPlayerScore(Player player)
	{
		if (m_mahjongPlayer == null)
			throw new InvalidModelException("Round has no mahjong hand");
		
		int score = 0;
		
		if (player.equals(m_mahjongPlayer))
		{
			// Calculate score based on this being the mahjong player.
			
			Entry receivingEntry = m_entries.get(player);
			
			for (Entry givingEntry : m_entries.values())
			{
				if (givingEntry.player.equals(player))
					continue;
				
				int eastMultiplier = 1;
				
				if (receivingEntry.playerWind == Wind.EAST || givingEntry.playerWind == Wind.EAST)
					eastMultiplier = 2;
				
				score += receivingEntry.hand.getTotalScore() * eastMultiplier;
			}
		}
		else
		{
			// Calculate score based on this not being mahjong player.
			
			Entry thisPlayerEntry = m_entries.get(player);
			
			for (Entry thatPlayerEntry : m_entries.values())
			{
				if (thatPlayerEntry.player.equals(player))
					continue;
				
				int eastMultiplier = 1;
				
				if (thisPlayerEntry.playerWind == Wind.EAST || thatPlayerEntry.playerWind == Wind.EAST)
					eastMultiplier = 2;
				
				if (thatPlayerEntry.hand.isMahjong())
					score -= thatPlayerEntry.hand.getTotalScore() * eastMultiplier;
				else
					score += (thisPlayerEntry.hand.getTotalScore() - thatPlayerEntry.hand.getTotalScore()) * eastMultiplier;
			}
		}
		
		return score;
	}
	
	private class Entry
	{
		private final Player		player;
		private final ScoredHand	hand;
		private final Wind			playerWind;
		
		private Entry(Player player, ScoredHand hand, Wind playerWind)
		{
			this.player		= player;
			this.hand		= hand;
			this.playerWind	= playerWind;
		}
	}
}

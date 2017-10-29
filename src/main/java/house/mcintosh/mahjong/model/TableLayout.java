package house.mcintosh.mahjong.model;

import house.mcintosh.mahjong.exception.InvalidModelException;

public class TableLayout
{
	private Player[] m_players = new Player[4];
	
	public void setPlayer(Player player, int index)
	{
		if (index < 0 || index > 3)
			throw new InvalidModelException("Invalid seat index for player.");
		
		if (m_players[index] != null)
			throw new InvalidModelException("Seat already occupied.");
		
		m_players[index] = player;
	}
}

package house.mcintosh.mahjong.model;

import java.util.ArrayList;
import java.util.List;

public class Player
{
	// We expect no more than four players, so a list is probably more
	// efficient than a Set or Map.
	/** All the instances that have been created so far. */
	static private final List<Player> s_allPlayers = new ArrayList<>();
	
	private String m_name;
	
	/**
	 * Private constructor so that instance must be created through factory methods to
	 * ensure that there is only one instance for each Player.
	 */
	private Player(String name)
	{
		m_name = name;
	}
	
	/**
	 * Get an existing Player if there is one that matches the name, or
	 * create a new instance.
	 */
	static public synchronized Player get(String name)
	{
		Player newOne = new Player(name);
		
		for (Player player : s_allPlayers)
			if (player.equals(newOne))
				return player;
		
		s_allPlayers.add(newOne);
		
		return newOne;
	}
	
	/** Override so that instances can be used as keys in maps and sets */
	@Override
	public int hashCode()
	{
		return m_name.hashCode();
	}
	
	/** Override so that instances can be used as keys in maps and sets */
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Player))
			return false;
		
		Player otherPlayer = (Player)other;
		
		return m_name.equals(otherPlayer.m_name);
	}
}

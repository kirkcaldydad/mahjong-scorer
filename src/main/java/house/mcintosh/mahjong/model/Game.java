package house.mcintosh.mahjong.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import house.mcintosh.mahjong.exception.InvalidGameStateException;
import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public class Game
{
	private Player[]				m_seats				= new Player[4];;
	private int						m_seatsOccupied		= 0;
	private List<Round>				m_rounds			= new ArrayList<>();
	private Map<Player, Integer>	m_scores			= new HashMap<>();
	private boolean					m_started			= false;
	private boolean					m_finished			= false;
	
	private final ScoringScheme		m_scheme;
	
	private Player		m_startingPlayer;
	private Player		m_endingPlayer;
	private Player		m_eastPlayer;
	private Wind		m_prevailingWind;
	
	public Game(ScoringScheme scheme)
	{
		m_scheme = scheme;
	}
	
	public void setPlayer(Player player, int index)
	{
		if (m_started)
			throw new InvalidGameStateException("Cannot add players after game has started.");
		
		if (index < 0 || index > 3)
			throw new InvalidModelException("Invalid seat index for player.");
		
		if (m_seats[index] != null)
			throw new InvalidModelException("Seat already occupied.");
		
		m_seats[index] = player;
		m_seatsOccupied++;
		
		m_scores.put(player, m_scheme.InitialScore);
	}
	
	public void startGame(Player eastPlayer)
	{
		if (m_started)
			throw new InvalidGameStateException("Game is already started.");
		
		if (m_finished)
			throw new InvalidGameStateException("Game is finished.");
		
		if (m_seatsOccupied < 2)
			throw new InvalidGameStateException("Must have at least two players.");
		
		m_startingPlayer	= eastPlayer;
		m_endingPlayer		= endingPlayer();
		m_eastPlayer		= eastPlayer;
		m_prevailingWind	= Wind.EAST;
		m_started			= true;
	}
	
	public void addRound(Round round)
	{
		if (!m_started)
			throw new InvalidGameStateException("Game is not started.");
		
		if (m_finished)
			throw new InvalidGameStateException("Game is finished.");
		
		m_rounds.add(round);
		
		// Update the score with the new round scores.
		
		for (Player player : m_seats)
		{
			if (player == null)
				continue;
			
			Integer currentScore = m_scores.get(player);
			
			m_scores.put(player, currentScore + round.getPlayerScore(player));
		}
		
		// Move the player and prevailing wind on to the next round.
		
		if (round.getHand(m_eastPlayer).isMahjong())
			// Continue game without moving east player on.
			return;
		
		// Check for the end of the game.
		
		if (m_eastPlayer.equals(m_endingPlayer) && m_prevailingWind == Wind.NORTH)
		{
			m_finished = true;
			return;
		}
		
		//Step east player forward to find the next player.
		int eastPlayerIndex = findPlayerIndex(m_eastPlayer);
		
		do
		{
			eastPlayerIndex++;
			
			if (eastPlayerIndex >= m_seats.length)
				eastPlayerIndex = 0;
		}
		while (m_seats[eastPlayerIndex] == null);
		
		m_eastPlayer = m_seats[eastPlayerIndex];
		
		if (m_eastPlayer == m_startingPlayer)
			m_prevailingWind = m_prevailingWind.next();
	}
	
	public Wind getPrevailingWind()
	{
		return m_prevailingWind;
	}
	
	public Player getEastPlayer()
	{
		return m_eastPlayer;
	}
	
	public Wind getPlayerWind(Player player)
	{
		int		index	= findPlayerIndex(m_eastPlayer);
		Wind	wind	= Wind.EAST;
		
		while (true)
		{
			if (player.equals(m_seats[index]))
			{
				return wind;
			}
			
			index	= (index+1) % m_seats.length;
			wind	= wind.next();
		}
	}
	
	public int getPlayerScore(Player player)
	{
		return m_scores.get(player);
	}
	
	public boolean isStarted()
	{
		return m_started;
	}
	
	public boolean isFinished()
	{
		return m_finished;
	}
	
	
	
	/**
	 * @return The last player in the sequence around the table.
	 */
	private Player endingPlayer()
	{
		int		playerIndex;
		
		playerIndex = findPlayerIndex(m_startingPlayer);
		
		// Found the player that is east, so step backwards to find the previous player.
		
		do
		{
			if (playerIndex <= 0)
				playerIndex = m_seats.length;
			
			playerIndex--;
		}
		while (m_seats[playerIndex] == null);
		
		return m_seats[playerIndex];
	}

	private int findPlayerIndex(Player player)
	{
		for (int playerIndex = 0; playerIndex < m_seats.length ; playerIndex++)
			if (player.equals(m_seats[playerIndex]))
				return playerIndex;
		
		throw new InvalidModelException("Player not found");
	}
}

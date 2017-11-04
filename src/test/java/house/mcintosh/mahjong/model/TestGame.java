package house.mcintosh.mahjong.model;

import static org.junit.Assert.*;

import org.junit.Test;

import house.mcintosh.mahjong.exception.InvalidGameStateException;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public class TestGame
{

	@Test
	public void gameRun()
	{
		Game game = new Game(ScoringScheme.instance());
		
		Player[] players = new Player[4];
		
		Player mickey	= Player.get("Mickey");
		Player donald	= Player.get("Donald");
		Player pluto	= Player.get("Pluto");
		Player goofy	= Player.get("Goofy"); 
		
		players[0] = mickey;
		players[1] = donald;
		players[2] = pluto;
		players[3] = goofy;
		
		game.setPlayer(mickey, 0);
		game.setPlayer(donald, 1);
		game.setPlayer(pluto, 2);
		game.setPlayer(goofy, 3);
		
		game.startGame(mickey);
		
		// First prevailing wind - east moves on every round.
		
		Round round = RoundUtil.createRound(players, Wind.EAST, mickey, pluto);
		
		int scoreM = round.getPlayerScore(mickey);
		int scoreD = round.getPlayerScore(donald);
		int scoreP = round.getPlayerScore(pluto);
		int scoreG = round.getPlayerScore(goofy);
		
		game.addRound(round);
		
		assertEquals(Wind.EAST, game.getPrevailingWind());
		assertEquals(donald, game.getEastPlayer());
		
		scoreM = game.getPlayerScore(mickey);
		scoreD = game.getPlayerScore(donald);
		scoreP = game.getPlayerScore(pluto);
		scoreG = game.getPlayerScore(goofy);
		
		assertEquals(2000-2*136+4-8+4-4, game.getPlayerScore(mickey));
		assertEquals(2000-1*136+8-4+4-2, game.getPlayerScore(donald));
		assertEquals(2000+4*136, game.getPlayerScore(pluto));
		assertEquals(2000-1*136+4-4+2-4, game.getPlayerScore(goofy));
		
		assertEquals(8000, scoreM + scoreD + scoreP + scoreG);
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.EAST, game.getPrevailingWind());
		assertEquals(pluto, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		// Second prevailing wind - east wins sometimes so no moving on.
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(donald, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));

		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(pluto, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(pluto, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(goofy, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.WEST, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		// Third prevailing wind
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), donald));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.NORTH, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		// Fourth prevailing wind
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), donald));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), donald));
		assertFalse(game.isFinished());
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
		assertFalse(game.isFinished());
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		// When game is finished, play sticks at last round position without advancing.
		assertEquals(Wind.NORTH, game.getPrevailingWind());
		assertEquals(goofy, game.getEastPlayer());
		assertTrue(game.isFinished());
		
		scoreM = game.getPlayerScore(mickey);
		scoreD = game.getPlayerScore(donald);
		scoreP = game.getPlayerScore(pluto);
		scoreG = game.getPlayerScore(goofy);
		assertEquals(8000, scoreM + scoreD + scoreP + scoreG);
		
		
		// Try adding another round to the finished game.
		
		try
		{
			game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
			fail();
		}
		catch(InvalidGameStateException e)
		{
			// OK - expected.
		}
	}
}


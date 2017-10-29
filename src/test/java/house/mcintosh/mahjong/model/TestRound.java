package house.mcintosh.mahjong.model;

import static org.junit.Assert.*;

import org.junit.Test;

import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public class TestRound
{
	@Test
	public void testCreatingRound1()
	{
		Wind prevailingWind = Wind.EAST;
		
		Round round = new Round(prevailingWind);
		
		Player eastPlayer	= Player.get("East Player");
		Player southPlayer	= Player.get("South Player");
		Player westPlayer	= Player.get("West Player");
		Player northPlayer	= Player.get("North Player");
		
		round.addHand(eastPlayer, createMahjongHand136(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, createHand2(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, createHand4(Wind.WEST, prevailingWind), Wind.WEST);
		round.addHand(northPlayer, createHand16(Wind.NORTH, prevailingWind), Wind.NORTH);
		
		// Check the score for each player.
		
		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);
		int northScore	= round.getPlayerScore(northPlayer);

		assertEquals(+136*6, eastScore);
		assertEquals(-136*2 +2-4+2-16, southScore);
		assertEquals(-136*2 +4-2+4-16, westScore);
		assertEquals(-136*2 +16-2+16-4, northScore);
		assertEquals(0, eastScore + southScore + westScore + northScore);		
	}
	
	@Test
	public void testCreatingRound2()
	{
		Wind prevailingWind = Wind.NORTH;
		
		Round round = new Round(prevailingWind);
		
		Player eastPlayer	= Player.get("East Player");
		Player southPlayer	= Player.get("South Player");
		Player westPlayer	= Player.get("West Player");
		Player northPlayer	= Player.get("North Player");
		
		round.addHand(eastPlayer, createHand2(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, createMahjongHand136(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, createHand4(Wind.WEST, prevailingWind), Wind.WEST);
		round.addHand(northPlayer, createHand16(Wind.NORTH, prevailingWind), Wind.NORTH);
		
		// Check the score for each player.
		
		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);
		int northScore	= round.getPlayerScore(northPlayer);

		assertEquals(-136*2 +4-8+4-32, eastScore);
		assertEquals(+136*4, southScore);
		assertEquals(-136 +8-4+4-16, westScore);
		assertEquals(-136 +32-4+16-4, northScore);
		assertEquals(0, eastScore + southScore + westScore + northScore);		
	}
	
	@Test
	public void testCreatingRound3()
	{
		Wind prevailingWind = Wind.WEST;
		
		Round round = new Round(prevailingWind);
		
		Player eastPlayer	= Player.get("East Player");
		Player southPlayer	= Player.get("South Player");
		Player westPlayer	= Player.get("West Player");
		Player northPlayer	= Player.get("North Player");
		
		round.addHand(eastPlayer, createHand2(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, createHand4(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, createMahjongHand136(Wind.WEST, prevailingWind), Wind.WEST);
		round.addHand(northPlayer, createHand16(Wind.NORTH, prevailingWind), Wind.NORTH);
		
		// Check the score for each player.
		
		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);
		int northScore	= round.getPlayerScore(northPlayer);

		assertEquals(-136*2 +4-8+4-32, eastScore);
		assertEquals(-136 +8-4+4-16, southScore);
		assertEquals(+136*4, westScore);
		assertEquals(-136 +32-4+16-4, northScore);
		assertEquals(0, eastScore + southScore + westScore + northScore);		
	}
	
	private ScoredHand createMahjongHand136(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Dragon.RED), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO), Group.Visibility.CONCEALED), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN), Group.Visibility.CONCEALED), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.THREE)), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Dragon.WHITE), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		assertTrue(hand.isMahjong());
		assertEquals((10+4+16+4)*2*2, hand.getTotalScoreUnlimited());
		
		return hand;
	}
	
	private ScoredHand createHand2(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		assertEquals(2, hand.getTotalScoreUnlimited());
		
		return hand;
	}
	
	private ScoredHand createHand4(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.BAMBOO, Tile.Number.THREE), Group.Visibility.CONCEALED), scheme, playerWind, prevailingWind));
		assertEquals(4, hand.getTotalScoreUnlimited());
		
		return hand;
	}

	private ScoredHand createHand16(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Suit.CIRCLES, Tile.Number.NINE), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		assertEquals(16, hand.getTotalScoreUnlimited());
		
		return hand;
	}

}

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.HumanPlayer;
import clueGame.Solution;
import clueGame.Player;

/**
 * GameSetupTests Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 *	Tests the player, weapons and cards setup
 */
public class GameSetupTests {

	//Setup Tests
	/*
	 * People are loaded in (6 people)
	 * Proper Human or Computer player is initialized based on people data
	 * Deck of all cards is created (composed of rooms, weapons, and people)
	 * The solution to the game is dealt
	 * The other cards are dealt to the players.
	 *
	 * Load people and weapons from ClueSetup.txt and ensure the data was loaded properly.
	 * Create Player class with human and computer child classes.   Use people data to instantiate 6 players (1 human and 5 computer)
	 * Create complete deck of cards (weapons, people and rooms)
	 * Deal cards to the Answer and the players (all cards dealt, players have roughly same # of cards, no card dealt twice)
	 */

	private static Board board;

	@BeforeAll
	public static void setUp() {

		board = Board.getInstance();

		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");

		board.initialize();

	}

	@Test
	public void testPeopleData() {
		//tests that people have been loaded correctly

		List<Player> players = board.getPlayers();

		assertEquals(6, players.size());

		//player 1
		Player player = players.get(0);
		assertTrue(player.getName().equals("Cassidy Murderdoer"));
		assertTrue(player.getColor().equals(new Color (214, 109, 149)));
		assertEquals(7, player.getRow());
		assertEquals(11, player.getCol());
		assertTrue(player.getClass() == HumanPlayer.class);

		//player 2
		player = players.get(1);
		assertTrue(player.getName().equals("Omni-Man"));
		assertTrue(player.getColor().equals(new Color (161, 21, 27)));
		assertEquals(7, player.getRow());
		assertEquals(14, player.getCol());
		assertFalse(player.getClass() == HumanPlayer.class);

		//player 3
		player = players.get(2);
		assertTrue(player.getName().equals("Elisabeth Womanslaughter"));
		assertTrue(player.getColor().equals(new Color (190, 88, 199)));
		assertEquals(10, player.getRow());
		assertEquals(7, player.getCol());
		assertFalse(player.getClass() == HumanPlayer.class);

		//player 4
		player = players.get(3);
		assertTrue(player.getName().equals("John Wick"));
		assertTrue(player.getColor().equals(Color.black));
		assertEquals(10, player.getRow());
		assertEquals(18, player.getCol());
		assertFalse(player.getClass() == HumanPlayer.class);

		//player 5
		player = players.get(4);
		assertTrue(player.getName().equals("Shrek"));
		assertTrue(player.getColor().equals(new Color (36, 133, 81)));
		assertEquals(13, player.getRow());
		assertEquals(11, player.getCol());
		assertFalse(player.getClass() == HumanPlayer.class);

		//player 6
		player = players.get(5);
		assertTrue(player.getName().equals("Anakin Skywalker"));
		assertTrue(player.getColor().equals(new Color(6, 136, 150)));
		assertEquals(13, player.getRow());
		assertEquals(14, player.getCol());
		assertFalse(player.getClass() == HumanPlayer.class);



	}

	@Test
	public void testCardChecks() {
		String sameName = "card";
		CardType sameType = CardType.WEAPON;

		Card card1 = new Card(sameName, sameType);
		Card card2 = new Card(sameName, sameType);
		Card card3 = new Card("not card", CardType.ROOM);
		Card card4 = new Card(sameName, CardType.ROOM);

		assertTrue(card1.equals(card2));
		assertFalse(card1.equals(card3));
		assertFalse(card1.equals(card4));
	}

	@Test
	public void testWeaponData() {
		List<Card> cards = board.getDeck();

		CardType type = CardType.WEAPON;
		Card[] weapons = new Card[6];
		weapons[0] = new Card("Vinyl Record", type);
		weapons[1] = new Card("Phone Cord", type);
		weapons[2] = new Card("Ray Gun", type);
		weapons[3] = new Card("Pencil", type);
		weapons[4] = new Card("Lightsaber", type);
		weapons[5] = new Card("Hydro Flask", type);

		//verify the weapons are all in the card list
		for(Card weapon : weapons) {
			assertTrue(cards.contains(weapon));
		}
	}

	@Test
	public void testCardDeck() {
		List<Card> cards = board.getDeck();

		// DEBUG
		// System.out.println(cards);

		// verify every card is present
		assertEquals(21, cards.size());

		// verify list contains each type of card
		assertTrue(cards.contains(new Card("Ray Gun", CardType.WEAPON)));

		assertTrue(cards.contains(new Card("Garden", CardType.ROOM)));

		assertTrue(cards.contains(new Card("Anakin Skywalker", CardType.PERSON)));
	}

	@Test
	public void testDeal() {
		List<Player> players = board.getPlayers();
		Set<Card> cards = new HashSet<>();
		List<Card> boardCards = board.getDeck();
		board.deal();

		// ensure there are no duplicate cards
		Solution solution = board.getSolution();
		cards.add(solution.getRoom());
		cards.add(solution.getPerson());
		cards.add(solution.getWeapon());
		for(Player player : players) {
			Set<Card> playerHand = player.getHand();
			assertFalse(cards.containsAll(playerHand));
			cards.addAll(playerHand);
		}

		//ensure all cards are present
		assertEquals(boardCards.size(), cards.size());
		for(Card card : boardCards) {
			assertTrue(cards.contains(card));
		}

		//ensure even spread of Cards
		int minHandSize = players.getFirst().getHand().size();
		int maxHandSize = minHandSize;
		for (Player player : players) {
			int handSize = player.getHand().size();
			if (handSize > maxHandSize) {
				maxHandSize = handSize;
			} else if (handSize < maxHandSize) {
				minHandSize = handSize;
			}
		}

	}

}

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Solution;
import clueGame.Player;

public class GameSolutionTest {
	
	private static Board board;
	private static List<Player> players;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
		
		players = board.getPlayers();
		
		// I created new cards for every test because I wanted to switch it up
	}
	
	// Check an Accusation
    /*
     * solution that is correct
     * solution with wrong person
     * solution with wrong weapon
     * solution with wrong room 
     */
	
	@Test
	public void correctAccusationTest() {
		Card person = new Card("Dr. Badguy", CardType.PERSON);
		Card room = new Card("Observatory", CardType.ROOM);
		Card weapon = new Card("Pencil", CardType.WEAPON);
		
		Solution solution = new Solution(person, room, weapon);
		board.setSolution(solution);
		
		Solution accusation = new Solution(person, room, weapon);
		
		assertTrue(board.checkAccusation(accusation));
	}
	
	@Test
	public void incorrectPersonAccusationTest() {
		Card person = new Card("David Deathmaker", CardType.PERSON);
		Card room = new Card("Observatory", CardType.ROOM);
		Card weapon = new Card("Phone Cord", CardType.WEAPON);
		
		Solution solution = new Solution(person, room, weapon);
		board.setSolution(solution);
		
		Card person_wrong = new Card("Anakin", CardType.PERSON);
		
		Solution accusation = new Solution(person_wrong, room, weapon);
		
		assertFalse(board.checkAccusation(accusation));
		
	}
	
	@Test
	public void incorrectWeaponAccusationTest() {
		Card person = new Card("Elisabeth", CardType.PERSON);
		Card room = new Card("Art Gallery", CardType.ROOM);
		Card weapon = new Card("Vinyl Record", CardType.WEAPON);
		
		Solution solution = new Solution(person, room, weapon);
		board.setSolution(solution);
		
		Card weapon_wrong = new Card("Hydro Flask", CardType.WEAPON);
		
		Solution accusation = new Solution(person, room, weapon_wrong);
		
		assertFalse(board.checkAccusation(accusation));
	}
	
	@Test
	public void incorrectRoomAccusationTest() {
		Card person = new Card("Elisabeth", CardType.PERSON);
		Card room = new Card("Lounge", CardType.ROOM);
		Card weapon = new Card("Ray Gun", CardType.WEAPON);
		
		Solution solution = new Solution(person, room, weapon);
		board.setSolution(solution);
		
		Card room_wrong = new Card("Garden", CardType.ROOM);
		
		Solution accusation = new Solution(person, room_wrong, weapon);
		
		assertFalse(board.checkAccusation(accusation));
	}
	
	// Player disproves a suggestion, tests include:
    /* 
     * If player has only one matching card it should be returned
     * If players has >1 matching card, returned card should be chosen randomly
     * If player has no matching cards, null is returned
     */
	
	@Test
	public void disproveSuggestionOneMatchTest() {
		ComputerPlayer player = new ComputerPlayer("AI", Color.red, 0, 0);
		
		Card person = new Card("Elisabeth", CardType.PERSON);
		Card room = new Card("Art Gallery", CardType.ROOM);
		Card weapon = new Card("Vinyl Record", CardType.WEAPON);
		
		player.updateHand(person);
		player.updateHand(weapon);
		player.updateHand(room);
		
		Card weapon_suggested = new Card("Phone Cord", CardType.WEAPON);
		Card room_suggested = new Card("Study", CardType.ROOM);
		
		Solution suggestion = new Solution(person, weapon_suggested, room_suggested);
		
		Card result = player.disproveSuggestion(suggestion);
		
		assertNotNull(result);
		assertEquals(person, result);
	}
	
	@Test
	public void disproveSuggestionMoreMatchesTest() {
		ComputerPlayer player = new ComputerPlayer("AI", Color.magenta, 0, 0);
		
		Card person = new Card("Phillip", CardType.PERSON);
		Card room = new Card("Dungeon", CardType.ROOM);
		Card weapon = new Card("Hydro Flask", CardType.WEAPON);
		Card weapon_diff = new Card("Pencil", CardType.WEAPON);
		
		player.updateHand(person);
		player.updateHand(room);
		player.updateHand(weapon_diff);
		
		Solution suggestion = new Solution(person, room, weapon);
		
		int personCount = 0;
		int roomCount = 0;
		
		for (int i = 0; i < 200; i++) {
			Card result = player.disproveSuggestion(suggestion);
			assertNotNull(result);
			if(result.equals(person)) personCount++;
			else if (result.equals(room)) roomCount ++;
		}
		
		assertTrue(personCount > 0);
		assertTrue(roomCount > 0);
		
	}
	
	@Test
	public void disproveSuggestionNoMatchesTest() {
		ComputerPlayer player = new ComputerPlayer("AI", Color.blue, 0, 0);
		
		Card person = new Card("David", CardType.PERSON);
		Card room = new Card("Study", CardType.ROOM);
		Card weapon = new Card("Phone Cord", CardType.WEAPON);
		
		player.updateHand(person);
		player.updateHand(weapon);
		player.updateHand(room);
		
		Card person_suggested = new Card("Cassidy", CardType.PERSON);
		Card weapon_suggested = new Card("Light Saber", CardType.WEAPON);
		Card room_suggested = new Card("Garden", CardType.ROOM);
		
		Solution suggestion = new Solution(person_suggested, weapon_suggested, room_suggested);
		
		Card result = player.disproveSuggestion(suggestion);
		
		assertNull(result);
		
	}
	
	// Handle a suggestion made, tests include:
    /*
     * Suggestion no one can disprove returns null
     * Suggestion only suggesting player can disprove returns null
     * Suggestion only human can disprove returns answer (i.e., card that disproves suggestion)
     * Suggestion that two players can disprove, correct player (based on starting with next player in list) returns answer
     */
	
	@Test
	public void handleSuggestionNoDisproveTest() {
		for (Player p : players) {
			p.clearHand();
		}
		
		Card person = new Card("Dr. Badguy", CardType.PERSON);
		Card room = new Card("Obseratory", CardType.ROOM);
		Card weapon = new Card("Pencil", CardType.WEAPON);
		Solution suggestion = new Solution(person, room, weapon);
		
		Player suggester = players.get(0);
		
		Card result = board.handleSuggestion(suggestion, suggester);
		
		assertNull(result);
	}
	
	@Test
	public void handleSuggestionOnePlayerDisproveTest() {
		for (Player p : players) {
			p.clearHand();
		}
		
		Player suggester = players.get(0);
		
		Card person = new Card("David Deathmaker", CardType.PERSON);
		Card room = new Card("Lounge", CardType.ROOM);
		Card weapon = new Card("Phone Cord", CardType.WEAPON);
		
		suggester.updateHand(person);
		
		Solution suggestion = new Solution(person, room, weapon);
		
		Card result = board.handleSuggestion(suggestion, suggester);
		
		assertNull(result);
	}
	
	@Test
	public void handleSuggestionOnlyHumanDisproveTest() {
		for (Player p : players) {
			p.clearHand();
		}
		
		HumanPlayer human = null;
		Player suggester = null;
		for (Player p : players) {
			if (p instanceof HumanPlayer) {
				human = (HumanPlayer) p;
			} else if (suggester == null) {
				suggester = p;
			}
		}
		
		Card room = new Card("Art Gallery", CardType.ROOM);
		Card person = new Card("Elisabeth Womanslaughter", CardType.PERSON);
		Card weapon = new Card("Vinyl Record", CardType.WEAPON);
		
		human.updateHand(room);
		
		Solution suggestion = new Solution(person, room, weapon);
		
		Card result = board.handleSuggestion(suggestion, suggester);
		
		assertNotNull(result);
		assertEquals(room, result);
	}
	
	@Test
	public void handleSuggestionTwoPlayersDisproveTest() {
		for (Player p : players) {
			p.clearHand();
		}
		
		Player suggester = players.get(0);
		Player p1 = players.get(1);
		Player p2 = players.get(2);
		
		Card person = new Card("Phillip Headscrew", CardType.PERSON);
		Card room = new Card("Dungeon", CardType.ROOM);
		Card weapon = new Card("Hydro Flask", CardType.WEAPON);
		
		p1.updateHand(person);
		p2.updateHand(room);
		
		Solution suggestion = new Solution(person, room, weapon);
		
		Card result = board.handleSuggestion(suggestion, suggester);
		
		assertNotNull(result);
		assertEquals(person, result);
		
	}
	
	 

}

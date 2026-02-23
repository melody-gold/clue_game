package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.*;

public class ComputerAITest {

    private static Board board;
    private static Map<String, Card> roomCards;
    private static Set<Card> weaponCards;
    private static Set<Card> personCards;
   
    @BeforeAll
    public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
		
		//Separate the cards into 3 collections
		List<Card> deck = board.getDeck();
		roomCards = new HashMap<>();
		weaponCards = new HashSet<>();
		personCards = new HashSet<>();
		
		for(Card card : deck) {
			switch(card.getType()) {
				case ROOM : 
					roomCards.put(card.getCardName(), card);
					break;
				case WEAPON : 
					weaponCards.add(card);
					break;
				case PERSON : 
					personCards.add(card);
					break;	
			}
		}
		
		//make these lists immuteable
		
		personCards = Set.copyOf(personCards);
		weaponCards = Set.copyOf(weaponCards);
		roomCards = Map.copyOf(roomCards);
		
	}
    
    private Card fillSeenWith(ComputerPlayer player, Set<Card> cards) {    	
    	var iter = cards.iterator(); //var avoids extra import
    	
    	Card unSeen = iter.next(); //store card to avoid
    	
    	while (iter.hasNext()) { //add every other card
    		player.updateHand(iter.next());
    	}
    	
    	return unSeen;
    }
    
    @Test
    public void testRoomCorrectness() {
    	//System.out.println("Hello");
    	ComputerPlayer suggester =  new ComputerPlayer("test", null, 0, 0);
    	//Ensure the suggester always asks about the room it is passed
		for(Card room : roomCards.values()) {
			Card suggestedRoom = suggester.createSuggestion(room).getRoom();
			//DEBUG
			//System.out.println("actual room: " + room +"\nsuggestion room: " + suggestedRoom);
			assertTrue(suggestedRoom.equals(room));
		}
    	
    }
    
    @Test
    public void testSelectionLogic() {
    	ComputerPlayer suggester =  new ComputerPlayer("test", null, 0, 0);
    	
    	
    	//ensure that computer chooses the unseen person
    	
    	//fill suggester's seen cards and store the unseen card
    	Card UnseenPerson = fillSeenWith(suggester, personCards);
    	Solution suggestion = suggester.createSuggestion(roomCards.get("Observatory"));
    	//Debug
    	//System.out.println(suggester.getName() + " suggested: " + suggestion + 
    	//		"\nthey've seen: " + suggester.getSeenCards());
    	assertTrue(suggestion.getPerson().equals(UnseenPerson));
    	
    	//ensure that computer chooses the unseen weapon
    	suggester =  new ComputerPlayer("test", null, 0, 0);
    	
    	Card UnseenWeapon = fillSeenWith(suggester, weaponCards);
    	suggestion = suggester.createSuggestion(roomCards.get("Art Gallery"));
    	//Debug
    	//System.out.println(suggester.getName() + " suggested: " + suggestion);
    	assertTrue(suggestion.getWeapon().equals(UnseenWeapon));
    	
    	//ensure that computer chooses the unseen weapon and person
    	suggester =  new ComputerPlayer("test", null, 0, 0);
    	
    	UnseenWeapon = fillSeenWith(suggester, weaponCards);
    	UnseenPerson = fillSeenWith(suggester, personCards);
    	
    	suggestion = suggester.createSuggestion(roomCards.get("Laboratory"));
    	Solution correctSuggestion = new Solution(UnseenPerson, roomCards.get("Laboratory"), UnseenWeapon);
    	//Debug
    	//System.out.println("suggested: " + suggestion + "\nCorrect: " + correctSuggestion);
    	assertTrue(suggestion.equals(correctSuggestion));
    	
    }
    
    @Test
    public void testSelectionRandomness(){
    	ComputerPlayer testPlayer = new ComputerPlayer("test", null, 0, 0);
    	Card room = roomCards.get("Library");
    	
    	Set<Card> suggestedPeople = new HashSet<>(); //tracks people suggested
    	Set<Card> suggestedWeapons = new HashSet<>(); //tracks weapons suggested
    	
    	//test random card selection
    	
    	for(int i = 0; i < 1000; i++) {
    		Solution suggestion = testPlayer.createSuggestion(room);
    		
    		suggestedPeople.add(suggestion.getPerson());
    		suggestedWeapons.add(suggestion.getWeapon());
    		
    	}
    	
    	assertTrue(suggestedPeople.equals(personCards));
    	assertTrue(suggestedWeapons.equals(weaponCards));
    	
    	//test random excluding seen
    	
    	var allCards = new ArrayList<>(board.getDeck());
    	Collections.shuffle(allCards);
    	//add 5 random cards testPlayer's seen cards
    	for(int i = 0; i < 5; i++) {
    		testPlayer.updateSeen(allCards.get(i));
    	}
    	

    	suggestedPeople = new HashSet<>();
    	suggestedWeapons = new HashSet<>();
    	
    	for(int i = 0; i < 1000; i++) {
    		Solution suggestion = testPlayer.createSuggestion(room);
    		
    		suggestedPeople.add(suggestion.getPerson());
    		suggestedWeapons.add(suggestion.getWeapon());
    		
    	}
    	
    	var seenCards = testPlayer.getSeenCards();
    	//create target sets without the seen cards
    	Set<Card> targetPeople = new HashSet<>(personCards);
    	targetPeople.removeAll(seenCards);
    	Set<Card> targetWeapons = new HashSet<>(weaponCards);
    	targetWeapons.removeAll(seenCards);
    	
    	assertTrue(suggestedPeople.equals(targetPeople));
    	assertTrue(suggestedWeapons.equals(targetWeapons));
    
    }
    
    @Test
    public void	testRoomlessTargets(){
    	//ensure a target is randomly selected if no rooms are present
  
    	//test cell @ (10, 20)
    	ComputerPlayer testPlayer = new ComputerPlayer("test", null, 10, 20);
    	BoardCell playerCell = board.getCell(testPlayer.getRow(), testPlayer.getCol());
    	board.calcTargets(playerCell, 1); //has no roomcenters
    	
    	Set<BoardCell> targets = board.getTargets();
    	
    	Set<BoardCell> targetsSelected = new HashSet<>();
    	for(int i = 0; i < 1000; i++) {
    		targetsSelected.add(testPlayer.selectTarget(targets));
    	}
    	
    	assertTrue(targetsSelected.equals(targets));
    	
    	//test cell @ (18, 0)
    	testPlayer = new ComputerPlayer("test", null, 18, 0);
    	playerCell = board.getCell(testPlayer.getRow(), testPlayer.getCol());
    	board.calcTargets(playerCell, 3); //has no roomcenters
    	
    	targets = board.getTargets();
    	
    	targetsSelected = new HashSet<>();
    	for(int i = 0; i < 1000; i++) {
    		targetsSelected.add(testPlayer.selectTarget(targets));
    	}
    	
    	assertTrue(targetsSelected.equals(targets));
    	
    }
    
    @Test 
    public void testTargetsWithRooms() {
    	//ensure an unseen room is targeted
    	
    	//test cell @ (10, 3)
    	ComputerPlayer testPlayer = new ComputerPlayer("test", null, 10, 3);
    	BoardCell playerCell = board.getCell(testPlayer.getRow(), testPlayer.getCol());
    	board.calcTargets(playerCell, 1); //adjacent to library
    	
    	Set<BoardCell> targets = board.getTargets();
    	//DEBUG
    	//System.out.println(targets);
    	//System.out.println(testPlayer.selectTarget(targets));
    	assertTrue(board.getCell(12, 1).equals(testPlayer.selectTarget(targets)));
    	
    	//ensure seen room is randomized
    	//test cell @ (18, 4)
    	testPlayer = new ComputerPlayer("test", null, 18, 4);
    	testPlayer.updateSeen(roomCards.get("Study")); //room adj to testPlayer
    	playerCell = board.getCell(testPlayer.getRow(), testPlayer.getCol());
    	board.calcTargets(playerCell, 1); //adj to a seen room
    	
    	targets = board.getTargets();
    	
    	Set<BoardCell> targetsSelected = new HashSet<>();
    	for(int i = 0; i < 1000; i++) {
    		targetsSelected.add(testPlayer.selectTarget(targets));
    	}
    	
    	assertTrue(targetsSelected.equals(targets));
    	
    }
    
}

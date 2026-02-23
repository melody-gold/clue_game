package clueGame;

import java.awt.Color;
import java.util.Set;

import gui.ClueGame;

import java.util.Collections;

/**
 * ComputerPlayer Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 *	Extends Player, handles an computer player which makes logic-based moves
 */
public class ComputerPlayer extends Player {
	Solution accusation;
	
	public ComputerPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
		this.accusation = null;
	}
	
	
	public Solution createSuggestion(Card room) {
		//debug artifact
		//Card nullCard = new Card("INVALID_ROOM", null);
		
		Set<Card> unseenPeople = getUnseenCards(ALL_PEOPLE_SET);
		Set<Card> unseenWeapons = getUnseenCards(ALL_WEAPONS_SET);
		
		//select a random Unseen person
		Card suggestionPerson = getRandomElement(unseenPeople);  
		
		//select an Unseen weapon
		Card suggestionsWeapon = getRandomElement(unseenWeapons);
		
		return new Solution(suggestionPerson, room, suggestionsWeapon);
	}
	
	public Solution createSuggestion(char roomInitial) {
		Card roomCard = ALL_ROOMS_MAP.get(roomInitial);
		return createSuggestion(roomCard);
	}
	
	public BoardCell selectTarget(Set<BoardCell> targets) {
		Set<Card> seenCards = getSeenCards();
		
		for(BoardCell cell : targets) {
			if(cell.isRoomCenter() && seenCards.contains(ALL_ROOMS_MAP.get(cell.getRoomInitial())) == false) {
				return cell;
			}
		}
		
		return getRandomElement(targets);
	}
	
	@Override
	public void receiveSuggestionResult(Card card, Solution suggestion) {
		if(card == null) {
			//ensure nothing in the suggestion has been seen already
			if(Collections.disjoint(suggestion.toSet(), this.getSeenCards())) {
				//DEBUG
				if(ClueGame.TEST_COMPUTER_SOLUTION_METHOD && !(willAccuse())) {
					System.out.println("\n_____" + getName() + " Has made a correct Suggestion_______"
							+ "\n Suggestion: " + suggestion);
				}
				
				this.accusation = suggestion;	
			}
		}else {
			this.checkUnseenCards();
		}
	}
	
	//check unSeen Cards to see if unseen card requirments are met to accuse
	private void checkUnseenCards() {
		Set<Card> unseenPeople = getUnseenCards(ALL_PEOPLE_SET);
		Set<Card> unseenWeapons = getUnseenCards(ALL_WEAPONS_SET);
		Set<Card> unseenRooms = getUnseenCards(ALL_ROOMS_MAP.values());
		
		//the comp player will accuse if there is only a single unseen card in every category
		if(unseenPeople.size() == 1 && unseenWeapons.size() == 1 && unseenRooms.size() == 1) {
			Card accusedPerson = getRandomElement(unseenPeople);
			Card murderWeapon = getRandomElement(unseenWeapons);
			Card murderLocation = getRandomElement(unseenRooms);
			
			//DEBUG
			if(ClueGame.TEST_COMPUTER_SOLUTION_METHOD && !(willAccuse())) {
				System.out.println("\n_____" + getName() + " Has eliminated all incorrect solutions_______"
						+ "\n Accusation: " +  new Solution(accusedPerson, murderLocation,  murderWeapon));
			}
			
			this.accusation =  new Solution(accusedPerson, murderLocation,  murderWeapon);
			
			
		}
	}
	
	//returns true if all conditions are met to accuse 
	@Override
	public boolean willAccuse() {
		if(this.accusation == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public Solution makeAccusation() {
		return this.accusation;
	}


}

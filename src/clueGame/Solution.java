package clueGame;

import java.util.HashSet;
import java.util.Set;

/**
 * Solution Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 *	Holds 3 cards that make up a Solution
 */
public class Solution {
	private Card room;
	private Card person;
	private Card weapon;
	
	public Solution(Card person, Card room, Card weapon) {
		this.person = person;
		this.room = room;
		this.weapon = weapon;
	}

	public Card getRoom() {
		return room;
	}

	public Card getPerson() {
		return person;
	}

	public Card getWeapon() {
		return weapon;
	}
	
	public Set<Card> toSet() {
		Set<Card> set =  new HashSet<>();
		set.add(person);
		set.add(weapon);
		set.add(room);
		return set;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Solution)) return false;
		Solution other = (Solution) obj;
		return person.equals(other.person) && room.equals(other.room) && weapon.equals(other.weapon);
	}

	@Override
	public String toString() {
		return person.getCardName() + " in the " + 
				room.getCardName() + " with the " + 
				weapon.getCardName();
	}
		
}

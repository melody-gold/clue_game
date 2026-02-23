package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Player Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 * Abstract Class which represents a Player on the board, has a position, name,
 * color and a hand
 */
public abstract class Player {
	private String name;
	private Color color;
	private int row;
	private int col;
	private Set<Card> hand;
	private Set<Card> seenCards;

	protected static Set<Card> ALL_PEOPLE_SET;
	protected static Map<Character, Card> ALL_ROOMS_MAP;
	protected static Set<Card> ALL_WEAPONS_SET;

	public Player(String name, Color color, int row, int col) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
		hand = new HashSet<>();
		seenCards = new HashSet<>();
	}
	
	//returns true if the player is ready to accuse
	public abstract boolean willAccuse();
	
	public abstract Solution makeAccusation();
	
	public abstract void receiveSuggestionResult(Card card, Solution suggestion);

	public void moveTo(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Card disproveSuggestion(Solution suggestion) {
		// create set from Solution cards (toSet method)
		Set<Card> matches = new HashSet<>(hand);
		// only keep cards that are also suggested
		matches.retainAll(suggestion.toSet());

		return getRandomElement(matches);

	}

	public void updateHand(Card card) {
		hand.add(card);
		card.addToHand(this);
		seenCards.add(card);
	}

	public void clearHand() {
		hand.clear();
	}

	public void updateSeen(Card card) {
		seenCards.add(card);
	}

	public String getName() {
		return this.name;
	}

	public Color getColor() {
		return this.color;
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public Set<Card> getHand(){
		return this.hand;
	}

	public Set<Card> getSeenCards() {
		return this.seenCards;
	}

	public static void setCards(Collection<Card> allCards, Map<Character, Room> roomMap) {
		Set<Card> personCards = new HashSet<>();
		Map<Character, Card> roomCards = new HashMap<>();
		Set<Card> weaponCards = new HashSet<>();

		// sort non-room Cards
		for(Card card : allCards) {
			switch(card.getType()) {
			case PERSON : 
				personCards.add(card);
				break;	
			case WEAPON : 
				weaponCards.add(card);
				break;
			case ROOM : 
				break;
			}
		}

		// map each room card
		for (Character initial : roomMap.keySet()) {
			Room room = roomMap.get(initial);
			for (Card card : allCards) {
				if (card.getCardName().equals(room.getName())) {
					roomCards.put(initial, card);
				}
			}
		}

		if (allCards.containsAll(roomCards.values()) == false) {
			System.out.println("Player Card allocation Error");
		}

		//give static sets immutable copies
		ALL_PEOPLE_SET =  Set.copyOf(personCards);
		ALL_ROOMS_MAP =  Map.copyOf(roomCards);
		ALL_WEAPONS_SET =  Set.copyOf(weaponCards);
	}

	// return a random element of a set
	protected static <T> T getRandomElement(Set<T> set) {
		if (set.size() == 1) {
			// if there is one elem in set, return that elem
			return set.stream().iterator().next();
		} else if (set.isEmpty()) {
			// return null for an empty set
			return null;
		} else {
			// else cast to an arrayList and shuffle
			ArrayList<T> rand = new ArrayList<>(set);
			Collections.shuffle(rand);
			return rand.getFirst();
		}
	}

	public void draw(Graphics g, int cellWidth, int cellHeight) {
		// default player: no offset
		draw(g, cellWidth, cellHeight, 0, 0);
	}

	public void draw(Graphics g, int width, int height, int offsetX, int offsetY) {
		int r = row;
		int c = col;

		// make the player slightly smaller than the cell
		int margin = Math.min(width, height) / 10;
		int diameter = Math.min(width, height) - margin * 2;

		// center player
		int x = c * width + (width - diameter) / 2 + offsetX;;
		int y = r * height + (height - diameter) / 2 + offsetY;

		// draw player!
		g.setColor(color);
		g.fillOval(x, y, diameter, diameter);

		// draw player outline!
		g.setColor(Color.LIGHT_GRAY);
		g.drawOval(x, y, diameter, diameter);
	}

	protected HashSet<Card> getUnseenCards(Collection<Card> allCards) {
		HashSet<Card> unseenCards = new HashSet<>(allCards);
		unseenCards.removeAll(getSeenCards());
		return unseenCards;
	}
	
}


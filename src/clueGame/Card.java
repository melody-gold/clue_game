package clueGame;

/**
 * Card Class
 * 
 * @author Jacob Dionne
 * @author Melody Goldanloo
 * 
 * Represents card objects in the game with their name and type (room, player, or weapon)
 */

public class Card {
	private String cardName;
	private CardType type;
	private Player inHandOf;

	public Card(String cardName, CardType type) {
		this.cardName = cardName;
		this.type = type;
		inHandOf = null;
	}

	public String getCardName() {
		return cardName;
	}

	public CardType getType() {
		return type;
	}

	public void addToHand(Player player) {
		inHandOf = player;
	}

	public Player getPlayerHoldingThis() {
		return inHandOf;
	}
	/**
	 * @param target
	 */
	@Override
	public boolean equals(Object target){
		if (this == target) {
			return true;
		}
		if (target == null || getClass() != target.getClass()) {
			return false;
		}
		Card other = (Card) target;
		return this.cardName.equals(other.cardName) && this.type == other.type;
	}

	@Override
	public String toString() {
		return cardName;
	}
}

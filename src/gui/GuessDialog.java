package gui;

import clueGame.Card;
import clueGame.CardType;
import clueGame.Room;
import clueGame.Solution;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuessDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final boolean accusation;
	private final Card currentRoom; // only for suggestions

	private JComboBox<Card> roomBox;  // only for accusations
	private JComboBox<Card> personBox;
	private JComboBox<Card> weaponBox;

	private boolean submitted = false;
	
	/**
	 * General constructor for both suggestion & accusation.
	 *
	 * @param parent         parent frame (for modality + centering)
	 * @param currentRoomCard room the human is in (for suggestions)
	 * @param rooms          list of all room cards (for accusations)
	 * @param people         list of all person cards
	 * @param weapons        list of all weapon cards
	 * @param accusation     true = accusation dialog, false = suggestion dialog
	 */
	public GuessDialog(JFrame parent, Card currentRoom, Collection<Card> rooms, Collection<Card> people, Collection<Card> weapons, boolean accusation) {
		super(parent, accusation ? "Make an Accusation" : "Make a Suggestion", true);
		getContentPane().setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);
		this.accusation = accusation;
		this.currentRoom = currentRoom;

		setSize(350, 200);
		setLayout(new GridLayout(4, 2, 8, 8));

		// ROW 1: room
		JLabel roomLabel = ClueGUIUtil.createDefaultLabel("Room");
		add(roomLabel);

		if (accusation) {
			// Accusation: choose from all rooms
			roomBox = new JComboBox<>(rooms.toArray(new Card[0]));
			add(roomBox);
		} else {
			// Suggestion: show current room as fixed text
			if (currentRoom == null) {
				add(ClueGUIUtil.createDefaultLabel("ERROR: no room"));
			} else {
				add(ClueGUIUtil.createDefaultLabel(currentRoom.getCardName()));
			}
		}

		// ROW 2: person
		JLabel personLabel = ClueGUIUtil.createDefaultLabel("Person");
		add(personLabel);
		personBox = new JComboBox<>(people.toArray(new Card[0]));
		add(personBox);

		// ROW 3: weapon
		JLabel weaponLabel = ClueGUIUtil.createDefaultLabel("Weapon");
		add(weaponLabel);
		weaponBox = new JComboBox<>(weapons.toArray(new Card[0]));
		add(weaponBox);

		// ROW 4: buttons (Submit/Cancel"
		JButton submitButton = new JButton("Submit");


		JButton cancelButton = new JButton("Cancel");


		submitButton.addActionListener(e -> {
			submitted = true;
			setVisible(false);
		});

		cancelButton.addActionListener(e -> {
			submitted = false;
			setVisible(false);
		});

		add(submitButton);
		add(cancelButton);
	}

	public boolean wasSubmitted() {
		return submitted;
	}

	public Solution getSolution() {
		if (!submitted) return null;

		Card roomCard;
		if (accusation) {
			roomCard = (Card) roomBox.getSelectedItem();
		} else {
			roomCard = currentRoom;
		}

		Card personCard = (Card) personBox.getSelectedItem();
		Card weaponCard = (Card) weaponBox.getSelectedItem();

		return new Solution(personCard, roomCard, weaponCard);
	}


	public static void main(String[] args) {
		Card r1 = new Card("Kitchen", CardType.ROOM);
		Card r2 = new Card("Library", CardType.ROOM);
		Card p1 = new Card("Miss Scarlet", CardType.PERSON);
		Card p2 = new Card("Col. Mustard", CardType.PERSON);
		Card w1 = new Card("Knife", CardType.WEAPON);
		Card w2 = new Card("Rope", CardType.WEAPON);

		java.util.List<Card> rooms   = java.util.Arrays.asList(r1, r2);
		java.util.List<Card> people  = java.util.Arrays.asList(p1, p2);
		java.util.List<Card> weapons = java.util.Arrays.asList(w1, w2);

		JFrame frame = new JFrame("GuessDialog Harness");

		GuessDialog suggestionDialog = new GuessDialog(frame, r1, rooms, people, weapons, false);
		suggestionDialog.setLocationRelativeTo(frame);
		suggestionDialog.setVisible(true);

		System.out.println("Suggestion submitted? " + suggestionDialog.wasSubmitted());
		if (suggestionDialog.wasSubmitted()) {
			System.out.println("Suggestion = " + suggestionDialog.getSolution());
		}

		GuessDialog accusationDialog = new GuessDialog(frame, null, rooms, people, weapons, true);
		accusationDialog.setLocationRelativeTo(frame);
		accusationDialog.setVisible(true);

		System.out.println("Accusation submitted? " + accusationDialog.wasSubmitted());
		if (accusationDialog.wasSubmitted()) {
			System.out.println("Accusation = " + accusationDialog.getSolution());
		}

	}

}

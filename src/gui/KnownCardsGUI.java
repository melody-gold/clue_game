package gui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class KnownCardsGUI extends JPanel {
	private static final long serialVersionUID = 2L;

	private static final int WIDTH = 225;
	private static final int HEIGHT = 600;

	private CardInfoPanel peoplePanel;
	private CardInfoPanel roomPanel;
	private CardInfoPanel weaponPanel;

	public KnownCardsGUI(Set<Card> hand, Set<Card> seenCards) {
		super();

		// we will use a box layout, this makes it easy to arrange elements in a single row
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.peoplePanel = new CardInfoPanel("People", CardType.PERSON, hand, seenCards);
		this.roomPanel = new CardInfoPanel("Rooms", CardType.ROOM, hand, seenCards);
		this.weaponPanel = new CardInfoPanel("Weapons", CardType.WEAPON, hand, seenCards);

		this.add(peoplePanel);
		this.add(roomPanel);
		this.add(weaponPanel);

		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(this.getSize());

		this.setName("Known Cards");

		// create custom border
		TitledBorder guiBorder = ClueGUIUtil.createDefaultTitledBorder(this.getName());
		guiBorder.setTitleJustification(TitledBorder.CENTER);

		this.setBorder(guiBorder);
		this.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);
	}

	// initializes an empty gui
	public KnownCardsGUI() {
		this(new HashSet<Card>(), new HashSet<Card>());
	}

	private static Set<Card> getHumanHand() {
		Board board = Board.getInstance();
		Player human = null;
		for (Player p : board.getPlayers()) {
			if (p instanceof HumanPlayer) {
				human = p;
				break;
			}
		}
		if (human == null) {
			return Collections.emptySet();
		}
		Set<Card> hand = human.getHand();
		return (hand != null) ? hand : Collections.emptySet();
	}


	private static Set<Card> getHumanSeen() {
		Board board = Board.getInstance();
		Player human = null;
		for (Player p : board.getPlayers()) {
			if (p instanceof HumanPlayer) {
				human = p;
				break;
			}
		}
		if (human == null) {
			return Collections.emptySet();
		}
		Set<Card> seen = human.getSeenCards();
		return (seen != null) ? seen : Collections.emptySet();
	}


	public void updatePanels() {
		this.peoplePanel.drawPanel();
		this.weaponPanel.drawPanel();
		this.roomPanel.drawPanel();
		this.revalidate();
	}

	public static void main(String []args) {
		JFrame frame = new JFrame();

		Set<Card> hand = new HashSet<>();
		hand.add(new Card("hand person", CardType.PERSON));
		hand.add(new Card("hand weapon", CardType.WEAPON));
		// hand.add(new Card("hand room", CardType.ROOM));

		Set<Card> seen = new HashSet<>(hand);
		seen.add(new Card("seen person1", CardType.PERSON));
		Card testCard = new Card("seen person2", CardType.PERSON);
		testCard.addToHand(new ComputerPlayer("test", Color.red, 0,0));
		seen.add(testCard);
		testCard = new Card("seen room", CardType.ROOM);
		testCard.addToHand(new ComputerPlayer("test", Color.blue, 0,0));
		seen.add(testCard);
		// seen.add(new Card("seen weapon", CardType.WEAPON));
		KnownCardsGUI panel = new KnownCardsGUI(hand, seen);
		// panel.setMaximumSize(new Dimension(WIDTH, HEIGHT));

		// add new cards
		testCard = new Card("seen room # 2", CardType.ROOM);
		testCard.addToHand(new ComputerPlayer("test", Color.orange, 0,0));
		seen.add(testCard);
		hand.add(new Card("hand room", CardType.ROOM));
		panel.updatePanels();

		frame.add(panel);
		frame.pack(); 

		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}



	private class CardInfoPanel extends JPanel{
		private static final long serialVersionUID = 3L;

		private static final String HAND_TEXT = "In Hand:";
		private static final String SEEN_TEXT = "Seen:";
		private static final int STRUT_HEIGHT = 1;
		private static final Dimension PREFERRED_DIM = new Dimension(KnownCardsGUI.WIDTH, KnownCardsGUI.HEIGHT/3 - 10);
		private static final Dimension MAX_DIM = new Dimension(KnownCardsGUI.WIDTH, KnownCardsGUI.HEIGHT/3 + 20);
		private static final Dimension TEXT_FIELD_MIN_DIM = new Dimension(10, 20);	
		private static final Dimension LABEL_DIM = new Dimension(60, 16);

		private Set<Card> hand;
		private Set<Card> seenCards;
		private CardType panelType;
		private JLabel inHand;
		private JLabel seen;


		CardInfoPanel(String display, CardType panelType, Set<Card> hand, Set<Card> seenCards){
			super();

			// ensure the original sets are not modified by CardInfoPanel, but the
			// set is updated whenever the original is
			this.hand = Collections.unmodifiableSet(hand);
			this.seenCards = Collections.unmodifiableSet(seenCards);
			this.panelType = panelType;

			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setName(display);

			// create labels
			this.inHand = createLabel(HAND_TEXT);
			this.seen = createLabel(SEEN_TEXT);

			this.drawPanel();

			// configure the panel
			this.setMaximumSize(MAX_DIM);
			this.setPreferredSize(PREFERRED_DIM);
			this.setBorder(ClueGUIUtil.createDefaultTitledBorder(this.getName()));
			this.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);
		}


		public void drawPanel() {
			// remove previous elements
			this.removeAll();

			// create text fields for hand data
			Set<JTextField> handFields = new HashSet<>();
			for (Card card : hand) {
				if (card.getType() == panelType) {
					JTextField field = createTextField(card.getCardName());
					// hand cards should never have a non-default color
					handFields.add(field);
				}
			}

			// create text fields for seen data
			Set<JTextField> seenFields = new HashSet<>();
			for (Card card : seenCards) {
				// skip cards in the hand
				if (this.hand.contains(card)) {
					continue;
				}

				if (card.getType() == panelType) {
					JTextField field = createTextField(card.getCardName());
					// retrieve color data and set textField to match
					if (card.getPlayerHoldingThis() != null) {
						Color playerColor = card.getPlayerHoldingThis().getColor();
						field.setBackground(ClueGUIUtil.darken(playerColor, 0.65));
					}
					seenFields.add(field);
				}
			}

			// add elements
			this.buildSection(inHand, handFields);
			this.buildSection(seen, seenFields);

		}

		private void buildSection(JLabel sectionLabel, Set<JTextField> fields) {
			// add section label
			this.add(Box.createVerticalStrut(STRUT_HEIGHT));
			this.add(sectionLabel);
			this.add(Box.createVerticalStrut(STRUT_HEIGHT));
			// add corresponding fields
			if (fields.isEmpty()) {
				// if fields is empty add a "None Field" 
				this.add(createTextField("None"));
			} else {
				for (JTextField field : fields) {
					this.add(field);
				}
			}
		}

		private static JLabel createLabel(String text) {
			JLabel newLabel = ClueGUIUtil.createDefaultLabel(text);
			newLabel.setPreferredSize(LABEL_DIM);
			newLabel.setMaximumSize(LABEL_DIM);
			return newLabel;
		}

		private static JTextField createTextField(String text) {
			JTextField newField = ClueGUIUtil.createDefaultTextField(text);
			newField.setMinimumSize(TEXT_FIELD_MIN_DIM);
			return newField;
		}
	}

}

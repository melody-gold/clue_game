package gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import clueGame.Board;
import clueGame.Card;
import clueGame.HumanPlayer;
import clueGame.Player;

public class ClueGame extends JFrame {
	//tests
	public static final boolean TEST_SET_ALL_PLAYER_COMPUTER = false;
	public static final boolean TEST_COMPUTER_SEEN_ALL = false;
	public static final boolean TEST_SUGGESTION = false;
	public static final boolean TEST_COMPUTER_SOLUTION_METHOD = false;

	private static final long serialVersionUID = 1L;

	private Board board;
	private BoardPanel boardPanel;
	private GameControlPanel controlPanel;
	private KnownCardsGUI knownCardsPanel;


	/**
	 * ClueBoard Class
	 *
	 * @author Melody Goldanloo
	 * @author Jacob Dionne
	 *
	 * Entry point of the Clue Game and puts board pieces together in the game window.
	 */
	public ClueGame() {
		// window
		setTitle("Clue Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// initialize board
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
		board.deal();

		HumanPlayer human = board.getHumanPlayer();

		// create panels
		controlPanel = new GameControlPanel(board);
		controlPanel.setGuess("Make a Move!");
		boardPanel = new BoardPanel(board, controlPanel);
		knownCardsPanel = new KnownCardsGUI(human.getHand(), human.getSeenCards());
		human.setInfoPanel(knownCardsPanel);

		// add panels to frame
		add(boardPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
		add(knownCardsPanel, BorderLayout.EAST);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		// show starting message
		String player = human.getName();
		String message = "You are " + player + 
				".\nFind the murderer before" +
				"\nsomeone else does!";
		String title = "Welcome to Clue";
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		
		runTests();

		// start the first turn
		controlPanel.setTurn(board.getCurrentPlayer(), board.getRoll());
		board.doFirstMove();
		board.startTurn();

	}


	public static void main(String[] args) {
		// entry point for game
		SwingUtilities.invokeLater(() -> new ClueGame());

	}

	private void runTests() {
		ArrayList<String> tests = new ArrayList<>();
		tests.add("The following Tests are active: ");
		
		
		if(TEST_SET_ALL_PLAYER_COMPUTER) {
			tests.add("All players are computers");
		}
		
		if (TEST_COMPUTER_SEEN_ALL) {
			tests.add("Computer Player Seen All Cards");
			
			Set<Card> solutionCards = board.getSolution().toSet();
			//get non-computer player
			Player accusingPlayer = board.getPlayers().get(2);
			//show the player all non-solution cards
			for(Card card : board.getDeck()) {
				if(solutionCards.contains(card) == false) {
					accusingPlayer.updateSeen(card);
				}
			}
		}
		
		if(TEST_SUGGESTION) {
			tests.add("Computer Player makes a suggestion");
			System.out.println("List of Players: ");
			for( Player player : board.getPlayers()) {
				System.out.println("\t" + player.getName());
			}
		}
		
		if(TEST_COMPUTER_SOLUTION_METHOD) {
			tests.add("Show the method the computer player found the solution with");
		}
		
		
		
		if(tests.size() > 1) {
			String message = tests.getFirst();
			
			for(int i = 1; i < tests.size(); ++i) {
				message += "\n- " + tests.get(i);
			}
			
			JOptionPane.showMessageDialog(this, message, "Test Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}

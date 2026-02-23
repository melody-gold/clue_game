package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import clueGame.Board;
import clueGame.ComputerPlayer;
import clueGame.Player;
import clueGame.HumanPlayer;


/**
 * Control Panel Class
 *
 * @author Melody Goldanloo
 * @author Jacob Dionne
 *
 * Extends JPanel to draw the Clue Board Control Panel.
 */
public class GameControlPanel extends JPanel {
	private static final long serialVersionUID = 4L;

	private JTextField theGuess;
	private JTextField guessResult;
	private JTextField playerTurn;
	private JTextField numRolled;

	private AccuseButtonListener accussationListener;
	private NextButtonListener nextListener;
	
	private Board board;


	public GameControlPanel(Board board) {
		this.board = board;
		if(board != null) {
			board.setGamePanel(this);
		}
		
		this.accussationListener = new AccuseButtonListener();
		this.nextListener = new NextButtonListener();

		// Create a layout with 2 rows
		setLayout(new GridLayout(2, 1, 8, 8));
		setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);

		// TOP
		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);

		JPanel topRow = new JPanel(new GridLayout(1, 3, 8, 4));
		topRow.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);

		// TOP LEFT
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);

		left.add(Box.createVerticalStrut(4));

		JLabel turnLabel = ClueGUIUtil.createDefaultLabel("Whose turn?");
		turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		left.add(turnLabel);

		left.add(Box.createVerticalStrut(8));

		playerTurn = ClueGUIUtil.createDefaultTextField();
		Dimension playerSize = new Dimension(400, 40);
		playerTurn.setMinimumSize(playerSize);
		playerTurn.setMaximumSize(playerSize);
		left.add(Box.createHorizontalGlue());
		left.add(playerTurn);

		// TOP MIDDLE
		JPanel middle = new JPanel();
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		middle.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);

		middle.add(Box.createVerticalStrut(4));

		JLabel rollLabel = ClueGUIUtil.createDefaultLabel("Roll:");
		rollLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		middle.add(rollLabel);

		middle.add(Box.createVerticalStrut(8));


		numRolled = ClueGUIUtil.createDefaultTextField();
		numRolled.setHorizontalAlignment(JTextField.CENTER);
		Dimension rollTBoxSize = new Dimension(50, 40);
		numRolled.setMinimumSize(rollTBoxSize);
		numRolled.setMaximumSize(rollTBoxSize);
		middle.add(numRolled);

		// TOP RIGHT
		JPanel buttons = new JPanel(new GridLayout(2, 1, 6, 2));
		buttons.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);
		JButton accuseButton = new JButton("Make Accusation");
		accuseButton.addActionListener(accussationListener);
		JButton nextButton   = new JButton("NEXT!");
		nextButton.addActionListener(nextListener);
		buttons.add(accuseButton);
		buttons.add(nextButton);

		topRow.add(left);
		topRow.add(middle);
		topRow.add(buttons);

		top.add(topRow, BorderLayout.CENTER);
		add(top);

		// BOTTOM
		JPanel bottom = new JPanel(new GridLayout(1, 2, 8, 0));
		bottom.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);

		JPanel guessPanel = new JPanel(new BorderLayout());
		guessPanel.setBorder(ClueGUIUtil.createDefaultTitledBorder("Guess"));
		guessPanel.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);
		theGuess = ClueGUIUtil.createDefaultTextField();
		guessPanel.add(theGuess, BorderLayout.CENTER);

		JPanel resultPanel = new JPanel(new BorderLayout());
		resultPanel.setBorder(ClueGUIUtil.createDefaultTitledBorder("Guess Result"));
		resultPanel.setBackground(ClueGUIUtil.DEFAULT_BACKGROUND_COLOR);
		guessResult = ClueGUIUtil.createDefaultTextField();
		resultPanel.add(guessResult, BorderLayout.CENTER);

		bottom.add(guessPanel);
		bottom.add(resultPanel);
		add(bottom);

	}

	public GameControlPanel() {
		this(null);
	}

	private class AccuseButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(board == null) {
				System.out.println("Accuse Button Clicked");
			} else {
				Player player = board.getCurrentPlayer();
				if (player instanceof HumanPlayer) {
					HumanPlayer hPlayer = (HumanPlayer) player;
					hPlayer.startAccusing();
					hPlayer.makeAccusation();
				} else {
					JOptionPane.showMessageDialog(null, "ERROR: cannot accuse on someone else's turn", 
							"Accuse Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

	}

	private class NextButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(board == null) {
				System.out.println("Next Button Clicked");
			} else {
				int turnsPerClick = 1;
				if(ClueGame.TEST_COMPUTER_SOLUTION_METHOD) {
					turnsPerClick = 50;
				}
				
				for(int i = 0; i < turnsPerClick; i++) {
					//board.nextPlayer() increments players when it is appropriate and returns false when it isn't
					if (board.nextPlayer()) {
						Player player = board.getCurrentPlayer();

						setTurn(player, board.getRoll());

						board.startTurn();
						
					} else {
						JOptionPane.showMessageDialog(null, "ERROR: cannot proceed at this time", 
								"Next Turn Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	public void setTurn(Player player, int numRolled) {
		playerTurn.setText(player.getName());
		playerTurn.setBackground(ClueGUIUtil.darken(player.getColor(), 0.65));
		this.numRolled.setText(Integer.toString(numRolled));
	}

	public void setGuess(String guess) {
		theGuess.setText(guess);
	}

	public void setGuessResult(String result) {
		guessResult.setText(result);
	}


	/**
	 * Main to test the panel
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameControlPanel panel = new GameControlPanel();  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(1000, 200);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible

		// test filling in the data
		panel.setTurn(new ComputerPlayer( "Col. Mustard", Color.orange, 0, 0), 5);
		panel.setGuess( "I have no guess!");
		panel.setGuessResult( "So you have nothing?");
	}

}

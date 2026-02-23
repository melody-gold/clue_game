package gui;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.Room;
import clueGame.Solution;
import clueGame.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * Board Panel Class
 *
 * @author Melody Goldanloo
 * @author Jacob Dionne
 *
 * Extends JPanel to draw the Clue Board.
 */
public class BoardPanel extends JPanel {
	private static final long serialVersionUID = 5L;

	private Board board;
	private GameControlPanel gameControlPanel;
	
	private BoardCell selected;
	private boolean doListening;
	private int cellHeight;
	private int cellWidth;
	private static String IMAGE_PATH = "data/clue_board.png";

	private BufferedImage boardBackground;

	public BoardPanel(Board board, GameControlPanel gameControlPanel) {
		this.board = board;
		board.setPanel(this);
		
		this.gameControlPanel = gameControlPanel;
		
		this.doListening = false;
		this.addMouseListener(new ClickListener());

		try {
			boardBackground = ImageIO.read(new File(IMAGE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			boardBackground = null;
		}
	}




	public static void main(String[] args) {
		Board board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();

//		int width = 1000 - 225; // width of GameControlPanel - width of KnownCardsGUI
//		int height = 600; // width of KnownCardsGUI

		BoardPanel testPanel = new BoardPanel(board, null);
		JFrame frame = new JFrame();
		frame.setContentPane(testPanel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * 
	 */
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		// get number of rows and columns in board
		int numRows = board.getNumRows();
		int numCols = board.getNumColumns();

		int boardPixelW = cellWidth  * numCols;
		int boardPixelH = cellHeight * numRows;

		if (boardBackground != null) {
			g.drawImage(boardBackground, 0, 0, boardPixelW, boardPixelH, this);
		}

		boolean hasBackground = (boardBackground != null);

		// draw every cell
		Set<BoardCell> doorways = board.getDoorways();
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				BoardCell cell = board.getCell(r, c);
				if (hasBackground && cell.isRoomCell()) {
					continue;
				}
				cell.draw(g, cellWidth, cellHeight);
			}
		}

		// draw doorway cells last so their doors aren't overridden by other cells
		for (BoardCell cell : doorways) {
			cell.draw(g, cellWidth, cellHeight);
			cell.drawDoor(g, cellWidth, cellHeight);
		}

		// draw room labels
		Collection<Room> rooms = board.getRooms();
		for (Room room : rooms) {
			room.draw(g, cellWidth, cellHeight);
		}

		// draw players
		Collection<Player> players = board.getPlayers();
		Map<String, java.util.List<Player>> byCell = new HashMap<>();

		for (Player p : players) {
			String key = p.getRow() + "," + p.getCol();
			byCell.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
		}
		// draw stacked players
		for (List<Player> stack : byCell.values()) {
			int n = stack.size();
			if (n == 1) {
				stack.get(0).draw(g, cellWidth, cellHeight);
				continue;
			}
			// compute token size for consistent offset
			int pawnDiameter = Math.min(cellWidth, cellHeight) - (Math.min(cellWidth, cellHeight) / 10) * 2;
			int offsetStep = pawnDiameter / 3;

			for (int i = 0; i < n; i++) {
				int offset = i * offsetStep;
				Player p = stack.get(i);
				// pushes each player down-right diagonally
				p.draw(g, cellWidth, cellHeight, offset, offset);
			}
		}

		// if listening flag is set, highlight all target cells
		if (doListening) {
			for (BoardCell cell : board.getTargets()) {
				cell.highlight(g, cellWidth, cellHeight);
			}
		}
	}

	public void handleHumanPlayerTurn(boolean newVal) {
		// highlight player targets and wait for selection
		this.doListening = newVal;
		updateUI();
	}

	public boolean isListening() {
		return this.doListening;
	}

	public BoardCell getSelected() {
		return this.selected;
	}

	private class ClickListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!doListening) {
				return;
			}

			BoardCell clickedOn = getCellOn(e.getX(), e.getY());
			Player player = board.getCurrentPlayer();
			
			//don't move a player if they are accusing
			if (board.getTargets().contains(clickedOn) && player.willAccuse() == false) {
				
				selected = clickedOn;

				// move the player and end the move selection phase
				doListening = false; 
				board.movePlayerTo(player, clickedOn);
				
				
				if( clickedOn.isRoomCenter() ) {
					board.doPlayerDecision(player);
				}
				
				updateUI();
			} else {
				/* DEBUG
				System.out.println("fail: " + clickedOn.getRow()+ ", " + clickedOn.getColumn()
						+ "\nClicked: " + e.getX() + ", " + e.getY()
						+ "\nHeight: " + cellHeight + " Width: " + cellWidth);
				 */

				// display error
				JOptionPane.showMessageDialog(null, "ERROR: select a valid target", 
						"Selection Error", JOptionPane.ERROR_MESSAGE);
			}
		} 

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	private BoardCell getCellOn(int x, int y) {
		calculateCellSize();
		return board.getCell(y / cellHeight, x / cellWidth);
	}

	private void calculateCellSize() {
		cellHeight = this.getHeight() / board.getNumRows();
		cellWidth = this.getWidth() / board.getNumColumns();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		calculateCellSize();
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		calculateCellSize();
	}

	@Override
	public Dimension getPreferredSize() {
		if (boardBackground != null) {
			return new Dimension((int) (boardBackground.getWidth() * 1.2), (int) (boardBackground.getHeight() * 1.2));
		} else {
			int cellSize = 24; 
			int width  = board.getNumColumns() * cellSize;
			int height = board.getNumRows() * cellSize;
			return new Dimension(width, height);
		}
	}

}
package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import gui.ClueGUIUtil;


/**
 * BoardCell Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 * Represents one Cell in the Board, can have different properties and each cell interacts with the players
 *
 */
public class BoardCell {
	private static int DOORWAY_THICKNESS_FACTOR = 4;

	private int row;
	private int col;
	private char initial; // initial can never be null therefore the primitive was used
	private DoorDirection doorDirection;
	private boolean roomLabel;
	private boolean roomCenter;
	private boolean isOccupied;
	private Character secretPassage;
	private Set<BoardCell> adjList;

	public BoardCell(int row, int col, char initial) {
		super();

		this.row = row;
		this.col = col;
		this.initial = initial;
		this.roomLabel = false;
		this.roomCenter = false;
		this.isOccupied = false;
		this.doorDirection = DoorDirection.NONE;
		this.secretPassage = null;
		this.adjList = new HashSet<>();
	}

	public boolean isDoorway() {
		return doorDirection != DoorDirection.NONE;
	}

	public char getRoomInitial() {
		return initial;
	}

	public boolean isLabel() {
		return roomLabel;
	}

	public void setLabel() {
		this.roomLabel = true;
	}

	public boolean isRoomCell() {
		return initial != 'W' && initial != 'X';
	}

	public boolean isRoomCenter() {
		return roomCenter;
	}

	public void setRoomCenter() {
		this.roomCenter = true;
	}

	public void setOccupied(boolean occupied) {
		this.isOccupied = occupied;
	}

	public boolean getOccupied() {
		return isOccupied;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	public void setDoorDirection(DoorDirection newVal) {
		this.doorDirection = newVal;
	}

	public Character getSecretPassage() {
		return secretPassage;
	}

	public void setSecretPassage(char newVal) {
		this.secretPassage = newVal;
	}

	public void addAdj(BoardCell adj) {
		// add adjacent cell to adjacency list
		adjList.add(adj);
	}

	public Set<BoardCell> getAdjList() {
		return adjList;
	}

	public int getColumn() {
		return col;
	}

	public int getRow() {
		return row;
	}

	// toString for debugging
	@Override
	public String toString() {
		return "BoardCell " + debugPos();
	}

	public String debugPos() {
		return "(" + row + "," + col + ",'" + getRoomInitial() + "')";
	}

	public String debugPos2() {
		return row + "," + col;
	} 

	public String debugAdjSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("Adj of ").append(debugPos()).append(" = { ");

		for (BoardCell adj : getAdjList()) {
			sb.append(adj.debugPos()).append(" ");
		}

		sb.append("}");
		return sb.toString();
	}

	// ***** GUI METHODS *****
	/**
	 * 
	 * @param g
	 * @param width
	 * @param height
	 */
	public void draw(Graphics g, int width, int height) {
		int x = col * width;
		int y = row * height;

		//compute colors
		Color fillColor;
		Color borderColor;
		switch(initial){
		case 'W': // walkway
			// draw border
			fillColor = new Color(112, 81, 64);
			borderColor = Color.BLACK;
			break;
		case 'X': // unused
			fillColor = Color.BLACK;
			borderColor = fillColor;
			break;
		default: // room
			fillColor = Color.LIGHT_GRAY;
			borderColor = fillColor;
			break;
		}

		g.setColor(fillColor);
		g.fillRect(x, y, width, height);
		g.setColor(borderColor);
		g.drawRect(x, y, width, height);
	}

	public void highlight(Graphics g, int width, int height) {
		int x = col * width;
		int y = row * height;

		if (initial == 'W') {
			// base walkway color from your draw() method
			Color walkway = new Color(112, 81, 64);

			// make it darker – you can tweak this factor
			Color darkerWalkway = ClueGUIUtil.darken(walkway, 0.6);

			g.setColor(darkerWalkway);
			g.fillRect(x, y, width, height);

			// keep same border as normal walkways
			g.setColor(Color.BLACK);
			g.drawRect(x, y, width, height);
			return;
		}

		// room highlight
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

		Color overlay = new Color(100, 0, 0, 160); // dark red overlay
		g2.setColor(overlay);
		g2.fillRect(x, y, width, height);

		// room highlight border
		g2.setColor(Color.WHITE);
		g2.drawRect(x, y, width, height);

		return;
	}

	public void drawDoor(Graphics g, int width, int height){
		int cellX = col * width;
		int cellY = row * height;
		g.setColor(new Color (56, 71, 133));
		int thickness;

		switch(doorDirection) {
		case UP:
			thickness = Math.max(2, height / DOORWAY_THICKNESS_FACTOR);
			g.fillRect(cellX, cellY - thickness, width - 1, thickness);
			break;
		case DOWN:
			thickness = Math.max(3, height / DOORWAY_THICKNESS_FACTOR);
			g.fillRect(cellX, cellY + height, width - 1, thickness);
			break;
		case RIGHT:
			thickness = Math.max(2, width / DOORWAY_THICKNESS_FACTOR);
			g.fillRect(cellX + width, cellY, thickness, height - 1);
			break;
		case LEFT:
			thickness = Math.max(2, width / DOORWAY_THICKNESS_FACTOR);
			g.fillRect(cellX - thickness, cellY, thickness , height - 1);
			break;
		default:
			return;
		}
	}

}

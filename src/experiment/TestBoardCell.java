package experiment;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

/**
 * TestBoardCell Class
 * 
 * @author Jacob Dionne
 * @author Melody Goldanloo
 * 
 * A testing prototype of the BoardCell class, verifies that the testing suite is functional
 * 
 */
public class TestBoardCell {
	private int row;
	private int col;
	private boolean isInRoom;
	private boolean isOccupied;
	private Set<TestBoardCell> adjList;
	
	
	public TestBoardCell(int row, int column) {
		this.row = row;
		this.col = column;
		this.isInRoom = false;
		this.isOccupied = false;
		//decided to use Hashsets as comparisons aren't done on the entries
		this.adjList = new HashSet<TestBoardCell>();
	}
	
	public void addAdjacency(TestBoardCell cell) {
		// Add cell to adjacency list
		adjList.add(cell);
	}
	
	// return adjacency list
	public Set<TestBoardCell> getAdjList() {
		return adjList;
	}
	
	// ***** isInRoom Getters/Setters *****
	public void setRoom(boolean room) {
		// Cell has a room indicator
		this.isInRoom = room;
	}
	
	public boolean isRoom() {
		// returns T/F if cell is in a room
		return isInRoom;
	}
	
	// ***** isOccupied Getters/Setters *****
	public void setOccupied(boolean occupied) {
		// Cell occupied by other play indicator
		this.isOccupied = occupied;
	}
	
	public boolean getOccupied() {
		// returns T/F if cell is occupied 
		return isOccupied;
	}
	
	// ***** Row and Column getters *****
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { // if same object
			return true;
		}
		if (!(obj instanceof TestBoardCell)) { // check if same type
			return false;
		}
		TestBoardCell other = (TestBoardCell) obj;
		// two cells are equal if they occupy the same (row, col) position
		boolean sameRow = this.row == other.row;
		boolean sameCol = this.col == other.col;
		return sameRow && sameCol;
	}
	
	@Override
	public int hashCode() {
		// generate hash code using a cell's row and column
		return Objects.hash(row, col);
	}

}

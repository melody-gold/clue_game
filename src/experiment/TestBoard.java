package experiment;

import java.util.*;

/**
 * TestBoard Class
 * 
 * @author Jacob Dionne
 * @author Melody Goldanloo
 * 
 * A testing prototype of the Board class, verifies that the testing suite is functional
 * 
 */
public class TestBoard {
	
	public static final int ROWS = 4;
	public static final int COLS = 4;
	
	private TestBoardCell[][] grid;
	private Set<TestBoardCell> targets;
	private Set<TestBoardCell> visited;

	public TestBoard() {
		grid = new TestBoardCell[ROWS][COLS]; // initialize cells
		
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				grid[r][c] = new TestBoardCell(r, c);
			}
		}
		
		// create adjacency list for each cell
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				TestBoardCell cell = grid[r][c];
				if (r > 0) { 			// cell above
					cell.addAdjacency(grid[r - 1][c]);
				}
				if (c > 0) { 			// cell left
					cell.addAdjacency(grid[r][c - 1]);
				}
				if (r < ROWS - 1) { 	// cell below
					cell.addAdjacency(grid[r + 1][c]);
				}
				if (c < COLS - 1) { 	// cell right
					cell.addAdjacency(grid[r][c + 1]);
				}
			}
		}
	}
	
	public void calcTargets(TestBoardCell startCell, int pathLength) {
		// calculates reachable targets for a move from startCell of length pathLength
		targets = new HashSet<>(); //decided to use Hashsets as comparisons aren't done on the entries
		visited = new HashSet<>();
		visited.add(startCell);
		// compute targets via BFS
		searchTargets(startCell, pathLength);
	}
	
	private void searchTargets(TestBoardCell currentCell, int remainingSteps) {
		// DFS to explore possible paths from currentCell using remainingSteps
		for (TestBoardCell adjacent : currentCell.getAdjList()) {
			if (visited.contains(adjacent) || adjacent.getOccupied()) {
				// skip invalid moves
				continue;
			}
			visited.add(adjacent); // mark cell visited
			
			// check if the adjacent cell should be a target
			if (adjacent.isRoom()) { 
				targets.add(adjacent);
			} else if (remainingSteps == 1) { // last step
				targets.add(adjacent);
			} else { 	// if more steps left, recursively call
				searchTargets(adjacent, remainingSteps - 1);
			}
			
			visited.remove(adjacent);
		}
		
	}
	
	// ***** Getters *****
	public TestBoardCell getCell(int row, int col) {
		return grid[row][col];
	}
	
	public Set<TestBoardCell> getTargets() {
		return targets;
	}

}

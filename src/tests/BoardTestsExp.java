package tests;

import static org.junit.jupiter.api.Assertions.*;

import experiment.TestBoard;
import experiment.TestBoardCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * BoardTestsExp Class
 * 
 * @author Jacob Dionne
 * @author Melody Goldanloo
 * 
 * JUnit testing class designed to test the accuracy and functionality of various board methods and properties
 * 
 */
public class BoardTestsExp {
	private TestBoard board;
	
	@BeforeEach
	public void setUp() {
		board = new TestBoard();
	}
	
	// Methods to test adjacency list for 4x4 board
	@Test
	public void testTopLeftAdjacency() {
		// test top left corner (0,0)
		TestBoardCell cell = board.getCell(0, 0);
		var adj = cell.getAdjList();
		assertEquals(2, adj.size());
		assertTrue(adj.contains(board.getCell(0, 1)));
		assertTrue(adj.contains(board.getCell(1, 0)));
	}
	
	@Test
	public void testBottomRightAdjacency() {
		// test bottom right corner (3,3)
		TestBoardCell cell = board.getCell(3, 3);
		var adj = cell.getAdjList();
		assertEquals(2, adj.size());
		assertTrue(adj.contains(board.getCell(3, 2)));
		assertTrue(adj.contains(board.getCell(2, 3)));
	}
	
	@Test
	public void testRightEdgeAcjacency() {
		// test right edge (1,3)
		TestBoardCell cell = board.getCell(1, 3);
		var adj = cell.getAdjList();
		assertEquals(3, adj.size());
		assertTrue(adj.contains(board.getCell(1, 2)));
		assertTrue(adj.contains(board.getCell(0, 3)));
		assertTrue(adj.contains(board.getCell(2, 3)));
	}
	
	@Test
	public void testLeftEdgeAdjacency() {
		// test a left edge (2,0)
		TestBoardCell cell = board.getCell(2, 0);
		var adj = cell.getAdjList();
		assertEquals(3, adj.size());
		assertTrue(adj.contains(board.getCell(1, 0)));
		assertTrue(adj.contains(board.getCell(3, 0)));
		assertTrue(adj.contains(board.getCell(2, 1)));
	}
	
	@Test
	  public void testMiddleAdjacency() {
		// Tests a non-edge cell adjacencies
	      TestBoardCell cell = board.getCell(1,1);
	      Set<TestBoardCell> list = cell.getAdjList();
	      assertEquals(4, list.size());
	      assertTrue(list.contains(board.getCell(0, 1)));
	      assertTrue(list.contains(board.getCell(2, 1)));
	      assertTrue(list.contains(board.getCell(1, 0)));
	      assertTrue(list.contains(board.getCell(1, 2)));
	  }

	// Methods to test target creation
	// calcTarget Tests
	@Test
	public void testTargetsLen1FromCenter() {
	    TestBoardCell start = board.getCell(1, 1);
	    board.calcTargets(start, 1);
	    Set<TestBoardCell> t = board.getTargets();

	    assertEquals(4, t.size());
	    assertTrue(t.contains(board.getCell(0, 1)));
	    assertTrue(t.contains(board.getCell(2, 1)));
	    assertTrue(t.contains(board.getCell(1, 0)));
	    assertTrue(t.contains(board.getCell(1, 2)));
	    assertFalse(t.contains(start));
	}

	@Test
	public void testTargetsLen2FromCorner() {
	    TestBoardCell start = board.getCell(0, 0);
	    board.calcTargets(start, 2);
	    Set<TestBoardCell> t = board.getTargets();

	    // reachable: (2,0), (0,2), (1,1)
	    assertEquals(3, t.size());
	    assertTrue(t.contains(board.getCell(2, 0)));
	    assertTrue(t.contains(board.getCell(0, 2)));
	    assertTrue(t.contains(board.getCell(1, 1)));
	    assertFalse(t.contains(start));
	}

	@Test
	public void testTargetsLen3FromCorner() {
	    TestBoardCell start = board.getCell(0, 0);
	    board.calcTargets(start, 3);
	    Set<TestBoardCell> t = board.getTargets();

	    // reachable in 3: (3,0), (2,1), (1,2), (0,3), plus (1,0), (0,1) via detours
	    assertEquals(6, t.size());
	    assertTrue(t.contains(board.getCell(3, 0)));
	    assertTrue(t.contains(board.getCell(2, 1)));
	    assertTrue(t.contains(board.getCell(1, 2)));
	    assertTrue(t.contains(board.getCell(0, 3)));
	    assertTrue(t.contains(board.getCell(1, 0)));
	    assertTrue(t.contains(board.getCell(0, 1)));
	}

	@Test
	public void testTargetsLen4FromEdge() {
	    TestBoardCell start = board.getCell(0, 1);
	    board.calcTargets(start, 4);
	    Set<TestBoardCell> t = board.getTargets();

	    // not pinning exact size, just check some known cells
	    assertTrue(t.contains(board.getCell(0, 3)));
	    assertTrue(t.contains(board.getCell(2, 1)));
	    assertTrue(t.contains(board.getCell(3, 0)));
	    assertTrue(t.size() >= 5);
	}

	@Test
	public void testTargetsLen6MaxRoll() {
		TestBoardCell start = board.getCell(1, 1);
		board.calcTargets(start, 6);
		Set<TestBoardCell> t = board.getTargets();

		assertTrue(t.contains(board.getCell(0, 0)));
		assertTrue(t.contains(board.getCell(0, 2)));
		assertTrue(t.contains(board.getCell(2, 0)));
		assertTrue(t.contains(board.getCell(2, 2)));
		assertTrue(t.contains(board.getCell(3, 1)));
		assertTrue(t.contains(board.getCell(1, 3)));
		assertEquals(7, t.size());
	}
	
	// TODO: Test occupied cell
	
	// Test targets room
	@Test
	public void testTargetsRoom() {
		board.getCell(0, 2).setRoom(true);
        board.getCell(0, 3).setRoom(true);
        board.getCell(1, 1).setRoom(true);
        
        TestBoardCell cell = board.getCell(0, 1);
        board.calcTargets(cell, 2);
        Set<TestBoardCell> targets = board.getTargets();
        
        assertEquals(3, targets.size());
        // rooms adjacent to cell are always targets
        assertTrue(targets.contains(board.getCell(0, 2)));
        assertTrue(targets.contains(board.getCell(1, 1)));
        
        assertTrue(targets.contains(board.getCell(1, 0)));
        
        // don't go through rooms
        assertFalse(targets.contains(board.getCell(0, 3)));
        assertFalse(targets.contains(board.getCell(2, 1)));
        assertFalse(targets.contains(board.getCell(1, 2)));
	}

}

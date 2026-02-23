package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {
	private static Board board;
	
	/* TEST FOR:
	 *
	 * ___ADJACENCIES____
	 * - Walkways only connect to adjacent walkways.
	 * - Walkways with doors will also connect to the room center the door points to.
	 * - The cell that represents the Room (i.e. connects to walkway) is the cell with a second character of ‘*’ (no other cells in a room should have adjacencies).
	 * - Room center cells ONLY connect to 1) door walkways that enter the room and 2) another room center cell if there is a secret passage connecting.
	 *
	 *___TARGETS____
	 * the target lists will behave like in your experiment package. But the situation is more complex now because of the complex board.
	 */
	
	/* REQUIREMENTS:
	 * 
	 * ___ADJACENCIES___
	 * - Locations with only walkways as adjacent locations
	 * - Locations within rooms not center. Note, this test is allowed to pass even for failing test.  (Should have empty adjacency list)  
	 * - Locations that are at each edge of the board 
	 * - Locations that are beside a room cell that is not a doorway
	 * - Locations that are doorways
	 * - Locations that are connected by secret passage
	 * 
	 * ___TARGETS___
	 * - Targets along walkways, at various distances
	 * - Targets that allow the user to enter a room
	 * - Targets calculated when leaving a room without secret passage
	 * - Targets calculated when leaving a room with secret passage
	 * - Targets that reflect blocking by other players
	 */
	
	@BeforeAll
	public static void setUp() {
		
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");		
		board.initialize();
	}

	//ADJACENCY TESTS
	
	// Dark orange cells
	@Test
	public void testWalkwayAdjacencies() {
		// test that walkways connect only to other walkways
		Set<BoardCell> testList = board.getAdjList(0, 20);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(1, 20)));
		assertTrue(testList.contains(board.getCell(0, 21)));

		testList = board.getAdjList(4, 6);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(4, 5)));
		assertTrue(testList.contains(board.getCell(3, 6)));
		assertTrue(testList.contains(board.getCell(4, 7)));
		assertTrue(testList.contains(board.getCell(5, 6)));

		testList = board.getAdjList(10, 19);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(10, 18)));
		assertTrue(testList.contains(board.getCell(10, 20)));
		assertTrue(testList.contains(board.getCell(9, 19)));
		assertTrue(testList.contains(board.getCell(11, 19)));

		testList = board.getAdjList(19, 4);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(19, 5)));
		assertTrue(testList.contains(board.getCell(18, 4)));
		assertTrue(testList.contains(board.getCell(20, 4)));
		
		testList = board.getAdjList(17, 25);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(17, 24)));
	}

	// Light Pink Cells
	@Test
	public void testDoorwayAdjacencies() {
		// test that doorway adjacencies
		Set<BoardCell> testList = board.getAdjList(1, 20);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(0, 20)));
		assertTrue(testList.contains(board.getCell(1, 21)));
		assertTrue(testList.contains(board.getCell(2, 20)));
		assertTrue(testList.contains(board.getCell(2, 13))); // room center
		
		
		testList = board.getAdjList(8, 5);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(8, 4)));
		assertTrue(testList.contains(board.getCell(8, 6)));
		assertTrue(testList.contains(board.getCell(9, 5)));
		assertTrue(testList.contains(board.getCell(6, 5))); // room center
		
		testList = board.getAdjList(18, 24);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(18, 23)));
		assertTrue(testList.contains(board.getCell(17, 24)));
		assertTrue(testList.contains(board.getCell(21, 24)));
	}

	// Light pink cells
	@Test 
	public void testRoomAdjacencies() {
		//  test for center and non-center room cells
		
		//test center with no secret passages
		Set<BoardCell> testList = board.getAdjList(21, 2); // room center
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(18, 3)));
		assertTrue(testList.contains(board.getCell(21, 4)));

		//test center with secret passage
		testList = board.getAdjList(21, 24); //room center
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(18, 24)));
		assertTrue(testList.contains(board.getCell(21, 21)));
		assertTrue(testList.contains(board.getCell(12, 1))); // library center cell

		//test non-center has empty adjacency
		testList = board.getAdjList(12, 23);
		assertEquals(0, testList.size());
	}

//TARGET TESTS

	// Light blue cells
	@Test
	public void testWalkwayTargets() {
		// test walkway-walkway 
		// (no rooms within 3 steps)
		board.calcTargets(board.getCell(6, 20), 3); // roll 3
		Set<BoardCell> targets= board.getTargets();
		assertEquals(15, targets.size());
		assertTrue(targets.contains(board.getCell(6, 17)));
		assertTrue(targets.contains(board.getCell(3, 20)));
		assertTrue(targets.contains(board.getCell(8, 19)));	
		assertTrue(targets.contains(board.getCell(5, 22)));
		assertTrue(targets.contains(board.getCell(9, 20)));
		assertTrue(targets.contains(board.getCell(7, 18)));
	}

	// Light blue cells
	@Test 
	public void testRoomTarget() {
		// test a target that let's you enter a room
		board.calcTargets(board.getCell(18, 3), 1); // roll 1
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(21, 2))); //room center
		assertTrue(targets.contains(board.getCell(17, 3)));
		assertTrue(targets.contains(board.getCell(18, 4)));

		board.calcTargets(board.getCell(18, 3), 2); // roll 2
		targets= board.getTargets();
		assertEquals(7, targets.size());
		assertTrue(targets.contains(board.getCell(21, 2))); //room center
		assertTrue(targets.contains(board.getCell(16, 3)));
		assertTrue(targets.contains(board.getCell(19, 4)));

		board.calcTargets(board.getCell(18, 3), 3); // roll 3
		targets= board.getTargets();
		assertEquals(10, targets.size());
		assertTrue(targets.contains(board.getCell(21, 2))); //room center
		assertTrue(targets.contains(board.getCell(20, 4)));
		assertTrue(targets.contains(board.getCell(15, 3)));
		assertTrue(targets.contains(board.getCell(18, 0)));

	}

	// Light blue cells
	@Test
	public void testLeavingRoom() {
		// test leaving rooms w/ and w/o secret passages
		
		// without secret Passage (2, 13)
		board.calcTargets(board.getCell(2, 13), 1); //roll 1
		Set<BoardCell> targets = board.getTargets();
		assertEquals(10, targets.size()); 
		assertTrue(targets.contains(board.getCell(1, 5)));
		assertTrue(targets.contains(board.getCell(5, 9)));
		assertTrue(targets.contains(board.getCell(5, 17)));
		assertTrue(targets.contains(board.getCell(2, 20)));

		board.calcTargets(board.getCell(2, 13), 2); // roll 2
		targets = board.getTargets();
		assertEquals(28, targets.size()); 
		assertTrue(targets.contains(board.getCell(0, 5)));
		assertTrue(targets.contains(board.getCell(2, 4)));
		assertTrue(targets.contains(board.getCell(5, 7)));
		assertTrue(targets.contains(board.getCell(5, 11)));
		assertTrue(targets.contains(board.getCell(5, 14)));
		assertTrue(targets.contains(board.getCell(5, 18)));
		assertTrue(targets.contains(board.getCell(1, 21)));
		assertTrue(targets.contains(board.getCell(1, 20)));

		//with secret passage
		board.calcTargets(board.getCell(2, 24), 2); // roll 2
		targets = board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(2, 21)));
		assertTrue(targets.contains(board.getCell(2, 22)));
		assertTrue(targets.contains(board.getCell(4, 22))); 
		assertTrue(targets.contains(board.getCell(21, 12))); // secret passage

		board.calcTargets(board.getCell(2, 24), 3); // roll 3
		targets = board.getTargets();
		assertEquals(10, targets.size());
		assertTrue(targets.contains(board.getCell(1, 21)));
		assertTrue(targets.contains(board.getCell(4, 22)));
		assertTrue(targets.contains(board.getCell(3, 20)));
		assertTrue(targets.contains(board.getCell(5, 22)));
		assertTrue(targets.contains(board.getCell(4, 23)));
		assertTrue(targets.contains(board.getCell(21, 12))); // secret passage

		
	}

	// Red cells
	@Test
	public void testOccupiedTarget() {
		// test when there is a single occupied cell in 
		// (10,5), occupied cell = (10,4)
		board.getCell(10, 4).setOccupied(true); // set (10,4) occupied
		board.calcTargets(board.getCell(10, 5), 3); // roll 3
		Set<BoardCell> targets= board.getTargets();
		board.getCell(10, 4).setOccupied(false); // set (10, 4) unoccupied
		assertEquals(13, targets.size());
		assertFalse(targets.contains(board.getCell(10, 4)));
		assertTrue(targets.contains(board.getCell(9, 3)));
		assertTrue(targets.contains(board.getCell(11, 3)));
		assertTrue(targets.contains(board.getCell(10, 6)));
		assertTrue(targets.contains(board.getCell(8, 6)));
		assertTrue(targets.contains(board.getCell(6, 5)));  // Lounge
	}
	
	// Red cells
	@Test 
	public void testOccupiedDoorWay() {
		// test blocked doorways
		// (2, 24), occupied cells = (2,22), (3,22)
		board.getCell(2, 22).setOccupied(true); // set (2,22) occupied
		board.getCell(3, 22).setOccupied(true); // set (3,22) occupied
		board.calcTargets(board.getCell(2, 24), 2); // roll 2
		Set<BoardCell> targets = board.getTargets();
		board.getCell(2, 22).setOccupied(false); // set (2,22) unoccupied
		board.getCell(3, 22).setOccupied(false); // set (3,22) unoccupied
		assertFalse(targets.contains(board.getCell(2, 22)));
		assertFalse(targets.contains(board.getCell(3, 22)));
		assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCell(21, 12))); // secret passage
		
		//test 1 blocked door
		// (2, 24), occupied cells = (2,22)
		board.getCell(2, 22).setOccupied(true); // set (2,22) occupied
		board.calcTargets(board.getCell(2, 24), 2); // roll 2
		targets = board.getTargets();
		board.getCell(2, 22).setOccupied(false); // set (2,22) unoccupied
		assertEquals(3, targets.size());
		assertFalse(targets.contains(board.getCell(2, 22)));
		assertTrue(targets.contains(board.getCell(3, 21)));
		assertTrue(targets.contains(board.getCell(21, 12))); // secret passage
		
	}

	// Red cells
	@Test
	public void testOccupiedRoom() {
		// validate a room can be entered even if it's occupied
		// (18,3), occupied cells = (18,2), (21,2)
		Set<BoardCell> targets;
		board.getCell(18, 2).setOccupied(true); // set (18,2) occupied
		board.getCell(21, 2).setOccupied(true); // set (21,2) occupied
		board.calcTargets(board.getCell(18, 3), 1); 
		targets = board.getTargets();
		board.getCell(18, 2).setOccupied(false); // set (18,2) unoccupied
		board.getCell(21, 2).setOccupied(false); // set (21,2) unoccupied
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(21, 2)));
		assertTrue(targets.contains(board.getCell(17, 3)));
		assertTrue(targets.contains(board.getCell(18, 4)));
	}
	
	
}
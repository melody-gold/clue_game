package tests;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.Room;

/**
 * FileInitTests Class
 * 
 * @author Jacob Dionne
 * @author Melody Goldanloo
 * 
 * JUnit testing class which verifies the board is initialized correctly and 
 * all data is transferred properly
 * 
 */
public class FileInitTests {

	// Constants that I will use to test whether the file was loaded correctly
	public static final int LEGEND_SIZE = 11;
	public static final int BOARD_LENGTH = 26;
	public static final int BOARD_HEIGHT = 23;

	
	private static Board board;
	
	@BeforeAll
	public static void setUp() {
		
		board = Board.getInstance();
		
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");

		board.initialize();
		
	}
	
	/*
	 * Verifies dimensions are correctly loaded
	 */
	@Test 
	public void testBoardDimensions() {
		assertEquals(BOARD_LENGTH, board.getNumColumns());
		assertEquals(BOARD_HEIGHT, board.getNumRows());
	}
	
	/*
	 *Ensures that room labels and names exist and are accurate
	 */
	@Test
	public void testRoomData(){
		assertEquals("Observatory", board.getRoom('O').getName() );
		assertEquals("Garden", board.getRoom('G').getName() );
		assertEquals("Music Room", board.getRoom('M').getName() );
		assertEquals("Library", board.getRoom('L').getName() );
		assertEquals("Lounge", board.getRoom('U').getName() );
		assertEquals("Study", board.getRoom('S').getName() );
		assertEquals("Dungeon", board.getRoom('D').getName() );
		assertEquals("Laboratory", board.getRoom('C').getName() );
		assertEquals("Art Gallery", board.getRoom('A').getName() );
		
		assertEquals("Unused", board.getRoom('X').getName() );
		assertEquals("Walkway", board.getRoom('W').getName() );		
		
	}
	
	/*
	 * Tests cells which are doorways of each direction as well as various non-doorway cells for accuracy
	 */
	@Test
	public void testDoorwayAccuracy() {
		BoardCell cell = board.getCell(5, 8);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());
		cell = board.getCell(14, 11);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());
		cell = board.getCell(17, 9);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
		cell = board.getCell(21, 21);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());
		// Test that walkways & unused cells are not doors
		cell = board.getCell(0, 5);
		assertFalse(cell.isDoorway());
		cell = board.getCell(0, 6);
		assertFalse(cell.isDoorway());
	}
	
	/*
	 * Tests that a correct number of doorway cells are present
	 */
	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if (cell.isDoorway())
					numDoors++;
			}
		//43 door cells present in our board currently
		Assert.assertEquals(43, numDoors);
	}
	
	/*
	 * Test every cell type to make sure they have the correct data
	 */
	@Test
	public void testCellAccuracy() {
		//Room cells
		BoardCell cell = board.getCell(0, 0);
		Room room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Library" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		
		cell = board.getCell(5, 5);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Lounge" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		
		cell = board.getCell(19, 2);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Study" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		
		cell = board.getCell(20, 9);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Garden" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		
		cell = board.getCell(10, 24);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Laboratory") ;
		assertTrue( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		
		cell = board.getCell(1, 25);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Observatory" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		
		//Center cell
		cell = board.getCell(2, 13);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Dungeon" ) ;
		assertFalse( cell.isLabel() );
		assertTrue( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		assertTrue( cell == room.getCenterCell());

		//Label cell
		cell = board.getCell(16, 12);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Art Gallery" ) ;
		assertTrue( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		assertTrue( cell == room.getLabelCell());
		
		//Secret Passage cell
		cell = board.getCell(22, 25);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Music Room" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;
		assertTrue( cell.getSecretPassage() == 'L');
		
		//Walkway Cell
		cell = board.getCell(11, 6);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Walkway" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		
		//Unused Cell
		cell = board.getCell(9, 8);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Unused" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;


	}
}

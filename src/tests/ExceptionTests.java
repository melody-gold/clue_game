package tests;


import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import clueGame.BadConfigFormatException;
import clueGame.Board;

/**
 * ExceptionTests Class
 * 
 * @author Jacob Dionne
 * @author Melody Goldanloo
 * 
 * JUnit testing class which verifies a the BadConfigFormatException exception is thrown properly
 * 
 */
public class ExceptionTests {

	/*
	 * Test that an exception is thrown for a layout file with an uneven amount of 
	 * rows in each column
	 */
	@Test
	public void testBadColumns() throws BadConfigFormatException, FileNotFoundException {
		assertThrows(BadConfigFormatException.class, () -> {
			
			Board board = Board.getInstance();
			board.setConfigFiles("ClueLayoutBadColumns306.csv", "ClueSetup.txt");
			// call the two load functions directly to avoid try-catch in initialize
			board.loadSetupConfig();
			board.loadLayoutConfig();
		});
	}

	/*
	 *  Tests for a layout file with a room not in its setup file
	 */
	@Test
	public void testBadRoom() throws BadConfigFormatException, FileNotFoundException {
		assertThrows(BadConfigFormatException.class, () -> {
			Board board = Board.getInstance();
			board.setConfigFiles("ClueLayoutBadRoom306.csv", "ClueSetup.txt");
			board.loadSetupConfig();
			board.loadLayoutConfig();
		});
	}

	/*
	 * Test that an exception is thrown in a badly formatted Setup file
	 */
	@Test
	public void testBadRoomFormat() throws BadConfigFormatException, FileNotFoundException {
		assertThrows(BadConfigFormatException.class, () -> {
			Board board = Board.getInstance();
			board.setConfigFiles("ClueLayout.csv", "ClueSetupBadFormat306.txt");
			board.loadSetupConfig();
			board.loadLayoutConfig();
		});
	}

}

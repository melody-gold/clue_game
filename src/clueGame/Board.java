package clueGame;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

import gui.BoardPanel;
import gui.ClueGame;
import gui.GameControlPanel;
import gui.GuessDialog;

/**
 * Board Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 *         Represents the Clue board. Handles loading the setup and layout
 *         config files, building the grid of BoardCells, linking them to Rooms,
 *         computing adjacency lists, and computing reachable targets given a
 *         dice roll.
 */
public class Board {
	private static final int MAX_SETUP_FIELDS = 6;
	private static final int MAX_DICE_ROLL = 6;
	private static final int MIN_DICE_ROLL = 1;
	private static final String SETUPTYPE_ROOM = "Room";
	private static final String SETUPTYPE_SPACE = "Space";
	private static final String SETUPTYPE_Player = "Player";
	private static final String SETUPTYPE_Weapon = "Weapon";
	// mapping from layout modifiers to room metadata
	private static final Map<Character, String> ROOM_MODIFIERS = Map.of('#', "Label", '*', "RoomCenter");
	// mapping from layout modifiers to door directions
	private static final Map<Character, DoorDirection> DIRECTION_MODIFIERS = 
			Map.of('^', DoorDirection.UP, 
					'v', DoorDirection.DOWN, 
					'>', DoorDirection.RIGHT, 
					'<', DoorDirection.LEFT);
	private static final Map<String, Color> COLOR_MAP = 
			Map.of("pink", new Color (214, 109, 149), 
					"red", new Color (161, 21, 27), 
					"magenta", new Color (190, 88, 199), 
					"black", Color.black, 
					"green", new Color (36, 133, 81), 
					"teal", new Color(6, 136, 150));
	// singleton instance
	private static Board theInstance = new Board();

	/**
	 * @return singleton instance of the Board
	 */
	public static Board getInstance() {
		return theInstance;
	}

	private BoardCell[][] grid;

	private int numRows;
	private int numColumns;
	private String layoutConfigFile;
	private String setupConfigFile;
	private Set<BoardCell> targets;
	
	private GameControlPanel gamePanel;
	private BoardPanel panel; 

	private Set<BoardCell> doorways;

	private Map<Character, Room> roomMap;

	private List<Character> spaceInitials;

	private List<Player> playerList;

	private int currentPlayerIndex;

	private int roll;

	private List<Card> deck;

	private Solution classified;

	private Solution currSuggestion;

	private Card currSuggestionResult;
	
	private boolean gameOver;
	


	private Board() {
		// private constructor to enforce singleton instance
		super();
	}

	/**
	 * Computes adjacency lists for all cells
	 */
	private void calcAdj() {
		// calculate adjacent cells
		for (int i = 0; i < this.numRows; i++) {
			for (int j = 0; j < this.numColumns; j++) {
				BoardCell currCell = grid[i][j];

				// skip cells that cannot have adj list (must be a walkway or room center)
				if ((currCell.getRoomInitial() != 'W') && !currCell.isRoomCenter()) {
					continue;
				}

				// if cell is room center
				if (currCell.isRoomCenter()) {
					// get the secret passage initial from the cell's room
					Character passageInitial = roomMap.get(currCell.getRoomInitial()).getSecretPassageTarget();
					// if cell has a secret passage
					if (passageInitial != null) {
						// add the centerCell of the secretPassage room to the currentCell's adj list
						currCell.addAdj(roomMap.get(passageInitial).getCenterCell());
					}
					continue;
				}

				// if cell is a doorway
				if (currCell.isDoorway()) {
					Room doorwayTo = null;
					switch (currCell.getDoorDirection()) {
					case UP:
						if (i > 0) {
							doorwayTo = getRoom(grid[i - 1][j]);
						}
						break;
					case DOWN:
						if (i < numRows - 1) {
							doorwayTo = getRoom(grid[i + 1][j]);
						}
						break;
					case LEFT:
						if (j > 0) {
							doorwayTo = getRoom(grid[i][j - 1]);
						}
						break;
					case RIGHT:
						if (j < numColumns - 1) {
							doorwayTo = getRoom(grid[i][j + 1]);
						}
						break;
					default:
						continue;
					}

					currCell.addAdj(doorwayTo.getCenterCell());
					doorwayTo.getCenterCell().addAdj(currCell);
				}

				// Walkway adjacency: up, down, left, right (within bounds)
				// above
				if (i > 0) {
					BoardCell cellAbove = grid[i - 1][j];
					if (cellAbove.getRoomInitial() == 'W') {
						currCell.addAdj(cellAbove);
					}
				}

				// below
				if (i < numRows - 1) {
					BoardCell cellBelow = grid[i + 1][j];
					if (cellBelow.getRoomInitial() == 'W') {
						currCell.addAdj(cellBelow);
					}
				}

				// left
				if (j > 0) {
					BoardCell cellToLeft = grid[i][j - 1];
					if (cellToLeft.getRoomInitial() == 'W') {
						currCell.addAdj(cellToLeft);
					}
				}

				// right
				if (j < numColumns - 1) {
					BoardCell cellToRight = grid[i][j + 1];
					if (cellToRight.getRoomInitial() == 'W') {
						currCell.addAdj(cellToRight);
					}
				}
			}
		}
	}

	public void setPanel(BoardPanel panel) {
		this.panel = panel;
	}
	
	public void setGamePanel(GameControlPanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	/**
	 * Computed all reachable target cells from {@code cell} given a roll
	 * 
	 * @param cell BoardCell
	 * @param roll int
	 */
	public void calcTargets(BoardCell cell, int roll) {
		targets = new HashSet<>();
		Set<BoardCell> initialVisited = new HashSet<>();

		initialVisited.add(cell);
		searchTargets(cell, roll, initialVisited);
	}

	public boolean checkAccusation(Solution accusation) {
		// all 3 cards must be correct
		return accusation.equals(classified);
	}

	/**
	 * deals cards to classified (solution) and each player
	 */
	public void deal() {
		List<Card> deckList = new ArrayList<>(deck);
		Collections.shuffle(deckList); // shuffle deck of cards

		Card room = null;
		Card person = null;
		Card weapon = null;
		for (Card card : deckList) { // select first instance of each type of card in deck
			if (person == null && card.getType() == CardType.PERSON) {
				person = card;
			} else if (weapon == null && card.getType() == CardType.WEAPON) {
				weapon = card;
			} else if (room == null && card.getType() == CardType.ROOM) {
				room = card;
			}
			if (person != null && weapon != null & room != null) {
				break;
			}
		}

		// place first instances in classified
		classified = new Solution(person, room, weapon);

		deckList.remove(room);
		deckList.remove(person);
		deckList.remove(weapon);

		int playerIndex = 0;
		while (!deckList.isEmpty()) {
			playerList.get(playerIndex).updateHand(deckList.remove(0));
			playerIndex = (playerIndex + 1) % playerList.size();
		}
	}

	public HumanPlayer getHumanPlayer() {
		return (HumanPlayer) this.playerList.getFirst();
	}

	/**
	 *
	 * @param rowNum row of cell
	 * @param colNum row of cell
	 * @return pre-computed adjacency list for the cell at (rowNum, colNum)
	 */
	public Set<BoardCell> getAdjList(int rowNum, int colNum) {
		// adjacent cells of current walkway cell
		// -> walkway if up/down/left/right and not occupied (but can pass through
		// occupied)
		// -> doorway leads to center of room
		// room center cells -> adjacent to all doorways for that room
		return grid[rowNum][colNum].getAdjList();
	}

	/**
	 * (0,0) is top left (22, 25) is bottom right
	 *
	 * @param rowNum yPos of Cell
	 * @param colNum xPos of Cell
	 * @return a cell at (row, col) in the grid
	 */
	public BoardCell getCell(int rowNum, int colNum) {
		return grid[rowNum][colNum];
	}

	public List<Card> getDeck() {
		return deck;
	}

	public int getNumColumns() {
		return this.numColumns;
	}

	public int getNumRows() {
		return this.numRows;
	}

	public List<Player> getPlayers() {
		return this.playerList;
	}

	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getRoomInitial());
	}

	public Room getRoom(char label) {
		return roomMap.get(label);
	}

	public Collection<Room> getRooms() {
		return roomMap.values();
	}

	public Solution getSolution() {
		return this.classified;
	}

	public Set<BoardCell> getDoorways() {
		return doorways;
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}

	public List<Card> getCardsByType(CardType type) {
		List<Card> result = new ArrayList<>();
		for (Card c : deck) {
			if (c.getType() == type) {
				result.add(c);
			}
		}
		return result;
	}

	public List<Card> getRoomCards() {
		return getCardsByType(CardType.ROOM);
	}

	public Card getRoomCardByName(String roomName) {
		for (Card c : deck) {
			if (c.getType() == CardType.ROOM &&
					c.getCardName().equals(roomName)) {
				return c;
			}
		}
		return null;
	}

	public List<Card> getPersonCards() {
		return getCardsByType(CardType.PERSON);
	}

	public List<Card> getWeaponCards() {
		return getCardsByType(CardType.WEAPON);
	}

	/**
	 * Loads configuration files and initializes the board
	 */
	public void initialize() {
		// Initialized in setConfigFiles()
		/*
		 * this.setupConfigFile this.layoutConfigFile
		 */

		// Initialized in loadSetupConfig:
		/*
		 * this.roomMap this.spaceInitials this.playerList this.deck
		 */

		// Initialized in loadLayoutConfig()
		/*
		 * this.grid this.numRows this.numColumns
		 */

		// Initialized in calcAdj()
		/*
		 * this.grid
		 */

		// Initialized in calcTargets
		/*
		 * this.targets
		 */

		try {
			loadSetupConfig();
			loadLayoutConfig();
			calcAdj();
		} catch (FileNotFoundException | BadConfigFormatException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public void doFirstMove() {
		currentPlayerIndex = 0; 
		rollDice();
		this.currSuggestion = null;
		gameOver = false;

		Player firstPlayer = getCurrentPlayer();
		calcTargets(getCell(firstPlayer.getRow(), firstPlayer.getCol()), roll);
	}

	/**
	 * Loads the layout file and builds the grid of BoardCells
	 *
	 * @throws BadConfigFormatException if layout data is invalid
	 * @throws FileNotFoundException    if the file cannot be opened
	 */
	public void loadLayoutConfig() throws BadConfigFormatException, FileNotFoundException {
		FileReader reader = new FileReader(this.layoutConfigFile);
		Scanner in = new Scanner(reader);
		// used an arrayList to avoid initializing grid many times
		ArrayList<BoardCell[]> boardRows = new ArrayList<>();
		doorways = new HashSet<>();
		this.numRows = 0;
		// -1 indicates that an initial columns number has yet to be set
		this.numColumns = -1;

		while (in.hasNextLine()) {
			String lineContents[] = in.nextLine().split(",");
			this.numRows++;

			// check for inconsistent columns per row
			if (numColumns != lineContents.length) {
				if (numColumns < 0) { // if numColums is yet to be set, set it
					this.numColumns = lineContents.length;
				} else { // else throw exception
					throwBadConfig(new BadConfigFormatException(this.layoutConfigFile,
							"discrepancy with number of columns in row" + numRows), in);
				}
			}

			// read cells in this row
			BoardCell[] currRow = new BoardCell[numColumns];
			// config errors encountered in this set are mostly the same, so an object is
			// created beforehand
			BadConfigFormatException invalidCellData = new BadConfigFormatException(this.layoutConfigFile,
					"Invalid cell data in row:" + (numRows - 1));
			for (int i = 0; i < numColumns; ++i) {
				String cellStr = lineContents[i];

				char initial = cellStr.charAt(0);

				// if there is too much or too little cell data throw a bad config error
				if (cellStr.length() < 1 || cellStr.length() > 2) {
					throwBadConfig(invalidCellData, in);
				}

				// if this Cell is part of a room in the setup file initialize, else throw a bad
				// config error
				if (roomMap.containsKey(initial)) {
					currRow[i] = new BoardCell(numRows - 1, i, initial);
					BoardCell currCell = currRow[i];

					// handle modifiers
					if (cellStr.length() > 1) {
						char modifier = cellStr.charAt(1);

						// if the cell is a doorway
						if (Board.DIRECTION_MODIFIERS.containsKey(modifier)) {
							// ensure the modifiers are on a Space room type, no non-space rooms will have
							// doors
							if (!spaceInitials.contains(initial)) {
								throwBadConfig(invalidCellData, in);
							}

							currCell.setDoorDirection(DIRECTION_MODIFIERS.get(modifier));
							doorways.add(currCell);
							continue;

						}

						// if the cell is a Room modifier
						Room currRoom = roomMap.get(initial);
						if (Board.ROOM_MODIFIERS.containsKey(modifier)) {
							// ensure the room modifier isn't on a space room type, these will never have
							// room modifiers
							if (spaceInitials.contains(initial)) {
								throwBadConfig(invalidCellData, in);
							}

							switch (Board.ROOM_MODIFIERS.get(modifier)) {
							case "Label":
								currCell.setLabel();
								currRoom.setLabelCell(currCell);
								break;
							case "RoomCenter":
								currCell.setRoomCenter();
								currRoom.setCenterCell(currCell);
								break;
							default:
								break;
							}
							continue;
						}

						// if the cell has a valid secretPassage
						if (roomMap.containsKey(modifier) && !(spaceInitials.contains(modifier))) {
							currCell.setSecretPassage(modifier);
							currRoom.setSecretPassageTarget(modifier);
							continue;
						}
					}

				} else {
					throwBadConfig(invalidCellData, in);
				}
			}

			boardRows.add(currRow); // add row to arrayList
		}

		// add rows to grid
		this.grid = new BoardCell[numRows][];
		for (int i = 0; i < boardRows.size(); ++i) {
			// DEBUG
			// System.out.println(boardRows.get(i));
			grid[i] = boardRows.get(i);
		}

		//set all player occupied cells accordingly
		for (Player player : playerList) {
			grid[player.getRow()][player.getCol()].setOccupied(true);
		}

		in.close();
	}

	/**
	 * Loads the setup file and populates: - roomMap: mapping from room initial to
	 * Room - spaceInitials: initials representing "space" (walkway)
	 *
	 * @throws BadConfigFormatException is a line has invalid format or room type
	 * @throws FileNotFoundException    if the file cannot be opened
	 */
	public void loadSetupConfig() throws BadConfigFormatException, FileNotFoundException {
		// takes setup file and loads into room map
		FileReader reader = new FileReader(this.setupConfigFile);
		Scanner in = new Scanner(reader);
		int index = 0; // index is purely for error messages

		this.roomMap = new HashMap<>(); // hashMap for unordered storage
		this.spaceInitials = new ArrayList<>(); // arrayList for varying space type rooms
		this.playerList = new ArrayList<>(); // arrayList for varying numbers of players
		this.roll = 0;
		this.deck = new ArrayList<>();

		while (in.hasNextLine()) {
			// read each line into {"room type", "room name", "room character"}
			String[] lineContents = in.nextLine().split(",\\s*");
			String setupType = lineContents[0];

			// skip lines preceded "//" (comment)
			if (setupType.startsWith("//")) {
				// DEBUG
				// System.out.println(lineContents[0]);
				index++;
				continue;
			}

			// throws an error if incorrect number of data is passed per line
			if (lineContents.length > MAX_SETUP_FIELDS) {
				throwBadConfig(new BadConfigFormatException(setupConfigFile, "invalid input format on line " + index),
						in);
			}

			// assign the data of lineContents
			if (setupType.equals(Board.SETUPTYPE_ROOM)) {
				Room newRoom = new Room(lineContents[1]);
				roomMap.put(lineContents[2].charAt(0), newRoom);
				// if data is a room add a card
				Card room = new Card(lineContents[1], CardType.ROOM);
				deck.add(room);

			} else if (setupType.equals(Board.SETUPTYPE_SPACE)) {
				Room newRoom = new Room(lineContents[1]);
				roomMap.put(lineContents[2].charAt(0), newRoom);
				// if the data is a space, add it to the spaceInitials list
				spaceInitials.add(lineContents[2].charAt(0));
			} else if (setupType.equals(Board.SETUPTYPE_Player)) {
				Player newPlayer = null;
				// read player data
				String playerType = lineContents[1];
				String name = lineContents[2];
				String color = lineContents[3];
				int row = Integer.parseInt(lineContents[4]);
				int col = Integer.parseInt(lineContents[5]);

				//if all computer players flag set, set player type to computer
				if(ClueGame.TEST_SET_ALL_PLAYER_COMPUTER) {
					playerType = "Computer";
				}

				if (playerType.equals("Human")) {
					newPlayer = new HumanPlayer(name, COLOR_MAP.get(color), row, col);
				} else if (playerType.equals("Computer")) {
					newPlayer = new ComputerPlayer(name, COLOR_MAP.get(color), row, col);
				} else {
					throwBadConfig(new BadConfigFormatException(setupConfigFile,
							"invalid player type on line " + index + ": " + playerType), in);
				}
				this.playerList.add(newPlayer);

				Card person = new Card(name, CardType.PERSON);
				deck.add(person);

			} else if (setupType.equals(Board.SETUPTYPE_Weapon)) {
				Card weapon = new Card(lineContents[1], CardType.WEAPON);
				deck.add(weapon);

			} else {
				throwBadConfig(new BadConfigFormatException(setupConfigFile,
						"invalid Setup type on line " + index + ": " + setupType), in);
			}

			index++;
		}

		//set static player cards
		Player.setCards(deck,roomMap);

		in.close();
	}

	/**
	 * DFS helper for {@link #calcTargets(BoardCell, int)}
	 * 
	 * @param currCell
	 * @param remainingSteps
	 * @param visited
	 */
	private void searchTargets(BoardCell currCell, int remainingSteps, Set<BoardCell> visited) {
		// DFS to explore possible paths from currentCell using remainingSteps
		for (BoardCell adj : currCell.getAdjList()) {
			if (visited.contains(adj)) {
				// skip visited cells
				continue;
			} else if (adj.getOccupied() && !(adj.isRoomCenter())) {
				// skip occupied cells that aren't the room center
				continue;
			}

			visited.add(adj); // mark cell visited

			// if last step or or cell is a room center, add it to targets
			if (adj.isRoomCenter() || remainingSteps == 1) {
				targets.add(adj);
			} else { // if more steps left, recursively call
				searchTargets(adj, remainingSteps - 1, visited);
			}

			visited.remove(adj);
		}
	}

	/**
	 * Set the layout and setup config file paths. The files are expect to live
	 * under the data/ directory
	 * 
	 * @param layoutFileName
	 * @param setupFileName
	 */
	public void setConfigFiles(String layoutFileName, String setupFileName) {
		this.layoutConfigFile = "data/" + layoutFileName;
		this.setupConfigFile = "data/" + setupFileName;

	}

	public void setSolution(Solution answer) {
		this.classified = answer;
	}

	/**
	 * Centralizes cleanup for configuration-loading errors
	 * 
	 * @param exception
	 * @param scanner
	 * @throws BadConfigFormatException after closing the scanner
	 */
	private void throwBadConfig(BadConfigFormatException exception, Scanner scanner) throws BadConfigFormatException {
		scanner.close();
		throw exception;
	}

	public Player getCurrentPlayer() {
		return this.playerList.get(currentPlayerIndex);
	}

	public void doAccuse() {
		Player player = getCurrentPlayer();
		
		if(player.willAccuse() && !gameOver) {
			Solution accusation = player.makeAccusation();
			
			if(accusation == null) {
				return;
			}

			String message = null;
			String playerName = player.getName();
			if(player instanceof HumanPlayer) {
				playerName = "you";
			}

			if(checkAccusation(accusation) == true) {
				message = "Congratulations!!! " + playerName + " has won the game!"
						+"\nit was: " + this.getSolution();
			}else {
				message = "Dratz! " + playerName + " looks like you got it wrong." 
						+ "\nthe real solution was:\n" + this.getSolution();
			}
			message += "\nThanks for playing! Please close this window.";
			JOptionPane.showMessageDialog(panel, message, "GAME OVER!", JOptionPane.INFORMATION_MESSAGE);
			gameOver = true;

			
		}

	}

	/**
	 * updates the player to the next in the list
	 * @return true if successful, false if waiting for a selection
	 */
	public boolean nextPlayer() {
		if(panel.isListening() || gameOver == true) {
			// return false if panel is currently listening for a player selection or the game is over
			return false;
		}

		currentPlayerIndex = (1 + currentPlayerIndex) % playerList.size();

		rollDice();
		return true;
	}

	private void rollDice() {
		Random random = new Random();
		this.roll = random.nextInt(MAX_DICE_ROLL - MIN_DICE_ROLL + 1) + MIN_DICE_ROLL;
	}

	public void startTurn() {
		Player player = getCurrentPlayer();

		calcTargets(getCell(player.getRow(), player.getCol()), getRoll());

		// if the player is a human player 
		if(player instanceof HumanPlayer) {
			// ensure human player selects a valid target

			// get move data from panel
			// panel will call the decision phase
			if(player.willAccuse() == false) {
				panel.handleHumanPlayerTurn(true);
			}

		} else if (player instanceof ComputerPlayer) {
			ComputerPlayer compPlayer = (ComputerPlayer) player; // safe cast
			// move to a cell
			BoardCell newLocation = compPlayer.selectTarget(targets);
			movePlayerTo(compPlayer, newLocation);
			
			doAccuse();

			doPlayerDecision(compPlayer);

			// update board
			panel.updateUI();

		}
	}

	public void doPlayerDecision(Player player){
		BoardCell location = this.getCell(player.getRow(), player.getCol());

		//if in room, do room logic
		if(location.isRoomCenter()) {
			Solution suggestion = null;
			if(player instanceof HumanPlayer) {
				HumanPlayer humanPlayer = (HumanPlayer) player;

				Card room = getRoomCardByName(getRoom(location).getName());
				suggestion = humanPlayer.getSuggestion(room, panel);

			}else if(player instanceof ComputerPlayer) {
				ComputerPlayer compPlayer = (ComputerPlayer) player; // safe cast

				suggestion =  compPlayer.createSuggestion(location.getRoomInitial());
			}

			Card result = handleSuggestion(suggestion, player);
			player.receiveSuggestionResult(result, suggestion);
			//testing logic
			if(ClueGame.TEST_SUGGESTION) {
				if(result != null) {
					System.out.println("Suggestion: " + suggestion
							+ "\nMade by : " + player.getName()
							+ "\nDisproven by: " + result.getPlayerHoldingThis().getName()
							+ "\nWith the card: " + result);
				}else {
					System.out.println("Suggestion: " + suggestion
							+ "\nMade by : " + player.getName()
							+ "\n not disproven");
				}
				System.out.println();


			}
		}else {
			this.currSuggestion = null;
			//testing logic
			if(ClueGame.TEST_SUGGESTION) {
				System.out.println(player.getName() + " has not reached a room");
			}
		}
		
		panel.updateUI();
		
		gamePanel.setGuess(getCurrentGuess());
		gamePanel.setGuessResult(getCurrentGuessResult());
		gamePanel.updateUI();
	}

	public Card handleSuggestion(Solution suggestion, Player suggester) {
		
		int numPlayers = 6;
		int accuserIndex = playerList.indexOf(suggester);
		this.currSuggestion = suggestion;

		//find the player to move
		String suggestedPlayerName = suggestion.getPerson().getCardName();
		Player suggestedPlayer = null;
		for (Player player : playerList) {
			if(player.getName() == suggestedPlayerName) {
				suggestedPlayer = player;
			}
		}

		//find the room to move them to 
		String suggestedRoomName = suggestion.getRoom().getCardName();
		Room suggestedRoom = null;
		for(Room room : roomMap.values()) {
			if(room.getName() == suggestedRoomName) {
				suggestedRoom = room;
			}
		}

		//move player to room
		this.movePlayerTo(suggestedPlayer, suggestedRoom.getCenterCell());

		// start index is the player after the suggester, loop around playerList once
		for (int i = 1; i < numPlayers; i++) {
			int playerIndex = (accuserIndex + i) % numPlayers;
			Player currPlayer = playerList.get(playerIndex);

			// suggester can't disprove self
			if (currPlayer == suggester) {
				continue;
			}

			// see if current player can disprove the suggestion
			Card disprovingCard = currPlayer.disproveSuggestion(suggestion);

			if (disprovingCard != null) { 	// if current player can disprove
				suggester.updateSeen(disprovingCard);	// update suggester's seen cards
				this.currSuggestionResult = disprovingCard;
				return disprovingCard;		// return matching card
			}
		}

		// if no player could disprove, return null
		this.currSuggestionResult = null;
		return null;
	}

	private String getCurrentGuessResult() {
		if(currSuggestion == null) {
			return "";
		}
		
		if(currSuggestionResult == null) {
			return "Suggestion was not disproven";
		}else{
			return "Disproven by: " + currSuggestionResult.getPlayerHoldingThis().getName();
		}
	}

	private String getCurrentGuess() {
		if(currSuggestion == null) {
			return "Waiting For a Guess!";
		}else {
			return currSuggestion.toString();
		}
	}


	public void movePlayerTo(Player player, BoardCell target) {
		BoardCell currentCell = getCell(player.getRow(), player.getCol());
		currentCell.setOccupied(false);

		player.moveTo(target.getRow(), target.getColumn());
		target.setOccupied(true);
	}

	public int getRoll() {
		return this.roll;
	}

}
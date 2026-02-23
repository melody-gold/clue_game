package clueGame;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import gui.GuessDialog;
import gui.KnownCardsGUI;

/**
 * HumanPlayer Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 *	Extends Player, represents a human-controlled player on the board
 */
public class HumanPlayer extends Player {
	private boolean accusing;
	private KnownCardsGUI infoPanel;

	public HumanPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
		this.accusing = false;
	}
	
	public void setInfoPanel(KnownCardsGUI panel) {
		this.infoPanel = panel;
	}
	
	private void updateInfo() {
		if(infoPanel != null) {
			infoPanel.updatePanels();
		}
	}
	
	public Solution getSuggestion(Card currentRoom, Component locationComponent) {
		
		//Create Dialog
		GuessDialog suggestionDialog = new GuessDialog(null, currentRoom, ALL_ROOMS_MAP.values(), 
				Player.ALL_PEOPLE_SET, Player.ALL_WEAPONS_SET, false);
		suggestionDialog.setLocationRelativeTo(locationComponent);
		suggestionDialog.setVisible(true);
		

		if(suggestionDialog.wasSubmitted()) {			
			return suggestionDialog.getSolution();

		}else {
			JOptionPane.showMessageDialog(null, "ERROR: Please make a suggestion", 
					"Suggestion Error", JOptionPane.ERROR_MESSAGE);
			return getSuggestion(currentRoom, locationComponent);
		}
	}
	
	public void startAccusing() {
		this.accusing = true;
	}

	@Override
	public boolean willAccuse() {
		return this.accusing;
	}
	
	/**
	 * Returns Accusation
	 */
	@Override
	public Solution makeAccusation() {
		//Create Dialog                                                               
		GuessDialog accusationDialog = new GuessDialog(null, null, ALL_ROOMS_MAP.values(),    
				Player.ALL_PEOPLE_SET, Player.ALL_WEAPONS_SET, true);                                     
		accusationDialog.setLocationRelativeTo(null);                                   
		accusationDialog.setVisible(true);
		
		if(accusationDialog.wasSubmitted()) {
			return accusationDialog.getSolution();
		}else {
			this.accusing = false;
			return null;
		}
	}

	@Override
	public void receiveSuggestionResult(Card card, Solution suggestion) {
		updateInfo();
	} 

}

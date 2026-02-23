package gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * ClueGUIUtil Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 *	provides Clue GUI classes with some helper functions and useful constants
 */
public final class ClueGUIUtil{ //should not be inherited from
	//style constants
	public static final Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.DARK_GRAY;
	public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
	public static final Color DEFAULT_TEXTFIELD_BACKGROUND = new Color(48, 48, 48);

	//dimension constants
	//public static final int NUM_TEXT_FIELD_COLS = 10;
	//public static final Dimension DEFAULT_LABEL_DIM = new Dimension(60, 16);

	private ClueGUIUtil() {} //should not be instantiated

	//JLabel Classes

	/**
	 * Creates a JLabel with the default Clue GUI color
	 * @param text display text
	 * @return default clue JLabel
	 */
	public static JLabel createDefaultLabel(String text){
		JLabel newLabel = new JLabel(text);
		newLabel.setForeground(DEFAULT_TEXT_COLOR);
		return newLabel;
	}


	/**
	 * Creates a JTextField with default Clue GUI colors, editable is set to false and the
	 * @return default clue JTextField
	 */
	public static JTextField createDefaultTextField() {
		JTextField newField = new JTextField(0);
		newField.setForeground(DEFAULT_TEXT_COLOR);
		newField.setBackground(DEFAULT_TEXTFIELD_BACKGROUND);
		newField.setEditable(false);
		return newField;

	}

	/**
	 * Creates a JTextField with default Clue GUI colors, editable is set to false and the
	 * field is initialized with zero columns
	 * @param text display text
	 * @return default clue JTextField
	 */
	public static JTextField createDefaultTextField(String text) {
		JTextField newField = createDefaultTextField();
		newField.setText(text);
		return newField;

	}

	/**
	 * Creates a TitledBorder with Default ClueGUI colors
	 * @param title displayed title
	 * @return default clue TitleBorder
	 */
	public static TitledBorder createDefaultTitledBorder(String title) {
		TitledBorder newBorder =  BorderFactory.createTitledBorder(DEFAULT_BORDER, title);
		newBorder.setTitleColor(DEFAULT_TEXT_COLOR);
		return newBorder;
	}

	/**
	 * 
	 * @param color to be darkened
	 * @param darken factor (lower = darker)
	 * @return
	 */
	public static Color darken(Color c, double factor) {
		int r = (int)(c.getRed()   * factor);
		int g = (int)(c.getGreen() * factor);
		int b = (int)(c.getBlue()  * factor);

		return new Color(
				Math.max(0, r),
				Math.max(0, g),
				Math.max(0, b)
				);
	}
	
}
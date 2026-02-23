package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Room Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 * Holds data regarding the cluster of cells that make up a room
 *
 */
public class Room {
	private String name;
	private BoardCell centerCell;
	private BoardCell labelCell;
	// Chose to use the "Character" wrapping class, so that both Room and BoardCell
	// could share a null secretPassage value
	private Character secretPassageTarget;

	public Room(String name) {
		super();

		this.name = name;
		this.centerCell = null;
		this.labelCell = null;
	}



	public String getName() {
		return this.name;
	}

	public BoardCell getCenterCell() {
		return this.centerCell;
	}

	public void setCenterCell(BoardCell cell) {
		this.centerCell = cell;
	}

	public BoardCell getLabelCell() {
		return this.labelCell;
	}

	public void setLabelCell(BoardCell cell) {
		this.labelCell = cell;
	}

	public void setSecretPassageTarget(char initial) {
		this.secretPassageTarget = initial;
	}

	public Character getSecretPassageTarget() {
		return this.secretPassageTarget;
	}


	// ***** GUI METHOD *****
	/**
	 * Draw room name
	 * @param g
	 */
	public void draw(Graphics g, int width, int height) {
		if (labelCell == null) {
			return;
		}


		// room label row/col
		int row = labelCell.getRow();
		int col = labelCell.getColumn();

		// room label x and y position in BoardPanel
		int y = row * height;
		int x = col * width;

		// get text height/width
		int fontSize = Math.max(10, height / 2);
		g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
		FontMetrics fm = g.getFontMetrics();

		// text dimensions to center labels over desired cell
		int textHeight = fm.getAscent() - fm.getDescent();
		int centerY = y + (height / 2) + textHeight / 2;

		int textWidth = fm.stringWidth(name);
		int centerX = x + (width - textWidth) / 2;

		//		g.setColor(Color.RED);
		//		g.drawLine(x, y + height/2, x + width, y + height/2);  // label's horizontal center
		//		g.drawLine(x + width/2, y, x + width/2, y + height);   // label's vertical center

		// baseline (x, y) for the text
		int labelX = centerX;
		int labelY = centerY;

		// the lounge and observatory rooms have awkward label locations
		g.setColor(Color.BLACK);
		if (name.equals("Lounge")) {
			labelX += width / 2;
		} else if (name.equals("Observatory")) {
			labelX -= width / 2;
			labelY -= height / 2;
		} else if (name.equals("Music Room")) {
			labelX += width / 2;
		}

		// translucent background behind text
		int paddingX = 4;
		int paddingY = 3;

		int rectX = labelX - paddingX;
		int rectY = labelY - fm.getAscent() - paddingY;
		int rectW = textWidth + 2 * paddingX;
		int rectH = textHeight + 2 * paddingY;

		Color bg = new Color(240, 240, 240, 120);  // (r,g,b,alpha)
		Color oldColor = g.getColor();

		g.setColor(bg);
		g.fillRoundRect(rectX, rectY, rectW, rectH, 8, 8);

		g.setColor(Color.BLACK);
		g.drawString(name, labelX, labelY - fm.getDescent());

		g.setColor(oldColor);
	}

}

package clueGame;

/**
 * BadConfigFormatException Class
 *
 * @author Jacob Dionne
 * @author Melody Goldanloo
 *
 * Exception thrown when a layout or setup file is formatted incorrectly or missing information
 *
 */
public class BadConfigFormatException extends Exception{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MSG = "Something went wrong with the configuration process";

	private String file;

	public BadConfigFormatException() {
		super("Error: " + DEFAULT_MSG);
		this.file = null;
	}

	public BadConfigFormatException(String file) {
		super("Error in " + file + ": " + DEFAULT_MSG);
		this.file = file;
	}

	public BadConfigFormatException(String file, String msg) {
		super("Error in " + file + ": " + msg);
		this.file = file;
	}

	@Override
	public String toString() {
		if (file.equals(null)) {
			return "Configuration error";
		} else {
			return "Configuration error in: " + this.file;
		}

	}

}

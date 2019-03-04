import Tokens.*;

public class JackParserException extends Exception {

	public static final long serialVersionUID = 4487924411002636L;

	public JackParserException(String cause, Token tok) {
		super(cause + " en " + tok);
	}
}

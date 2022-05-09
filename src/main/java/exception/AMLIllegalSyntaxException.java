package exception;

public class AMLIllegalSyntaxException extends RuntimeException {
	private static final long serialVersionUID = 1622238471769492499L;
	private AMLSyntaxExceptions type;
	private int line;

	public AMLIllegalSyntaxException(AMLSyntaxExceptions type, int line) {
		this.type = type;
		this.line = line;
	}
	
	public AMLSyntaxExceptions getType() {
		return type;
	}
	
	public int getLine() {
		return line;
	}
	
}

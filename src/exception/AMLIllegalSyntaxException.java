package exception;

public class AMLIllegalSyntaxException extends RuntimeException {
	private static final long serialVersionUID = 1622238471769492499L;
	AMLSyntaxExceptions type;

	public AMLIllegalSyntaxException(AMLSyntaxExceptions type) {
		this.type = type;
	}
	
}

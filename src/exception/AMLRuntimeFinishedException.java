package exception;

public class AMLRuntimeFinishedException extends RuntimeException {
	private static final long serialVersionUID = 6337195373891813594L;
	boolean isWord;
	Character letter;
	public AMLRuntimeFinishedException(boolean isWord, Character letter) {
		this.isWord = isWord;
		this.letter = letter;
	}

	public AMLRuntimeFinishedException(boolean isWord) {
		this.isWord = isWord;
	}
	
	public boolean isWord() {
		return isWord;
	}
	public Character getLetter() {
		return letter;
	}
}

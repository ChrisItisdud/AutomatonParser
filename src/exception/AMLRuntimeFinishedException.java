package exception;

public class AMLRuntimeFinishedException extends RuntimeException {
	private static final long serialVersionUID = 6337195373891813594L;
	boolean isWord;
	Character letter;
	models.IState state;
	public AMLRuntimeFinishedException(boolean isWord, Character letter, models.IState state) {
		this.isWord = isWord;
		this.letter = letter;
		this.state = state;
	}

	public AMLRuntimeFinishedException(boolean isWord, models.IState state) {
		this.isWord = isWord;
	}
	
	public boolean isWord() {
		return isWord;
	}
	public Character getLetter() {
		return letter;
	}
	public models.IState getState() {
		return state;
	}
}

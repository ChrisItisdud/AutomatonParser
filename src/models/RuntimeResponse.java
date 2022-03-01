package models;

public class RuntimeResponse<T> {
	T state;
	Character readChar;
	boolean isFinished = false;
	boolean isWord;
	String word;

	public RuntimeResponse(T state, Character readChar, String word) {
		this.state = state;
		this.readChar = readChar;
		this.word = word;
	}

	public RuntimeResponse(T state, Character readChar, boolean isFinished, boolean isWord, String word) {
		this.state = state;
		this.readChar = readChar;
		this.isFinished = isFinished;
		this.isWord = isWord;
		this.word = word;
	}

	public Character getChar() {
		return readChar;
	}

	public T getState() {
		return state;
	}

	public boolean isWord() {
		return isWord;
	}

	public boolean isFinished() {
		return isFinished;
	}
	
	public String getWord() {
		return word;
	}
}

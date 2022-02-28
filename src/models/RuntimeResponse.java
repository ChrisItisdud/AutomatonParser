package models;

public class RuntimeResponse<T> {
	T state;
	Character readChar;
	boolean isFinished = false;
	boolean isWord;

	public RuntimeResponse(T state, Character readChar) {
		this.state = state;
		this.readChar = readChar;
	}

	public RuntimeResponse(T state, Character readChar, boolean isFinished, boolean isWord) {
		this.state = state;
		this.readChar = readChar;
		this.isFinished = isFinished;
		this.isWord = isWord;
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
}

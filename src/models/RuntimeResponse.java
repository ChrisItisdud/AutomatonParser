package models;

public class RuntimeResponse {
	IState state;
	Character readChar;
	boolean isFinished = false;
	boolean isWord;
	public RuntimeResponse (IState state, Character readChar) {
		this.state = state;
		this.readChar = readChar;
	}
	public RuntimeResponse(IState state, Character readChar, boolean isFinished, boolean isWord){
		this.state = state;
		this.readChar = readChar;
		this.isFinished = isFinished;
		this.isWord = isWord;
	}
	
	public Character getChar() {return readChar;}
	public IState getState() {return state;}
	public boolean isWord() {return isWord;}
	public boolean isFinished() {return isFinished;}
}

package models;

public interface IPDAState {
	public PDATransition[] transition(Character input, Character StackValue);
	public boolean isEndState();
	public void setEndState(boolean input);
	public String getName();
	public void addTransition(Character key, Character stackKey, IPDAState target, Character[] stackTarget);
}

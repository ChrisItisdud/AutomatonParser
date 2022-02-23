package models;

public interface IState {
	public IState[] transition(Character input);
	public boolean isEndState();
	public void setEndState(boolean input);
	public String getName();
}

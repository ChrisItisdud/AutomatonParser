package models;

public interface IFAState extends IState {
	public void addTransition(Character key, IState value);
}

package models;

public class PDATransition {
	IPDAState target;
	Character[] stackTarget;
	public PDATransition(IPDAState target, Character[] stackTarget) {
		this.target = target;
		this.stackTarget = stackTarget;
	}
	
	public IPDAState getTarget() {
		return target;
	}
	
	public Character[] getStackTarget() {
		return stackTarget;
	}
}

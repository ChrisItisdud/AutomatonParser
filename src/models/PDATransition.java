package models;

public class PDATransition {
	IPDAState target;
	Character[] stackTarget;
	boolean read;
	public PDATransition(IPDAState target, Character[] stackTarget, boolean read) {
		this.target = target;
		this.stackTarget = stackTarget;
		this.read = read;
	}
	
	public IPDAState getTarget() {
		return target;
	}
	
	public Character[] getStackTarget() {
		return stackTarget;
	}
	
	public boolean isRead() {
		return read;
	}
}

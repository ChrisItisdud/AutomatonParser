package models;

public class Automaton {
	private final AutomatonType type;
	private final String name;
	private final IState[] start;
	public Automaton(IState[] start, String name, AutomatonType type) {
		this.start = start;
		this.name = name;
		this.type = type;
	}
	
	public AutomatonType getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public IState[] getStart() {
		return start;
	}
}

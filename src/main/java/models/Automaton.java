package models;

public class Automaton {
	private final AutomatonType type;
	private final String name;
	private final IState[] start;
	private final IPDAState[] pdaStart;
	public Automaton(IState[] start, String name, AutomatonType type) {
		this.start = start;
		this.name = name;
		this.type = type;
		pdaStart = null;
	}
	
	public Automaton(IPDAState[] pdaStart, String name, AutomatonType type) {
		this.pdaStart = pdaStart;
		this.name = name;
		this.type = type;
		start = null;
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
	public IPDAState[] getPdaStart() {
		return pdaStart;
	}
}

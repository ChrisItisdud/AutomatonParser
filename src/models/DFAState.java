package models;

import java.util.HashMap;
import java.util.Map;

public class DFAState implements IState {
	private final String name;
	private Map<Character, DFAState> transitions;
	private boolean endState;
	
	public DFAState (String name) {
		this.name = name;
		transitions = new HashMap<>();
	}

	@Override
	public IState[] transition(Character input) {
		if(transitions.containsKey(input)) return new IState[] {transitions.get(input)};
		return null;
	}

	public void addTransition(Character key, IState iState) {
		if (transitions.containsKey(key)) throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_KEY_ALREADY_EXISTS);
		transitions.put(key, (DFAState)iState);
	}
	
	public void setEndState(boolean endState) {
		this.endState = endState;
	}

	@Override
	public boolean isEndState() {
		return endState;
	}
	
	public String getName() {
		return name;
	}

}

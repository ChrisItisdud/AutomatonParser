package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFAState implements IFAState {
	Map<Character, List<IState>> transitions = new HashMap<>();
	boolean endState = false;
	String name;
	
	public NFAState (String name) {
		this.name = name;
	}
	@Override
	public IState[] transition(Character input) {
		IState[] fromInput = transitions.containsKey(input) ? listToArray(transitions.get(input)) : null;
		return fromInput;
	}
	
	public IState[] transitionEmpty() {
		IState[] fromEmpty = transitions.containsKey('#') ? listToArray(transitions.get('#')) : null;
		return fromEmpty;
	}
	
	public void addTransition(Character key, IState value, int line) {
		if(transitions.containsKey(key)) {
			transitions.get(key).add(value);
		}
		else {
			transitions.put(key, new ArrayList<IState>());
			transitions.get(key).add(value);
		}
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

	private static IState[] listToArray(List<IState> input) {
		IState[] result = new IState[input.size()];
		for(int i=0;i<result.length;i++) {
			result[i]=input.get(i);
		}
		return result;
	}
	
}

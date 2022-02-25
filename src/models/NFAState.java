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
		if(transitions.containsKey(input)) {
			IState[] transArray = new IState[transitions.get(input).size()];
			for(int i=0;i<transArray.length;i++) {
				transArray[i]=transitions.get(input).get(i);
			}
			return transArray;
		}
		else return null;
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

}

package models;

import java.util.HashMap;

public class DPDAState implements IPDAState {
	HashMap<Character, HashMap<Character, PDATransition[]>> values = new HashMap<>();
	String name;
	boolean endState = false;
	
	public DPDAState(String name) {
		this.name = name;
	}

	@Override
	public PDATransition[] transition(Character input, Character StackValue) {
		if(!values.containsKey(input)||!values.get(input).containsKey(StackValue)) return null;
		return values.get(input).get(StackValue);
	}

	@Override
	public boolean isEndState() {
		return endState;
	}

	@Override
	public void setEndState(boolean input) {
		this.endState = input;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addTransition(Character key, Character stackKey, IPDAState target, Character[] stackTarget, int line) {
		if(values.containsKey(key)&&values.get(key).containsKey(stackKey)) throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_KEY_ALREADY_EXISTS, line);
		else if(!values.containsKey(key)) {
			values.put(key, new HashMap<>());
			values.get(key).put(stackKey, new PDATransition[] {new PDATransition(target, stackTarget)});
		}
		else {
			values.get(key).put(stackKey, new PDATransition[] {new PDATransition(target, stackTarget)});
		}
	}

}

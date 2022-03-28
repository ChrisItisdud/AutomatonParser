package models;

import java.util.Arrays;
import java.util.HashMap;

public class NPDAState implements IPDAState {
	HashMap<Character, HashMap<Character, PDATransition[]>> values = new HashMap<>();
	String name;
	boolean endState = false;
	
	public NPDAState(String name) {
		this.name = name;
	}

	@Override
	public PDATransition[] transition(Character input, Character StackValue) {
		PDATransition[] fromInput = values.containsKey(input) && values.get(input).containsKey(StackValue) ? values.get(input).get(StackValue) : null;
		PDATransition[] fromEmpty = values.containsKey('#') && values.get('#').containsKey(StackValue) ? values.get('#').get(StackValue) : null;
		return combineArray(fromInput, fromEmpty);
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
		if(values.containsKey(key)&&values.get(key).containsKey(stackKey)) {
			PDATransition[] transitions = values.get(key).get(stackKey);
			PDATransition[] newTransitions = Arrays.copyOf(transitions, transitions.length+1);
			newTransitions[transitions.length] = new PDATransition(target, stackTarget, !key.equals('#'));
			values.get(key).put(stackKey, newTransitions);
		}
		else if(!values.containsKey(key)) {
			values.put(key, new HashMap<>());
			values.get(key).put(stackKey, new PDATransition[] {new PDATransition(target, stackTarget, !key.equals('#'))});
		}
		else {
			values.get(key).put(stackKey, new PDATransition[] {new PDATransition(target, stackTarget, !key.equals('#'))});
		}
	}
	
	private static PDATransition[] combineArray(PDATransition[] arr1, PDATransition[] arr2){
		if(arr1 == null) return arr2;
		if(arr2 == null) return arr1;
		else {
			PDATransition[] result = new PDATransition[arr1.length +arr2.length];
			for(int i=0;i<arr1.length;i++) {
				result[i]=arr1[i];
			}
			for(int i=arr1.length;i<result.length;i++) {
				result[i]=arr2[i-arr1.length];
			}
			return result;
		}
		
	}
}

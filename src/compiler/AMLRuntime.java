package compiler;

public class AMLRuntime {
	private models.IState curr;
	private models.IPDAState pdaCurr;
	private models.Automaton automaton;
	private String word;
	private int wordIndex = 0;
	models.Stack<Character> pdaStack = new models.Stack<>();

	public AMLRuntime(models.Automaton automaton, String word) {
		this.word = word;
		this.automaton = automaton;
		pdaStack.push('#');
	}

	public models.RuntimeResponse stepDeterministic() {
		if (wordIndex >= word.length()) {
			switch (automaton.getType()) {
			case DFA:
				return new models.RuntimeResponse(curr, null, true, curr.isEndState());
			case DPDA:
				return new models.RuntimeResponse(pdaCurr, null, true, pdaCurr.isEndState());
			default:
				throw new exception.AMLRuntimeException();
			}
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		switch (automaton.getType()) {
		case DFA:
			return stepDFA(input);
		case DPDA:
			return stepDPDA(input);
		default:
			throw new exception.AMLRuntimeException();
		}
	}

	private models.RuntimeResponse stepDFA(Character input) {
		if (this.curr == null)
			this.curr = automaton.getStart()[0];
		else {
			models.IState[] transitions = this.curr.transition(input);
			if (transitions == null)
				return new models.RuntimeResponse(curr, input, true, false);
			curr = transitions[0];
		}
		return new models.RuntimeResponse(curr, input, false, false);
	}

	private models.RuntimeResponse stepDPDA(Character input) {
		if (this.pdaCurr == null)
			this.pdaCurr = automaton.getPdaStart()[0];
		else {
			models.PDATransition[] transitions = this.pdaCurr.transition(input, pdaStack.pop());
			if (transitions == null)
				return new models.RuntimeResponse(pdaCurr, input, true, false);
			pdaCurr = transitions[0].getTarget();
			pdaStack.push(transitions[0].getStackTarget());
		}
		return new models.RuntimeResponse(pdaCurr, input, false, false);
	}

	public models.IState[] chooseNonDeterministic() {
		// TODO: use less hacky solution than exception
		if (automaton.getType() != models.AutomatonType.NFA)
			throw new exception.AMLRuntimeException();
		if (word.length() <= wordIndex) {
			throw new exception.AMLRuntimeFinishedException(curr.isEndState(), curr);
		}
		Character input = word.charAt(wordIndex);
		if (this.curr == null)
			return automaton.getStart();
		return curr.transition(input);
	}

	public models.IPDAState[] chooseNPDA() {
		if (automaton.getType() != models.AutomatonType.NPDA)
			throw new exception.AMLRuntimeException();
		if (word.length() <= wordIndex) {
			throw new exception.AMLRuntimeFinishedException(pdaCurr.isEndState() || pdaStack.pop() == null, pdaCurr);
		}
		Character input = word.charAt(wordIndex);
		if (this.pdaCurr == null) {
			return automaton.getPdaStart();
		}
		Character stack = pdaStack.pop();
		pdaStack.push(stack);
		models.PDATransition[] transitions = pdaCurr.transition(input, stack);
		if (transitions == null)
			return null;
		return getStatesFromTransitions(transitions);
	}

	public models.RuntimeResponse stepNonDeterministic(models.IState newState) {
		if (automaton.getType() != models.AutomatonType.NFA)
			throw new exception.AMLRuntimeException();
		if (this.curr == null && arrayContains(automaton.getStart(), newState)){
			this.curr = newState;
			return new models.RuntimeResponse(curr, '#', false, false);
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if(this.curr != null && arrayContains(curr.transition(input), newState)) {
			this.curr = newState;
			return new models.RuntimeResponse(curr, input, false, false);
		} else
			throw new exception.AMLRuntimeException();
	}

	public models.RuntimeResponse stepNPDA(models.IPDAState newState) {
		if (automaton.getType() != models.AutomatonType.NPDA)
			throw new exception.AMLRuntimeException();
		if ((this.pdaCurr == null && arrayContains(automaton.getPdaStart(), newState))) {
			this.pdaCurr = newState;
			return new models.RuntimeResponse(pdaCurr, '#', false, false);
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		Character stack = pdaStack.pop();
		if (this.pdaCurr != null
				&& arrayContains(getStatesFromTransitions(pdaCurr.transition(input, stack)), newState)) {
			models.PDATransition[] transitions = pdaCurr.transition(input, stack);
			for (models.PDATransition t : transitions) {
				if (t.getTarget() == newState) {
					pdaStack.push(t.getStackTarget());
					this.pdaCurr = newState;
					return new models.RuntimeResponse(pdaCurr, input, false, false);
				}
			}
		}
		throw new exception.AMLRuntimeException();
	}

	private static <T> boolean arrayContains(T[] array, T object) {
		for (T t : array) {
			if (t == object)
				return true;
		}
		return false;
	}

	public models.IState getCurr() {
		return curr;
	}

	public models.IPDAState getPdaCurr() {
		return pdaCurr;
	}

	public models.Stack<Character> getStack() {
		return pdaStack;
	}

	private static models.IPDAState[] getStatesFromTransitions(models.PDATransition[] transitions) {
		models.IPDAState[] state = new models.IPDAState[transitions.length];
		for (int i = 0; i < transitions.length; i++) {
			state[i] = transitions[i].getTarget();
		}
		return state;
	}
}

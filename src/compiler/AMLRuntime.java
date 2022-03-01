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

	public models.RuntimeResponse<models.IState> stepDFA() {
		if(wordIndex >= word.length()) {
				return new models.RuntimeResponse<models.IState>(curr, null, true, curr.isEndState());
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if (this.curr == null)
			this.curr = automaton.getStart()[0];
		else {
			models.IState[] transitions = this.curr.transition(input);
			if (transitions == null)
				return new models.RuntimeResponse<>(curr, input, true, false);
			curr = transitions[0];
		}
		return new models.RuntimeResponse<>(curr, input, false, false);
	}

	public models.RuntimeResponse<models.IPDAState> stepDPDA() {
		if(wordIndex >= word.length()) {
				return new models.RuntimeResponse<models.IPDAState>(pdaCurr, null, true, pdaCurr.isEndState());
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if (this.pdaCurr == null)
			this.pdaCurr = automaton.getPdaStart()[0];
		else {
			models.PDATransition[] transitions = this.pdaCurr.transition(input, pdaStack.pop());
			if (transitions == null)
				return new models.RuntimeResponse<>(pdaCurr, input, true, false);
			pdaCurr = transitions[0].getTarget();
			pdaStack.push(transitions[0].getStackTarget());
		}
		return new models.RuntimeResponse<>(pdaCurr, input, false, false);
	}

	public models.RuntimeResponse<models.StateChoice<models.IState>>chooseNFA() {
		// TODO: use less hacky solution than exception
		if (automaton.getType() != models.AutomatonType.NFA)
			throw new exception.AMLRuntimeException();
		if (word.length() <= wordIndex) {
			return new models.RuntimeResponse<models.StateChoice<models.IState>>(null, word.charAt(wordIndex), true, curr.isEndState());
		}
		Character input = word.charAt(wordIndex);
		if (this.curr == null)
			return new models.RuntimeResponse<models.StateChoice<models.IState>>(new models.StateChoice<>(automaton.getStart()), input, false, false);
		return new models.RuntimeResponse<models.StateChoice<models.IState>>(new models.StateChoice<>(curr.transition(input)), input, false, false);
	}

	public models.RuntimeResponse<models.StateChoice<models.IPDAState>>chooseNPDA() {
		if (automaton.getType() != models.AutomatonType.NPDA)
			throw new exception.AMLRuntimeException();
		if (word.length() <= wordIndex) {
			return new models.RuntimeResponse<models.StateChoice<models.IPDAState>>(new models.StateChoice<>(null), '#', true, (pdaCurr.isEndState() || pdaStack.pop()==null));
		}
		Character input = word.charAt(wordIndex);
		if (this.pdaCurr == null) {
			return new models.RuntimeResponse<models.StateChoice<models.IPDAState>>(new models.StateChoice<>(automaton.getPdaStart()), '#', false, false);
		}
		Character stack = pdaStack.pop();
		pdaStack.push(stack);
		models.PDATransition[] transitions = pdaCurr.transition(input, stack);
		if (transitions == null)
			return new models.RuntimeResponse<models.StateChoice<models.IPDAState>>(new models.StateChoice<>(null), word.charAt(wordIndex), false, false);
		return new models.RuntimeResponse<models.StateChoice<models.IPDAState>>(new models.StateChoice<>(getStatesFromTransitions(transitions)), word.charAt(wordIndex), false, false);
	}

	public models.RuntimeResponse<models.IState> stepNFA(models.IState newState) {
		if (automaton.getType() != models.AutomatonType.NFA)
			throw new exception.AMLRuntimeException();
		if (this.curr == null && arrayContains(automaton.getStart(), newState)){
			this.curr = newState;
			return new models.RuntimeResponse<>(curr, '#', false, false);
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if(this.curr != null && arrayContains(curr.transition(input), newState)) {
			this.curr = newState;
			return new models.RuntimeResponse<>(curr, input, false, false);
		} else
			throw new exception.AMLRuntimeException();
	}

	public models.RuntimeResponse<models.IPDAState> stepNPDA(models.IPDAState newState) {
		if (automaton.getType() != models.AutomatonType.NPDA)
			throw new exception.AMLRuntimeException();
		if ((this.pdaCurr == null && arrayContains(automaton.getPdaStart(), newState))) {
			this.pdaCurr = newState;
			return new models.RuntimeResponse<>(pdaCurr, '#', false, false);
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
					return new models.RuntimeResponse<>(pdaCurr, input, false, false);
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

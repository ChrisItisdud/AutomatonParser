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

	public models.RuntimeResponse stepDPDA(Character input) {
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
			if (automaton.getType() == models.AutomatonType.NFA)
				throw new exception.AMLRuntimeFinishedException(curr.isEndState(), curr);
			else
				throw new exception.AMLRuntimeFinishedException(curr.isEndState() || pdaStack.pop() == null, curr);
		}
		Character input = word.charAt(wordIndex);
		if (this.curr == null)
			return automaton.getStart();
		return curr.transition(input);
	}

	public models.RuntimeResponse stepNonDeterministic(models.IState newState) {
		if (automaton.getType() != models.AutomatonType.NFA)
			throw new exception.AMLRuntimeException();
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if ((this.curr == null && arrayContains(automaton.getStart(), newState))
				|| (this.curr != null && arrayContains(curr.transition(input), newState))) {
			this.curr = newState;
			return new models.RuntimeResponse(curr, input, false, false);
		} else
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
}

package compiler;

public class AMLRuntime {
	private models.IState curr;
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
			return new models.RuntimeResponse(curr, null, true, curr.isEndState());
		}
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if (automaton.getType() != models.AutomatonType.DFA)
			throw new exception.AMLRuntimeException();
		if (this.curr == null)
			this.curr = automaton.getStart()[0];
		else {
			models.IState[] transitions = this.curr.transition(input);
			if (transitions == null && automaton.getType() == models.AutomatonType.DFA)
				return new models.RuntimeResponse(curr, input, true, false);
			curr = transitions[0];
		}
		return new models.RuntimeResponse(curr, input, false, false);
	}

	public models.IState[] chooseNonDeterministic() {
		// TODO: use less hacky solution than exception
		if (automaton.getType() != models.AutomatonType.NFA)
			throw new exception.AMLRuntimeException();
		if (word.length() <= wordIndex) {
			if (automaton.getType() == models.AutomatonType.NFA)
				throw new exception.AMLRuntimeFinishedException(curr.isEndState());
			else
				throw new exception.AMLRuntimeFinishedException(curr.isEndState() || pdaStack.pop() == null);
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
		if (arrayContains(curr.transition(input), newState)) {
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
}

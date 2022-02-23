package compiler;

public class AMLRuntime {
	private models.IState curr;
	private models.Automaton automaton;
	private String word;
	private int wordIndex = 0;
	public AMLRuntime(models.Automaton automaton, String word) {
		this.word = word;
		this.automaton = automaton;
	}
	public models.IState stepDeterministic() {
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if(automaton.getType()==models.AutomatonType.NFA || automaton.getType()==models.AutomatonType.NPDA)
			throw new exception.AMLRuntimeException();
		if(this.curr==null) this.curr = automaton.getStart()[0];
		else this.curr = curr.transition(input)[0];
		return curr;
	}
	
	public models.IState[] chooseNonDeterministic() {
		Character input = word.charAt(wordIndex);
		if(automaton.getType()==models.AutomatonType.DFA || automaton.getType()==models.AutomatonType.DPDA)
			throw new exception.AMLRuntimeException();
		if(this.curr==null) return automaton.getStart();
		else return curr.transition(input);
	}
	
	public models.IState stepNonDeterministic(models.IState newState) {
		Character input = word.charAt(wordIndex);
		wordIndex++;
		if(arrayContains(curr.transition(input), newState)) {
			this.curr = newState;
			return curr;
		}
		else throw new exception.AMLRuntimeException();
	}
	
	private static <T> boolean arrayContains (T[] array, T object){
		for(T t : array) {
			if(t==object) return true;
		}
		return false;
	}
}

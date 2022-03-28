package compiler;

import exception.AMLRuntimeException;

public class AMLRuntime {
    private models.IState curr;
    private models.IPDAState pdaCurr;
    private models.Automaton automaton;
    private String word;
    private int wordIndex = 0;
    private models.Stack<Character> pdaStack = new models.Stack<>();
    private models.IState[] nfaChoices;
    private boolean[] read;

    public AMLRuntime(models.Automaton automaton, String word) {
        this.word = word;
        this.automaton = automaton;
        pdaStack.push('#');
    }

    public models.RuntimeResponse<models.IState> stepDFA() {
        if (wordIndex >= word.length()) {
            return new models.RuntimeResponse<models.IState>(curr, null, true, curr.isEndState(), word);
        }
        Character input = word.charAt(wordIndex);
        wordIndex++;
        if (this.curr == null)
            this.curr = automaton.getStart()[0];
        else {
            models.IState[] transitions = this.curr.transition(input);
            if (transitions == null)
                return new models.RuntimeResponse<>(curr, input, true, false, word.substring(wordIndex));
            curr = transitions[0];
        }
        return new models.RuntimeResponse<>(curr, input, false, false, word.substring(wordIndex));
    }

    public models.RuntimeResponse<models.IPDAState> stepDPDA() {
        if (wordIndex >= word.length()) {
            return new models.RuntimeResponse<models.IPDAState>(pdaCurr, null, true, pdaCurr.isEndState(), word);
        }
        Character input = word.charAt(wordIndex);
        wordIndex++;
        if (this.pdaCurr == null)
            this.pdaCurr = automaton.getPdaStart()[0];
        else {
            models.PDATransition[] transitions = this.pdaCurr.transition(input, pdaStack.pop());
            if (transitions == null)
                return new models.RuntimeResponse<>(pdaCurr, input, true, false, word.substring(wordIndex));
            pdaCurr = transitions[0].getTarget();
            pdaStack.push(transitions[0].getStackTarget());
        }
        return new models.RuntimeResponse<>(pdaCurr, input, false, false, word.substring(wordIndex));
    }

    // TODO: Allow empty transitions at end of word
    public models.RuntimeResponse<models.StateChoice<models.IState>> chooseNFA() {
        if (automaton.getType() != models.AutomatonType.NFA)
            throw new exception.AMLRuntimeException();
        if (word.length() <= wordIndex) {
            if (curr == null) {
                boolean startStateIsEnd = false;    //to allow empty word
                for (models.IState s : automaton.getStart()) {
                    if (s.isEndState()) {
                        startStateIsEnd = true;
                        break;
                    }
                }
                return new models.RuntimeResponse<>(new models.StateChoice<>(null, null),
                        '#', true, startStateIsEnd, word);
            }
            return new models.RuntimeResponse<>(new models.StateChoice<>(null, null), '#',
                    true, curr.isEndState(), word);
        }
        Character input = word.charAt(wordIndex);
        if (this.curr == null)
            return new models.RuntimeResponse<>(
                    new models.StateChoice<>(automaton.getStart(), null), input, false, false, word.substring(wordIndex));
        models.IState[] fromChar = curr.transition(input);
        models.IState[] fromEmpty = curr.transitionEmpty();
        models.IState[] result = combineArrays(fromChar, fromEmpty);
        boolean[] read = result == null ? null : new boolean[result.length];
        if(read!=null) for (int i = 0; i < fromChar.length; i++) read[i] = true;
        this.nfaChoices = result;
        this.read = read;
        return new models.RuntimeResponse<>(
                new models.StateChoice<>(result, read), input, result==null, false, word.substring(wordIndex));    //result==null so that if no transitions are available, run ends
    }

    public models.RuntimeResponse<models.StateChoice<models.IPDAState>> chooseNPDA() {
        if (automaton.getType() != models.AutomatonType.NPDA)
            throw new exception.AMLRuntimeException();
        if (word.length() <= wordIndex) {
            if (pdaCurr == null) {
                boolean startStateIsEnd = false;
                for (models.IPDAState s : automaton.getPdaStart()) {
                    if (s.isEndState()) {
                        startStateIsEnd = true;
                        break;
                    }
                }
                return new models.RuntimeResponse<>(new models.StateChoice<>(null, null),
                        '#', true, startStateIsEnd, word);
            }
            return new models.RuntimeResponse<>(new models.StateChoice<>(null, null), '#',
                    true, (pdaCurr.isEndState() || pdaStack.pop() == null), word);
        }
        Character input = word.charAt(wordIndex);
        if (this.pdaCurr == null) {
            return new models.RuntimeResponse<>(
                    new models.StateChoice<>(automaton.getPdaStart(), null), '#', false, false, word.substring(wordIndex));
        }
        Character stack = pdaStack.pop();
        pdaStack.push(stack);
        models.PDATransition[] transitions = pdaCurr.transition(input, stack);
        if (transitions == null)
            return new models.RuntimeResponse<>(new models.StateChoice<>(null, null),
                    word.charAt(wordIndex), false, false, word.substring(wordIndex));
        return new models.RuntimeResponse<>(
                new models.StateChoice<>(getStatesFromTransitions(transitions), null), word.charAt(wordIndex), false, false,
                word.substring(wordIndex));
    }

    public models.RuntimeResponse<models.IState> stepNFA(int index) {
        if (automaton.getType() != models.AutomatonType.NFA)
            throw new exception.AMLRuntimeException();
        if (this.curr == null) {
            if (index > automaton.getStart().length) throw new AMLRuntimeException();
            curr = automaton.getStart()[index];
            return new models.RuntimeResponse<>(curr, '#', false, false, word.substring(wordIndex));
        }
        Character input = this.read[index] ? word.charAt(wordIndex) : '#';
        wordIndex++;
        if (index < this.nfaChoices.length) {
            curr = this.nfaChoices[index];
            return new models.RuntimeResponse<>(curr, input, false, false, word.substring(wordIndex));
        } else {
            throw new exception.AMLRuntimeException();
        }
    }

    public models.RuntimeResponse<models.IPDAState> stepNPDA(int index) {
        if (automaton.getType() != models.AutomatonType.NPDA)
            throw new exception.AMLRuntimeException();
        if (this.pdaCurr == null) {
            if (index > automaton.getPdaStart().length) throw new AMLRuntimeException();
            this.pdaCurr = automaton.getPdaStart()[index];
            return new models.RuntimeResponse<>(pdaCurr, '#', false, false, word.substring(wordIndex));
        }
        Character input = word.charAt(wordIndex);
        wordIndex++;
        Character stack = pdaStack.pop();
        models.PDATransition[] transitions = pdaCurr.transition(input, stack);
        if (index < transitions.length) {
            models.PDATransition t = transitions[index];
            if (!t.isRead()) {
                wordIndex--;
                input = '#';
            }
            this.pdaCurr = t.getTarget();
            pdaStack.push(t.getStackTarget());
            return new models.RuntimeResponse<>(pdaCurr, input, false, false, word.substring(wordIndex));
        } else {
            throw new exception.AMLRuntimeException();
        }
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

    private static models.IState[] combineArrays(models.IState[] arr1, models.IState[] arr2) {
        if (arr1 == null) return arr2;
        if (arr2 == null) return arr1;
        models.IState[] result = new models.IState[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i];
        }
        for (int i = arr1.length; i < result.length; i++) {
            result[i] = arr2[i - arr1.length];
        }
        return result;
    }
}

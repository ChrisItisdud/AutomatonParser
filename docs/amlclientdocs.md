# AML CLIENT DOCS

This file contains some information on how to build your own client using the provided AML compiler and runtime files.
You'll need all the files from the "compiler", "exception" and "models" packages for this, however this documentation will only contain information on the classes an AML client will interact with.

## package compiler

### AMLCompiler

The AML compiler is fairly straight-forward as it only has one public function:

``AMLCompiler.parse(String fname)`` attempts to parse the file at the location fname and returns a models.Automaton. If there are syntax errors, an AMLIllegalSyntaxException containing the line at which the error occured and the error type as an exception.AMLSyntaxExceptions type is thrown.

### AMLRuntime

The AML Runtime will receive a models.Automaton and a String upon being created and then allows you to run that automaton to check whether the String is a part of the language described by the automaton.

``AMLRuntime(Automaton automaton, String word)`` returns a new AMLRuntime.

``AMLRuntime.stepDeterministic()`` If the models.Automaton given to the AMLRuntime is deterministic, this steps to the next character in the provided word and steps through the automaton accordingly. It then returns a models.RuntimeResponse with the current state of the automaton. The models.RuntimeResponse's "pdaState" field is null if the automaton is a DFA, the "state" field is null if the automaton is a DPDA. If the models.Automaton is non-deterministic, an AMLRuntimeException is thrown.

``AMLRuntime.chooseNonDeterministic()`` If the models.Automaton given to the AMLRuntime is an NFA, this provides an Array of all the potential transitions from the current state with the current letter as a models.IState[]. The client gets to decide how to choose the most appropriate state to then give to AMLRuntime.stepNonDeterministic(IState newState). If the models.Automaton is not an NFA, an AMLRuntimeException is thrown.

``AMLRuntime.stepNonDeterministic(IState newState)`` If the models.Automaton given to the AMLRuntime is an NFA, this checks whether newState is a valid transition from the automaton's current state and if so, steps to said state and returns an according models.RuntimeResponse.

``AMLRuntime.chooseNPDA()`` like chooseNonDeterministic(), but will return a models.IPDAState[] if the models.Automaton is an NPDA and throw an AMLRuntimeException() otherwise.

``AMLRuntime.stepNPDA(IPDAState newState)`` like stepNonDeterministic(), but for NPDAs.

``AMLRuntime.getCurr()`` returns the current state if the models.Automaton is an NFA or DFA, returns null otherwise.

``AMLRuntime.getPdaCurr()`` returns the current state if the models.Automaton is a PDA, returns null otherwise.

``AMLRuntime.getStack()`` returns the current stack as a models.Stack<Character> if the models.Automaton is a PDA, returns an empty models.Stack<Character> otherwise.

## package exception

### AMLIllegalSyntaxException

This exception is thrown whenever the AMLCompiler detects a syntax error. It contains two fields for the type of exception (as an exception.AMLSyntaxExceptions) and the line the error was found in as an int.

``AMLIllegalSyntaxException.getType()`` returns the type of exception as an exception.AMLSyntaxExceptions.

``AMLIllegalSyntaxException.getLine()`` returns the line the exception was thrown in.

### AMLRuntimeException

This exception is thrown when the AMLRuntime runs into an unexpected error, usually trying to call methods on the wrong kind of automaton. It does not contain any fields or methods.

### AMLRuntimeFinishedException

This exception is sometimes thrown when an AMLRuntime figures it's finished during the chooseNPDA() or chooseNonDeterministic() methods. It contains information on whether the word was a part of the language described by the automaton, the Character the runtime finished on and the models.IState or models.IPDAState the runtime finished on.

``AMLRuntimeFinishedException.isWord()`` returns whether the word was part of the language described by the models.Automaton passed to the runtime as a boolean.

``AMLRuntimeFinishedException.getLetter()`` returns the letter the AMLRuntime finished on, if applicable. Returns null otherwise.

``AMLRuntimeFinishedException.getState()`` returns the models.IState the AMLRuntime finished on, if the models.Automaton passed to the runtime was a DFA or NFA. Returns null otherwise.

``AMLRuntimeFinishedException.getPdaState()`` like getState(), but for PDAs. Returns a models.IPDAState.

### AMLSyntaxExceptions

This Enumerator contains all the possible types of syntax errors:

``ERR_KEY_ALREADY_EXISTS``: The program tried to define two states with the same name, or tried to have two transitions with the same Character or Character/Stack-combination on a deterministic models.Automaton.

``ERR_SECTION_INFO_MISSING``: The SECTION INFO is not defined in the correct place, or not defined at all.

``ERR_SECTION_STATES_MISSING``: The SECTION STATES is not defined in the correct place, or not defined at all.

``ERR_SECTION_TRANSITIONS_MISSING``: The SECTION TRANSITIONS is not defined in the correct place, or not defined at all.

``ERR_UNEXPECTED_IDENTIFIER``: Catch-all error for lines not containing proper keywords.

``ERR_UNKNOWN_TYPE``: The type in the SECTION INFO is not a proper automaton type.

``ERR_UNKNOWN_STATE``: A transition or the "start" or "end" segment in the SECTION STATES tried to use a state that does not exist.

``ERR_ILLEGAL_KEY_LENGTH``: A transition tried to use a key longer than one character.

## package models

### Automaton

### AutomatonType

### IState

### IPDAState

### RuntimeResponse

### Stack
# AML CLIENT DOCS

This file contains some information on how to build your own client using the provided AML compiler and runtime files.
You'll need all the files from the "compiler", "exception" and "models" packages for this, however this documentation will only contain information on the classes an AML client will interact with.

## package compiler

### AMLCompiler

The AML compiler is fairly straight-forward as it only has one public function:

``models.Automaton parse(String fname)`` attempts to parse the file at the location fname and returns a models.Automaton. If there are syntax errors, an AMLIllegalSyntaxException containing the line at which the error occured and the error type as an exception.AMLSyntaxExceptions type is thrown.

### AMLRuntime

The AML Runtime will receive a models.Automaton and a String upon being created and then allows you to run that automaton to check whether the String is a part of the language described by the automaton.

``AMLRuntime(Automaton automaton, String word)`` returns a new AMLRuntime.

``models.RuntimeResponse<models.IState> stepDFA()`` If the models.Automaton given to the AMLRuntime is a DFA, this reads the next character from the word, transitions accordingly and returns an according models.RuntimeResponse<models.IState>. If he models.Automaton is not a DFA, an AMLRuntimeException is thrown.

``models.RuntimeResponse<models.IPDAState> stepDPDA()`` Like AMLRuntime.stepDFA(), but for DPDAs and returns a models.RuntimeResponse<models.IPDAState>.

``models.RuntimeResponse<models.StateChoice<models.IState>> chooseNFA()`` If the models.Automaton given to the AMLRuntime is an NFA, this returns a models.RuntimeResponse<models.StateChoice<models.IState>> containing all the possible states the NFA could transition to. The decision on which of these states to choose and pass to stepNFA() is left up to the client.

``models.RuntimeResponse<models.IState> stepNFA(IState newState)`` If the models.Automaton given to the AMLRuntime is an NFA, this checks whether newState is a valid transition from the automaton's current state and if so, steps to said state and returns an according models.RuntimeResponse<models.IState>. Otherwise, an AMLRuntimeException is thrown.

``models.RuntimeResponse<models.StateChoice<models.IPDAState>> chooseNPDA()`` like chooseNonDeterministic(), but will return a models.RuntimeResponse<models.StateChoice<models.IPDAState>> if the models.Automaton is an NPDA and throw an AMLRuntimeException() otherwise.

``models.RuntimeResponse<models.IPDAState>> stepNPDA(IPDAState newState)`` like stepNonDeterministic(), but for NPDAs. Returns a models.RuntimeResponse<models.IPDAState>.

``models.IState getCurr()`` returns the current state if the models.Automaton is an NFA or DFA, returns null otherwise.

``models.IPDAState getPdaCurr()`` returns the current state if the models.Automaton is a PDA, returns null otherwise.

``models.Stack<Character> getStack()`` returns the current stack as a models.Stack<Character> if the models.Automaton is a PDA, returns an empty models.Stack<Character> otherwise.

## package exception

### AMLIllegalSyntaxException

This exception is thrown whenever the AMLCompiler detects a syntax error. It contains two fields for the type of exception (as an exception.AMLSyntaxExceptions) and the line the error was found in as an int.

``exception.AMLSyntaxExceptions getType()`` returns the type of exception as an exception.AMLSyntaxExceptions.

``int getLine()`` returns the line the exception was thrown at.

### AMLRuntimeException

This exception is thrown when the AMLRuntime runs into an unexpected error, usually trying to call methods on the wrong kind of automaton. It does not contain any fields or methods.

### AMLRuntimeFinishedException

**NO LONGER IN USE.** This exception is sometimes thrown when an AMLRuntime figures it's finished during the chooseNPDA() or chooseNonDeterministic() methods. It contains information on whether the word was a part of the language described by the automaton, the Character the runtime finished on and the models.IState or models.IPDAState the runtime finished on.

``boolean isWord()`` returns whether the word was part of the language described by the models.Automaton passed to the runtime as a boolean.

``Character getLetter()`` returns the letter the AMLRuntime finished on, if applicable. Returns null otherwise.

``models.IState getState()`` returns the models.IState the AMLRuntime finished on, if the models.Automaton passed to the runtime was a DFA or NFA. Returns null otherwise.

``models.IPDAState getPdaState()`` like getState(), but for PDAs. Returns a models.IPDAState.

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

The model class for Automata.

``Automaton(IState[] start, String name, AutomatonType type)`` Constructor for a new Automaton. This constructor should be used for DFAs and NFAs.

``Automaton(IPDAState[] pdaStart, String name, AutomatonType type)`` Constructor for a new Automaton. This constructor should be used for PDAs.

``AutomatonType getType()`` returns the Automaton's type.

``String getName()`` returns the Automaton's name.

``IState[] getStart()`` returns the Automaton's starting states if it's a DFA or NFA.

``IPDAState[] getPdaStart()`` returns the Automaton's starting states if it's a PDA.

### AutomatonType

Simple enumerator containing the possible types of automata.

``DFA``: DFA
``NFA``: NFA
``DPDA``: DPDA
``NPDA``: NPDA

### IState

Interface for DFA and NFA states.

``IState[] transition(Character input)`` returns all the states the state could transition to with the given input. If the state cannot transition to any state, null is returned. A DFA state will always return an array with exactly one entry or null. Not relevant for client development as this is handled by the AMLRuntime.

``boolean isEndState()`` returns true if the state is an end state, returns false otherwise.

``void setEndState(boolean input)`` sets the state's endState field. Not relevant for client development as this is handled by the AMLCompiler.

``String getName()`` returns the state's name.

### IPDAState

``PDATransition[] transition(Character input, Character StackValue)`` returns all transitions possible from the state with the given input and stack value. If the state cannot transition to any state, null is returned. A DPDA state will always return an array with exactly one entry or null. Not relevant for client development as this is handled by the AMLRuntime.

``boolean isEndState()`` returns true if the state is an end state, returns false otherwise.

``void setEndState(boolean input)`` sets the state's endState field. Not relevant for client development as this is handled by the AMLCompiler.

``String getName()`` returns the state's name.

``void addTransition(Character key, Character stackKey, IPDAState target, Character[] stackTarget, int line)`` adds a new transition to the state. If the state is invalid for any reason, an AMLIllegalSyntaxException is thrown. Not relevant for client development as this is handle by the AMLCompiler.

### RuntimeResponse<T>

The responses the client will receive from the AMLRuntime, containing info about the Runtime state.

``RuntimeResponse(T state, Character readChar)`` creates a new RuntimeResponse with the given state and character.

``RuntimeResponse(T state, Character readChar, boolean isFinished, boolean isWord)`` creates a new RuntimeResponse with the provided information.

``Character getChar()`` returns the RuntimeResponse's readChar field, which is usually the char read by the AMLRuntime or '#'.

``T getState()`` returns the RuntimeResponse's state field.

``boolean isWord()`` returns the RuntimeResponse's isWord field.

``boolean isFinished()`` returns the RuntimeResponse's isFinished field.

### Stack<T>

A simple stack data structure, used to store the stack of a PDA.

``void push(T t)`` pushes the given T to the stack. Not relevant for client development as this is handled by the AMLRuntime.

``void push(T[] t)`` pushes all the T in the array to the stack, with t[t.length-1] being pushed first and t[0] being pushed last. Not relevant for client development as this is handled by the AMLRuntime.

``T pop()`` returns the T at the top of the stack. Returns null if the stack is empty. Not relevant for client development as this is handled by the AMLRuntime.

``ArrayList<T> output()`` returns an ArrayList of all the T in the stack, with the first element being the top of the stack.

### StateChoice<T>

A container for an array, as Java doesn't support generics with arrays.

``StateChoice(T[] values)`` creates a new StateChoice. Not relevant for client development as this is handled by the AMLRuntime.

``T[] getValues()`` returns the array stored by the StateChoice.

## Workflow
The client should start by parsing a given document using AMLCompiler.parse(), then create a new AMLRuntime with that automaton and the word it wishes to check. If the automaton is deterministic, simply step through the automaton using stepDFA() or stepDPDA() and provide according info to the user from the RuntimeResponses. If the automaton is non-deterministic, get a RuntimeResponse containing a StateChoice with the possible transition states using chooseNFA()/chooseNPDA(), then handle choosing the correct state and passing it to stepNFA()/stepNPDA().
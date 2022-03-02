# AUTOMATON MARKUP LANGUAGE (AML) SYNTAX

AML is the language used to describe deterministic and non-deterministic finite automata (DFA/NFA) as well as pushdown automata/PDAs (NPDA/DPDA).
These are all essentially state machines, the difference being that a. PDAs make use of a stack to be able to parse type-2 languages and DFAs don't, and b. NFAs and NPDAs are non-deterministic, while DFAs and DPDAs always have one clear path.
An AML program is divided into three sections: The *INFO*, *STATES* and *TRANSITIONS* section.
Each automaton starts out with its type and name info in the *INFO* section, followed by a set of states it contains as well as its start and end states in the *STATES* section, then the transitions between states are defined in the *TRANSITIONS* section.

## Sections

### The INFO section

The *INFO* section is fairly simple:

```
SECTION INFO
name: my_cool_automaton
type: DFA //can alternatively be NFA, DPDA or NPDA
```

The *type* field defines which type of automaton you're programming, which will have them vary in their functionality and syntax. The syntax differences will be explained in the documentation for the other sections, the functionality differences will be roughly outlined in the **Automaton Types** appendix.

### The STATES section

The *STATES* section will look the same for basically every program:

```
SECTION STATES
states: s1, s2 //,...
start: s1
end: s2
```

Firstly all the possible states are defined under "states", then the "start" and "end" fields specify which states your program starts and ends with. Note that only NFAs and NPDAs can have more than one starting state, while DFAs and PDAs always start with the same initial state, as having multiple starting states is non-deterministic behaviour. All automata support having more than one final state, but only NPDAs support having no finishing states, as they can end based on the stack instead.

### The TRANSITIONS section

This is where the automaton's logic is found - in this segment, the transitions between states are defined. The syntax for these varies slightly between automata:

For DFAs and NFAs, you specify the starting state, the character that is read and the state you transition to:

```
SECTION TRANSITIONS
s1, a, s2
s1, b, s3
//starting-state, character-to-read, target-state
```

*Note that AML will only accept single characters.*

For DPDAs and NPDAs, you need to additionally specify the characters read from and pushed to the stack:

```
SECTION TRANSITIONS
s1, #, a, s1, A#
s1, A, b, s2, BB#
s2, B, b, s2, -	//- will push nothing to the stack
//starting-state, character-on-stack, character-to-read, target-state, characters-for-stack
```

*Note that  at the start of the program, the stack will consist of a single #. You can only read one character from the stack per transition, but are allowed to push multiple characters to the stack, the first letter being the new top of the stack. A NPDA is allowed to end as soon as its stack is empty, while a DPDA is only allowed to end on an end state independently of the stack*

*NOTE: The character '#' has special significance for NFAs and NPDAs, as it will count as a transition that doesn't involve reading a letter. This functionality is deactivated for DFAs and DPDAs as it can lead to non-deterministic behaviour. THIS IS NOT YET IMPLEMENTED.*

## Running an AML Program

Now that you have the syntax down, you can simply run an AML program using the command line client provided in this repository. As of current, it only has three commands:

``parse FNAME`` will try and parse an automaton from a given text file.

``check WORD`` will run the provided word through the automaton and check whether it is compatible, giving you information on the states it travels through at each point.

``quit`` closes the command line client.

## Syntax Errors

Attempting to compile an invalid AML program will throw one of the following errors:

``ERR_KEY_ALREADY_EXISTS``: The program tried to define two states with the same name, or tried to have two transitions with the same Character or Character/Stack-combination on a DFA or DPDA.

``ERR_SECTION_INFO_MISSING``: The SECTION INFO is not defined in the correct place, or not defined at all.

``ERR_SECTION_STATES_MISSING``: The SECTION STATES is not defined in the correct place, or not defined at all.

``ERR_SECTION_TRANSITIONS_MISSING``: The SECTION TRANSITIONS is not defined in the correct place, or not defined at all.

``ERR_UNEXPECTED_IDENTIFIER``: Catch-all error for lines not containing proper keywords.

``ERR_UNKNOWN_TYPE``: The type specified in the SECTION INFO is not a proper automaton type.

``ERR_UNKNOWN_STATE``: A transition or the "start" or "end" segment in the SECTION STATES tried to use a state that does not exist.

## APPENDIX: Automaton Types

AML supports four different types of automata. All of these are finite state machines, but vary slightly in what they do or don't do:

**DFA**: A DFA (Deterministic Finite Automaton) has a set of states and a set of transitions triggered upon reading a letter (or a terminal in proper theoretical CS speak). It will always start at the same state and transition to a different one after reading each terminal from a word. As it is deterministic, there can always only be either one or no valid transition from any state - so for example, there cannot be a situation where a state s1 could transition to either a state s2 or a state s3 when reading the terminal 'a'. If the state the automaton lands on after going through the entire word is an end state, the word is accepted as part of the language defined by that DFA. If at any point during the process of going through the word there is no valid transition or if the automaton doesn't end on an end state, the word is not accepted.

**NFA**: A NFA (Non-deterministic Finite Automaton) functions like a DFA, but allows for non-deterministic behaviour, i.e. having several possible valid transitions at a given point - The example above would be valid for an NFA for instance. In a case like this, the user would have to 'guess' which transition is the correct one.

**DPDA**: A DPDA (Deterministic Push-Down Automaton) functions similarly to a DFA, but adds a stack that the automaton pushes values to/reads them from during transitions. Each transition pops the top character from the stack and pushes any amount of characters to them. The example above could work like this, too - if the DPDA is at a state s1, reads an 'a' and the top character on the stack is an 'A', it could move to s2, if the top character on the stack is a 'B', it could move to s3. These transitions need to still be deterministic though - for the same terminal read and the same character on the stack, there can only be 0 or 1 valid transitions from any state.

**NPDA**: A NPDA (Non-deterministic Push-Down Automaton) functions like a DPDA, but allows for non-deterministic behaviour, which is explained above.

DFA and NFA can only be used to parse regular/type-3 languages, while DPDA and NPDA can be used to parse context-free/type-2 languages too. The deterministic and non-deterministic versions of these automata are equally powerful, but you might find the non-deterministic versions to be easier to write.
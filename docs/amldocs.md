# AUTOMATON MARKUP LANGUAGE (AML) SYNTAX

AML is the language used to describe deterministic and non-deterministic finite automata (DFA/NFA) as well as pushdown automata/PDAs (NPDA/DPDA).
An AML program is divided into three sections: The *INFO*, *STATES* and *TRANSITIONS* section.
Each automaton starts out with its type and name info in the *INFO* section, followed by a set of states it contains as well as its start and end states in the *STATES* section, then the transitions between states are defined in the *TRANSITIONS* section.
Note that while the following examples will include comments marked with //, the language does not yet support comments.

## The INFO section

The *INFO* section is fairly simple:

```
SECTION INFO
name: my_cool_automaton
type: DFA //can alternatively be NFA, DPDA or NPDA
```

The *type* field defines which type of automaton you're programming, which will have them vary in their functionality and syntax (this is explained later).

## The STATES section

The *STATES* section will look the same for basically every program:

```
SECTION STATES
states: s1, s2 //,...
start: s1
fin: s2
```

Firstly all the possible states are defined under "states", then the "start" and "fin" fields specify which states your program starts and ends with. Note that only NFAs and NPDAs can have more than one starting state, while DFAs and PDAs always start with the same initial state. All automatons support having more than one final state.

## The TRANSITIONS section

This is where the automaton's logic is found - in this segment, the transitions between states are defined. The syntax for these varies slightly between automata:

For DFAs and NFAs, you specify the starting state, the character that is read and the state you transition to:

```
SECTION TRANSITIONS
s1, a, s2
s1, b, s3
```

*Note that AML will only accept single characters.*

For DPDAs and NPDAs, you need to additionally specify the characters read from and pushed to the stack:

```
SECTION TRANSITIONS
s1, #, a, s1, A#
s1, A, b, s2, BB#
s2, B, b, s2, -	//- will push nothing to the stack
```

*Note that  at the start of the program, the stack will consist of a single #. You can only read one character from the stack per transition, but are allowed to push multiple characters to the stack, the first letter being the new top of the stack. A NPDA is allowed to end as soon as its stack is empty, while a DPDA is only allowed to end on an end state independently of the stack*

For both NFAs and NPDAs, the # character carries special significance as it allows for transitioning to a different state without reading a character. This is disabled for DFAs and DPDAs as it can lead to non-deterministic behaviour.
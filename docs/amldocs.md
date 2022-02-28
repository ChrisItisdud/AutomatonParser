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

The *type* field defines which type of automaton you're programming, which will have them vary in their functionality and syntax (this is explained later).

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

For both NFAs and NPDAs, the # character carries special significance as it allows for transitioning to a different state without reading a character. This is disabled for DFAs and DPDAs as it can lead to non-deterministic behaviour. *NOT YET SUPPORTED; WIP*

## Running an AML Program

Now that you have the syntax down, you can simply run an AML program using the command line client provided in this repository. As of current, it only has three commands:

``parse FNAME`` will try and parse an automaton from a given text file. As of current, proper error messages for invalid syntax are not supported.

``check WORD`` will run the provided word through the automaton and check whether it is compatible, giving you information on the states it travels through at each point.

``quit`` closes the command line client.
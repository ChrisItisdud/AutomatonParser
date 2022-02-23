package compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import models.AutomatonType;

public class AMLCompiler {

	public static models.Automaton parse(String fname) {
		try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
			// Start section info
			String line = br.readLine();
			if (!line.equals("SECTION INFO"))
				throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_SECTION_START_MISSING);
			// grab automaton name
			line = br.readLine();
			if (!line.startsWith("name: "))
				throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNEXPECTED_IDENTIFIER);
			String name = line.substring(6);
			// grab automaton type
			line = br.readLine();
			if (!line.startsWith("type: "))
				throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNEXPECTED_IDENTIFIER);
			String typeString = line.substring(6);
			AutomatonType type = null;
			switch (typeString) {
			case "DFA":
				type = AutomatonType.DFA;
				break;
			case "NFA":
				type = AutomatonType.NFA;
				break;
			case "NPDA":
				type = AutomatonType.NPDA;
				break;
			case "DPDA":
				type = AutomatonType.DPDA;
				break;
			default:
				throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNKNOWN_TYPE);
			}
			if (type == AutomatonType.NFA || type == AutomatonType.DFA) {
				// start section states
				line = br.readLine();
				if (!line.equals("SECTION STATES"))
					throw new exception.AMLIllegalSyntaxException(
							exception.AMLSyntaxExceptions.ERR_SECTION_STATES_MISSING);
				// grab states
				// TODO: Check for duplicate state names
				line = br.readLine();
				if (!line.startsWith("states: "))
					throw new exception.AMLIllegalSyntaxException(
							exception.AMLSyntaxExceptions.ERR_UNEXPECTED_IDENTIFIER);
				String[] statesString = line.substring(8).split(", ");
				HashMap<String, models.IState> states = new HashMap<>();
				for (String s : statesString) {
					if (states.containsKey(s))
						throw new exception.AMLIllegalSyntaxException(
								exception.AMLSyntaxExceptions.ERR_KEY_ALREADY_EXISTS);
					switch (type) {
					case DFA:
						states.put(s, new models.DFAState(s));
						break;
					case NFA:
						states.put(s, new models.NFAState(s));
						break;
					default:
						throw new RuntimeException("Not Implemented Yet!");
					}
				}
				// grab start state(s)
				line = br.readLine();
				ArrayList<models.IState> startStates = new ArrayList<>();
				if (!line.startsWith("start: "))
					throw new exception.AMLIllegalSyntaxException(
							exception.AMLSyntaxExceptions.ERR_UNEXPECTED_IDENTIFIER);
				if (type == AutomatonType.NFA || type == AutomatonType.NPDA) {
					String[] startsString = line.substring(7).split(", ");
					for (String s : startsString) {
						if (!states.containsKey(s))
							throw new exception.AMLIllegalSyntaxException(
									exception.AMLSyntaxExceptions.ERR_UNKNOWN_STATE);
						startStates.add(states.get(s));
					}
				} else {
					String startsString = line.substring(7);
					if (!states.containsKey(startsString))
						throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNKNOWN_STATE);
					startStates.add(states.get(startsString));
				}
				// grab end states
				line = br.readLine();
				if (!line.startsWith("fin: "))
					throw new exception.AMLIllegalSyntaxException(
							exception.AMLSyntaxExceptions.ERR_UNEXPECTED_IDENTIFIER);
				String[] endsString = line.substring(5).split(", ");
				for (String s : endsString) {
					if (!states.containsKey(s))
						throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNKNOWN_STATE);
					states.get(s).setEndState(true);
				}
				// start section transitions
				line = br.readLine();
				if (!line.equals("SECTION TRANSITIONS"))
					throw new exception.AMLIllegalSyntaxException(
							exception.AMLSyntaxExceptions.ERR_SECTION_TRANSITIONS_MISSING);
				// TODO: validate transitions before parsing
				// parse transitions
				line = br.readLine();
				while (line != null) {
					String[] transString = line.split(", ");
					if(transString.length!=3) throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNEXPECTED_IDENTIFIER);
					if(transString[1].length()!=1) throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_ILLEGAL_KEY_LENGTH);
					if (!states.containsKey(transString[0]) || !states.containsKey(transString[2]))
						throw new exception.AMLIllegalSyntaxException(exception.AMLSyntaxExceptions.ERR_UNKNOWN_STATE);
					switch (type) {
					case DFA:
						models.DFAState state = (models.DFAState) states.get(transString[0]);
						state.addTransition(transString[1].charAt(0), states.get(transString[2]));
						break;
					case NFA:
						models.NFAState nfastate = (models.NFAState) states.get(transString[0]);
						nfastate.addTransition(transString[1].charAt(0), states.get(transString[2]));
						break;
					default:
						break;
					}

					line = br.readLine();
				}
				models.IState[] starts = new models.IState[startStates.size()];
				int i = 0;
				for (models.IState s : startStates) {
					starts[i] = s;
					i++;
				}
				return new models.Automaton(starts, name, type);
			} else {
				//TODO: Implement compiler logic for DPDAs and NPDAs
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException("Something went wrong while reading the file");
		}
	}
}

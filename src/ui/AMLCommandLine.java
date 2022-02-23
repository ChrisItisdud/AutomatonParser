package ui;

import java.util.Scanner;

import compiler.AMLCompiler;

//Test command line client.
public class AMLCommandLine {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		models.Automaton automaton = null;
		while (true) {
			String command = scanner.nextLine();
			if (command.startsWith("quit")) {
				System.out.println("quitting...");
				break;
			} else if (command.startsWith("parse")) {
				automaton = AMLCompiler.parse(command.split(" ")[1]);
			} else if (command.startsWith("check")) {
				if (automaton == null)
					System.out.println("Error: no automaton defined yet!");
				else {
					compiler.AMLRuntime runtime = new compiler.AMLRuntime(automaton, command.substring(6));
					if (automaton.getType() == models.AutomatonType.DFA
							|| automaton.getType() == models.AutomatonType.DPDA) {
						models.RuntimeResponse state = null;
						do {
							state = runtime.stepDeterministic();
							System.out.println(
									"Read: " + state.getChar() + ", Now entering state " + state.getState().getName());
						} while (!state.isFinished());
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (state.getChar() != null) {
							System.out.println("Failure: State " + state.getState().getName()
									+ " doesn't accept letter " + state.getChar() + ". Word is not part of language.");
						} else {
							System.out.println("The given word " + command.substring(6) + " is part of language!");
						}
					}
				}
			}
		}

		scanner.close();
	}
}

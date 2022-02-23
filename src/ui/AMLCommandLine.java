package ui;

import java.util.Scanner;

import compiler.AMLCompiler;
import models.RuntimeResponse;

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
			}
			else if (command.startsWith("check")) {
				if (automaton == null)
					System.out.println("Error: no automaton defined yet!");
				else {
					compiler.AMLRuntime runtime = new compiler.AMLRuntime(automaton, command.substring(6));
					if (automaton.getType() == models.AutomatonType.DFA) {
						models.RuntimeResponse state = null;
						do {
							state = runtime.stepDeterministic();
							System.out.println(
									"Read: " + state.getChar() + ", Now entering state " + state.getState().getName());
							scanner.nextLine();
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
					} else if (automaton.getType() == models.AutomatonType.NFA) {
						models.RuntimeResponse state = null;
						try {
							do {
								models.IState[] options = runtime.chooseNonDeterministic();
								if (options == null) {
									state = new models.RuntimeResponse(runtime.getCurr(), '#', true, false);
								} else if (options.length == 1) {
									state = runtime.stepNonDeterministic(options[0]);
								} else {
									System.out.println("Please choose the most appropriate option:");
									int i = 0;
									for (models.IState s : options) {
										System.out.println(i + ": " + s.getName());
										i++;
									}
									int index = -1;
									do {
										index = -1;
										try {
											index = scanner.nextInt();
											scanner.nextLine();
										} catch (Exception e) {
											System.out.println("Invalid number, please enter valid number!");
										}
									} while (index == -1 || index >= options.length);
									state = runtime.stepNonDeterministic(options[index]);
								}
								System.out.println("Read: " + state.getChar() + ", Now entering state "
										+ state.getState().getName());
								scanner.nextLine();
							} while (!state.isFinished());
						} catch (exception.AMLRuntimeFinishedException e) {
							state = new RuntimeResponse(e.getState(), e.getLetter(), true, e.isWord());
						}
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

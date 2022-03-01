package ui;

import java.util.List;
import java.util.Scanner;

import compiler.AMLCompiler;
import models.RuntimeResponse;

//Test command line client.
public class AMLCommandLine {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		models.Automaton automaton = null;
		while (true) {
			// get command
			String command = scanner.nextLine();
			// "quit" command
			if (command.startsWith("quit")) {
				System.out.println("quitting...");
				break;
			}
			// "parse" command
			else if (command.startsWith("parse")) {
				try {
					automaton = AMLCompiler.parse(command.split(" ")[1]);
				} catch (exception.AMLIllegalSyntaxException e) {
					System.out.println("Syntax Error: Error " + e.getType() + " in line " + e.getLine() + ".");
					continue;
				} catch (Exception e) {
					System.out.println(
							"Something went wrong while reading the file. Please check filename spelling and try again.");
					continue;
				}
				System.out.println("Successfully parsed "+command.split(" ")[1]);
			}
			// "check" command
			else if (command.startsWith("check")) {
				if (automaton == null)
					System.out.println("Error: no automaton defined yet!");
				else {
					compiler.AMLRuntime runtime = new compiler.AMLRuntime(automaton, command.substring(6));
					// LOGIC FOR DFAs
					if (automaton.getType() == models.AutomatonType.DFA) {
						// Step through word
						models.RuntimeResponse<models.IState> state = null;
						do {
							state = runtime.stepDeterministic();
							System.out.println(
									"Read: " + state.getChar() + ", Now entering state " + state.getState().getName());
							scanner.nextLine();
						} while (!state.isFinished());
						// output result
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (!state.isWord() && state.getChar() != null) {
							System.out.println("Failure: State " + state.getState().getName()
									+ " doesn't accept letter " + state.getChar() + ". Word is not part of language.");
						} else {
							System.out.println("The given word " + command.substring(6) + " is part of language!");
						}
						// LOGIC FOR NFAs
					} else if (automaton.getType() == models.AutomatonType.NFA) {
						models.RuntimeResponse<models.IState> state = null;
						models.RuntimeResponse<models.StateChoice<models.IState>> options = null;
						do {
							// Check options for next state
							options = runtime.chooseNonDeterministic();
							models.IState[] optArr = options.getState().getValues();
							if (options.isFinished()) {
								state = new models.RuntimeResponse<models.IState>(runtime.getCurr(), '#', true,
										options.isWord());
							} else if (optArr == null) { // ==failure
								state = new models.RuntimeResponse<>(runtime.getCurr(), '#', true, false);
							} else if (optArr.length == 1) { // ==deterministic clear path
								state = runtime.stepNonDeterministic(optArr[0]);
							} else { // ==non-deterministic - let user decide
								System.out.println("Please choose the most appropriate option:");
								int i = 0;
								for (models.IState s : optArr) {
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
								} while (index == -1 || index >= optArr.length);
								state = runtime.stepNonDeterministic(optArr[index]);
							}
							System.out.println(
									"Read: " + state.getChar() + ", Now entering state " + state.getState().getName());
							scanner.nextLine();
						} while (!state.isFinished());
						// output result
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (!state.isWord() && state.getChar() != null) {
							System.out.println("Failure: State " + state.getState().getName()
									+ " doesn't accept letter " + state.getChar() + ". Word is not part of language.");
						} else {
							System.out.println("The given word " + command.substring(6) + " is part of language!");
						}
					}
					// LOGIC FOR DPDAS
					else if (automaton.getType() == models.AutomatonType.DPDA) {
						// Step through word
						models.RuntimeResponse<models.IPDAState> state = null;
						do {
							state = runtime.stepDeterministic();
							List<Character> stack = runtime.getStack().output();
							System.out.print("Read: " + state.getChar() + ", Now entering state "
									+ state.getState().getName() + ". Stack: ");
							for (Character c : stack) {
								System.out.print(c);
							}
							System.out.println();
							scanner.nextLine();
						} while (!state.isFinished());
						// output result
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (!state.isWord() && state.getChar() != null) {
							System.out.println("Failure: State " + state.getState().getName()
									+ " doesn't accept letter " + state.getChar() + ". Word is not part of language.");
						} else {
							System.out.println("The given word " + command.substring(6) + " is part of language!");
						}
					}
					// LOGIC FOR NPDAS
					else {
						models.RuntimeResponse<models.IPDAState> state = null;
						models.RuntimeResponse<models.StateChoice<models.IPDAState>> options = null;
						do {
							// Check options for next state
							options = runtime.chooseNPDA();
							models.IPDAState[] optArr = options.getState().getValues();
							if (options.isFinished()) {
								state = new models.RuntimeResponse<>(runtime.getPdaCurr(), '#', true,
										options.isWord());
								break;
							} else if (optArr == null) { // ==failure
								state = new models.RuntimeResponse<>(runtime.getPdaCurr(), '#', true, false);
							} else if (optArr.length == 1) { // ==deterministic clear path
								state = runtime.stepNPDA(optArr[0]);
							} else { // ==non-deterministic - let user decide
								System.out.println("Please choose the most appropriate option:");
								int i = 0;
								for (models.IPDAState s : optArr) {
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
								} while (index == -1 || index >= optArr.length);
								state = runtime.stepNPDA(optArr[index]);
							}
							List<Character> stack = runtime.getStack().output();
							System.out.print("Read: " + state.getChar() + ", Now entering state "
									+ state.getState().getName() + ". Stack: ");
							for (Character c : stack) {
								System.out.print(c);
							}
							System.out.println();
							scanner.nextLine();
						} while (!state.isFinished());
						// output result
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (!state.isWord() && state.getChar() != null) {
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

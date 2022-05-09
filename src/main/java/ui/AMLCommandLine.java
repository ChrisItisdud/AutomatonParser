package ui;

import compiler.AMLRuntime;
import models.IPDAState;
import models.RuntimeResponse;

import java.util.List;
import java.util.Scanner;

//Test command line client.
public class AMLCommandLine {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		models.Automaton automaton = null;
		while (true) {
			System.out.println("Enter command (parse <FILE>, check <WORD>, quit)");
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
					automaton = compiler.AMLCompiler.parse(command.split(" ")[1]);
				} catch (exception.AMLIllegalSyntaxException e) {
					System.out.println("Syntax Error: Error " + e.getType() + " in line " + e.getLine() + ".");
					continue;
				} catch (Exception e) {
					System.out.println(
							"Something went wrong while reading the file. Please check filename spelling and try again.");
					continue;
				}
				System.out.println("Successfully parsed " + command.split(" ")[1]);
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
							state = runtime.stepDFA();
							if (state.isFinished())
								break;
							System.out.println("Read: " + state.getChar() + ", Now entering state "
									+ state.getState().getName() + ". Remaining word: " + state.getWord());
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
							options = runtime.chooseNFA();
							models.IState[] optArr = options.getState().getValues();
							if (options.isFinished()) {
								state = new models.RuntimeResponse<>(runtime.getCurr(), '#', true,
										options.isWord(), command.substring(6));
							} else if (optArr == null) { // ==failure
								state = new models.RuntimeResponse<>(runtime.getCurr(), '#', true, false,
										command.substring(6));
							} else if (optArr.length == 1) { // ==deterministic clear path
								state = runtime.stepNFA(0);
							} else { // ==non-deterministic - let user decide
								System.out.println("Please choose the most appropriate option for the character "
										+ options.getChar() + " and the remaining word " + options.getWord() + ":");
								int i = 0;
								for (models.IState s : optArr) {
									System.out.println(i + ": " + s.getName() + (options.getState().getRead()[i] ? ", reading" : ", not reading"));
									i++;
								}
								int index;
								do {
									index = -1;
									try {
										index = scanner.nextInt();
										scanner.nextLine();
									} catch (Exception e) {
										System.out.println("Invalid number, please enter valid number!");
									}
								} while (index == -1 || index >= optArr.length);
								state = runtime.stepNFA(index);
							}
							if (!state.isFinished()) {
								System.out.println("Read: " + state.getChar() + ", Now entering state "
										+ state.getState().getName() + ". Remaining word: " + state.getWord());
								scanner.nextLine();
							}
						} while (!state.isFinished());
						// output result
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (!state.isWord() && state.getChar() != null) {
							System.out.println("Failure: State " + (state.getState() == null ? "null" : state.getState().getName())
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
							state = runtime.stepDPDA();
							if (state.isFinished())
								break;
							printStack(scanner, runtime, state);
						} while (!state.isFinished());
						// output result
						if (!state.isWord() && state.getChar() == null) {
							System.out.println("Failure: Word ended on non-final state " + state.getState().getName()
									+ ". Word is not part of language.");
						} else if (!state.isWord() && state.getChar() != null) {
							System.out.println("Failure: State " + state.getState() == null ? "null" : state.getState().getName()
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
								state = new models.RuntimeResponse<>(runtime.getPdaCurr(), '#', true, options.isWord(),
										command.substring(6));
								break;
							} else if (optArr == null) { // ==failure
								state = new models.RuntimeResponse<>(runtime.getPdaCurr(), '#', true, false,
										command.substring(6));
							} else if (optArr.length == 1) { // ==deterministic clear path
								state = runtime.stepNPDA(0);
							} else { // ==non-deterministic - let user decide
								System.out.println("Please choose the most appropriate option for the character "
										+ options.getChar() + " and the remaining word " + options.getWord() + ":");
								int i = 0;
								for (models.IPDAState s : optArr) {
									System.out.println(i + ": " + s.getName() + (options.getState().getRead()[i] ? ", reads letter" : ", not reading"));
									i++;
								}
								int index;
								do {
									index = -1;
									try {
										index = scanner.nextInt();
										scanner.nextLine();
									} catch (Exception e) {
										System.out.println("Invalid number, please enter valid number!");
									}
								} while (index == -1 || index >= optArr.length);
								state = runtime.stepNPDA(index);
							}
							if (!state.isFinished()) {
								printStack(scanner, runtime, state);
							}
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

	private static void printStack(Scanner scanner, AMLRuntime runtime, RuntimeResponse<IPDAState> state) {
		List<Character> stack = runtime.getStack().output();
		System.out.print("Read: " + state.getChar() + ", Now entering state "
				+ state.getState().getName() + ". Remaining word:" + state.getWord()
				+ ". Stack: ");
		for (Character c : stack) {
			System.out.print(c);
		}
		System.out.println();
		scanner.nextLine();
	}
}

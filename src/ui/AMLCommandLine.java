package ui;

import java.util.Scanner;

import compiler.AMLCompiler;

public class AMLCommandLine {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		models.Automaton automaton = null;
		while(true) {
			String command = scanner.nextLine();
			if(command.startsWith("quit")) {
				System.out.println("quitting...");
				break;
			}
			else if(command.startsWith("parse")) {
				automaton = AMLCompiler.parse(command.split(" ")[1]);
			}
			else if(command.startsWith("check")) {
				if(automaton == null) System.out.println("Error: no automaton defined yet!");
			}
		}
		
		scanner.close();
	}
}

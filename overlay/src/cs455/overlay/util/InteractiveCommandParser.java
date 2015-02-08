package cs455.overlay.util;

import java.util.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class InteractiveCommandParser {

	String usage;
	Map<String, String[]> commands;

	public InteractiveCommandParser(String usage) {
		this.usage = usage;
		commands = new HashMap<String, String[]>();
	}

	public void addCommand(String command, String[] args) {
		commands.put(command, args);
	}

	public String[] receiveCommand() {
		Scanner input = new Scanner(System.in);
		String[] command = input.nextLine().split("\\s");
		if (command.length < 1 || command[0].equalsIgnoreCase("help")) {
			showUsage();
			return null;
		}
		if (commands.containsKey(command[0]) && command.length -1 == commands.get(command[0]).length)
			return command;

		System.err.print("Unknown command:");
		for(String s: command) {
			System.err.print(" " +s);
		}
		System.err.println();
		return null;
	}

	public void showUsage() {
		System.out.println(usage);
		System.out.println("Available Commands:");
		for(String com: commands.keySet()) {
			System.out.print("    " + com);
			for(String arg: commands.get(com)) {
				System.out.print(" <" +arg +">");
			}
			System.out.println();
		}
		System.out.println();
	}
}

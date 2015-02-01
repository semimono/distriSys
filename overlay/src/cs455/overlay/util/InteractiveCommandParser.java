package cs455.overlay.util;

import java.util.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class InteractiveCommandParser {

	String usage;
	Set<String> commands;

	public InteractiveCommandParser(String usage) {
		this.usage = usage;
		commands = new HashSet<String>();
	}

	public void addCommand(String command) {
		commands.add(command);
	}

	public String receiveCommand() {
		Scanner input = new Scanner(System.in);
		String command = input.nextLine().trim();
		if (commands.contains(command))
			return command;
		System.err.println("Unknown command: " +command);
	}
}

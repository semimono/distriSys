package cs455.overlay.util;

import org.omg.CORBA.COMM_FAILURE;

import java.util.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class InteractiveCommandParser {

	String usage;
	List<Command> commands;
	Map<String, Command> commandMap;

	public InteractiveCommandParser(String usage) {
		this.usage = usage;
		commands = new LinkedList<Command>();
		commandMap = new HashMap<String, Command>();
	}

	public void addCommand(String command) {
		addCommand(command, null, null);
	}
	public void addCommand(String command, String shortcut) {
		addCommand(command, shortcut, null);
	}
	public void addCommand(String command, String[] parameters) {
		addCommand(command, null, parameters);
	}
	public void addCommand(String command, String shortcut, String[] args) {
		commands.add(new Command(this, command, shortcut, args));
	}

	public String[] receiveCommand() {
		Scanner input = new Scanner(System.in);
		String fullCommand = input.nextLine();
		String[] command = fullCommand.split("\\s");
		if (command[0].length() < 1 || command[0].equals("help") || command[0].equals("h")) {
			showUsage();
			return null;
		}
		Command com = commandMap.get(command[0]);
		if (com == null) {
			System.err.println("Unknown command: " + fullCommand);
			return null;
		}
		command[0] = com.name;
		if (command.length -1 != com.parameters.length) {
			System.err.println("Wrong number of parameters for '" +com.name +"': " +fullCommand);
			return null;
		}

		return command;
	}

	public void showUsage() {
		System.out.println(usage);
		System.out.println("Available Commands:");
		for(Command com: commands) {
			System.out.println("\t" +com);
		}
	}

	private class Command {

		public String name, shortcut;
		public String[] parameters;

		public Command(InteractiveCommandParser parser, String name, String shortcut, String[] parameters) {
			this.name = name;
			parser.commandMap.put(name, this);

			if (parameters == null || parameters.length < 1)
				this.parameters = new String[0];
			else
				this.parameters = parameters;

			if (shortcut == null || shortcut.length() < 1) {
				this.shortcut = null;
			} else {
				this.shortcut = shortcut;
				parser.commandMap.put(shortcut, this);
			}
		}

		@Override
		public String toString() {
			String out = name;
			if (shortcut != null)
				out += " (" +shortcut +")";
			for(String param: parameters) {
				out += " <" +param +">";
			}
			return out;
		}
	}
}

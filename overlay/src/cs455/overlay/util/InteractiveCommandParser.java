package cs455.overlay.util;

import java.util.Scanner;

/**
 * Created by Cullen on 1/25/2015.
 */
public class InteractiveCommandParser {

	String usage;

	public InteractiveCommandParser(String usage) {
		this.usage = usage;
	}

	public String receiveCommand() {
		Scanner input = new Scanner(System.in);
		String command = input.nextLine();
	}
}

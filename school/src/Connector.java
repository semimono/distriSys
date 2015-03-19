import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by Cullen on 3/17/2015.
 */
public class Connector {
	
	private static Connection con;


    public static void connect(String url, String user, String password) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
		con = DriverManager.getConnection(url, props);
    }
	
	public static void addStudent(String[] command) {
		if (command.length != 5) {
			improperFormat();
			return;
		}
		try {
		int ID = Integer.parseInt(command[1]);
		} catch (NumberFormatException e) {
			improperFormat();
			return;
		}
		String firstName = command[2];
		String lastName = command[3];
		String degree = command[4];
		
	}

	public static void showHelp() {
		System.out.println("Commands:");
		System.out.println("\tas <ID> <first name> <last name> <degree> - adds a student");
		System.out.println("\tab <ISBN> <name> <year> <copies> - adds a book");
		System.out.println("\trs <ID> - removes a student");
		System.out.println("\trb <ISBN> - removes a book");
		System.out.println("\tib <ISBN> <ID> - issues a book to a student");
		System.out.println("\tq - quit");
		System.out.println();
	}
	
	public static void improperFormat() {
		System.out.println("Improper format for command. Enter 'h' for usage.");
	}

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
		
		// initialization
        String host = "faure.cs.colostate.edu";
        int port = 5432;
        String schema = "semimono";
		Class.forName("org.postgresql.Driver");
        connect("jdbc:postgresql://" +host +":" +port +"/" +schema, "semimono", "829904489");
		
		
		// interaction
		boolean running = true;
		Scanner in = new Scanner(System.in);
		do{
			System.out.print("Enter a command: ");
			String[] command = in.nextLine().split("\\s+");
			switch(command[0]) {
				case "as":
					addStudent(command);
					break;
				case "ab":
					
					break;
				case "rs":
					
					break;
				case "rb":
					
					break;
				case "ib":
					
					break;
				case "q":
					running = false;
					break;
				case "h":
					showHelp();
					break;
				default:
					System.out.println("Unknown command '" +command[0] +"'. Enter 'h' for usage.");
			}
		} while(running);
		
		con.close();
    }
}

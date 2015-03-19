import java.sql.*;
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
        int id;
		try {
            id = Integer.parseInt(command[1]);
		} catch (NumberFormatException e) {
			improperFormat();
			return;
		}
		String firstName = command[2];
		String lastName = command[3];
		String degree = command[4];
		try {
			Statement stmt = con.createStatement();
			if (!stmt.executeQuery("SELECT * FROM students WHERE StudentID = " +id).next()) {
				System.out.println("Student with ID " +id +" already exists; cannot overwrite.");
				return;
			}
            stmt.executeUpdate("INSERT INTO students (StudentID, FName, LName, Degree) values ("
				+id +",'" +firstName +"','" +lastName +"','" +degree +"')");
			System.out.println("Added student " + id + ".");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static void addBook(String[] command) {
		if (command.length != 5) {
			improperFormat();
			return;
		}
		int isbn, year, copies;
		try {
			isbn = Integer.parseInt(command[1]);
			year = Integer.parseInt(command[3]);
			copies = Integer.parseInt(command[4]);
		} catch (NumberFormatException e) {
			improperFormat();
			return;
		}
		String name = command[2];
		try {
			Statement stmt = con.createStatement();
			if (!stmt.executeQuery("SELECT * FROM books WHERE ISBN = " +isbn).next()) {
				System.out.println("Book with ISBN " +isbn +" already exists; cannot overwrite.");
				return;
			}
			stmt.executeUpdate("INSERT INTO books (ISBN, Name, Year, Copies) values ("
				+ isbn + ",'" + name + "'," + year + "," + copies + ")");
			System.out.println("Added book " + isbn + ".");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeStudent(String[] command) {
		if (command.length != 2) {
			improperFormat();
			return;
		}
		int id;
		try {
			id = Integer.parseInt(command[1]);
		} catch (NumberFormatException e) {
			improperFormat();
			return;
		}
		try {
			Statement stmt = con.createStatement();
			int rows = stmt.executeUpdate("DELETE FROM students WHERE StudentID = " +id);
			if (rows < 1)
				System.out.println("No students with student ID " +id +".");
			else
				System.out.println("Removed student " +id +".");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeBook(String[] command) {
		if (command.length != 2) {
			improperFormat();
			return;
		}
		int isbn;
		try {
			isbn = Integer.parseInt(command[1]);
		} catch (NumberFormatException e) {
			improperFormat();
			return;
		}
		try {
			Statement stmt = con.createStatement();
			int rows = stmt.executeUpdate("DELETE FROM books WHERE ISBN = " +isbn);
			if (rows < 1)
				System.out.println("No books with ISBN " +isbn +".");
			else
				System.out.println("Removed book " +isbn +".");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void issueBook(String[] command) {
		if (command.length != 3) {
			improperFormat();
			return;
		}
		int isbn, id;
		try {
			isbn = Integer.parseInt(command[1]);
			id = Integer.parseInt(command[2]);
		} catch (NumberFormatException e) {
			improperFormat();
			return;
		}
		try {
			Statement stmt = con.createStatement();
			ResultSet set = stmt.executeQuery("SELECT * FROM books WHERE ISBN = " +isbn);
			if (!set.next()) {

			}
			int copies = set.getInt("Copies") -1;
			if (copies < 0) {
				System.out.println("No copies of book " +isbn +" left to issue out.");
				return;
			}
			int rows = stmt.executeUpdate("INSERT INTO books2students (StudentID, ISBN, IssueDate, DueDate) VALUES ("
				+id +"," +isbn +"," +"CURRENT_DATE,CURRENT_DATE+" +30 +")");
			stmt.executeUpdate("UPDATE books SET Copies = " + copies + " WHERE ISBN = " + isbn);
			System.out.println("Issued book " + isbn + " to student " + id + ".");
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
					addBook(command);
					break;
				case "rs":
					removeStudent(command);
					break;
				case "rb":
					removeBook(command);
					break;
				case "ib":
					issueBook(command);
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

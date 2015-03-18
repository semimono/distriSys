import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Cullen on 3/17/2015.
 */
public class Connector {


    public static Connection connect(String url, String user, String password) throws SQLException {

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        return DriverManager.getConnection(url, props);
    }


    public static void main(String[] args) throws SQLException {
        String host = "faure.cs.colostate.edu";
        int port = 5432;
        String schema = "semimono";
        Connection con = connect("jdbc:postgresql://" +host +":" +port +"/" +schema, "semimono", "829904489");


    }
}

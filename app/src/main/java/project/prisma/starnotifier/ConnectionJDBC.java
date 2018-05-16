package project.prisma.starnotifier;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionJDBC {

    public static void main(String[] args) {

        try
        {
            ConnectionJDBC connectionJDBC = new ConnectionJDBC();

            Connection conn = connectionJDBC.getMySqlConnection();
            String output;
            String query = "select * from evento";
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())

            {
                output= resultSet.getString(2);
                System.out.print("--------- "+output+ " -----------");
            }

            /* You can use the connection object to do any insert, delete, query or update action to the mysql server.*/

            /* Do not forget to close the database connection after use, this can release the database connection.*/
            conn.close();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /* This method return java.sql.Connection object from MySQL server. */
    public Connection getMySqlConnection()
    {
        /* Declare and initialize a sql Connection variable. */
        Connection connect = null;

        try
        {

            /* Register for jdbc driver class. */
            Class.forName("com.mysql.jdbc.Driver");

            /* Create connection url. */
            String mysqlConnUrl = "jdbc:mysql://localhost:3306/starnotifierdb?verifyServerCertificate=false&useSSL=false&requireSSL=false";

            /* db user name. */
            String mysqlUserName = "root";

            /* db password. */
            String mysqlPassword = "toor";

            /* Get the Connection object. */
            connect = DriverManager.getConnection(mysqlConnUrl, mysqlUserName , mysqlPassword);

            /* Get related meta data for this mysql server to verify db connect successfully.. */
            DatabaseMetaData dbmd = connect.getMetaData();

            String dbName = dbmd.getDatabaseProductName();

            String dbVersion = dbmd.getDatabaseProductVersion();

            String dbUrl = dbmd.getURL();

            String userName = dbmd.getUserName();

            String driverName = dbmd.getDriverName();

            System.out.println("Database Name is " + dbName);

            System.out.println("Database Version is " + dbVersion);

            System.out.println("Database Connection Url is " + dbUrl);

            System.out.println("Database User Name is " + userName);

            System.out.println("Database Driver Name is " + driverName);

        }catch(Exception ex)
        {
            ex.printStackTrace();
        }finally
        {
            return connect;
        }
    }

}

import java.util.*;
import java.sql.*;

class DBConnectionManager
{

    String databaseUrl = "jdbc:mysql://localhost:3306/nvr";
    String userName = "root";
    String password = "pass";

    Vector connectionPool = new Vector();

    public DBConnectionManager()
    {
        initialize();
    }

    public DBConnectionManager(
            String databaseUrl,
            String userName,
            String password
    )
    {
        this.databaseUrl = databaseUrl;
        this.userName = userName;
        this.password = password;
        initialize();
    }

    private void initialize()
    {
        initializeConnectionPool();
    }

    private void initializeConnectionPool()
    {
        while(!checkIfConnectionPoolIsFull())
        {
            System.out.println("Connection Pool is NOT full. Proceeding with adding new connections");
            connectionPool.addElement(createNewConnectionForPool());
        }
        System.out.println("Connection Pool is full.");
    }

    private synchronized boolean checkIfConnectionPoolIsFull()
    {
        final int MAX_POOL_SIZE = 25;

        //Check if the pool size
        if(connectionPool.size() < 25)
        {
            return false;
        }

        return true;
    }

    //Creating a connection
    private Connection createNewConnectionForPool()
    {
        Connection connection = null;

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(databaseUrl, userName, password);
            System.out.println("Connection: "+connection);
        }
        catch(SQLException sqle)
        {
            System.err.println("SQLException: "+sqle);
            return null;
        }
        catch(ClassNotFoundException cnfe)
        {
            System.err.println("ClassNotFoundException: "+cnfe);
            return null;
        }

        return connection;
    }

    public synchronized Connection getConnectionFromPool()
    {
        Connection connection = null;

        //Check if there is a connection available. There are times when all the connections in the pool may be used up
        if(connectionPool.size() > 0)
        {
            connection = (Connection) connectionPool.firstElement();
            connectionPool.removeElementAt(0);
        }
        return connection;
    }

    public synchronized void returnConnectionToPool(Connection connection)
    {
        connectionPool.addElement(connection);
    }

    public static void main(String args[])
    {
        DBConnectionManager dbConnectionManager = new DBConnectionManager();
    }

}
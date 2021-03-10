
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;




public class sqlConnectionPool {

    private static final String url = "jdbc:mysql://localhost:3306/nvr?autoReconnect=true&useSSL=false";
    private static final String user = "root";
    private static final String password = "pass";

    private Connection connection;
    private static DataSource DS;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private static boolean eu;


    public DataSource getDataSource() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setMaximumPoolSize(20);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }


    synchronized static void addRecord(int cameraId, String fileUrl, long nowDate, int status, DataSource ds){
        String request ="INSERT INTO `record` (`cam_id`, `date`, `url`, `status`) VALUES ('"+cameraId+"', '"+nowDate+"', '"+fileUrl+"', '"+status+"')";
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            eu = stmt.execute(request);
        }   catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    synchronized static void AddStatus(int cameraId, int cameraStatus, DataSource ds) {
        String request ="UPDATE `camera` SET `status` = '"+cameraStatus+"' WHERE `camera`.`id` = "+cameraId;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            eu = stmt.execute(request);
        }   catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    synchronized  static String[][] getCamera(DataSource ds) {
        String request = "SELECT `id`, `ip`, `login`, `password`, `url`, `name` FROM `camera`";
        String countRequest = "SELECT COUNT(*) FROM `camera`";
        // System.out.println(request);
        try {

            con = ds.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(countRequest);
            int masLengh = 0;
            while (rs.next()){
                masLengh = rs.getInt(1);
            }
            //  System.out.println(masLengh);
            rs = stmt.executeQuery(request);
            int i = 0;
            String[][] cameraUrls = new String[masLengh][4];
            while (rs.next()) {
                String id = rs.getString(1);
                String ip_Adr = rs.getString(2);
                String login = rs.getString(3);
                String passwd = rs.getString(4);
                String url = rs.getString(5);
                String name = rs.getString(6);
                String cameraUrl = "rtsp://" + login + ":" + passwd + "@" + ip_Adr + url;
                cameraUrls[i][0] = cameraUrl;
                cameraUrls[i][1] = id;
                cameraUrls[i][2] = ip_Adr;
                cameraUrls[i][3] = name;
                i++;
            }
            try {
                stmt.close();
                con.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return cameraUrls;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}

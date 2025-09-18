package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/TALLERJDBC";
        String user = "root";
        String pass = "contraseñadoclinapp";
        return DriverManager.getConnection(url, user, pass);
    }
}

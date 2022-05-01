package dev.dadowl.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String url = "jdbc:postgresql://10.8.0.11/java-jun-aikamsoft";
    private static final String user = "dadowl";
    private static final String password = "dadowl";

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn;
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            return conn;
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
}

package dev.dadowl.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String url = "jdbc:postgresql://10.8.0.11/java-jun-aikamsoft";
    private static final String user = "dadowl";
    private static final String password = "dadowl";

    public static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn;
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            connection = conn;
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            connection = null;
        }
    }

    public static Boolean initTables(){
        try {
            PreparedStatement query = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Buyers (buyerId serial PRIMARY KEY, name varchar(50), lastName varchar(50));"
            );
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            PreparedStatement query = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Goods (goodId serial PRIMARY KEY, name varchar(50), price float);"
            );
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            PreparedStatement query = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Purchases (purchaseId serial PRIMARY KEY, " +
                            "buyer integer REFERENCES Buyers (buyerid), " +
                            "item integer REFERENCES Goods (goodId), date date);"
            );
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

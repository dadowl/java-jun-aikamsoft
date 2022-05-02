package dev.dadowl.testtask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.dadowl.testtask.utils.DatabaseUtils;
import dev.dadowl.testtask.utils.FileUtils;
import dev.dadowl.testtask.utils.JsonBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static RequestType REQUEST_TYPE;
    public static JsonElement INPUT_FILE;
    public static String OUTPUT_FILE;

    public static void main(String[] args) throws Exception {
        if (args.length < 3){
            System.out.println("Incomplete arguments.");
            return;
        }
        try {
            REQUEST_TYPE = RequestType.valueOf(args[0].toUpperCase().toUpperCase());
        } catch (Exception e){
            System.out.println("Request type is invalid.");
            return;
        }

        INPUT_FILE = FileUtils.openFile(args[1]);
        if (INPUT_FILE == null){
            throw new Exception("The input file is not found or JSON error.");
        }
        if (INPUT_FILE.isJsonNull()){
            throw new Exception("The input file is empty.");
        }

        OUTPUT_FILE = args[2];
        if (OUTPUT_FILE.isEmpty()){
            throw new Exception("Output file entered incorrectly.");
        }

        DatabaseUtils.connect();
        if (!DatabaseUtils.initTables()){
            throw new Exception("Database tables error.");
        }

        if (REQUEST_TYPE == RequestType.SEARCH){
            FileUtils.saveFile(OUTPUT_FILE, search());
        }
    }

    public static JsonObject search(){

        if (INPUT_FILE.getAsJsonObject().get("criterias") == null){
            return new JsonBuilder()
                .add("type", "error")
                .add("message","Критерии не найдены.")
            .build();
        }

        JsonBuilder builder = new JsonBuilder();
        builder.add("type", "search");

        JsonArray results = new JsonArray();

        for (JsonElement criteria : INPUT_FILE.getAsJsonObject().get("criterias").getAsJsonArray()) {
            JsonBuilder row = new JsonBuilder();
            row.add("criteria", criteria.getAsJsonObject());
            JsonArray rowResults = new JsonArray();

            if (criteria.getAsJsonObject().get("lastName") != null){
                String lastName = criteria.getAsJsonObject().get("lastName").getAsString();

                try {
                    PreparedStatement query = DatabaseUtils.connection.prepareStatement(
                            "SELECT * FROM Buyers WHERE lastName = '"+lastName+"';");
                    ResultSet result = query.executeQuery();
                    while (result.next()){
                        rowResults.add(new JsonBuilder()
                            .add("lastName", result.getString("lastName"))
                            .add("firstname", result.getString("name"))
                        .build());
                    }
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else if (criteria.getAsJsonObject().get("productName") != null && criteria.getAsJsonObject().get("minTimes") != null) {
                String productName = criteria.getAsJsonObject().get("productName").getAsString();
                int minTimes = criteria.getAsJsonObject().get("minTimes").getAsInt();

                try {
                    PreparedStatement query = DatabaseUtils.connection.prepareStatement(
                    "SELECT * FROM Purchases " +
                        "INNER JOIN Buyers ON Purchases.buyer = Buyers.buyerId " +
                        "INNER JOIN Goods ON Purchases.item = Goods.goodId " +
                        "WHERE Goods.name = '"+productName+"' " +
                        "LIMIT "+minTimes+";");
                    ResultSet result = query.executeQuery();
                    while (result.next()){
                        rowResults.add(new JsonBuilder()
                                .add("lastName", result.getString("lastName"))
                                .add("firstname", result.getString("name"))
                            .build());
                    }
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (criteria.getAsJsonObject().get("minExpenses") != null && criteria.getAsJsonObject().get("maxExpenses") != null) {
                int minExpenses = criteria.getAsJsonObject().get("minExpenses").getAsInt();
                int maxExpenses = criteria.getAsJsonObject().get("maxExpenses").getAsInt();

                try {
                    PreparedStatement query = DatabaseUtils.connection.prepareStatement(
                    "WITH totalItemsPrice AS (" +
                        "    SELECT DISTINCT Buyers.buyerId, Buyers.name, Buyers.lastName," +
                        "    sum(Goods.price) OVER(PARTITION BY Buyers.buyerId) AS totlaPrice FROM Purchases" +
                        "    INNER JOIN Buyers ON Purchases.buyer = Buyers.buyerId" +
                        "    INNER JOIN Goods ON Purchases.item = Goods.goodId)" +
                        "SELECT * FROM totalItemsPrice WHERE totlaPrice BETWEEN "+minExpenses+" AND "+maxExpenses+";");
                    ResultSet result = query.executeQuery();
                    while (result.next()){
                        rowResults.add(new JsonBuilder()
                                .add("lastName", result.getString("lastName"))
                                .add("firstname", result.getString("name"))
                                .build());
                    }
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (criteria.getAsJsonObject().get("badCustomers") != null) {
                int badCustomers = criteria.getAsJsonObject().get("badCustomers").getAsInt();
                try {
                    PreparedStatement query = DatabaseUtils.connection.prepareStatement(
                    "WITH buys AS ( " +
                        "    SELECT DISTINCT Buyers.buyerId, Buyers.name, Buyers.lastName," +
                        "        COUNT(Purchases.buyer) AS totalMinBuys " +
                        "    FROM Purchases " +
                        "             INNER JOIN Buyers ON Purchases.buyer = Buyers.buyerId " +
                        "             INNER JOIN Goods ON Purchases.item = Goods.goodId " +
                        "    group by Buyers.buyerId, Buyers.name, Buyers.lastName)" +
                        "SELECT * FROM buys WHERE totalMinBuys = (" +
                        "    SELECT MIN(totalMinBuys) FROM buys" +
                        ") LIMIT "+badCustomers+";");
                    ResultSet result = query.executeQuery();
                    while (result.next()){
                        rowResults.add(new JsonBuilder()
                                .add("lastName", result.getString("lastName"))
                                .add("firstname", result.getString("name"))
                                .build());
                    }
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                continue;
//                rowResults.add(new JsonBuilder()
//                    .add("type", "error")
//                    .add("message","Критерий указан не верно.")
//                .build());
            }

            row.add("results", rowResults);
            results.add(row.build());
        }

        builder.add("results", results);

        return builder.build();
    }

}

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        } else if (REQUEST_TYPE == RequestType.STAT){
            FileUtils.saveFile(OUTPUT_FILE, stat());
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

    public static JsonObject stat(){
        if (INPUT_FILE.getAsJsonObject().get("startDate") == null || INPUT_FILE.getAsJsonObject().get("endDate") == null){
            return new JsonBuilder()
                    .add("type", "error")
                    .add("message","Даты не указаны.")
                .build();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = formatter.parse(INPUT_FILE.getAsJsonObject().get("startDate").getAsString());
            endDate = formatter.parse(INPUT_FILE.getAsJsonObject().get("endDate").getAsString());
        } catch (ParseException e) {
            return new JsonBuilder()
                    .add("type", "error")
                    .add("message","Неправильный формат даты")
                    .build();
        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(startDate);
        cal2.setTime(endDate);
        int totalDays = 0;
        while (cal1.before(cal2)) {
            if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK)) && (Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK))) {
                totalDays++;
            }
            cal1.add(Calendar.DATE,1);
        }

        JsonBuilder builder = new JsonBuilder();
        builder.add("type", "stat");
        builder.add("totalDays", totalDays);

        JsonArray customers = new JsonArray();

        try {
            PreparedStatement buyers = DatabaseUtils.connection.prepareStatement("SELECT * FROM buyers");
            ResultSet buyersResult = buyers.executeQuery();
            while(buyersResult.next()){
                JsonBuilder buyerRow = new JsonBuilder();
                buyerRow.add("name", buyersResult.getString("name")+" "+buyersResult.getString("lastName"));
                PreparedStatement query = DatabaseUtils.connection.prepareStatement(
                        "SELECT Buyers.buyerId, Goods.name, goods.price, purchases.date " +
                            "FROM Purchases " +
                            "INNER JOIN Buyers ON Purchases.buyer = Buyers.buyerId " +
                            "INNER JOIN Goods ON Purchases.item = Goods.goodId " +
                            "group by Buyers.buyerId, Goods.name, goods.price, purchases.date " +
                            "HAVING purchases.date BETWEEN '"+startDate+"'::date AND '"+endDate+"'::date " +
                            "   AND EXTRACT(DOW FROM purchases.date) <> '0' AND EXTRACT(DOW FROM purchases.date) <> '6' " +
                            "   AND Buyers.buyerId = "+buyersResult.getInt("buyerId"));
                ResultSet result = query.executeQuery();
                JsonArray products = new JsonArray();
                int rows = 0;
                while (result.next()){
                    products.add(new JsonBuilder()
                            .add("name", result.getString("name"))
                            .add("expenses", result.getFloat("price"))
                        .build());
                    rows++;
                }
                if(rows == 0) continue;
                buyerRow.add("purchases", products);
                customers.add(buyerRow.build());
                result.close();
            }
            buyersResult.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        builder.add("customers", customers);

        return builder.build();
    }

}

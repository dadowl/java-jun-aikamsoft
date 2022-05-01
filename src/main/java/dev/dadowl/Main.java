package dev.dadowl;

import com.google.gson.JsonElement;
import dev.dadowl.utils.DatabaseUtil;
import dev.dadowl.utils.FileUtil;

import java.sql.Connection;

public class Main {

    public static RequestType REQUEST_TYPE = RequestType.NONE;
    public static JsonElement INPUT_FILE;
    public static String OUTPUT_FILE = "";

    public static void main(String[] args) throws Exception {
        if (args.length < 3){
            System.out.println("Incomplete arguments.");
            return;
        }
        try {
            REQUEST_TYPE = RequestType.valueOf("search".toUpperCase()/*args[0].toUpperCase()*/);
        } catch (Exception e){
            System.out.println("Request type is invalid.");
            return;
        }

        INPUT_FILE = FileUtil.openFile(args[1]);
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

        DatabaseUtil.connect();
        if (!DatabaseUtil.initTables()){
            throw new Exception("Dtabase tables error.");
        }
    }

}

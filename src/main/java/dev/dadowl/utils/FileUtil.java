package dev.dadowl.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class FileUtil {

    public static JsonElement openFile(String fileName){
        JsonElement json;
        Reader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
        } catch (Exception e) {
            return null;
        }
        try {
            json = JsonParser.parseReader(reader);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean saveFile(JsonObject jsonToSave){

        return false;
    }

}

package dev.dadowl.testtask.utils;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static JsonElement openFile(String fileName){
        JsonElement json;
        Reader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
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

    public static Boolean saveFile(String fileName, JsonObject jsonToSave){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File(fileName);

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,  StandardCharsets.UTF_8);
            outputStreamWriter.write(gson.toJson(jsonToSave));
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}

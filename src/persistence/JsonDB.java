package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonDB {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public <T> T read(String path, Class<T> type, T defaultValue) {

        File f = new File(path);
        if (!f.exists()) return defaultValue;

        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            T data = gson.fromJson(r, type);
            return data == null ? defaultValue : data;
        }
        catch (IOException e) {
            throw new RuntimeException("FAILED TO READ: " + path, e);
        }
    }

    public void write(String path, Object data) {
        File f = new File(path);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            gson.toJson(data, w);
        }
        catch (IOException e) {
            throw new RuntimeException("FAILED TO WRITE: " + path, e);
        }
    }
}
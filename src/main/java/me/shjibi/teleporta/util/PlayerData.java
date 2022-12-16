package me.shjibi.teleporta.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shjibi.teleporta.Main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final File folder;
    private final Map<UUID, JsonArray> data;
    private final String name;

    public PlayerData(String name) {
        this.name = name;
        this.folder = mkdir();
        this.data = readData();
    }

    public void removeData(UUID uuid, JsonObject obj) {
        JsonArray arr = data.get(uuid);
        if (arr != null) {
            arr.remove(obj);
        }
    }

    public void removeData(UUID uuid) {
        data.remove(uuid);
    }

    public void addData(UUID uuid, JsonObject obj) {
        JsonArray arr = data.get(uuid);
        if (arr == null) {
            JsonArray array = new JsonArray();
            array.add(obj);
            data.put(uuid, array);
        } else {
            arr.add(obj);
        }
    }

    public void setData(UUID uuid, JsonArray obj) {
        data.put(uuid, obj);
    }

    public JsonArray getData(UUID uuid) {
        return data.get(uuid);
    }

    public void saveData(UUID uuid) {
        File file = new File(folder.getAbsolutePath() + "\\" + uuid + ".json");
        try {
            Files.writeString(file.toPath(), data.get(uuid).toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("无法保存数据");
        }
    }

    public void save() {
        for (UUID uuid : data.keySet()) {
            saveData(uuid);
        }
    }

    private File mkdir() {
        File dataFolder = Main.getInstance().getDataFolder();
        File folder = new File(dataFolder.getAbsolutePath() + "\\" + name);

        if (!dataFolder.exists()) {
            boolean result = dataFolder.mkdir();
            if (!result) throw new RuntimeException("无法创建插件数据文件夹!");
        }
        if (!folder.exists()) {
            boolean result = folder.mkdir();
            if (!result) throw new RuntimeException("无法创建" + folder.getName() + "文件夹!");
        }
        return folder;
    }

    private Map<UUID, JsonArray> readData() {
        File[] files = folder.listFiles((f -> f.isFile() && f.getName().endsWith(".json")));
        Map<UUID, JsonArray> map = new HashMap<>();
        if (files == null) return map;
        for (File file : files) {
            try {
                String name = file.getName();
                UUID uuid = UUID.fromString(name.substring(0, name.length() - 5));
                String value = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                JsonElement element = JsonParser.parseString(value);
                if (element instanceof JsonArray arr) map.put(uuid, arr);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("无法读取数据");
            }
        }
        return map;
    }

    public Map<UUID, JsonArray> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PlayerData[name: " + name + ", data: " + data + "]";
    }

}
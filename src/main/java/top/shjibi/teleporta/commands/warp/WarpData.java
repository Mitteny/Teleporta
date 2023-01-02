package top.shjibi.teleporta.commands.warp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import top.shjibi.plugineer.config.Data;
import top.shjibi.teleporta.Main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpData extends Data {

    public WarpData() {
        super(Main.getInstance(), "warp");
        adaptOldData();
    }

    private JsonArray getPointsArray(Player p) {
        JsonObject obj = getData(p.getUniqueId());
        if (obj == null) return new JsonArray();
        return obj.getAsJsonArray("points");
    }

    public List<WarpPoint> getWarpPoints(Player p) {
        return getPointsArray(p).asList().stream()
                .filter(x -> x instanceof JsonObject)
                .map(x -> WarpPoint.fromJson((JsonObject) x))
                .collect(Collectors.toList());
    }

    public WarpPoint getWarpPoint(Player p, String warpName) {
        for (WarpPoint warpPoint : getWarpPoints(p)) {
            if (warpPoint.name().equals(warpName)) return warpPoint;
        }
        return null;
    }

    public boolean addWarpPoint(Player p, WarpPoint point) {
        UUID uuid = p.getUniqueId();
        if (!getData().containsKey(uuid)) {
            JsonObject obj = new JsonObject();
            JsonArray array = new JsonArray();
            array.add(point.toJson());
            obj.add("points", array);
            getData().put(uuid, obj);
        } else {
            if (warpPointExists(p, point.name())) return false;
            getPointsArray(p).add(point.toJson());
        }
        return true;
    }

    public boolean removeWarpPoint(Player p, String warpName) {
        if (!warpPointExists(p, warpName)) return false;
        getPointsArray(p).remove(getWarpPoint(p, warpName).toJson());
        return true;
    }

    public boolean warpPointExists(Player p, String warpName) {
        for (WarpPoint warpPoint : getWarpPoints(p)) {
            if (warpPoint.name().equals(warpName))
                return true;
        }
        return false;
    }

    private void adaptOldData() {
        String path = Main.getInstance().getDataFolder().getAbsolutePath();
        File oldDir = new File(path, "warp_points");
        if (!oldDir.exists()) return;
        File[] files = oldDir.listFiles();
        if (files == null || files.length == 0) return;
        List<File> jsonFiles = Arrays.stream(files).filter(x -> x.getName().endsWith(".json")).toList();
        for (File jsonFile : jsonFiles) {
            try {
                String name = jsonFile.getName();
                UUID uuid = UUID.fromString(name.substring(0, name.length() - 5));
                String value = Files.readString(jsonFile.toPath(), StandardCharsets.UTF_8);
                JsonElement element = JsonParser.parseString(value);
                if (element instanceof JsonArray arr) {
                    JsonObject obj = new JsonObject();
                    obj.add("points", arr);
                    addData(uuid, obj);
                }
            } catch (IOException e) {
                throw new RuntimeException("无法读取文件: " + jsonFile.getName());
            }
        }
        Arrays.stream(files).forEach(File::delete);
        oldDir.delete();
    }
}
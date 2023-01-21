package top.shjibi.teleporta.commands.warp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import top.shjibi.plugineer.config.Data;
import top.shjibi.teleporta.Main;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpData extends Data {

    public WarpData() {
        super(Main.getInstance(), "warp");
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

    public JsonObject getJsonObject(Player p, String warpName) {
        for (JsonElement json : getPointsArray(p)) {
            if (json instanceof JsonObject obj && WarpPoint.fromJson(obj).name().equals(warpName))
                return obj;
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
        JsonObject obj = getJsonObject(p, warpName);
        return getPointsArray(p).remove(obj);
    }

    public boolean warpPointExists(Player p, String warpName) {
        for (WarpPoint warpPoint : getWarpPoints(p)) {
            if (warpPoint.name().equals(warpName))
                return true;
        }
        return false;
    }
}
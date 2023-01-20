package top.shjibi.teleporta.commands.warp;


import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public record WarpPoint(String name, Location location) {

    public static WarpPoint fromJson(JsonObject obj) {
        if (obj == null) return null;

        String name = obj.get("name").getAsString();
        JsonObject loc = obj.getAsJsonObject("loc");

        World world = Bukkit.getWorld(loc.get("world").getAsString());
        double x = loc.get("x").getAsDouble();
        double y = loc.get("y").getAsDouble();
        double z = loc.get("z").getAsDouble();
        float yaw = loc.get("yaw").getAsFloat();
        float pitch = loc.get("pitch").getAsFloat();

        return new WarpPoint(name, new Location(world, x, y, z, yaw, pitch));
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        JsonObject loc = new JsonObject();
        loc.addProperty("world", Objects.requireNonNull(location.getWorld(), "World is null!").getName());
        loc.addProperty("x", location.getX());
        loc.addProperty("y", location.getY());
        loc.addProperty("z", location.getZ());
        loc.addProperty("yaw", location.getYaw());
        loc.addProperty("pitch", location.getPitch());
        obj.add("loc", loc);
        return obj;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WarpPoint point)) return false;
        return name.equals(point.name) && location.equals(point.location);
    }
}

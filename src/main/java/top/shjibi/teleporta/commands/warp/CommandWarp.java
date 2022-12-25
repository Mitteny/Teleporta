package top.shjibi.teleporta.commands.warp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.shjibi.teleporta.Main;
import top.shjibi.teleporta.base.PlayerCommandHandler;
import top.shjibi.teleporta.commands.warp.WarpPoint;
import top.shjibi.teleporta.util.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static top.shjibi.teleporta.util.StringUtil.color;

public class CommandWarp extends PlayerCommandHandler {

    private static final PlayerData data = new PlayerData("warp_points");

    public CommandWarp() {
        super(Main.getInstance(), "warp", 1, color("&c/warp <to/add/remove> <传送点名字>"));
    }

    @Override
    protected void execute(Player p, Command command, String label, String[] args) {
        String operation = args[0];
        String warpName = args[1];
        UUID uuid = p.getUniqueId();
        JsonObject pointObj = getWarpPoint(p, warpName);
        WarpPoint point = WarpPoint.fromJson(pointObj);

        switch (operation) {
            case "add":
                JsonArray warps = data.getData(uuid);

                if (warps == null || warps.isEmpty()) {
                    data.addData(uuid, new WarpPoint(warpName, p.getLocation()).toJson());
                    p.sendMessage(color( "&a成功建立了传送点&6" + warpName + "&a!"));
                } else {
                    for (JsonElement warp : warps) {

                        if (warp instanceof JsonObject obj && obj.has("name")) {
                            String each = obj.get("name").getAsString();
                            if (each.equalsIgnoreCase(warpName)) {
                                p.sendMessage(color("&c\"" + each + "\"传送点已存在!"));
                                return;
                            }
                        }
                    }

                    int count = 10;

                    if (warps.size() >= count) {
                        p.sendMessage(color(  "&c你最多建立" + count + "个传送点!"));
                    } else {
                        data.addData(uuid, new WarpPoint(warpName, p.getLocation()).toJson());
                        p.sendMessage(color( "&a成功建立了传送点&6" + warpName + "&a!"));
                    }
                }
                break;
            case "remove":
                if (pointObj != null) {
                    data.removeData(uuid, pointObj);
                    p.sendMessage(color("&a成功移除了传送点&6" + point.name() + "&a!"));
                } else {
                    p.sendMessage(color("&c该传送点不存在!"));
                }
                break;
            case "save":
                if (!p.isOp()) {
                    p.sendMessage(color("&c你不是OP!"));
                } else {
                    data.save();
                    p.sendMessage(color("&a已保存数据!"));
                }
                break;
            case "get":
                if (!p.isOp()) {
                    p.sendMessage(color("&c你不是OP!"));
                } else {
                    p.sendMessage(color("&7" + getData()));
                }
            case "to":
                if (pointObj != null) {
                    p.teleport(point.location());
                    p.sendMessage(color("&a已传送至&6" + point.name() + "&a!"));
                } else {
                    p.sendMessage(color("&c你没有名为\"" + warpName + "\"的传送点"));
                }
                break;
            default:
                pointObj = getWarpPoint(p, operation);
                point = WarpPoint.fromJson(pointObj);
                if (pointObj != null) {
                    p.teleport(point.location());
                    p.sendMessage(color("&a已传送至&6" + point.name() + "&a!"));
                } else {
                    sendUsage(p, label);
                }
        }
    }

    @Override
    public List<String> completeTab(Player p, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("to", "add", "remove");
        } else if (args.length == 2) {
            JsonArray array = data.getData(p.getUniqueId());
            if (array == null) return Collections.emptyList();
            List<String> result = new ArrayList<>();
            array.forEach(x -> {
                if (x instanceof JsonObject obj && obj.has("name"))
                    result.add(obj.get("name").getAsString());
            });
            return result;
        }
        return Collections.emptyList();
    }

    public static void saveData() {
        data.save();
    }

    public static PlayerData getData() {
        return data;
    }

    private JsonObject getWarpPoint(Player p, final String warpName) {
        JsonArray points = data.getData(p.getUniqueId());
        if (points == null) return null;
        for (JsonElement e : points) {
            if (!(e instanceof JsonObject obj)) continue;
            if (!obj.has("name")) continue;
            if (obj.get("name").getAsString().equalsIgnoreCase(warpName)) return obj;
        }
        return null;
    }
}

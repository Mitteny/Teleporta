package top.shjibi.teleporta.commands.warp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.shjibi.plugineer.command.base.CommandInfo;
import top.shjibi.plugineer.command.base.PlayerCommand;

import java.util.Collections;
import java.util.List;

import static top.shjibi.plugineer.util.StringUtil.color;

@CommandInfo(name = "warp", minArgs = 1, usage = {"&c用法: ", "&c/%s <传送点/add/remove/to> <传送点>"})
public class CommandWarp extends PlayerCommand {

    private static final WarpData data = new WarpData();

    public CommandWarp(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(Player p, @NotNull Command command, @NotNull String label, String[] args) {
        String operation = args[0];
        String warpName = args.length > 1 ? args[1] : args[0];
        List<WarpPoint> points = data.getWarpPoints(p);
        WarpPoint point = data.getWarpPoint(p, warpName);

        switch (operation) {
            case "add":
                if (points.size() >= 10) {
                    p.sendMessage(color("&c一个玩家最多有10个传送点"));
                } else {
                    boolean result = data.addWarpPoint(p, new WarpPoint(warpName, p.getLocation()));
                    if (result) {
                        p.sendMessage(color("&a成功建立了传送点&6" + warpName + "&a!"));
                    } else {
                        p.sendMessage(color("&c\"" + warpName + "\"传送点已存在!"));
                    }
                }
                break;
            case "remove":
                boolean result = data.removeWarpPoint(p, warpName);
                if (result) {
                    p.sendMessage(color("&a成功移除了传送点&6" + warpName + "&a!"));
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
                    if (warpName.equals("get")) {
                        p.sendMessage(color("&7" + data));
                    } else {
                        Player target = Bukkit.getPlayerExact(warpName);
                        if (target == null) p.sendMessage(color("&c/warp get <玩家名>"));
                        else p.sendMessage(color("&7" + data.getWarpPoints(target)));
                    }
                }
                break;
            case "to":
                if (point != null) {
                    p.teleport(point.location());
                    p.sendMessage(color("&a已传送至&6" + point.name() + "&a!"));
                } else {
                    p.sendMessage(color("&c你没有名为\"" + warpName + "\"的传送点"));
                }
                break;
            default:
                if (point != null) {
                    p.teleport(point.location());
                    p.sendMessage(color("&a已传送至&6" + point.name() + "&a!"));
                } else {
                    sendUsage(p, label);
                }
        }
    }

    @Override
    public List<String> completeTab(@NotNull Player p, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            List<String> list = List.of("to", "add", "remove");
            if (list.stream().noneMatch(x -> x.startsWith(args[0]))) return data.getWarpPoints(p).stream().map(WarpPoint::name).toList();
            return list;
        } else if (args.length == 2) {
            List<WarpPoint> list = data.getWarpPoints(p);
            if (list.isEmpty()) return Collections.emptyList();
            return list.stream().map(WarpPoint::name).toList();
        }
        return Collections.emptyList();
    }

    public static void saveData() {
        data.save();
    }
}

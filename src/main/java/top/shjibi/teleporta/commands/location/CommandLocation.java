package top.shjibi.teleporta.commands.location;

import top.shjibi.teleporta.Main;
import top.shjibi.teleporta.base.PlayerCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static top.shjibi.teleporta.util.StringUtil.color;

public final class CommandLocation extends PlayerCommandHandler {


    public CommandLocation() {
        super(Main.getInstance(), "location", 0, null);
    }

    /* 向全世界/某个人发送的坐标及所在维度 */
    @Override
    protected void execute(Player p, Command command, String s, String[] args) {
        Location loc = p.getLocation();
        String world = getWorldName(p.getWorld());

        String message = color("&b世界: &e" + world + ", 坐标: &6[&b" + loc.getBlockX() + "&e, &b" + loc.getBlockY() + "&e, &b" + loc.getBlockZ() + "&6]");

        if (args.length >= 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                p.sendMessage(color("&a玩家") + args[0] + "&c不存在!");
                return;
            }
            target.sendMessage(color("&6" + p.getName() + "&a向&9你&a公布了自己的&e坐标: "));
            target.sendMessage(message);
        } else {
            Bukkit.broadcastMessage(color("&6" + p.getName() + "&a向全服公布了自己的&e坐标: "));
            Bukkit.broadcastMessage(message);
        }
    }

    public static String getWorldName(World world) {
        if (world == null)
            return "未知";
        if (world.getName().equals("world"))
            return "主世界";
        if (world.getName().equals("world_nether"))
            return "地狱";
        if (world.getName().equals("world_the_end"))
            return "末地";
        return world.getName();
    }

}

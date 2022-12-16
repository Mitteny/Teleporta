package me.shjibi.teleporta.base;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static me.shjibi.teleporta.util.StringUtil.color;


/** 玩家指令(只有玩家才可以使用的指令) */
public abstract class PlayerCommandHandler extends BaseCommandHandler {

    private static final String NOT_PLAYER_MESSAGE = color("&c该指令只能由玩家执行");

    public PlayerCommandHandler(JavaPlugin plugin, String name, int minArgs, String usage) {
        super(plugin, name, minArgs, usage == null ? null : usage.split("\n"));
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(NOT_PLAYER_MESSAGE);
            return true;
        }

        if (args.length < minArgs) {
            sendUsage(sender, label);
            return true;
        }
        execute(p, command, label, args);
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(NOT_PLAYER_MESSAGE);
            return new ArrayList<>();
        }
        return completeTab(p, command, label, args);
    }

    public List<String> completeTab(Player sender, Command command, String label, String[] args) {return null;}

    protected abstract void execute(Player sender, Command command, String label, String[] args);

}

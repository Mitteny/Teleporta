package me.shjibi.teleporta.base;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class CommandHandler extends BaseCommandHandler {

    public CommandHandler(JavaPlugin plugin, String name, int minArgs, String usage) {
        super(plugin, name, minArgs, usage == null ? null : usage.split("\n"));
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < minArgs) {
            sendUsage(sender, label);
            return true;
        }
        execute(sender, command, label, args);
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return completeTab(sender, command, label, args);
    }

    public List<String> completeTab(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    protected abstract void execute(CommandSender sender, Command command, String label, String[] args);

}

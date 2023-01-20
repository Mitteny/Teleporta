package top.shjibi.teleporta.commands.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.shjibi.plugineer.command.base.PlayerCommandHandler;
import top.shjibi.plugineer.command.base.annotations.CommandInfo;
import top.shjibi.teleporta.config.MessageManager;

import java.util.Collections;
import java.util.List;

import static top.shjibi.plugineer.util.StringUtil.color;


@CommandInfo(name = "location")
public final class CommandLocation extends PlayerCommandHandler {

    private final MessageManager messenger;

    public CommandLocation(JavaPlugin plugin) {
        super(plugin);
        messenger = MessageManager.getInstance();
    }

    @Override
    public void execute(@NotNull Player p, @NotNull Command command,@NotNull String s, String[] args) {
        Location loc = p.getLocation();
        String world = getWorldName(p.getWorld());

        String message = color(messenger.getMessage("location.message",
                world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        if (args.length >= 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                p.sendMessage(messenger.getMessage("general.no_such_player", args[0]));
                return;
            }

            target.sendMessage(messenger.getMessage("location.tell_you", p.getName()));
            target.sendMessage(message);
        } else {
            Bukkit.broadcastMessage(messenger.getMessage("location.tell_server"));
            Bukkit.broadcastMessage(message);
        }
    }

    @Override
    public List<String> completeTab(@NotNull Player p, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }

    public String getWorldName(World world) {
        if (world == null)
            return messenger.getMessage("world.unknown");
        if (world.getName().equals("world"))
            return messenger.getMessage("world.overworld");
        if (world.getName().equals("world_nether"))
            return messenger.getMessage("world.nether");
        if (world.getName().equals("world_the_end"))
            return messenger.getMessage("world.the_end");
        return world.getName();
    }

}

package top.shjibi.teleporta.commands.tpa;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.shjibi.plugineer.command.base.annotations.CommandInfo;

import static top.shjibi.teleporta.commands.tpa.TeleportType.HERE;
import static top.shjibi.teleporta.commands.tpa.TeleportType.THERE;

@CommandInfo(name = {"tpaccept", "tpadeny", "tpacancel"}, minArgs = 1)
public class CommandProcessRequest extends RequestCommandHandler {

    public CommandProcessRequest(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void processCommand(@NotNull Player sender, @NotNull OfflinePlayer target, @NotNull Command cmd) {
        ProcessOperation operation = getOperation(cmd);
        TeleportRequest request = checkRequest(sender, target, operation);
        if (request == null) {
            sender.sendMessage(messenger.getMessage("tpa.no_such_request"));
            return;
        }
        processRequest(sender, target, request, operation);
    }

    // Gets a ProcessOperation by the input Command
    private ProcessOperation getOperation(Command cmd) {
        return switch (cmd.getName().toLowerCase()) {
            case "tpaccept" -> ProcessOperation.ACCEPT;
            case "tpadeny" -> ProcessOperation.DENY;
            case "tpacancel" -> ProcessOperation.CANCEL;
            default -> null; // Normally this won't happen
        };
    }

    // Returns the request, If the request doesn't exist, returns null
    private TeleportRequest checkRequest(Player sender, OfflinePlayer target, ProcessOperation operation) {
        boolean isCancel = operation == ProcessOperation.CANCEL;
        TPAManager manager = TPAManager.getInstance();
        TeleportRequest here = manager.getRequest(sender.getUniqueId(), target.getUniqueId(), isCancel ? THERE : HERE);
        TeleportRequest there = manager.getRequest(target.getUniqueId(), sender.getUniqueId(), isCancel ? HERE : THERE);
        if (there != null) {
            return there;
        } else return here;
    }
}

package top.shjibi.teleporta.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.shjibi.plugineer.command.base.PlayerCommandHandler;
import top.shjibi.teleporta.config.MessageManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static top.shjibi.teleporta.commands.tpa.TeleportType.THERE;

public abstract class RequestCommandHandler extends PlayerCommandHandler {

    protected static final MessageManager messenger = MessageManager.get();

    public RequestCommandHandler(JavaPlugin plugin) {
        super(plugin);
    }

    public abstract void processCommand(@NotNull Player sender, @NotNull OfflinePlayer target, @NotNull Command cmd);

    @Override
    public final void execute(@NotNull Player p, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null && List.of("tpa", "tpaccept", "tpahere").contains(cmd.getName())) {
            p.sendMessage(messenger.getMessage("general.no_such_player", args[0]));
            return;
        }

        if (target != null && target.isDead() && cmd.getName().equals("tpaccept")) {
            p.sendMessage(messenger.getMessage("general.dead_player"));
            return;
        }

        if (target != null && target.getName().equals(p.getName())) {
            p.sendMessage(messenger.getMessage("general.invalid_target"));
            return;
        }

        if (target == null) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (Objects.equals(player.getName(), args[0])) {
                    processCommand(p, player, cmd);
                    return;
                }
            }
            p.sendMessage(messenger.getMessage("general.no_such_player", args[0]));
        } else {
            processCommand(p, target, cmd);
        }
    }

    @Override
    public List<String> completeTab(@NotNull Player p, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(x -> !x.equals(p.getName()))
                    .toList();
        }
        return Collections.emptyList();
    }

    protected void processRequest(Player sender, OfflinePlayer target, TeleportRequest request, ProcessOperation operation) {
        Player onlineTarget = target.getPlayer();

        switch (operation) {
            case ACCEPT -> {
                boolean result = request.accept();
                if (!result) {
                    sender.sendMessage(messenger.getMessage("general.offline_player"));
                } else {
                    boolean isTPA = request.type() == THERE;
                    Player from = isTPA ? sender : onlineTarget;
                    Player to = isTPA ? onlineTarget : sender;
                    if (onlineTarget != null) from.teleport(to);
                    String senderMessage = isTPA ? messenger.getMessage("tpa.accept_tpa", target.getName())
                            : messenger.getMessage("tpa.accept_tpahere", target.getName());
                    String targetMessage = isTPA ? messenger.getMessage("tpa.get_accepted_tpa", sender.getName())
                            : messenger.getMessage("tpa.get_accepted_tpahere", sender.getName());

                    sender.sendMessage(senderMessage);
                    if (onlineTarget != null) onlineTarget.sendMessage(targetMessage);
                }
            }
            case DENY -> {
                sender.sendMessage(messenger.getMessage("tpa.deny_request", target.getName()));
                if (onlineTarget != null)
                    onlineTarget.sendMessage(messenger.getMessage("tpa.get_denied", sender.getName()));
            }
            case CANCEL -> {
                sender.sendMessage(messenger.getMessage("tpa.cancel_tpa", target.getName()));
                if (onlineTarget != null)
                    onlineTarget.sendMessage(messenger.getMessage("tpa.get_canceled", sender.getName()));
            }
        }
        TPAManager.getInstance().removeRequest(request);
    }

    protected enum ProcessOperation {
        ACCEPT, DENY, CANCEL
    }


}

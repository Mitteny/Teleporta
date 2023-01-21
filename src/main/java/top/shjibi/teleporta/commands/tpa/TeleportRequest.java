package top.shjibi.teleporta.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.shjibi.teleporta.config.MessageManager;

import java.util.UUID;

/**
 * A teleport request
 */
public record TeleportRequest(UUID from, UUID to, long start, TeleportType type) {

    public static final int REMOVE_DELAY = 60;
    private static final MessageManager messenger = MessageManager.get();

    public boolean accept() {
        Player to = Bukkit.getPlayer(this.to);
        Player from = Bukkit.getPlayer(this.from);
        if (from == null || to == null) return false;
        if (shouldRemove()) return false;
        from.teleport(to.getLocation());
        return true;
    }

    public boolean shouldRemove() {
        return (System.currentTimeMillis() - start) > REMOVE_DELAY * 1000;
    }

    public void sendRemoveMessage() {
        boolean typeBool = (type == TeleportType.THERE);
        UUID senderID = typeBool ? from : to;
        UUID receiverID = typeBool ? to : from;
        Player sender = Bukkit.getPlayer(senderID);
        Player receiver = Bukkit.getPlayer(receiverID);
        if (sender != null)
            sender.sendMessage(messenger.getMessage("tpa.outdated_hint_1", Bukkit.getOfflinePlayer(receiverID).getName()));
        if (receiver != null)
            receiver.sendMessage(messenger.getMessage("tpa.outdated_hint_2", Bukkit.getOfflinePlayer(senderID).getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TeleportRequest request)) return false;
        return from.equals(request.from) &&
                to.equals(request.to) &&
                type == request.type &&
                start == request.start;
    }

}

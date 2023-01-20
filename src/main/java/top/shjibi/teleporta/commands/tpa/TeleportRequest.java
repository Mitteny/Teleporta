package top.shjibi.teleporta.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.shjibi.teleporta.config.MessageManager;

import static top.shjibi.plugineer.util.StringUtil.color;

/**
 * A teleport request
 */
public record TeleportRequest(String from, String to, long start, TeleportType type) {

    public static final int REMOVE_DELAY = 60;
    private static final MessageManager manager = MessageManager.getInstance();

    public boolean accept() {
        Player to = Bukkit.getPlayerExact(this.to);
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
        String senderName = typeBool ? from : to;
        String receiverName = typeBool ? to : from;
        Player reqSender = Bukkit.getPlayerExact(senderName);
        Player receiver = Bukkit.getPlayerExact(receiverName);
        if (reqSender != null) reqSender.sendMessage(manager.getMessage("tpa.outdated_hint_1", receiverName));
        if (receiver != null) receiver.sendMessage(manager.getMessage("tpa.outdated_hint_2", senderName));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TeleportRequest request)) return false;
        return from.equals(request.from) &&
                to.equals(request.to) &&
                type == request.type;
    }

}

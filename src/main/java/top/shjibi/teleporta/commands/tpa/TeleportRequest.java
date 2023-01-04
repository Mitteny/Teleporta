package top.shjibi.teleporta.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static top.shjibi.plugineer.util.StringUtil.color;

/**
 * 代表了一个传送请求,包含了起始地(Player),目的地(Player),请求时间(long),以及类型(TeleportType)
 */
public record TeleportRequest(String from, String to, long start, TeleportType type) {

    public static final int REMOVE_DELAY = 60;

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
        if (reqSender != null) reqSender.sendMessage(color("&7你发送给&e" + receiverName + "&7的请求已过期!"));
        if (receiver != null) receiver.sendMessage(color("&e" + senderName + "&7发送你的请求已过期!"));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TeleportRequest request)) return false;
        return from.equals(request.from) &&
                to.equals(request.to) &&
                type == request.type;
    }

}

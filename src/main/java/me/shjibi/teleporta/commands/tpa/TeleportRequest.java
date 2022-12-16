package me.shjibi.teleporta.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static me.shjibi.teleporta.util.StringUtil.*;

/** 代表了一个传送请求,包含了起始地(Player),目的地(Player),请求时间(long),以及类型(TeleportType) */
public record TeleportRequest(String from, String to, long start, TeleportType type) {

    public static final int REMOVE_DELAY = 60;

    public Player getFrom() {
        return Bukkit.getPlayerExact(this.from);
    }

    public Player getTo() {
        return Bukkit.getPlayerExact(this.to);
    }

    public TeleportType getType() {
        return type;
    }

    public boolean accept() {
        Player to = getTo();
        Player from = getFrom();
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
        Player reqSender = typeBool ? getFrom() : getTo();
        Player receiver = typeBool ? getTo() : getFrom();
        reqSender.sendMessage(color("&7你发送给&e" + receiver.getName() + "&7的请求已过期!"));
        receiver.sendMessage(color("&e" + reqSender.getName() + "&7发送你的请求已过期!"));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TeleportRequest request)) return false;
        return from.equals(request.from) &&
                to.equals(request.to) &&
                type == request.type;
    }

}

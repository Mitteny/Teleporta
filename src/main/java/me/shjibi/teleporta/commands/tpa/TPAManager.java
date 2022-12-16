package me.shjibi.teleporta.commands.tpa;

import me.shjibi.teleporta.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** 管理TPA请求,指令等 */
public final class TPAManager {

    private TPAManager() {
        requests = new ArrayList<>();
    }

    public static final String[] commands = {
            "tpa", "tpahere", "tpaccept", "tpadeny", "tpacancel"
    };

    private static TPAManager instance;

    private final List<TeleportRequest> requests;

    public static TPAManager getInstance() {
        if (instance == null) instance = new TPAManager();
        return instance;
    }

    public boolean containsRequest(TeleportRequest request) {
        for(TeleportRequest each : requests) {
            if(each.equals(request)) return true;
        }
        return false;
    }

    public boolean containsRequest(Player from, Player to, TeleportType type) {
        return containsRequest(new TeleportRequest(from.getName(), to.getName(), 0L, type));
    }

    public boolean containsRequestExactly(TeleportRequest request) {
        for(TeleportRequest each : requests) {
            if(each.equals(request) && each.start() == request.start()) return true;
        }
        return false;
    }

    public void addRequest(TeleportRequest request) {
        requests.add(request);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!containsRequestExactly(request)) return;
            request.sendRemoveMessage();
            requests.remove(request);
        }, TeleportRequest.REMOVE_DELAY * 20);
    }

    public void removeRequest(TeleportRequest request) {
        requests.remove(request);
    }

    public TeleportRequest getRequest(Player from, Player to, TeleportType type) {
        for(TeleportRequest each : requests) {
            if(each.equals(new TeleportRequest(from.getName(), to.getName(), 0L, type))) return each;
        }
        return null;
    }

}

package top.shjibi.teleporta.commands.tpa;

import org.bukkit.Bukkit;
import top.shjibi.teleporta.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TPAManager {

    private TPAManager() {
        requests = new ArrayList<>();
    }

    private static TPAManager instance;

    private final List<TeleportRequest> requests;

    public static TPAManager getInstance() {
        if (instance == null) instance = new TPAManager();
        return instance;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean containsRequest(TeleportRequest request) {
        for (TeleportRequest each : requests) {
            if (each.equals(request)) return true;
        }
        return false;
    }

    public void addRequest(TeleportRequest request) {
        requests.add(request);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!containsRequest(request)) return;
            request.sendRemoveMessage();
            requests.remove(request);
        }, TeleportRequest.REMOVE_DELAY * 20);
    }

    public void removeRequest(TeleportRequest request) {
        requests.remove(request);
    }

    public TeleportRequest getRequest(UUID from, UUID to, TeleportType type) {
        for (TeleportRequest request : requests) {
            if (request.from().equals(from) && request.to().equals(to) && request.type() == type) {
                return request;
            }
        }
        return null;
    }

}

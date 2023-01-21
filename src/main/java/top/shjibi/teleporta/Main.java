package top.shjibi.teleporta;


import org.bukkit.plugin.java.JavaPlugin;
import top.shjibi.plugineer.command.CommandManager;
import top.shjibi.teleporta.commands.location.CommandLocation;
import top.shjibi.teleporta.commands.tpa.CommandCreateRequest;
import top.shjibi.teleporta.commands.tpa.CommandProcessRequest;
import top.shjibi.teleporta.commands.warp.CommandWarp;

public final class Main extends JavaPlugin {

    private static final boolean DEV_MODE = true;

    @Override
    public void onEnable() {
        CommandManager commandManager = CommandManager.getInstance(getInstance(),
                CommandCreateRequest.class, CommandProcessRequest.class,
                CommandWarp.class, CommandLocation.class);
        commandManager.register();
        getLogger().info(getDescription().getFullName() + " is enabled.");
    }

    @Override
    public void onDisable() {
        CommandWarp.saveData();
        getLogger().info(getDescription().getFullName() + " is disabled.");
    }

    // Get the instance of plugin
    public static JavaPlugin getInstance() {
        return getPlugin(Main.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDevMode() {
        return DEV_MODE;
    }

}

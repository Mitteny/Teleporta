package top.shjibi.teleporta;


import org.bukkit.plugin.java.JavaPlugin;
import top.shjibi.plugineer.command.CommandManager;
import top.shjibi.teleporta.commands.location.CommandLocation;
import top.shjibi.teleporta.commands.tpa.CommandCreateRequest;
import top.shjibi.teleporta.commands.tpa.CommandProcessRequest;
import top.shjibi.teleporta.commands.warp.CommandWarp;
import top.shjibi.teleporta.config.MessageManager;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Command manager instance
        CommandManager commandManager = CommandManager.getInstance(getInstance(),
                CommandCreateRequest.class, CommandProcessRequest.class,
                CommandWarp.class, CommandLocation.class);
        commandManager.register();

        getLogger().info(getDescription().getFullName() + " is enabled");
    }

    @Override
    public void onDisable() {
        CommandWarp.saveData();
        getLogger().info(getDescription().getFullName() + " is disabled");
    }

    // Get the instance of plugin
    public static JavaPlugin getInstance() {
        return getPlugin(Main.class);
    }
}

package top.shjibi.teleporta;


import top.shjibi.teleporta.util.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    /**
     * 唯一的实例
     */
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        CommandManager.registerHandlers();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /**
     * 获取插件实例
     */
    public static JavaPlugin getInstance() {
        return instance;
    }
}

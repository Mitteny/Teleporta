package top.shjibi.teleporta;


import org.bukkit.plugin.java.JavaPlugin;
import top.shjibi.plugineer.command.CommandManager;
import top.shjibi.teleporta.commands.location.CommandLocation;
import top.shjibi.teleporta.commands.tpa.CommandTPA;
import top.shjibi.teleporta.commands.warp.CommandWarp;

public final class Main extends JavaPlugin {

    /**
     * 唯一的实例
     */
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        /* 指令管理者实例 */
        CommandManager commandManager = CommandManager.newInstance(instance, CommandTPA.class, CommandWarp.class, CommandLocation.class);
        commandManager.register();

        getLogger().info("已注册指令~");
    }

    @Override
    public void onDisable() {
        CommandWarp.saveData();
        instance = null;
        getLogger().info("已保存数据~");
    }

    /**
     * 获取插件实例
     */
    public static JavaPlugin getInstance() {
        return instance;
    }
}

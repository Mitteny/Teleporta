package top.shjibi.teleporta.base;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


/** 基本指令处理者 */
public abstract class BaseCommandHandler implements TabExecutor {

    protected final String[] usage;
    protected final int minArgs;
    protected final String name;
    protected final JavaPlugin plugin;

    public BaseCommandHandler(JavaPlugin plugin, String name, int minArgs, String[] usage) {
        this.plugin = Objects.requireNonNull(plugin);
        this.name = Objects.requireNonNull(name);
        this.minArgs = minArgs;
        this.usage = usage;
    }

    /** 注册指令 */
    public void register() {
        PluginCommand command = Objects.requireNonNull(plugin.getCommand(name));
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /** 发送用法 */
    protected final void sendUsage(CommandSender sender, String label) {
        if (usage == null) return;
        for (String line : usage) {
            line = line.replace("$label", label);
            sender.sendMessage(line);
        }
    }
}

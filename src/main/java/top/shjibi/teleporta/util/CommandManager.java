package top.shjibi.teleporta.util;

import top.shjibi.teleporta.Main;
import top.shjibi.teleporta.base.BaseCommandHandler;
import top.shjibi.teleporta.commands.tpa.CommandTPA;
import top.shjibi.teleporta.commands.location.CommandLocation;
import top.shjibi.teleporta.commands.warp.CommandWarp;

import java.util.logging.Level;

public final class CommandManager {

    private CommandManager() {}

    /** 所有指令处理者的类 */
    public static final Class<?>[] HANDLERS = {
            CommandTPA.class, CommandWarp.class, CommandLocation.class
    };


    /** 注册指令处理者(用BaseCommandHandler的register方法) */
    public static void registerHandlers() {
        for (Class<?> clazz : HANDLERS) {
            try {
                Object obj = clazz.getConstructor().newInstance();
                if (!(obj instanceof BaseCommandHandler handler)) return;
                handler.register();
            } catch (ReflectiveOperationException ignored) {
                Main.getInstance().getLogger().log(Level.SEVERE, "无法加载指令!");
            }
        }
    }

}

package top.shjibi.teleporta.config;

import top.shjibi.plugineer.config.Config;
import top.shjibi.teleporta.Main;

import static top.shjibi.plugineer.util.StringUtil.color;

public class MessageManager extends Config {

    private static MessageManager instance;

    private MessageManager() {
        super(Main.getInstance(), "messages");
    }

    public static MessageManager get() {
        if (instance == null) instance = new MessageManager();
        return instance;
    }

    public String getMessage(String path, Object... obj) {
        String message = getConfig(path);
        if (message == null) message = getDefaultConfig(path);
        return message == null ? path : color(message.formatted(obj));
    }

}

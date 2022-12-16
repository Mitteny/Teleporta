package me.shjibi.teleporta.commands;

import me.shjibi.teleporta.Main;
import me.shjibi.teleporta.base.PlayerCommandHandler;
import me.shjibi.teleporta.commands.tpa.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.shjibi.teleporta.commands.tpa.TeleportType.HERE;
import static me.shjibi.teleporta.commands.tpa.TeleportType.THERE;
import static me.shjibi.teleporta.util.StringUtil.color;
import static me.shjibi.teleporta.util.StringUtil.strip;

public final class CommandTPA extends PlayerCommandHandler {


    public CommandTPA() {
        super(Main.getInstance(), "tpa", 1, color("&c用法: /$label <玩家>"));
    }


    /* 处理tpa, tpahere, tpaccept, tpadeny, tpacancel */
    @Override
    protected void execute(Player p, Command command, String label, String[] args) {
        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            p.sendMessage(color("&c该玩家不存在"));
            return;
        }

        if (target.isDead() && label.equalsIgnoreCase("tpaccept")) {
            p.sendMessage(color("&c该玩家已死亡"));
            return;
        }

        if (target.getName().equals(p.getName())) {
            p.sendMessage(color("&c你不能对自己使用这条指令"));
            return;
        }

        if (label.equalsIgnoreCase("tpa") || label.equalsIgnoreCase("tpahere")) {
            createRequest(label, p, target);
        } else if (label.equalsIgnoreCase("tpaccept")) {
            boolean[] result = checkRequest(target, p);
            if (!result[0]) return;
            Player from = result[1] ? target : p;
            Player to = result[1] ? p : target;
            TeleportType type = result[1] ? THERE : HERE;

            acceptRequest(from, to, type);

            boolean[] shouldDeleteResult = checkRequest(p, target, false);
            if (!shouldDeleteResult[0]) return;
            if (shouldDeleteResult[1] == result[1]) {
                TPAManager.getInstance().removeRequest(new TeleportRequest(to.getName(), from.getName(), 0L, type));
            }
        } else if (label.equalsIgnoreCase("tpadeny")) {
            boolean[] result = checkRequest(target, p);
            if (!result[0]) return;
            Player from = result[1] ? target : p;
            Player to = result[1] ? p : target;
            TeleportRequest request = TPAManager.getInstance().getRequest(from, to, result[1] ? THERE : HERE);
            TPAManager.getInstance().removeRequest(request);
            p.sendMessage(color("&c成功拒绝!"));
            target.sendMessage(color("&c对方已拒绝!"));
        } else if (label.equalsIgnoreCase("tpacancel")) {
            boolean[] results = checkRequest(p, target, false);
            if (!results[0]) {
                p.sendMessage(color("&c你没有向对方发送过传送请求!"));
                return;
            }
            Player from = results[1] ? p : target;
            Player to = results[1] ? target : p;
            TeleportType type = results[1] ? THERE : HERE;
            TPAManager.getInstance().removeRequest(new TeleportRequest(from.getName(), to.getName(), 0L, type));
            p.sendMessage(color("&7成功撤回对&e" + target.getName() + "&7的传送请求!"));
            target.sendMessage(color("&e" + p.getName() + "&7已撤回对你的传送请求!"));
        }
    }

    /* 创建TeleportRequest */
    private void createRequest(String label, Player p, Player target) {
        boolean typeBool = label.equalsIgnoreCase("tpa"); // true则为tpa,否则为tpahere
        TeleportType type = typeBool ? THERE : HERE;  // 获取枚举常量
        TeleportType anotherType = type == THERE ? HERE : THERE;

        // 判断目的地和起始地
        Player from = typeBool ? p : target;
        Player to = typeBool ? target : p;

        // 创建请求
        TeleportRequest request = new TeleportRequest(from.getName(), to.getName(), System.currentTimeMillis(), type);

        // 判断是否已有请求,如果有则进行提示
        if (!TPAManager.getInstance().containsRequest(request)) {
            // 判断对方是否发送了对应的请求, 如果是, 则直接同意
            if (TPAManager.getInstance().containsRequest(from, to, anotherType)) {
                acceptRequest(from, to, anotherType);
                return;
            }
            TPAManager.getInstance().addRequest(request);
        } else {
            p.sendMessage(color("&c你已经给该玩家发送过" + (typeBool ? "传送" : "拉人") + "请求了"));
            return;
        }

        TextComponent senderMessage = new TextComponent(color(typeBool ? "&a你给&6" + target.getName() + "&a发送了传送请求!" : "&9你给&6" + target.getName() + "&9发送了拉人请求!"));
        TextComponent cancel = new TextComponent(color("&7[撤回]"));
        cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&8&o点击撤回"))));
        cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpacancel " + target.getName()));
        senderMessage.addExtra("   ");
        senderMessage.addExtra(cancel);

        String receiverMessage = typeBool ? "&6" + p.getName() + "&a给你发送了传送请求" : "&6" + p.getName() + "&9给你发送了拉人请求";

        p.spigot().sendMessage(senderMessage);
        target.sendMessage(color(receiverMessage));


        // 发送同意/拒绝
        TextComponent accept = new TextComponent(color("&a[同意]"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&2&o点击同意"))));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + p.getName()));

        int length = strip(receiverMessage).length();

        TextComponent deny = new TextComponent(color("&c[拒绝]"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&4&o点击拒绝"))));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + p.getName()));

        TextComponent whiteSpace = new TextComponent(String.format("%" + length + "s", ""));
        whiteSpace.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
        whiteSpace.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("")));

        accept.addExtra(whiteSpace);
        accept.addExtra(deny);

        target.spigot().sendMessage(accept);
    }


    /* 同意请求 */
    private void acceptRequest(Player from, Player to, TeleportType type) {
        TeleportRequest request = TPAManager.getInstance().getRequest(from, to, type);
        boolean result = request != null && request.accept();
        if (!result)
            from.sendMessage(color((request != null ? "&a对方已下线" : "&c请求已过期")));
        else {
            boolean typeBool = type == THERE;
            String fromMessage = typeBool ? "&a已传送至&6" + to.getName() + "&a!" : "&9已同意&6" + to.getName() + "&9的拉人请求!";
            String toMessage = typeBool ? "&a已同意&6" + from.getName() + "&a的传送请求!" : "&9已将&6" + from.getName() + "&9拉到了你的位置!";
            from.sendMessage(color(fromMessage));
            to.sendMessage(color(toMessage));
        }

        TPAManager.getInstance().removeRequest(request);
    }

    /* 检查请求,返回布尔数组,包含{是否存在该请求(bool), 请求类型(bool)} */
    private boolean[] checkRequest(Player target, Player p) {
        return checkRequest(target, p, true);
    }

    /* 检查请求,返回布尔数组,包含{是否存在该请求(bool), 请求类型(bool)} */
    private boolean[] checkRequest(Player reqSender, Player receiver, boolean message) {
        boolean typeBool;
        boolean exists = (typeBool = TPAManager.getInstance().containsRequest(reqSender, receiver, THERE)) ||
                TPAManager.getInstance().containsRequest(receiver, reqSender, HERE);

        if (!exists && message) receiver.sendMessage(color("&c该&6传送&c/&9拉人&c请求不存在或已过期!"));
        return new boolean[]{exists, typeBool};
    }


    /* 重写了register,因为要注册多个指令 */
    @Override
    public void register() {
        for (String command : TPAManager.commands) {
            PluginCommand pluginCmd = Objects.requireNonNull(plugin.getCommand(command));
            pluginCmd.setExecutor(this);
            pluginCmd.setTabCompleter(this);
        }
    }

}

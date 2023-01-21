package top.shjibi.teleporta.commands.tpa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.shjibi.plugineer.command.base.annotations.CommandInfo;

import static top.shjibi.plugineer.util.StringUtil.strip;
import static top.shjibi.teleporta.commands.tpa.TeleportType.HERE;
import static top.shjibi.teleporta.commands.tpa.TeleportType.THERE;

@CommandInfo(name = {"tpa", "tpahere"}, minArgs = 1)
public final class CommandCreateRequest extends RequestCommandHandler {

    public CommandCreateRequest(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void processCommand(@NotNull Player sender, @NotNull OfflinePlayer target, @NotNull Command cmd) {
        sendRequest(cmd, sender, (Player) target);
    }

    private void sendRequest(Command cmd, Player p, Player target) {
        boolean isTPA = cmd.getName().equalsIgnoreCase("tpa");
        TeleportType type = isTPA ? THERE : HERE;
        TeleportType opposite = type == THERE ? HERE : THERE;

        Player from = isTPA ? p : target;
        Player to = isTPA ? target : p;

        TeleportRequest request = new TeleportRequest(from.getUniqueId(), to.getUniqueId(), System.currentTimeMillis(), type);

        TPAManager manager = TPAManager.getInstance();
        if (!manager.containsRequest(request)) {
            TeleportRequest oppositeRequest = manager.getRequest(from.getUniqueId(), to.getUniqueId(), opposite);
            if (oppositeRequest != null) {
                processRequest(from, to, oppositeRequest, ProcessOperation.ACCEPT);
                return;
            }
            manager.addRequest(request);
        } else {
            p.sendMessage(messenger.getMessage("tpa.already_sent"));
            return;
        }

        TextComponent senderMessage = createSenderMessage(isTPA, target.getName());

        p.spigot().sendMessage(senderMessage);

        TextComponent receiverMessage = new TextComponent(isTPA ? messenger.getMessage("tpa.receive_tpa", p.getName()) :
                messenger.getMessage("tpa.receive_tpahere", p.getName()));
        TextComponent acceptDeny = createAcceptDenyComponent(p.getName(), receiverMessage.getText());
        target.spigot().sendMessage(receiverMessage);
        target.spigot().sendMessage(acceptDeny);
    }

    private TextComponent createSenderMessage(boolean isTPA, String targetName) {
        TextComponent senderMessage = new TextComponent(isTPA ? messenger.getMessage("tpa.send_tpa", targetName) :
                messenger.getMessage("tpa.send_tpahere", targetName));
        TextComponent cancel = new TextComponent(messenger.getMessage("tpa.cancel"));
        cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messenger.getMessage("tpa.cancel_hint"))));
        cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpacancel " + targetName));
        senderMessage.addExtra("   ");
        senderMessage.addExtra(cancel);
        return senderMessage;
    }

    private TextComponent createAcceptDenyComponent(String senderName, String receiverMessage) {
        TextComponent accept = new TextComponent(messenger.getMessage("tpa.accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messenger.getMessage("tpa.accept_hint"))));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + senderName));

        TextComponent deny = new TextComponent(messenger.getMessage("tpa.deny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messenger.getMessage("tpa.deny_hint"))));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + senderName));

        int blankCount = strip(receiverMessage).length() - strip(accept.getText()).length() - strip(deny.getText()).length();

        TextComponent blank = new TextComponent(String.format("%" + blankCount + "s", ""));
        blank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
        blank.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("")));

        accept.addExtra(blank);
        accept.addExtra(deny);
        return accept;
    }


}

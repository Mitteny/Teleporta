package top.shjibi.teleporta.commands.tpa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.shjibi.plugineer.command.base.annotations.CommandInfo;
import top.shjibi.plugineer.util.StringUtil;

import static top.shjibi.plugineer.util.StringUtil.color;
import static top.shjibi.teleporta.commands.tpa.TeleportType.HERE;
import static top.shjibi.teleporta.commands.tpa.TeleportType.THERE;

@CommandInfo(name = {"tpa", "tpahere"}, minArgs = 1)
public final class CommandCreateRequest extends RequestCommandHandler {


    public CommandCreateRequest(JavaPlugin plugin) {
        super(plugin);
    }

    /* Process tpa, tpahere */
    @Override
    public void processRequest(@NotNull Player sender, @NotNull Player target, @NotNull Command cmd, @NotNull String label) {
        sendRequest(cmd, sender, target);
    }

    // Sends a TeleportRequest
    private void sendRequest(Command cmd, Player sender, Player target) {
        String targetName = target.getName();
        boolean isTpa = cmd.getName().equalsIgnoreCase("tpa");

        TeleportType type = isTpa ? THERE : HERE;
        TeleportType opposite = isTpa ? HERE : THERE;

        // Find out 'from' and 'to'
        Player from = isTpa ? sender : target;
        Player to = isTpa ? target : sender;

        // Create the request
        TeleportRequest request = new TeleportRequest(from.getName(), to.getName(), System.currentTimeMillis(), type);

        // Make sure the request doesn't exist.
        if (!TPAManager.getInstance().containsRequest(request)) {
            // If the target has an opposite request, then accept it.
            if (TPAManager.getInstance().containsRequest(from, to, opposite)) {
                acceptRequest(from, to, opposite);
                return;
            }
            // Otherwise add the request
            TPAManager.getInstance().addRequest(request);
        } else {
            sender.sendMessage(manager.getMessage("tpa.already_sent", targetName));
            return;
        }

        // Build the text components for player
        String tpaMessage = manager.getMessage("tpa.send_tpa", targetName);
        String tpahereMessage = manager.getMessage("tpa.send_tpahere", targetName);
        
        TextComponent senderMessage = new TextComponent(color(isTpa ? tpaMessage : tpahereMessage));
        TextComponent cancel = new TextComponent(manager.getMessage("tpa.cancel"));
        
        cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(manager.getMessage("tpa.cancel_hint"))));
        cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpacancel " + targetName));
        
        senderMessage.addExtra("   ");
        senderMessage.addExtra(cancel);

        // Build the text for target
        String receiverMessage = isTpa ? manager.getMessage("tpa.receive_tpa") : manager.getMessage("tpa.receive_tpahere");

        // Send the messages to player and target
        sender.spigot().sendMessage(senderMessage);
        target.sendMessage(color(receiverMessage));


        // Build the [Accept / Deny] component
        TextComponent accept = new TextComponent(manager.getMessage("tpa.accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(manager.getMessage("tpa.accept_hint"))));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));

        int length = StringUtil.strip(receiverMessage).length();

        TextComponent deny = new TextComponent(manager.getMessage("tpa.deny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(manager.getMessage("tpa.deny_hint"))));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + sender.getName()));

        TextComponent blank = new TextComponent(String.format("%" + length + "s", ""));
        blank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
        blank.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("")));

        accept.addExtra(blank);
        accept.addExtra(deny);

        // Send the [Accept / Deny] component to target
        target.spigot().sendMessage(accept);
    }



}

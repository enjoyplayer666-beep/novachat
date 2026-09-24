package ru.antidepressant.novachat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private final NovaChat plugin;
    private final Map<UUID, ChannelType> playerChannels = new HashMap<>();

    public ChatListener(NovaChat plugin) {
        this.plugin = plugin;
    }

    public ChannelType getChannel(Player player) {
        return playerChannels.getOrDefault(player.getUniqueId(), plugin.getDefaultChannel());
    }

    public void setChannel(Player player, ChannelType type) {
        playerChannels.put(player.getUniqueId(), type);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

        String globalSymbol = plugin.getConfig().getString("global-symbol", "!");
        ChannelType channel = getChannel(player);

        if (!globalSymbol.isEmpty() && rawMessage.startsWith(globalSymbol)) {
            rawMessage = rawMessage.substring(globalSymbol.length()).trim();
            channel = ChannelType.GLOBAL;
        }

        if (rawMessage.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        boolean allowColor = player.hasPermission("novachat.color");
        Component messageComponent = allowColor
                ? ColorUtil.parse(rawMessage)
                : Component.text(rawMessage);

        String templatePath = channel == ChannelType.LOCAL ? "formats.local" : "formats.global";
        String template = plugin.getConfig().getString(templatePath, "<player>: <message>");

        if (plugin.hasPlaceholderApi()) {
            template = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, template);
        }
        template = ColorUtil.legacyToMiniMessage(template);

        Component finalComponent = ColorUtil.miniMessage().deserialize(
                template,
                Placeholder.unparsed("player", player.getName()),
                Placeholder.component("message", messageComponent)
        );

        event.setCancelled(true);

        if (channel == ChannelType.LOCAL) {
            int radius = plugin.getConfig().getInt("local-radius", 100);
            boolean anyoneElseHeard = false;

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.getWorld().equals(player.getWorld())) continue;
                if (online.getLocation().distance(player.getLocation()) > radius) continue;

                online.sendMessage(finalComponent);
                if (!online.equals(player)) {
                    anyoneElseHeard = true;
                }
            }
            Bukkit.getConsoleSender().sendMessage(finalComponent);

            if (!anyoneElseHeard) {
                String nearMsg = plugin.getConfig().getString("messages.no-one-nearby", "");
                if (!nearMsg.isEmpty()) {
                    player.sendMessage(ColorUtil.parse(nearMsg));
                }
            }
        } else {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(finalComponent);
            }
            Bukkit.getConsoleSender().sendMessage(finalComponent);
        }
    }
}

package ru.antidepressant.novachat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NovaChatCommand implements CommandExecutor {

    private final NovaChat plugin;
    private final ChatListener chatListener;

    public NovaChatCommand(NovaChat plugin, ChatListener chatListener) {
        this.plugin = plugin;
        this.chatListener = chatListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label.toLowerCase()) {
            case "novachat": {
                if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(msg("messages.reload-usage", "&cИспользование: /novachat reload"));
                    return true;
                }
                if (!sender.hasPermission("novachat.admin")) {
                    sender.sendMessage(msg("messages.no-permission", "&cУ вас нет прав для этого."));
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(msg("messages.reload-success", "&aNovaChat успешно перезагружен!"));
                return true;
            }
            case "global": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(msg("messages.players-only", "&cЭта команда только для игроков."));
                    return true;
                }
                chatListener.setChannel(player, ChannelType.GLOBAL);
                player.sendMessage(msg("messages.switched-global", "&7Теперь вы пишете в &fглобальный &7чат."));
                return true;
            }
            case "local": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(msg("messages.players-only", "&cЭта команда только для игроков."));
                    return true;
                }
                chatListener.setChannel(player, ChannelType.LOCAL);
                player.sendMessage(msg("messages.switched-local", "&7Теперь вы пишете в &fлокальный &7чат."));
                return true;
            }
            default:
                return false;
        }
    }

    private net.kyori.adventure.text.Component msg(String path, String def) {
        return ColorUtil.parse(plugin.getConfig().getString(path, def));
    }
}

package ru.antidepressant.novachat;

import org.bukkit.plugin.java.JavaPlugin;

public class NovaChat extends JavaPlugin {

    private boolean placeholderApiHooked;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        placeholderApiHooked = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        ChatListener chatListener = new ChatListener(this);
        getServer().getPluginManager().registerEvents(chatListener, this);

        NovaChatCommand commandExecutor = new NovaChatCommand(this, chatListener);
        getCommand("novachat").setExecutor(commandExecutor);
        getCommand("global").setExecutor(commandExecutor);
        getCommand("local").setExecutor(commandExecutor);

        getLogger().info("NovaChat включен. PlaceholderAPI: " + (placeholderApiHooked ? "найден" : "НЕ найден — тег клана и LuckPerms-префикс работать не будут!"));
    }

    public boolean hasPlaceholderApi() {
        return placeholderApiHooked;
    }

    public ChannelType getDefaultChannel() {
        String value = getConfig().getString("default-channel", "LOCAL");
        try {
            return ChannelType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChannelType.LOCAL;
        }
    }
}

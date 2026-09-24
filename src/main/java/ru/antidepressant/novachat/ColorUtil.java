package ru.antidepressant.novachat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилита для цвета.
 * Позволяет писать в config.yml старые & коды (&a, &c, &l...), hex вида &#RRGGBB
 * и нативные MiniMessage-теги (<gradient>, <rainbow>, <#RRGGBB> и т.д.) одновременно —
 * всё конвертируется в MiniMessage и парсится за один проход, без конфликтов синтаксиса.
 */
public final class ColorUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtil() {
    }

    public static String legacyToMiniMessage(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // &#RRGGBB -> <#RRGGBB>
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        String result = sb.toString();

        // Однобуквенные легаси-коды
        result = result
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");

        return result;
    }

    /** Парсит готовую (уже с подставленными плейсхолдерами) строку в Component. */
    public static Component parse(String raw) {
        return MM.deserialize(legacyToMiniMessage(raw));
    }

    public static MiniMessage miniMessage() {
        return MM;
    }
}

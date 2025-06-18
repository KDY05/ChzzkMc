package io.github.kdy05.chzzkMc.core.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ChatBroadcastManager {
    
    private static final NamedTextColor[] USER_COLORS = {
        NamedTextColor.DARK_RED,
        NamedTextColor.RED,
        NamedTextColor.GOLD,
        NamedTextColor.YELLOW,
        NamedTextColor.DARK_GREEN,
        NamedTextColor.GREEN,
        NamedTextColor.AQUA,
        NamedTextColor.DARK_AQUA,
        NamedTextColor.DARK_BLUE,
        NamedTextColor.BLUE,
        NamedTextColor.LIGHT_PURPLE,
        NamedTextColor.DARK_PURPLE,
        NamedTextColor.GRAY
    };
    
    private static final int MAX_CACHED_USERS = 1000;
    
    private final Map<String, NamedTextColor> userColors = new ConcurrentHashMap<>();
    
    public void broadcastChatMessage(String username, String content) {
        if (username.equals("익명")) {
            broadcastAnonymousMessage(content);
        } else {
            broadcastUserMessage(username, content);
        }
    }
    
    private void broadcastAnonymousMessage(String content) {
        Bukkit.broadcast(
            Component.text("⚡ ", NamedTextColor.GREEN)
                    .append(Component.text("[익명] " + content, NamedTextColor.GRAY))
        );
    }
    
    private void broadcastUserMessage(String username, String content) {
        NamedTextColor userColor = getUserColor(username);
        
        Bukkit.broadcast(
            Component.text("⚡ ", NamedTextColor.GREEN)
                    .append(Component.text(username, userColor))
                    .append(Component.text(" " + content, NamedTextColor.WHITE))
        );
    }
    
    private NamedTextColor getUserColor(String username) {
        NamedTextColor color = userColors.get(username);
        if (color == null) {
            color = generateColorForUser(username);
            
            if (userColors.size() >= MAX_CACHED_USERS) {
                userColors.clear();
            }
            
            userColors.put(username, color);
        }
        return color;
    }
    
    private NamedTextColor generateColorForUser(String username) {
        int hash = Math.abs(username.hashCode());
        int colorIndex = hash % USER_COLORS.length;
        return USER_COLORS[colorIndex];
    }

}
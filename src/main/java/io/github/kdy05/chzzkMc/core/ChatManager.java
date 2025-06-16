package io.github.kdy05.chzzkMc.core;

import io.github.kdy05.chzzkMc.ChzzkMc;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.chat.ChatMessage;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChatBuilder;
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent;
import xyz.r2turntrue.chzzk4j.chat.event.ConnectEvent;

import java.io.IOException;

@SuppressWarnings("unused")
public class ChatManager {
    
    private final ChzzkMc plugin;
    private final ChzzkClient client;
    private ChzzkChat chat;
    
    public ChatManager(ChzzkMc plugin, ChzzkClient client) {
        this.plugin = plugin;
        this.client = client;
    }
    
    public void initialize() {
        String channelId = plugin.getConfig().getString("channel-id");
        connect(channelId);
    }
    
    public void connect(String channelId) {
        try {
            if (chat != null) {
                chat.closeBlocking();
            }
            
            chat = new ChzzkChatBuilder(client, channelId).build();
            setupEventHandlers();
            chat.connectBlocking();
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to connect to chat: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public void reconnect() {
        String channelId = plugin.getConfig().getString("channel-id");
        plugin.getLogger().info("Reconnecting to channel: " + channelId);
        connect(channelId);
    }
    
    private void setupEventHandlers() {
        chat.on(ConnectEvent.class, (evt) -> {
            plugin.getLogger().info("설정한 채널의 채팅창에 연결되었습니다.");
            if (!evt.isReconnecting()) {
                chat.requestRecentChat(10);
            }
        });
        
        chat.on(ChatMessageEvent.class, (evt) -> {
            ChatMessage msg = evt.getMessage();
            String content = msg.getContent();
            String username = msg.getProfile() != null ? msg.getProfile().getNickname() : "익명";
            
            if (content.startsWith("!")) {
                if (ChzzkMc.getVoteManager().processVoteCommand(username, content)) {
                    return;
                }
            }

            if (!plugin.getConfig().getBoolean("broadcast-chat"))
                return;

            if (msg.getProfile() == null) {
                Bukkit.broadcast(Component.text(
                        "[Chat] 익명: " + content));
                return;
            }

            Bukkit.broadcast(Component.text(
                    "[Chat] " + username + ": " + content));
        });
    }

    public void disconnect() {
        if (chat != null) {
            chat.closeBlocking();
        }
    }
    
    public ChzzkChat getChat() {
        return chat;
    }

}
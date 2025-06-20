package io.github.kdy05.chzzkMc.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a chat message is received from Chzzk
 * This event is fired asynchronously
 */
@SuppressWarnings("unused")
public class AsyncChzzkChatEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final String username;
    private final String message;
    
    /**
     * Create a new ChzzkChatEvent
     * @param username The username of the chat sender (or "익명" for anonymous)
     * @param message The chat message content
     */
    public AsyncChzzkChatEvent(String username, String message) {
        super(true); // async event
        this.username = username;
        this.message = message;
    }
    
    /**
     * Get the username of the chat sender
     * @return username or "익명" for anonymous users
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the chat message content
     * @return message content
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Check if the message is from an anonymous user
     * @return true if anonymous, false otherwise
     */
    public boolean isAnonymous() {
        return "익명".equals(username);
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
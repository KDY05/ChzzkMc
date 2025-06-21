package io.github.kdy05.chzzkMc.api;

import io.github.kdy05.chzzkMc.config.ConfigManager;
import io.github.kdy05.chzzkMc.config.TimerDisplayType;
import io.github.kdy05.chzzkMc.core.VoteManager;

/**
 * API provider for ChzzkMc plugin
 * Provides external access to voting functionality
 */
@SuppressWarnings("unused")
public class ChzzkMcProvider {
    
    private static ChzzkMcProvider instance;
    private final VoteManager voteManager;
    private final ConfigManager configManager;
    
    private ChzzkMcProvider(VoteManager voteManager, ConfigManager configManager) {
        this.voteManager = voteManager;
        this.configManager = configManager;
    }
    
    /**
     * Get the singleton instance of ChzzkMcProvider
     * @return ChzzkMcProvider instance
     */
    public static ChzzkMcProvider getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ChzzkMcProvider not initialized. ChzzkMc plugin may not be loaded.");
        }
        return instance;
    }
    
    /**
     * Initialize the provider (called by ChzzkMc plugin)
     * @param voteManager The VoteManager instance
     * @param configManager The ConfigManager instance
     */
    public static void initialize(VoteManager voteManager, ConfigManager configManager) {
        if (instance == null) {
            instance = new ChzzkMcProvider(voteManager, configManager);
        }
    }
    
    /**
     * Shutdown the provider (called by ChzzkMc plugin)
     */
    public static void shutdown() {
        instance = null;
    }
    
    /**
     * Start a vote with custom options
     * @param optionTitles Array of vote option titles (2-4 options)
     * @param durationSeconds Duration of the vote in seconds
     * @return true if vote started successfully, false if another vote is already active
     * @throws IllegalArgumentException if parameters are invalid
     */
    public boolean startVote(String[] optionTitles, int durationSeconds) {
        return startVote(optionTitles, durationSeconds, false);
    }
    
    /**
     * Start a vote with custom options and result announcement setting
     * @param optionTitles Array of vote option titles (2-4 options)
     * @param durationSeconds Duration of the vote in seconds
     * @param showResult true to announce results, false to suppress them
     * @return true if vote started successfully, false if another vote is already active
     * @throws IllegalArgumentException if parameters are invalid
     */
    public boolean startVote(String[] optionTitles, int durationSeconds, boolean showResult) {
        if (optionTitles == null || optionTitles.length < 2 || optionTitles.length > 4) {
            throw new IllegalArgumentException("Vote must have 2-4 options");
        }
        
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        
        for (String title : optionTitles) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("All option titles must be non-empty");
            }
        }
        
        return voteManager.startVote(optionTitles, durationSeconds, true, showResult);
    }
    
    /**
     * Start a vote without automatic timer (infinite duration)
     * The vote must be manually ended using endVote()
     * @param optionTitles Array of vote option titles (2-4 options)
     * @return true if vote started successfully, false if another vote is already active
     * @throws IllegalArgumentException if parameters are invalid
     */
    public boolean startVoteWithoutTimer(String[] optionTitles) {
        return startVoteWithoutTimer(optionTitles, false);
    }
    
    /**
     * Start a vote without automatic timer (infinite duration) and result announcement setting
     * The vote must be manually ended using endVote()
     * @param optionTitles Array of vote option titles (2-4 options)
     * @param showResult true to announce results, false to suppress them
     * @return true if vote started successfully, false if another vote is already active
     * @throws IllegalArgumentException if parameters are invalid
     */
    public boolean startVoteWithoutTimer(String[] optionTitles, boolean showResult) {
        if (optionTitles == null || optionTitles.length < 2 || optionTitles.length > 4) {
            throw new IllegalArgumentException("Vote must have 2-4 options");
        }
        
        for (String title : optionTitles) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("All option titles must be non-empty");
            }
        }
        
        return voteManager.startVote(optionTitles, 0, true, showResult);
    }
    
    /**
     * Check if a vote is currently active
     * @return true if vote is active
     */
    public boolean isVoteActive() {
        return voteManager.isVoteActive();
    }
    
    /**
     * Force end the current vote
     * @return true if vote was ended, false if no vote was active
     */
    public boolean endVote() {
        if (!voteManager.isVoteActive()) {
            return false;
        }
        voteManager.endVote();
        return true;
    }
    
    // Configuration API methods
    
    /**
     * Get the current channel ID
     * @return Current channel ID
     */
    public String getChannelId() {
        return configManager.getChannelId();
    }
    
    /**
     * Set the channel ID
     * @param channelId New channel ID
     */
    public void setChannelId(String channelId) {
        configManager.setChannelId(channelId);
    }
    
    /**
     * Check if chat broadcasting is enabled
     * @return true if chat broadcasting is enabled
     */
    public boolean isBroadcastChatEnabled() {
        return configManager.isBroadcastChatEnabled();
    }
    
    /**
     * Enable or disable chat broadcasting
     * @param enabled true to enable, false to disable
     */
    public void setBroadcastChatEnabled(boolean enabled) {
        configManager.setBroadcastChatEnabled(enabled);
    }
    
    /**
     * Get the current timer display type
     * @return Current timer display type
     */
    public TimerDisplayType getTimerDisplayType() {
        return configManager.getTimerDisplayType();
    }
    
    /**
     * Set the timer display type
     * @param type New timer display type
     */
    public void setTimerDisplayType(TimerDisplayType type) {
        configManager.setTimerDisplayType(type);
    }
}
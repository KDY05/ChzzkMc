package io.github.kdy05.chzzkMc.api;

import io.github.kdy05.chzzkMc.ChzzkMc;

/**
 * API provider for ChzzkMc plugin
 * Provides external access to voting functionality
 */
@SuppressWarnings("unused")
public class ChzzkMcProvider {
    
    private static ChzzkMcProvider instance;
    private final ChzzkMc plugin;
    
    private ChzzkMcProvider(ChzzkMc plugin) {
        this.plugin = plugin;
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
     * @param plugin The ChzzkMc plugin instance
     */
    public static void initialize(ChzzkMc plugin) {
        if (instance == null) {
            instance = new ChzzkMcProvider(plugin);
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
        
        return ChzzkMc.getVoteManager().startApiVote(optionTitles, durationSeconds);
    }
    
    /**
     * Check if a vote is currently active
     * @return true if vote is active
     */
    public boolean isVoteActive() {
        return ChzzkMc.getVoteManager().isVoteActive();
    }
    
    /**
     * Force end the current vote
     * @return true if vote was ended, false if no vote was active
     */
    public boolean endVote() {
        return ChzzkMc.getVoteManager().forceEndVote();
    }
}
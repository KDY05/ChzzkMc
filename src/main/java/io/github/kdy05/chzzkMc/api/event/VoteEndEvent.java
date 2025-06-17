package io.github.kdy05.chzzkMc.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a vote ends
 * Contains the winning vote option number
 */
@SuppressWarnings("unused")
public class VoteEndEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final int winningOption;
    private final int totalVotes;
    private final String[] optionTitles;
    private final int[] voteCounts;
    
    /**
     * Create a new VoteEndEvent
     * @param winningOption The winning vote option number (1-based, lowest number wins in case of tie)
     * @param totalVotes Total number of votes cast
     * @param optionTitles Array of option titles
     * @param voteCounts Array of vote counts for each option
     */
    public VoteEndEvent(int winningOption, int totalVotes, String[] optionTitles, int[] voteCounts) {
        this.winningOption = winningOption;
        this.totalVotes = totalVotes;
        this.optionTitles = optionTitles.clone();
        this.voteCounts = voteCounts.clone();
    }
    
    /**
     * Get the winning vote option number (1-based)
     * In case of a tie, the lowest numbered option wins
     * @return winning option number
     */
    public int getWinningOption() {
        return winningOption;
    }
    
    /**
     * Get the total number of votes cast
     * @return total votes
     */
    public int getTotalVotes() {
        return totalVotes;
    }
    
    /**
     * Get the titles of all vote options
     * @return array of option titles
     */
    public String[] getOptionTitles() {
        return optionTitles.clone();
    }
    
    /**
     * Get the vote counts for all options
     * @return array of vote counts
     */
    public int[] getVoteCounts() {
        return voteCounts.clone();
    }
    
    /**
     * Get the title of the winning option
     * @return winning option title
     */
    public String getWinningOptionTitle() {
        if (winningOption < 1 || winningOption > optionTitles.length) {
            throw new IllegalStateException("Invalid winning option: " + winningOption);
        }
        return optionTitles[winningOption - 1];
    }
    
    /**
     * Get the vote count of the winning option
     * @return winning option vote count
     */
    public int getWinningOptionVotes() {
        if (winningOption < 1 || winningOption > voteCounts.length) {
            throw new IllegalStateException("Invalid winning option: " + winningOption);
        }
        return voteCounts[winningOption - 1];
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
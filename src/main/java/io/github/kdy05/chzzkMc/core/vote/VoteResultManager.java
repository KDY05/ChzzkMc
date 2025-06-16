package io.github.kdy05.chzzkMc.core.vote;

import io.github.kdy05.chzzkMc.api.event.VoteEndEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public class VoteResultManager {
    
    public void announceResults(String[] optionTitles, int[] voteCounts) {
        int totalVotes = 0;
        for (int i = 0; i < optionTitles.length; i++) {
            totalVotes += voteCounts[i];
        }

        Bukkit.broadcast(Component.text("=== 투표 결과 ===", NamedTextColor.GOLD));
        Bukkit.broadcast(Component.text("총 투표수: " + totalVotes, NamedTextColor.YELLOW));

        for (int i = 0; i < optionTitles.length; i++) {
            float percentage = totalVotes == 0 ? 0 : (float) voteCounts[i] / totalVotes * 100;
            Bukkit.broadcast(Component.text(
                String.format("%s: %d표 (%.1f%%)", optionTitles[i], voteCounts[i], percentage),
                getColorForOption(i)
            ));
        }
    }
    
    public void fireVoteEndEvent(String[] optionTitles, int[] voteCounts) {
        int winningOption = getWinningOption(voteCounts);
        int totalVotes = 0;
        for (int voteCount : voteCounts) {
            totalVotes += voteCount;
        }

        VoteEndEvent event = new VoteEndEvent(winningOption, totalVotes, optionTitles, voteCounts);
        Bukkit.getPluginManager().callEvent(event);
    }
    
    private int getWinningOption(int[] voteCounts) {
        int maxVotes = -1;
        int winningOption = 1;

        for (int i = 0; i < voteCounts.length; i++) {
            if (voteCounts[i] > maxVotes) {
                maxVotes = voteCounts[i];
                winningOption = i + 1;
            }
        }

        return winningOption;
    }
    
    private NamedTextColor getColorForOption(int option) {
        return switch (option) {
            case 0 -> NamedTextColor.RED;
            case 1 -> NamedTextColor.GREEN;
            case 2 -> NamedTextColor.BLUE;
            case 3 -> NamedTextColor.LIGHT_PURPLE;
            default -> NamedTextColor.WHITE;
        };
    }
}
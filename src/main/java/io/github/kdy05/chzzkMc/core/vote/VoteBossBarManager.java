package io.github.kdy05.chzzkMc.core.vote;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VoteBossBarManager {
    
    private BossBar[] voteBars;
    private String[] optionTitles;
    private int maxOptions;
    
    public void initializeBossBars(String[] optionTitles) {
        this.optionTitles = optionTitles.clone();
        this.maxOptions = optionTitles.length;
        this.voteBars = new BossBar[maxOptions];
        
        for (int i = 0; i < maxOptions; i++) {
            NamedTextColor color = getColorForOption(i);
            BossBar.Color barColor = getBossBarColor(i);

            voteBars[i] = BossBar.bossBar(
                Component.text("!투표" + (i + 1) + ": " + optionTitles[i], color),
                0.0f,
                barColor,
                BossBar.Overlay.PROGRESS
            );
        }
    }
    
    public void updateBossBars(int[] voteCounts) {
        int totalVotes = 0;
        for (int i = 0; i < maxOptions; i++) {
            totalVotes += voteCounts[i];
        }

        if (totalVotes == 0) {
            for (int i = 0; i < maxOptions; i++) {
                voteBars[i].progress(0.0f);
                voteBars[i].name(Component.text(String.format("!투표%d: %s - 0.0%%", i + 1, optionTitles[i]), getColorForOption(i)));
            }
            return;
        }

        for (int i = 0; i < maxOptions; i++) {
            float progress = (float) voteCounts[i] / totalVotes;
            float percentage = progress * 100;
            voteBars[i].progress(progress);
            voteBars[i].name(Component.text(String.format("!투표%d: %s - %.1f%%", i + 1, optionTitles[i], percentage), getColorForOption(i)));
        }
    }
    
    public void showBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < maxOptions; i++) {
                player.showBossBar(voteBars[i]);
            }
        }
    }
    
    public void hideBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < maxOptions; i++) {
                if (voteBars[i] != null) {
                    player.hideBossBar(voteBars[i]);
                }
            }
        }
    }
    
    public void addPlayerToBossBars(Player player) {
        if (voteBars != null) {
            for (int i = 0; i < maxOptions; i++) {
                if (voteBars[i] != null) {
                    player.showBossBar(voteBars[i]);
                }
            }
        }
    }
    
    public void removePlayerFromBossBars(Player player) {
        if (voteBars != null) {
            for (int i = 0; i < maxOptions; i++) {
                if (voteBars[i] != null) {
                    player.hideBossBar(voteBars[i]);
                }
            }
        }
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

    private BossBar.Color getBossBarColor(int option) {
        return switch (option) {
            case 0 -> BossBar.Color.RED;
            case 1 -> BossBar.Color.GREEN;
            case 2 -> BossBar.Color.BLUE;
            case 3 -> BossBar.Color.PURPLE;
            default -> BossBar.Color.WHITE;
        };
    }
}
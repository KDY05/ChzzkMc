package io.github.kdy05.chzzkMc.core.vote;

import io.github.kdy05.chzzkMc.ChzzkMc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class VoteTimerManager {
    
    private final ChzzkMc plugin;
    private BukkitTask voteTimer;
    private BukkitTask actionBarTimer;
    private int remainingSeconds = 0;
    
    public VoteTimerManager(ChzzkMc plugin) {
        this.plugin = plugin;
    }
    
    public void startTimer(int durationSeconds, Runnable onTimerEnd) {
        if (durationSeconds > 0) {
            remainingSeconds = durationSeconds;
            voteTimer = Bukkit.getScheduler().runTaskLater(plugin, onTimerEnd, durationSeconds * 20L);
            startActionBarTimer();
        }
    }
    
    public void cancelTimers() {
        if (voteTimer != null) {
            voteTimer.cancel();
            voteTimer = null;
        }
        if (actionBarTimer != null) {
            actionBarTimer.cancel();
            actionBarTimer = null;
        }
    }
    
    private void startActionBarTimer() {
        actionBarTimer = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (remainingSeconds <= 0) {
                if (actionBarTimer != null) {
                    actionBarTimer.cancel();
                    actionBarTimer = null;
                }
                return;
            }
            
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            String timeText = String.format("남은 투표 시간: %d:%02d", minutes, seconds);
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendActionBar(Component.text(timeText, NamedTextColor.YELLOW));
            }
            
            remainingSeconds--;
        }, 0L, 20L);
    }
}
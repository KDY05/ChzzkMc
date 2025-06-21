package io.github.kdy05.chzzkMc.core;

import io.github.kdy05.chzzkMc.ChzzkMc;
import io.github.kdy05.chzzkMc.core.vote.VoteBossBarManager;
import io.github.kdy05.chzzkMc.core.vote.VoteResultManager;
import io.github.kdy05.chzzkMc.core.vote.VoteTimerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class VoteManager {

    private final ChzzkMc plugin;
    private final VoteBossBarManager bossBarManager;
    private final VoteTimerManager timerManager;
    private final VoteResultManager resultManager;
    
    private volatile boolean voteActive = false;
    private final Map<String, Integer> userVotes = new HashMap<>();
    private AtomicIntegerArray voteCounts;
    private String[] optionTitles;
    private int maxOptions;
    private boolean isApiVote = false;
    private boolean showingResult = false;
    private BukkitTask bossBarUpdateTask;
    
    public VoteManager(ChzzkMc plugin) {
        this.plugin = plugin;
        this.bossBarManager = new VoteBossBarManager();
        this.timerManager = new VoteTimerManager(plugin);
        this.resultManager = new VoteResultManager(plugin);
    }
    
    public boolean startVote(String[] optionTitles, int durationSeconds, boolean isApiVote, boolean showingResult) {
        if (voteActive) {
            return false;
        }

        this.isApiVote = isApiVote;
        this.showingResult = showingResult;
        maxOptions = optionTitles.length;
        this.optionTitles = optionTitles.clone();
        
        resetVoteData();
        bossBarManager.initializeBossBars(optionTitles);
        int[] initialCounts = new int[maxOptions];
        for (int i = 0; i < maxOptions; i++) {
            initialCounts[i] = voteCounts.get(i);
        }
        bossBarManager.updateBossBars(initialCounts);
        bossBarManager.showBossBars();

        voteActive = true;
        
        announceVoteStart();
        startBossBarUpdateTask();
        timerManager.startTimer(durationSeconds, this::endVote);

        return true;
    }
    
    private void announceVoteStart() {
        StringBuilder commandText = new StringBuilder();
        for (int i = 0; i < maxOptions; i++) {
            if (i > 0) commandText.append(", ");
            commandText.append("!투표").append(i + 1);
        }
        Bukkit.broadcast(Component.text("투표가 시작되었습니다! " + commandText + " 으로 투표하세요.", NamedTextColor.YELLOW));
    }
    
    private void resetVoteData() {
        voteCounts = new AtomicIntegerArray(maxOptions);
        synchronized (userVotes) {
            userVotes.clear();
        }
    }

    public void endVote() {
        if (!voteActive) {
            return;
        }

        voteActive = false;
        timerManager.cancelTimers();
        stopBossBarUpdateTask();
        bossBarManager.hideBossBars();
        
        int[] finalCounts = new int[maxOptions];
        for (int i = 0; i < maxOptions; i++) {
            finalCounts[i] = voteCounts.get(i);
        }
        resultManager.announceResults(optionTitles, finalCounts, showingResult);

        if (isApiVote) {
            resultManager.fireVoteEndEvent(optionTitles, finalCounts);
        }

        resetVoteData();
    }

    public void processVoteCommand(String username, String message) {
        if (!voteActive || !message.startsWith("!투표")) {
            return;
        }

        String formatted = message.replaceAll("\\s+", ""); // 모든 공백 제거

        int voteOption = -1;

        for (int i = 1; i <= maxOptions; i++) {
            if (formatted.equals("!투표" + i)) {
                voteOption = i - 1;
                break;
            }
        }

        if (voteOption == -1) {
            return;
        }

        synchronized (userVotes) {
            Integer previousVote = userVotes.get(username);
            if (previousVote != null) {
                voteCounts.decrementAndGet(previousVote);
            }

            userVotes.put(username, voteOption);
            voteCounts.incrementAndGet(voteOption);
        }
    }

    public void addPlayerToBossBars(Player player) {
        if (voteActive) {
            bossBarManager.addPlayerToBossBars(player);
        }
    }

    public void removePlayerFromBossBars(Player player) {
        bossBarManager.removePlayerFromBossBars(player);
    }

    public boolean isVoteActive() {
        return voteActive;
    }
    
    private void startBossBarUpdateTask() {
        bossBarUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (voteActive) {
                int[] currentCounts = new int[maxOptions];
                for (int i = 0; i < maxOptions; i++) {
                    currentCounts[i] = voteCounts.get(i);
                }
                bossBarManager.updateBossBars(currentCounts);
            }
        }, 0L, 20L); // 0 ticks delay, 20 ticks interval (1 second)
    }
    
    private void stopBossBarUpdateTask() {
        if (bossBarUpdateTask != null) {
            bossBarUpdateTask.cancel();
            bossBarUpdateTask = null;
        }
    }

}
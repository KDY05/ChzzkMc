package io.github.kdy05.chzzkMc.core;

import io.github.kdy05.chzzkMc.ChzzkMc;
import io.github.kdy05.chzzkMc.core.vote.VoteBossBarManager;
import io.github.kdy05.chzzkMc.core.vote.VoteResultManager;
import io.github.kdy05.chzzkMc.core.vote.VoteTimerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class VoteManager {

    private final VoteBossBarManager bossBarManager;
    private final VoteTimerManager timerManager;
    private final VoteResultManager resultManager;
    
    private boolean voteActive = false;
    private final Map<String, Integer> userVotes = new HashMap<>();
    private int[] voteCounts;
    private String[] optionTitles;
    private int maxOptions;
    private boolean isApiVote = false;
    
    public VoteManager(ChzzkMc plugin) {
        this.bossBarManager = new VoteBossBarManager();
        this.timerManager = new VoteTimerManager(plugin);
        this.resultManager = new VoteResultManager();
    }

    public boolean startVote(String[] optionTitles, int durationSeconds, boolean isApiVote) {
        if (voteActive) {
            return false;
        }

        this.isApiVote = isApiVote;
        maxOptions = optionTitles.length;
        this.optionTitles = optionTitles.clone();
        
        resetVoteData();
        bossBarManager.initializeBossBars(optionTitles);
        bossBarManager.updateBossBars(voteCounts);
        bossBarManager.showBossBars();

        voteActive = true;
        
        announceVoteStart();
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
        voteCounts = new int[maxOptions];
        userVotes.clear();
    }

    public void endVote() {
        if (!voteActive) {
            return;
        }

        voteActive = false;
        timerManager.cancelTimers();
        bossBarManager.hideBossBars();
        resultManager.announceResults(optionTitles, voteCounts);

        if (isApiVote) {
            resultManager.fireVoteEndEvent(optionTitles, voteCounts);
        }

        resetVoteData();
    }

    public boolean processVoteCommand(String username, String message) {
        if (!voteActive || !message.startsWith("!투표")) {
            return false;
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
            return false;
        }

        Integer previousVote = userVotes.get(username);
        if (previousVote != null) {
            voteCounts[previousVote]--;
        }

        userVotes.put(username, voteOption);
        voteCounts[voteOption]++;

        bossBarManager.updateBossBars(voteCounts);
        return true;
    }

    public void addPlayerToBossBars(Player player) {
        if (voteActive) {
            bossBarManager.addPlayerToBossBars(player);
        }
    }

    public void removePlayerFromBossBars(Player player) {
        bossBarManager.removePlayerFromBossBars(player);
    }

    public boolean forceEndVote() {
        if (!voteActive) {
            return false;
        }
        endVote();
        return true;
    }

    public boolean isVoteActive() {
        return voteActive;
    }
}
package io.github.kdy05.chzzkMc.core;

import io.github.kdy05.chzzkMc.ChzzkMc;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class VoteManager {
    
    private final ChzzkMc plugin;
    private boolean voteActive = false;
    private final Map<String, Integer> userVotes = new HashMap<>();
    private final int[] voteCounts = new int[3];
    private final BossBar[] voteBars = new BossBar[3];
    
    public VoteManager(ChzzkMc plugin) {
        this.plugin = plugin;
        initializeBossBars();
    }
    
    private void initializeBossBars() {
        voteBars[0] = BossBar.bossBar(
            Component.text("투표 1번", NamedTextColor.RED),
            0.0f,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
        );
        voteBars[1] = BossBar.bossBar(
            Component.text("투표 2번", NamedTextColor.GREEN),
            0.0f,
            BossBar.Color.GREEN,
            BossBar.Overlay.PROGRESS
        );
        voteBars[2] = BossBar.bossBar(
            Component.text("투표 3번", NamedTextColor.BLUE),
            0.0f,
            BossBar.Color.BLUE,
            BossBar.Overlay.PROGRESS
        );
    }
    
    public void startVote() {
        if (voteActive) {
            return;
        }
        
        voteActive = true;
        resetVotes();
        showBossBars();
        
        Bukkit.broadcast(Component.text("투표가 시작되었습니다! !투표1, !투표2, !투표3 으로 투표하세요.", NamedTextColor.YELLOW));
    }
    
    public void endVote() {
        if (!voteActive) {
            return;
        }
        
        voteActive = false;
        hideBossBars();
        
        announceResults();
        resetVotes();
    }
    
    public boolean processVoteCommand(String username, String message) {
        if (!voteActive || !message.startsWith("!투표")) {
            return false;
        }

        String formatted = message.replaceAll("\\s+", ""); // 모든 공백 제거
        
        int voteOption;
        switch (formatted) {
            case "!투표1":
                voteOption = 0;
                break;
            case "!투표2":
                voteOption = 1;
                break;
            case "!투표3":
                voteOption = 2;
                break;
            default:
                return false;
        }
        
        Integer previousVote = userVotes.get(username);
        if (previousVote != null) {
            voteCounts[previousVote]--;
        }
        
        userVotes.put(username, voteOption);
        voteCounts[voteOption]++;
        
        updateBossBars();
        return true;
    }
    
    private void updateBossBars() {
        int totalVotes = voteCounts[0] + voteCounts[1] + voteCounts[2];

        if (totalVotes == 0) {
            for (int i = 0; i < 3; i++) {
                voteBars[i].progress(0.0f);
                voteBars[i].name(Component.text("투표 " + (i + 1) + "번: " + "0표", getColorForOption(i)));
            }
            return;
        }

        for (int i = 0; i < 3; i++) {
            float progress = (float) voteCounts[i] / totalVotes;
            voteBars[i].progress(progress);
            voteBars[i].name(Component.text("투표 " + (i + 1) + "번: " + voteCounts[i] + "표", getColorForOption(i)));
        }
    }
    
    private NamedTextColor getColorForOption(int option) {
        return switch (option) {
            case 0 -> NamedTextColor.RED;
            case 1 -> NamedTextColor.GREEN;
            case 2 -> NamedTextColor.BLUE;
            default -> NamedTextColor.WHITE;
        };
    }
    
    private void showBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (BossBar bar : voteBars) {
                player.showBossBar(bar);
            }
        }
    }
    
    private void hideBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (BossBar bar : voteBars) {
                player.hideBossBar(bar);
            }
        }
    }
    
    private void announceResults() {
        int totalVotes = voteCounts[0] + voteCounts[1] + voteCounts[2];
        
        Bukkit.broadcast(Component.text("=== 투표 결과 ===", NamedTextColor.GOLD));
        Bukkit.broadcast(Component.text("총 투표수: " + totalVotes, NamedTextColor.YELLOW));
        
        for (int i = 0; i < 3; i++) {
            float percentage = totalVotes == 0 ? 0 : (float) voteCounts[i] / totalVotes * 100;
            Bukkit.broadcast(Component.text(
                String.format("투표 %d번: %d표 (%.1f%%)", i + 1, voteCounts[i], percentage),
                getColorForOption(i)
            ));
        }
    }
    
    private void resetVotes() {
        userVotes.clear();
        for (int i = 0; i < 3; i++) {
            voteCounts[i] = 0;
        }
        updateBossBars();
    }
    
    public boolean isVoteActive() {
        return voteActive;
    }
    
    public void addPlayerToBossBars(Player player) {
        if (voteActive) {
            for (BossBar bar : voteBars) {
                player.showBossBar(bar);
            }
        }
    }
    
    public void removePlayerFromBossBars(Player player) {
        for (BossBar bar : voteBars) {
            player.hideBossBar(bar);
        }
    }
}
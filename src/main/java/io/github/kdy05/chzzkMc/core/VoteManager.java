package io.github.kdy05.chzzkMc.core;

import io.github.kdy05.chzzkMc.ChzzkMc;
import io.github.kdy05.chzzkMc.api.event.VoteEndEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class VoteManager {
    
    private final ChzzkMc plugin;
    private boolean voteActive = false;
    private final Map<String, Integer> userVotes = new HashMap<>();
    private int[] voteCounts;
    private BossBar[] voteBars;
    private String[] optionTitles;
    private int maxOptions = 3;
    private BukkitTask voteTimer;
    private BukkitTask actionBarTimer;
    private boolean isApiVote = false;
    private int remainingSeconds = 0;
    
    public VoteManager(ChzzkMc plugin) {
        this.plugin = plugin;
    }

    public void startVote() {
        String[] configTitles = plugin.getConfig().getStringList("vote.titles").toArray(new String[0]);
        int configOptions = plugin.getConfig().getInt("vote.option", 3);
        int configDuration = plugin.getConfig().getInt("vote.durationSec", 120);

        String[] titles = new String[configOptions];
        for (int i = 0; i < configOptions; i++) {
            if (i < configTitles.length) {
                titles[i] = configTitles[i];
            } else {
                titles[i] = "투표 " + (i + 1) + "번";
            }
        }

        startVoteInternal(titles, configDuration, false);
    }

    private boolean startVoteInternal(String[] optionTitles, int durationSeconds, boolean isApi) {
        if (voteActive) {
            return false;
        }

        isApiVote = isApi;
        maxOptions = optionTitles.length;
        this.optionTitles = optionTitles.clone();
        resetArrays();
        initializeBossBars();

        voteActive = true;
        resetVotes();
        showBossBars();

        StringBuilder commandText = new StringBuilder();
        for (int i = 0; i < maxOptions; i++) {
            if (i > 0) commandText.append(", ");
            commandText.append("!투표").append(i + 1);
        }

        Bukkit.broadcast(Component.text("투표가 시작되었습니다! " + commandText + " 으로 투표하세요.", NamedTextColor.YELLOW));

        if (durationSeconds > 0) {
            remainingSeconds = durationSeconds;
            voteTimer = Bukkit.getScheduler().runTaskLater(plugin, this::endVote, durationSeconds * 20L);
            startActionBarTimer();
        }

        return true;
    }

    private void initializeBossBars() {
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

    public void endVote() {
        if (!voteActive) {
            return;
        }

        voteActive = false;
        if (voteTimer != null) {
            voteTimer.cancel();
            voteTimer = null;
        }
        if (actionBarTimer != null) {
            actionBarTimer.cancel();
            actionBarTimer = null;
        }

        hideBossBars();
        announceResults();

        // Api가 호출한 투표인 경우 종료 이벤트 발생
        if (isApiVote) {
            fireVoteEndEvent();
        }

        resetVotes();
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

        updateBossBars();
        return true;
    }

    private void updateBossBars() {
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

    private void announceResults() {
        int totalVotes = 0;
        for (int i = 0; i < maxOptions; i++) {
            totalVotes += voteCounts[i];
        }

        Bukkit.broadcast(Component.text("=== 투표 결과 ===", NamedTextColor.GOLD));
        Bukkit.broadcast(Component.text("총 투표수: " + totalVotes, NamedTextColor.YELLOW));

        for (int i = 0; i < maxOptions; i++) {
            float percentage = totalVotes == 0 ? 0 : (float) voteCounts[i] / totalVotes * 100;
            Bukkit.broadcast(Component.text(
                String.format("%s: %d표 (%.1f%%)", optionTitles[i], voteCounts[i], percentage),
                getColorForOption(i)
            ));
        }
    }

    private void resetArrays() {
        voteCounts = new int[maxOptions];
        voteBars = new BossBar[maxOptions];
    }

    private void resetVotes() {
        userVotes.clear();
        for (int i = 0; i < maxOptions; i++) {
            voteCounts[i] = 0;
        }
        updateBossBars();
    }

    private void showBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < maxOptions; i++) {
                player.showBossBar(voteBars[i]);
            }
        }
    }

    private void hideBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < maxOptions; i++) {
                player.hideBossBar(voteBars[i]);
            }
        }
    }

    // 색깔 매핑
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

    // 입장, 퇴장 관련
    public void addPlayerToBossBars(Player player) {
        if (voteActive) {
            for (int i = 0; i < maxOptions; i++) {
                player.showBossBar(voteBars[i]);
            }
        }
    }

    public void removePlayerFromBossBars(Player player) {
        for (int i = 0; i < maxOptions; i++) {
            player.hideBossBar(voteBars[i]);
        }
    }

    // API 관련
    public boolean startApiVote(String[] optionTitles, int durationSeconds) {
        return startVoteInternal(optionTitles, durationSeconds, true);
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

    private void fireVoteEndEvent() {
        int winningOption = getWinningOption();
        int totalVotes = 0;
        for (int i = 0; i < maxOptions; i++) {
            totalVotes += voteCounts[i];
        }

        VoteEndEvent event = new VoteEndEvent(winningOption, totalVotes, optionTitles, voteCounts);
        Bukkit.getPluginManager().callEvent(event);
    }

    private int getWinningOption() {
        int maxVotes = -1;
        int winningOption = 1;

        for (int i = 0; i < maxOptions; i++) {
            if (voteCounts[i] > maxVotes) {
                maxVotes = voteCounts[i];
                winningOption = i + 1;
            }
        }

        return winningOption;
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
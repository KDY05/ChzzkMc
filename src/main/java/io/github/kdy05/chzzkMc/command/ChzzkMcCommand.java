package io.github.kdy05.chzzkMc.command;

import io.github.kdy05.chzzkMc.ChzzkMc;
import io.github.kdy05.chzzkMc.core.ChatManager;
import io.github.kdy05.chzzkMc.core.VoteManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChzzkMcCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUB_COMMANDS = Arrays.asList("help", "reload", "vote");
    
    private final ChzzkMc plugin;
    private final VoteManager voteManager;
    private final ChatManager chatManager;
    
    public ChzzkMcCommand(ChzzkMc plugin, VoteManager voteManager, ChatManager chatManager) {
        this.plugin = plugin;
        this.voteManager = voteManager;
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("chzzkmc.use")) {
            sender.sendMessage(Component.text("운영자 권한이 필요합니다.", NamedTextColor.RED));
            return false;
        }

        if (args.length == 0) {
            return false;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help" -> handleHelp(sender);
            case "reload" -> handleReload(sender);
            case "vote" -> handleVote(sender, args);
            default -> sender.sendMessage(Component.text("잘못된 사용", NamedTextColor.RED));
        }

        return false;
    }

    private void handleHelp(CommandSender sender) {
        sender.sendMessage(Component.text("/cm help: 이 메시지를 띄웁니다.", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/cm reload: config.yml 설정을 불러옵니다.", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/cm vote <start|end>: 투표를 시작/종료합니다.", NamedTextColor.GOLD));
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        chatManager.reconnect();
        voteManager.reloadConfig();
        sender.sendMessage(Component.text("config.yml 설정이 새로고침되었습니다.", NamedTextColor.GREEN));
    }

    private void handleVote(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /cm vote <start|end>", NamedTextColor.RED));
            return;
        }

        String voteCommand = args[1].toLowerCase();
        switch (voteCommand) {
            case "start" -> {
                if (voteManager.isVoteActive()) {
                    sender.sendMessage(Component.text("이미 투표가 진행중입니다.", NamedTextColor.RED));
                    return;
                }
                
                // config에서 투표 설정 읽기
                String[] configTitles = plugin.getConfig().getStringList("vote.titles").toArray(new String[0]);
                int configOptions = plugin.getConfig().getInt("vote.option", 3);
                int configDuration = plugin.getConfig().getInt("vote.durationSec", 120);
                
                // 투표 제목 배열 구성
                String[] titles = new String[configOptions];
                for (int i = 0; i < configOptions; i++) {
                    if (i < configTitles.length) {
                        titles[i] = configTitles[i];
                    } else {
                        titles[i] = "투표 " + (i + 1) + "번";
                    }
                }
                
                boolean success = voteManager.startVote(titles, configDuration, false);
                if (success) {
                    sender.sendMessage(Component.text("투표가 시작되었습니다!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("투표 시작에 실패했습니다.", NamedTextColor.RED));
                }
            }
            case "end" -> {
                if (!voteManager.isVoteActive()) {
                    sender.sendMessage(Component.text("진행중인 투표가 없습니다.", NamedTextColor.RED));
                    return;
                }
                voteManager.endVote();
                sender.sendMessage(Component.text("투표가 종료되었습니다!", NamedTextColor.GREEN));
            }
            default -> sender.sendMessage(Component.text("사용법: /cm vote <start|end>", NamedTextColor.RED));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("chzzkmc.use")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            return filterCompletions(args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("vote")) {
            List<String> voteCommands = Arrays.asList("start", "end");
            return voteCommands.stream()
                    .filter(completion -> completion.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return completions;
    }

    @NotNull
    private List<String> filterCompletions(@NotNull String input) {
        return ChzzkMcCommand.SUB_COMMANDS.stream()
                .filter(completion -> completion.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }
}

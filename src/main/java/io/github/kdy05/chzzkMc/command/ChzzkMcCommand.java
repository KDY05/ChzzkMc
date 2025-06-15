package io.github.kdy05.chzzkMc.command;

import io.github.kdy05.chzzkMc.ChzzkMc;
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

    private static final List<String> SUB_COMMANDS = Arrays.asList("help", "reload");

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
            default -> sender.sendMessage(Component.text("잘못된 사용", NamedTextColor.RED));
        }

        return false;
    }

    private void handleHelp(CommandSender sender) {
        sender.sendMessage(Component.text("/cm help: 이 메시지를 띄웁니다.", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/cm reload: config.yml 설정을 불러옵니다.", NamedTextColor.GOLD));
    }

    private void handleReload(CommandSender sender) {
        ChzzkMc.getPlugin().reloadConfig();
        ChzzkMc.getChatManager().reconnect();
        sender.sendMessage(Component.text("config.yml 설정이 새로고침되었습니다.", NamedTextColor.GREEN));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("chzzkmc.use")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            return filterCompletions(args[0]);
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

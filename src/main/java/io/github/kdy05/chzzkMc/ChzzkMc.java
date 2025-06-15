package io.github.kdy05.chzzkMc;

import io.github.kdy05.chzzkMc.command.ChzzkMcCommand;
import io.github.kdy05.chzzkMc.core.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder;

import java.util.Objects;

public final class ChzzkMc extends JavaPlugin {

    private static ChzzkMc plugin;
    private static ChatManager chatManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        plugin = this;

        ChzzkClient client = new ChzzkClientBuilder(
                getConfig().getString("API_CLIENT_ID", "client_id"),
                getConfig().getString("API_SECRET", "secret"))
                .build();

        chatManager = new ChatManager(this, client);
        chatManager.initialize();

        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("chzzkmc")).setExecutor(new ChzzkMcCommand());

        getLogger().info("플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        if (chatManager != null) {
            chatManager.disconnect();
        }
        getLogger().info("플러그인이 비활성화되었습니다.");
    }

    public static ChzzkMc getPlugin() {
        return plugin;
    }

    public static ChatManager getChatManager() {
        return chatManager;
    }
}

package io.github.kdy05.chzzkMc;

import io.github.kdy05.chzzkMc.api.ChzzkMcProvider;
import io.github.kdy05.chzzkMc.command.ChzzkMcCommand;
import io.github.kdy05.chzzkMc.core.ChatManager;
import io.github.kdy05.chzzkMc.listener.PlayerEventListener;
import io.github.kdy05.chzzkMc.core.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder;

import java.util.Objects;

public final class ChzzkMc extends JavaPlugin {

    private VoteManager voteManager;
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ChzzkClient client = new ChzzkClientBuilder().build();
        voteManager = new VoteManager(this);
        chatManager = new ChatManager(this, client);

        chatManager.initialize();
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("chzzkmc"))
                .setExecutor(new ChzzkMcCommand(this, voteManager, chatManager));
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(voteManager), this);
        ChzzkMcProvider.initialize(this, voteManager);

        getLogger().info("플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        if (chatManager != null) {
            chatManager.disconnect();
        }
        ChzzkMcProvider.shutdown();
        getLogger().info("플러그인이 비활성화되었습니다.");
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }
}

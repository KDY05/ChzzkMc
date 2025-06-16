package io.github.kdy05.chzzkMc.listener;

import io.github.kdy05.chzzkMc.core.VoteManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    
    private final VoteManager voteManager;
    
    public PlayerEventListener(VoteManager voteManager) {
        this.voteManager = voteManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        voteManager.addPlayerToBossBars(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        voteManager.removePlayerFromBossBars(event.getPlayer());
    }

}
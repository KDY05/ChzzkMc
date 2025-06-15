package io.github.kdy05.chzzkMc.listener;

import io.github.kdy05.chzzkMc.ChzzkMc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ChzzkMc.getVoteManager().addPlayerToBossBars(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ChzzkMc.getVoteManager().removePlayerFromBossBars(event.getPlayer());
    }
}
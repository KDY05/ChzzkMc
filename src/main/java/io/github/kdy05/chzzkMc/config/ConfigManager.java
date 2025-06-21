package io.github.kdy05.chzzkMc.config;

import io.github.kdy05.chzzkMc.ChzzkMc;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Manager class for handling plugin configuration
 */
public class ConfigManager {
    
    private final ChzzkMc plugin;
    private FileConfiguration config;
    
    public ConfigManager(ChzzkMc plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    /**
     * Reload configuration from file
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // Channel settings
    public String getChannelId() {
        return config.getString("channel-id", "");
    }
    
    public void setChannelId(String channelId) {
        config.set("channel-id", channelId);
        saveConfig();
    }
    
    // Chat broadcast settings
    public boolean isBroadcastChatEnabled() {
        return config.getBoolean("broadcast-chat", false);
    }
    
    public void setBroadcastChatEnabled(boolean enabled) {
        config.set("broadcast-chat", enabled);
        saveConfig();
    }
    
    // Timer display settings
    public TimerDisplayType getTimerDisplayType() {
        String value = config.getString("display.timer", "action_bar");
        return TimerDisplayType.fromConfigValue(value);
    }
    
    public void setTimerDisplayType(TimerDisplayType type) {
        config.set("display.timer", type.getConfigValue());
        saveConfig();
    }
    
    // Vote settings
    public int getVoteOptionCount() {
        return config.getInt("vote.option", 3);
    }
    
    public void setVoteOptionCount(int count) {
        if (count < 2 || count > 4) {
            throw new IllegalArgumentException("Vote option count must be between 2 and 4");
        }
        config.set("vote.option", count);
        saveConfig();
    }
    
    public String[] getVoteTitles() {
        List<String> titlesList = config.getStringList("vote.titles");
        return titlesList.toArray(new String[0]);
    }
    
    public void setVoteTitles(String[] titles) {
        if (titles.length < 2 || titles.length > 4) {
            throw new IllegalArgumentException("Vote titles array must have 2-4 elements");
        }
        config.set("vote.titles", List.of(titles));
        saveConfig();
    }
    
    public int getVoteDurationSec() {
        return config.getInt("vote.durationSec", 120);
    }
    
    public void setVoteDurationSec(int durationSec) {
        if (durationSec <= 0) {
            throw new IllegalArgumentException("Vote duration must be positive");
        }
        config.set("vote.durationSec", durationSec);
        saveConfig();
    }
    
    /**
     * Save configuration to file
     */
    private void saveConfig() {
        try {
            plugin.saveConfig();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }
    
    /**
     * Get the underlying FileConfiguration
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
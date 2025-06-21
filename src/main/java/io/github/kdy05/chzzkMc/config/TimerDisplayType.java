package io.github.kdy05.chzzkMc.config;

/**
 * Enumeration for timer display types
 */
public enum TimerDisplayType {
    ACTION_BAR("action_bar"),
    CHAT("chat");
    
    private final String configValue;
    
    TimerDisplayType(String configValue) {
        this.configValue = configValue;
    }
    
    public String getConfigValue() {
        return configValue;
    }
    
    /**
     * Get TimerDisplayType from config value
     * @param configValue The config value string
     * @return TimerDisplayType or ACTION_BAR as default
     */
    public static TimerDisplayType fromConfigValue(String configValue) {
        for (TimerDisplayType type : values()) {
            if (type.configValue.equalsIgnoreCase(configValue)) {
                return type;
            }
        }
        return ACTION_BAR; // default
    }
}
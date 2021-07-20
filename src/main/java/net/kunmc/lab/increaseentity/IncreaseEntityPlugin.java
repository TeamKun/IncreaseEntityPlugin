package net.kunmc.lab.increaseentity;

import net.kunmc.lab.increaseentity.config.ConfigCommand;
import net.kunmc.lab.increaseentity.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class IncreaseEntityPlugin extends JavaPlugin {
    private static IncreaseEntityPlugin instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.load();
        ConfigCommand configCommand = new ConfigCommand();
        Objects.requireNonNull(getCommand("ieconfig")).setExecutor(configCommand);
        Objects.requireNonNull(getCommand("ieconfig")).setTabCompleter(configCommand);
        ShowCommand showCommand = new ShowCommand();
        Objects.requireNonNull(getCommand("ieshow")).setExecutor(showCommand);
        Objects.requireNonNull(getCommand("ieshow")).setTabCompleter(showCommand);
    }

    public void onConfigChanged(String path, Object value) {
    }

    public static IncreaseEntityPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}

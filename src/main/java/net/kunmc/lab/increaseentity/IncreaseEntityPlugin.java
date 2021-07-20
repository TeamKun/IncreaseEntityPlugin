package net.kunmc.lab.increaseentity;

import net.kunmc.lab.increaseentity.config.ConfigCommand;
import net.kunmc.lab.increaseentity.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class IncreaseEntityPlugin extends JavaPlugin {
    private static IncreaseEntityPlugin instance;
    private ConfigManager configManager;
    private BukkitRunnable runnable;

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
        updateRunnable();
    }

    public void onConfigChanged(String path, Object value) {
        updateRunnable();
    }

    public void updateRunnable() {
        ConfigManager configManager = getConfigManager();
        int interval = configManager.getInterval();
        int increasePerTime = configManager.getIncreasePerTime();
        int distance = configManager.getDistance();
        Set<UUID> activatedPlayers = new HashSet<>(configManager.getActivatedPlayers());
        if (runnable != null) {
            runnable.cancel();
        }
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!activatedPlayers.contains(player.getUniqueId())) {
                        continue;
                    }
                    execute(player, distance, increasePerTime);
                }
            }
        };
        runnable.runTaskTimer(this, 0, interval);
    }

    public void execute(Player player, int distance, int numIncrease) {
        Entity targetEntity = player.getTargetEntity(distance);
        if (targetEntity == null) {
            return;
        }
        increase(targetEntity, numIncrease);
    }

    public void increase(Entity entity, int numIncrease) {
        System.out.println("ふえるよ!" + numIncrease + ", " + entity);
    }

    public static IncreaseEntityPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}

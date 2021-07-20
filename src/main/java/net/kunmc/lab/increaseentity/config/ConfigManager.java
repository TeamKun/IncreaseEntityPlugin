package net.kunmc.lab.increaseentity.config;

import net.kunmc.lab.increaseentity.IncreaseEntityPlugin;
import net.kunmc.lab.increaseentity.config.parser.IntParser;
import net.kunmc.lab.increaseentity.config.parser.Parser;
import net.kunmc.lab.increaseentity.config.parser.PlayerListParser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class ConfigManager {
    private FileConfiguration config;
    private static final Map<String, Parser<?>> CONFIGS = new HashMap<>() {{
        put("activatedPlayers", new PlayerListParser());
        put("distance", new IntParser(1, Integer.MAX_VALUE));
        put("interval", new IntParser(1, Integer.MAX_VALUE));
        put("increasePerTime", new IntParser(1, Integer.MAX_VALUE));
    }};

    public static String[] getConfigPaths() {
        return CONFIGS.keySet().toArray(new String[0]);
    }

    public void load() {
        IncreaseEntityPlugin plugin = IncreaseEntityPlugin.getInstance();
        plugin.saveDefaultConfig();
        if (config != null) {
            plugin.reloadConfig();
        }
        config = plugin.getConfig();
    }

    public boolean setConfig(String path, String valueString) {
        if (!CONFIGS.containsKey(path)) {
            return false;
        }
        Parser<?> parser = CONFIGS.get(path);
        if (!parser.canUseFromCommand()) {
            return false;
        }
        Object value = parser.parse(valueString);
        if (value == null) {
            return false;
        }
        IncreaseEntityPlugin plugin = IncreaseEntityPlugin.getInstance();
        config.set(path, value);
        plugin.saveConfig();
        plugin.onConfigChanged(path, value);
        return true;
    }

    public List<UUID> getActivatedPlayers() {
        List<UUID> activatedPlayers = new ArrayList<>();
        for (String uuidString : config.getStringList("activatedPlayers")) {
            activatedPlayers.add(UUID.fromString(uuidString));
        }
        return activatedPlayers;
    }

    public int getDistance() {
        return config.getInt("distance");
    }

    public int getInterval() {
        return config.getInt("interval");
    }

    public int getIncreasePerTime() {
        return config.getInt("increasePerTime");
    }
}

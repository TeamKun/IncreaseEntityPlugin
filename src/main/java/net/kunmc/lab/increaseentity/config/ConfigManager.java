package net.kunmc.lab.increaseentity.config;

import net.kunmc.lab.increaseentity.IncreaseEntityPlugin;
import net.kunmc.lab.increaseentity.config.parser.IntParser;
import net.kunmc.lab.increaseentity.config.parser.Parser;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {
    private static final Map<String, Parser<?>> CONFIGS = new HashMap<>() {{
        put("distance", new IntParser(1, Integer.MAX_VALUE));
        put("interval", new IntParser(1, Integer.MAX_VALUE));
        put("increasePerTime", new IntParser(1, Integer.MAX_VALUE));
    }};
    private FileConfiguration config;

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
        Object value = parser.parse(valueString);
        return setConfig(path, value);
    }

    private boolean setConfig(String path, Object value) {
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
        for (String uuid : config.getStringList("activatedPlayers")) {
            activatedPlayers.add(UUID.fromString(uuid));
        }
        return activatedPlayers;
    }

    public int addActivatedPlayers(List<UUID> add) {
        List<UUID> list = getActivatedPlayers();
        int added = -list.size();
        list.addAll(add);
        List<String> stringList = list.stream().distinct().map(UUID::toString).collect(Collectors.toList());
        added += stringList.size();
        setConfig("activatedPlayers", stringList);
        return added;
    }

    public int removeActivatedPlayers(List<UUID> remove) {
        List<UUID> list = getActivatedPlayers();
        int removed = list.size();
        list.removeAll(new HashSet<>(remove));
        List<String> stringList = list.stream().map(UUID::toString).collect(Collectors.toList());
        removed -= stringList.size();
        setConfig("activatedPlayers", stringList);
        return removed;
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

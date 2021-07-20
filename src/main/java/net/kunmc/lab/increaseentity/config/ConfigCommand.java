package net.kunmc.lab.increaseentity.config;

import net.kunmc.lab.increaseentity.IncreaseEntityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
        if (args.length == 2 && (args[0].equals("on") || args[0].equals("off"))) {
            List<UUID> entities = Bukkit.selectEntities(sender, args[1]).stream()
                    .filter(entity -> entity instanceof Player)
                    .map(Entity::getUniqueId)
                    .collect(Collectors.toList());
            if (args[0].equals("on")) {
                configManager.addActivatedPlayers(entities);
            } else {
                configManager.removeActivatedPlayers(entities);
            }
            sender.sendMessage(String.format("%d人のプレイヤーを%sに設定しました", entities.size(), args[0]));
            return true;
        }
        if (args.length == 1 && args[0].equals("reload")) {
            configManager.load();
            sender.sendMessage("コンフィグをリロードしました");
            return true;
        }
        if (args.length == 3 && args[0].equals("set")) {
            String path = args[1];
            String value = args[2];
            boolean result = configManager.setConfig(path, value);
            if (result) {
                sender.sendMessage(path + "を" + value + "にセットしました");
            } else {
                sender.sendMessage(ChatColor.RED + "コンフィグの設定に失敗しました");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return suggest(args[0], "reload", "set", "on", "off");
        } else if (args.length == 2 && args[0].equals("set")) {
            return suggest(args[1], ConfigManager.getConfigPaths());
        } else {
            return null;
        }
    }

    private List<String> suggest(String arg, String... candidates) {
        List<String> result = new ArrayList<>();
        for (String candidate : candidates) {
            if (candidate.startsWith(arg)) {
                result.add(candidate);
            }
        }
        return result;
    }
}

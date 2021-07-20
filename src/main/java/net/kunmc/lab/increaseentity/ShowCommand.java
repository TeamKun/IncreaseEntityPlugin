package net.kunmc.lab.increaseentity;

import net.kunmc.lab.increaseentity.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShowCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
            List<UUID> activatedPlayers = configManager.getActivatedPlayers();
            Set<UUID> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toSet());
            sender.sendMessage(activatedPlayers.size() + "人のプレイヤーが有効化されています：");
            StringJoiner joiner = new StringJoiner(", ");
            for (UUID uuid : activatedPlayers) {
                String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                ChatColor color = onlinePlayers.contains(uuid) ? ChatColor.GREEN : ChatColor.RED;
                joiner.add(color + playerName + ChatColor.RESET);
            }
            sender.sendMessage(joiner.toString());
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}

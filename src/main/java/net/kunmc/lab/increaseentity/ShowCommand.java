package net.kunmc.lab.increaseentity;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kunmc.lab.increaseentity.config.ConfigManager;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShowCommand {
    public static void register(CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> builder = LiteralArgumentBuilder.<CommandListenerWrapper>literal("ieshow")
                .requires(clw -> clw.getBukkitSender().hasPermission("increaseentity.showcommand"))
                .executes(ShowCommand::show);
        dispatcher.register(builder);
    }

    private static int show(CommandContext<CommandListenerWrapper> context) {
        ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
        List<UUID> activatedPlayers = configManager.getActivatedPlayers();
        Set<UUID> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        context.getSource().sendMessage(new ChatComponentText(activatedPlayers.size() + "人のプレイヤーが有効化されています："), false);
        StringJoiner joiner = new StringJoiner(", ");
        for (UUID uuid : activatedPlayers) {
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            ChatColor color = onlinePlayers.contains(uuid) ? ChatColor.GREEN : ChatColor.RED;
            joiner.add(color + playerName + ChatColor.RESET);
        }
        context.getSource().sendMessage(new ChatComponentText(joiner.toString()), false);
        return 0;
    }
}

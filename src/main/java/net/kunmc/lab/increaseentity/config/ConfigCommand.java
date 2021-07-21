package net.kunmc.lab.increaseentity.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kunmc.lab.increaseentity.IncreaseEntityPlugin;
import net.minecraft.server.v1_16_R3.ArgumentEntity;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> builder = LiteralArgumentBuilder.<CommandListenerWrapper>literal("ieconfig")
                .requires(clw -> clw.getBukkitSender().hasPermission("increaseentity.configcommand"));
        builder.then(net.minecraft.server.v1_16_R3.CommandDispatcher.a("on")
                .then(net.minecraft.server.v1_16_R3.CommandDispatcher.a("targets", ArgumentEntity.d())
                .executes(ConfigCommand::on)));
        builder.then(net.minecraft.server.v1_16_R3.CommandDispatcher.a("off")
                .then(net.minecraft.server.v1_16_R3.CommandDispatcher.a("targets", ArgumentEntity.d())
                .executes(ConfigCommand::off)));
        builder.then(net.minecraft.server.v1_16_R3.CommandDispatcher.a("reload")
                .executes(ConfigCommand::reload));
        LiteralArgumentBuilder<CommandListenerWrapper> setBuilder = net.minecraft.server.v1_16_R3.CommandDispatcher.a("set");
        for (String path : ConfigManager.getConfigPaths()) {
            setBuilder.then(net.minecraft.server.v1_16_R3.CommandDispatcher.a(path)
                    .then(net.minecraft.server.v1_16_R3.CommandDispatcher.a("value", StringArgumentType.word())
                    .executes(context -> set(context, path))));
        }
        builder.then(setBuilder);
        dispatcher.register(builder);
    }

    private static int on(CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
        Collection<EntityPlayer> targets = ArgumentEntity.f(context, "targets");
        List<UUID> entities = targets.stream()
                .map(EntityPlayer::getUniqueID)
                .collect(Collectors.toList());
        int result = configManager.addActivatedPlayers(entities);
        context.getSource().sendMessage(new ChatComponentText(result + "人のプレイヤーをonに設定しました"), false);
        return 0;
    }

    private static int off(CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
        Collection<EntityPlayer> targets = ArgumentEntity.f(context, "targets");
        List<UUID> entities = targets.stream()
                .map(EntityPlayer::getUniqueID)
                .collect(Collectors.toList());
        int result = configManager.removeActivatedPlayers(entities);
        context.getSource().sendMessage(new ChatComponentText(result + "人のプレイヤーをoffに設定しました"), false);
        return 0;
    }

    private static int set(CommandContext<CommandListenerWrapper> context, String path) {
        ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
        String value = StringArgumentType.getString(context, "value");
        boolean result = configManager.setConfig(path, value);
        if (result) {
            context.getSource().sendMessage(new ChatComponentText(path + "を" + value + "にセットしました"), false);
        } else {
            context.getSource().sendMessage(new ChatComponentText(ChatColor.RED + "コンフィグの設定に失敗しました"), false);
        }
        return 0;
    }

    private static int reload(CommandContext<CommandListenerWrapper> context) {
        ConfigManager configManager = IncreaseEntityPlugin.getInstance().getConfigManager();
        configManager.load();
        context.getSource().sendMessage(new ChatComponentText("コンフィグをリロードしました"), false);
        return 0;
    }
}

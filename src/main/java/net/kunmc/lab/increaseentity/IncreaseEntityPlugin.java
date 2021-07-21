package net.kunmc.lab.increaseentity;

import com.mojang.brigadier.CommandDispatcher;
import net.kunmc.lab.increaseentity.config.ConfigCommand;
import net.kunmc.lab.increaseentity.config.ConfigManager;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class IncreaseEntityPlugin extends JavaPlugin {
    private static IncreaseEntityPlugin instance;
    private ConfigManager configManager;
    private BukkitRunnable runnable;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.load();
        CommandDispatcher<CommandListenerWrapper> dispatcher = ((CraftServer)Bukkit.getServer()).getServer().vanillaCommandDispatcher.a();
        ConfigCommand.register(dispatcher);
        ShowCommand.register(dispatcher);
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
        if (targetEntity.getType() == EntityType.PLAYER || !(targetEntity instanceof LivingEntity)) {
            return;
        }
        for (int i = 0; i < numIncrease; i++) {
            copy((LivingEntity)targetEntity);
        }
    }

    public void copy(LivingEntity entity) {
        World world = entity.getWorld();
        LivingEntity spawned = (LivingEntity)world.spawnEntity(entity.getLocation(), entity.getType());
        EntityLiving source = ((CraftLivingEntity)entity).getHandle();
        EntityLiving dest = ((CraftLivingEntity)spawned).getHandle();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        source.save(nbtTagCompound);
        source.saveData(nbtTagCompound);
        dest.load(nbtTagCompound);
        dest.loadData(nbtTagCompound);
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        spawned.setVelocity(spawned.getVelocity().add(new Vector(rand.nextDouble() / 10, 0, rand.nextDouble() / 10)));
    }

    public static IncreaseEntityPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}

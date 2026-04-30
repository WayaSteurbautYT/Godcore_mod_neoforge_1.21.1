package com.godcore.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SkinManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkinManager.class);
    
    private GameProfile currentProfile;
    private EntityType<?> currentForm;
    
    public SkinManager() {
        this.currentProfile = new GameProfile(UUID.randomUUID(), "Godcore");
    }
    
    public void setSkinFromURL(String skinURL) {
        // Apply skin from URL
        LOGGER.info("Setting skin from URL: {}", skinURL);
        // This would use a skin API to set the skin
    }
    
    public void setSkinFromPlayer(ServerPlayer player) {
        this.currentProfile = player.getGameProfile();
        LOGGER.info("Set skin from player: {}", player.getName().getString());
    }
    
    public void setSkinFromUUID(String uuid) {
        // Set skin from Minecraft UUID
        this.currentProfile = new GameProfile(UUID.fromString(uuid), "Godcore");
        LOGGER.info("Set skin from UUID: {}", uuid);
    }
    
    public GameProfile getCurrentProfile() {
        return currentProfile;
    }
    
    public void transformToMob(EntityType<?> mobType, Level level) {
        this.currentForm = mobType;
        LOGGER.info("Transforming to mob: {}", mobType.getDescriptionId());
        // This would handle the actual transformation logic
    }
    
    public void transformToPlayer() {
        this.currentForm = EntityType.PLAYER;
        LOGGER.info("Transforming to player form");
    }
    
    public EntityType<?> getCurrentForm() {
        return currentForm;
    }
    
    public boolean isPlayerForm() {
        return currentForm == null || currentForm == EntityType.PLAYER;
    }
}

package com.godcore.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class PermissionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManager.class);
    private static PermissionManager instance;
    
    public enum PermissionTier {
        PLAYER,
        MODERATOR,
        ADMIN
    }
    
    public enum Permission {
        // Basic permissions
        CHAT,
        SUMMON,
        REQUEST_HELP,
        SIMPLE_BUILD,
        
        // Moderator permissions
        INSPECT_GRIEF,
        PATROL_AREA,
        MUTE_AI,
        VIEW_MEMORY,
        
        // Admin permissions
        RUN_SCRIPTS,
        WORLD_EDIT,
        CONFIG_AI,
        MEMORY_WIPE,
        UNDO_ROLLBACK,
        FREEZE_DISABLE,
        EDIT_PROMPT,
        CHANGE_PERSONALITY,
        APPROVE_ALWAYS_ALLOW
    }
    
    private final Map<String, PermissionTier> playerPermissions = new HashMap<>();
    private final Map<String, Set<Permission>> alwaysAllowedActions = new HashMap<>();
    
    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }
    
    public void setPlayerPermission(String playerUUID, PermissionTier tier) {
        playerPermissions.put(playerUUID, tier);
        LOGGER.info("Set {} permission to: {}", playerUUID, tier);
    }
    
    public PermissionTier getPlayerPermission(String playerUUID) {
        return playerPermissions.getOrDefault(playerUUID, PermissionTier.PLAYER);
    }
    
    public boolean hasPermission(String playerUUID, Permission permission) {
        PermissionTier tier = getPlayerPermission(playerUUID);
        
        return switch (permission) {
            case CHAT, SUMMON, REQUEST_HELP, SIMPLE_BUILD -> true;
            case INSPECT_GRIEF, PATROL_AREA, MUTE_AI, VIEW_MEMORY -> tier == PermissionTier.MODERATOR || tier == PermissionTier.ADMIN;
            case RUN_SCRIPTS, WORLD_EDIT, CONFIG_AI, MEMORY_WIPE, UNDO_ROLLBACK, 
                 FREEZE_DISABLE, EDIT_PROMPT, CHANGE_PERSONALITY, APPROVE_ALWAYS_ALLOW -> tier == PermissionTier.ADMIN;
        };
    }
    
    public boolean canExecuteCommand(String playerUUID, String command) {
        PermissionTier tier = getPlayerPermission(playerUUID);
        
        return switch (command) {
            case "god activate", "god chat", "god summon", "god sleep" -> true;
            case "god voice on", "god voice off" -> tier != PermissionTier.PLAYER;
            case "god undo", "god rollback" -> tier == PermissionTier.ADMIN;
            case "god freeze", "god disable", "god memory clear" -> tier == PermissionTier.ADMIN;
            case "god prompt edit", "god personality" -> tier == PermissionTier.ADMIN;
            case "god build", "god create" -> tier != PermissionTier.PLAYER;
            default -> false;
        };
    }
    
    public void setAlwaysAllowed(String playerUUID, String actionType) {
        alwaysAllowedActions.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(Permission.valueOf(actionType.toUpperCase()));
        LOGGER.info("Set always allow for {}: {}", playerUUID, actionType);
    }
    
    public boolean isAlwaysAllowed(String playerUUID, Permission permission) {
        Set<Permission> allowed = alwaysAllowedActions.get(playerUUID);
        return allowed != null && allowed.contains(permission);
    }
    
    public void clearAlwaysAllowed(String playerUUID) {
        alwaysAllowedActions.remove(playerUUID);
        LOGGER.info("Cleared always allow for: {}", playerUUID);
    }
}

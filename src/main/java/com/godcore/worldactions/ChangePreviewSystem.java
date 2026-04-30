package com.godcore.worldactions;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangePreviewSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangePreviewSystem.class);
    
    private static final Map<String, List<WorldChange>> pendingChanges = new HashMap<>();
    private static final Map<String, WorldChange> previewChanges = new HashMap<>();
    
    public static class WorldChange {
        public String changeId;
        public String changeType;
        public String description;
        public BlockPos position;
        public BlockState beforeState;
        public BlockState afterState;
        public String entityType;
        public String itemId;
        public String guiId;
        public long timestamp;
        
        public WorldChange(String changeId, String changeType, String description) {
            this.changeId = changeId;
            this.changeType = changeType;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static WorldChange createBlockChange(BlockPos pos, BlockState before, BlockState after) {
        WorldChange change = new WorldChange(
            "block_" + System.currentTimeMillis(),
            "block_change",
            "Change block at " + pos.toString()
        );
        change.position = pos;
        change.beforeState = before;
        change.afterState = after;
        return change;
    }
    
    public static WorldChange createEntitySpawn(String entityType, BlockPos pos) {
        WorldChange change = new WorldChange(
            "entity_" + System.currentTimeMillis(),
            "entity_spawn",
            "Spawn " + entityType + " at " + pos.toString()
        );
        change.entityType = entityType;
        change.position = pos;
        return change;
    }
    
    public static WorldChange createItemCreation(String itemId, String properties) {
        WorldChange change = new WorldChange(
            "item_" + System.currentTimeMillis(),
            "item_creation",
            "Create item " + itemId
        );
        change.itemId = itemId;
        return change;
    }
    
    public static WorldChange createGUIModification(String guiId, String modificationType) {
        WorldChange change = new WorldChange(
            "gui_" + System.currentTimeMillis(),
            "gui_modification",
            "Modify GUI " + guiId
        );
        change.guiId = guiId;
        return change;
    }
    
    public static void addPendingChange(String sessionId, WorldChange change) {
        pendingChanges.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(change);
        LOGGER.info("Added pending change: {} for session: {}", change.changeId, sessionId);
    }
    
    public static List<WorldChange> getPendingChanges(String sessionId) {
        return pendingChanges.getOrDefault(sessionId, new ArrayList<>());
    }
    
    public static void setPreviewChange(String changeId, WorldChange change) {
        previewChanges.put(changeId, change);
        LOGGER.info("Set preview change: {}", changeId);
    }
    
    public static WorldChange getPreviewChange(String changeId) {
        return previewChanges.get(changeId);
    }
    
    public static void approveChange(String changeId) {
        WorldChange change = previewChanges.remove(changeId);
        if (change != null) {
            applyChange(change);
            LOGGER.info("Approved and applied change: {}", changeId);
        }
    }
    
    public static void denyChange(String changeId) {
        previewChanges.remove(changeId);
        LOGGER.info("Denied change: {}", changeId);
    }
    
    public static void approveAllChanges(String sessionId) {
        List<WorldChange> changes = pendingChanges.remove(sessionId);
        if (changes != null) {
            for (WorldChange change : changes) {
                applyChange(change);
            }
            LOGGER.info("Approved and applied all changes for session: {}", sessionId);
        }
    }
    
    public static void denyAllChanges(String sessionId) {
        pendingChanges.remove(sessionId);
        LOGGER.info("Denied all changes for session: {}", sessionId);
    }
    
    private static void applyChange(WorldChange change) {
        switch (change.changeType) {
            case "block_change":
                applyBlockChange(change);
                break;
            case "entity_spawn":
                applyEntitySpawn(change);
                break;
            case "item_creation":
                applyItemCreation(change);
                break;
            case "gui_modification":
                applyGUIModification(change);
                break;
        }
    }
    
    private static void applyBlockChange(WorldChange change) {
        // Apply block change to the world
        LOGGER.info("Applying block change at: {}", change.position);
    }
    
    private static void applyEntitySpawn(WorldChange change) {
        // Spawn entity in the world
        LOGGER.info("Spawning entity: {} at: {}", change.entityType, change.position);
    }
    
    private static void applyItemCreation(WorldChange change) {
        // Create item in the game
        LOGGER.info("Creating item: {}", change.itemId);
    }
    
    private static void applyGUIModification(WorldChange change) {
        // Apply GUI modification
        LOGGER.info("Modifying GUI: {}", change.guiId);
    }
    
    public static JsonObject createChangePreviewJson(WorldChange change) {
        JsonObject json = new JsonObject();
        json.addProperty("change_id", change.changeId);
        json.addProperty("change_type", change.changeType);
        json.addProperty("description", change.description);
        json.addProperty("timestamp", change.timestamp);
        
        if (change.position != null) {
            JsonObject pos = new JsonObject();
            pos.addProperty("x", change.position.getX());
            pos.addProperty("y", change.position.getY());
            pos.addProperty("z", change.position.getZ());
            json.add("position", pos);
        }
        
        if (change.beforeState != null) {
            json.addProperty("before", change.beforeState.toString());
        }
        
        if (change.afterState != null) {
            json.addProperty("after", change.afterState.toString());
        }
        
        if (change.entityType != null) {
            json.addProperty("entity_type", change.entityType);
        }
        
        if (change.itemId != null) {
            json.addProperty("item_id", change.itemId);
        }
        
        if (change.guiId != null) {
            json.addProperty("gui_id", change.guiId);
        }
        
        return json;
    }
}

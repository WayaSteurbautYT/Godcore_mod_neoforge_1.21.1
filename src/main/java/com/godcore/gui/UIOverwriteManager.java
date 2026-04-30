package com.godcore.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UIOverwriteManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIOverwriteManager.class);
    
    private static final Map<ResourceLocation, JsonObject> originalGUIs = new HashMap<>();
    private static final Map<ResourceLocation, JsonObject> modifiedGUIs = new HashMap<>();
    private static final Map<ResourceLocation, Boolean> overwritePermissions = new HashMap<>();
    
    public static boolean canOverwriteGUI(ResourceLocation guiId) {
        return overwritePermissions.getOrDefault(guiId, false);
    }
    
    public static void requestOverwritePermission(ResourceLocation guiId, JsonObject proposedChanges) {
        LOGGER.info("Requesting overwrite permission for GUI: {}", guiId);
        
        // Store original GUI if not already stored
        if (!originalGUIs.containsKey(guiId)) {
            originalGUIs.put(guiId, getCurrentGUIState(guiId));
        }
        
        // Show approval UI with preview of changes
        JsonObject preview = ApprovalUI.createGUIModificationPreview(
            guiId.toString(),
            originalGUIs.get(guiId).toString(),
            proposedChanges.toString()
        );
        
        // This would open the approval UI for the player
        // onApprove: store the modified GUI and set permission
        // onDeny: cancel the modification
        // onAlwaysAllow: set permission to true for this GUI
    }
    
    public static void applyGUIOverwrite(ResourceLocation guiId, JsonObject newGUI) {
        if (!canOverwriteGUI(guiId)) {
            LOGGER.warn("Cannot overwrite GUI without permission: {}", guiId);
            return;
        }
        
        modifiedGUIs.put(guiId, newGUI);
        LOGGER.info("Applied GUI overwrite for: {}", guiId);
        
        // Apply the new GUI to the game
        applyGUIModification(guiId, newGUI);
    }
    
    public static void revertGUI(ResourceLocation guiId) {
        if (originalGUIs.containsKey(guiId)) {
            applyGUIModification(guiId, originalGUIs.get(guiId));
            modifiedGUIs.remove(guiId);
            overwritePermissions.put(guiId, false);
            LOGGER.info("Reverted GUI: {}", guiId);
        }
    }
    
    public static void revertAllGUIs() {
        for (ResourceLocation guiId : originalGUIs.keySet()) {
            revertGUI(guiId);
        }
        LOGGER.info("Reverted all GUI modifications");
    }
    
    public static JsonObject getCurrentGUIState(ResourceLocation guiId) {
        // Get the current state of the GUI from the game
        JsonObject state = new JsonObject();
        state.addProperty("gui_id", guiId.toString());
        state.addProperty("timestamp", System.currentTimeMillis());
        return state;
    }
    
    private static void applyGUIModification(ResourceLocation guiId, JsonObject guiData) {
        // Apply the GUI modification to the game
        // This would use LDLib2 or custom GUI system to apply changes
        LOGGER.info("Applying GUI modification for: {}", guiId);
    }
    
    public static void setOverwritePermission(ResourceLocation guiId, boolean allowed) {
        overwritePermissions.put(guiId, allowed);
        LOGGER.info("Set overwrite permission for {}: {}", guiId, allowed);
    }
    
    public static Map<ResourceLocation, JsonObject> getModifiedGUIs() {
        return new HashMap<>(modifiedGUIs);
    }
    
    public static Map<ResourceLocation, JsonObject> getOriginalGUIs() {
        return new HashMap<>(originalGUIs);
    }
    
    public static boolean isGUIModified(ResourceLocation guiId) {
        return modifiedGUIs.containsKey(guiId);
    }
    
    public static JsonObject createGUIConflictResolution(ResourceLocation guiId, String conflictType) {
        JsonObject resolution = new JsonObject();
        resolution.addProperty("type", "gui_conflict");
        resolution.addProperty("gui_id", guiId.toString());
        resolution.addProperty("conflict_type", conflictType);
        resolution.addProperty("resolution", "overwrite");
        return resolution;
    }
}

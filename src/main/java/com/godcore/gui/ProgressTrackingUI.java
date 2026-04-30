package com.godcore.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ProgressTrackingUI extends Screen {
    private static final Map<String, ActionProgress> activeProgress = new HashMap<>();
    
    public ProgressTrackingUI() {
        super(Component.literal("Godcore Progress Tracker"));
    }
    
    @Override
    protected void init() {
        // Create progress tracking UI showing:
        // - Active actions with progress bars
        // - Percentage completion
        // - Estimated time remaining
        // - Current step being performed
        // - Visual indicator of progress
    }
    
    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render progress bars for all active actions
        // Show percentage and status
        // Show what's being created/modified
    }
    
    public static void startTracking(String actionId, String actionType, String description, int totalSteps) {
        ActionProgress progress = new ActionProgress(actionId, actionType, description, totalSteps);
        activeProgress.put(actionId, progress);
    }
    
    public static void updateProgress(String actionId, int currentStep, String currentStepDescription) {
        ActionProgress progress = activeProgress.get(actionId);
        if (progress != null) {
            progress.currentStep = currentStep;
            progress.currentStepDescription = currentStepDescription;
            progress.calculatePercentage();
        }
    }
    
    public static void completeTracking(String actionId) {
        ActionProgress progress = activeProgress.remove(actionId);
        if (progress != null) {
            progress.completed = true;
            progress.endTime = System.currentTimeMillis();
        }
    }
    
    public static ActionProgress getProgress(String actionId) {
        return activeProgress.get(actionId);
    }
    
    public static Map<String, ActionProgress> getAllProgress() {
        return new HashMap<>(activeProgress);
    }
    
    public static class ActionProgress {
        public String actionId;
        public String actionType;
        public String description;
        public int totalSteps;
        public int currentStep;
        public String currentStepDescription;
        public float percentage;
        public long startTime;
        public long endTime;
        public boolean completed;
        
        public ActionProgress(String actionId, String actionType, String description, int totalSteps) {
            this.actionId = actionId;
            this.actionType = actionType;
            this.description = description;
            this.totalSteps = totalSteps;
            this.currentStep = 0;
            this.startTime = System.currentTimeMillis();
            this.completed = false;
            calculatePercentage();
        }
        
        public void calculatePercentage() {
            this.percentage = (float) currentStep / totalSteps * 100;
        }
        
        public long getElapsedTime() {
            if (completed) {
                return endTime - startTime;
            }
            return System.currentTimeMillis() - startTime;
        }
        
        public long getEstimatedTimeRemaining() {
            if (completed || currentStep == 0) {
                return 0;
            }
            long elapsed = getElapsedTime();
            long avgTimePerStep = elapsed / currentStep;
            return avgTimePerStep * (totalSteps - currentStep);
        }
    }
    
    public static JsonObject createProgressJson(String actionId) {
        ActionProgress progress = activeProgress.get(actionId);
        if (progress == null) {
            return null;
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("action_id", progress.actionId);
        json.addProperty("action_type", progress.actionType);
        json.addProperty("description", progress.description);
        json.addProperty("total_steps", progress.totalSteps);
        json.addProperty("current_step", progress.currentStep);
        json.addProperty("current_step_description", progress.currentStepDescription);
        json.addProperty("percentage", progress.percentage);
        json.addProperty("elapsed_time", progress.getElapsedTime());
        json.addProperty("estimated_time_remaining", progress.getEstimatedTimeRemaining());
        json.addProperty("completed", progress.completed);
        
        return json;
    }
}

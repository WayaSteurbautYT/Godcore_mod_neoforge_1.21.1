package com.godcore.gui;

import com.godcore.ai.GeminiAIProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIConfigScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIConfigScreen.class);
    
    private EditBox apiKeyField;
    private EditBox modelField;
    private String statusMessage = "";
    private int statusColor = 0xFFFFFF;
    
    public APIConfigScreen() {
        super(Component.literal("Godcore API Configuration"));
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // API Key field
        this.apiKeyField = new EditBox(this.font, centerX - 100, centerY - 40, 200, 20, Component.literal("API Key"));
        this.apiKeyField.setMaxLength(200);
        this.apiKeyField.setValue("");
        this.apiKeyField.setResponder(this::onAPIKeyChanged);
        this.addRenderableWidget(this.apiKeyField);
        
        // Model field
        this.modelField = new EditBox(this.font, centerX - 100, centerY, 200, 20, Component.literal("Model"));
        this.modelField.setMaxLength(50);
        this.modelField.setValue("gemini-1.5-flash");
        this.modelField.setResponder(this::onModelChanged);
        this.addRenderableWidget(this.modelField);
        
        // Test Connection button
        this.addRenderableWidget(Button.builder(Component.literal("Test Connection"), (button) -> {
            testConnection();
        }).bounds(centerX - 100, centerY + 40, 95, 20).build());
        
        // Save button
        this.addRenderableWidget(Button.builder(Component.literal("Save"), (button) -> {
            saveConfiguration();
            this.onClose();
        }).bounds(centerX + 5, centerY + 40, 95, 20).build());
        
        // Cancel button
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), (button) -> {
            this.onClose();
        }).bounds(centerX - 100, centerY + 70, 200, 20).build());
    }
    
    private void onAPIKeyChanged(String value) {
        // API key changed
    }
    
    private void onModelChanged(String value) {
        // Model changed
    }
    
    private void testConnection() {
        String apiKey = apiKeyField.getValue();
        String model = modelField.getValue();
        
        if (apiKey.isEmpty()) {
            statusMessage = "Please enter an API key";
            statusColor = 0xFF5555;
            return;
        }
        
        statusMessage = "Testing connection...";
        statusColor = 0xFFFF00;
        
        new Thread(() -> {
            try {
                GeminiAIProvider provider = new GeminiAIProvider(apiKey);
                boolean success = provider.testConnection();
                
                if (success) {
                    statusMessage = "Connection successful!";
                    statusColor = 0x55FF55;
                } else {
                    statusMessage = "Connection failed - check API key";
                    statusColor = 0xFF5555;
                }
            } catch (Exception e) {
                statusMessage = "Error: " + e.getMessage();
                statusColor = 0xFF5555;
                LOGGER.error("Connection test error", e);
            }
        }).start();
    }
    
    private void saveConfiguration() {
        String apiKey = apiKeyField.getValue();
        String model = modelField.getValue();
        
        // TODO: Save to config file
        LOGGER.info("Saving API configuration - Model: {}", model);
        
        // Update AI manager with new configuration
        if (!apiKey.isEmpty()) {
            // Godcore.getInstance().getAIManager().setAPIProvider(new GeminiAIProvider(apiKey));
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Title
        guiGraphics.drawCenteredString(this.font, Component.literal("Godcore API Configuration"), centerX, centerY - 70, 0xFFFFFF);
        
        // API Key label
        guiGraphics.drawString(this.font, Component.literal("API Key:"), centerX - 100, centerY - 52, 0xFFFFFF);
        
        // Model label
        guiGraphics.drawString(this.font, Component.literal("Model:"), centerX - 100, centerY - 12, 0xFFFFFF);
        
        // Status message
        guiGraphics.drawCenteredString(this.font, Component.literal(statusMessage), centerX, centerY + 100, statusColor);
        
        // Instructions
        guiGraphics.drawCenteredString(this.font, Component.literal("Get API key from: https://aistudio.google.com/app/apikey"), centerX, centerY + 115, 0xAAAAAA);
        
        this.apiKeyField.render(guiGraphics, mouseX, mouseY, partialTick);
        this.modelField.render(guiGraphics, mouseX, mouseY, partialTick);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
}

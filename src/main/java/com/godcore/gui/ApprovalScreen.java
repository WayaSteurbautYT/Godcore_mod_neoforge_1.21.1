package com.godcore.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApprovalScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalScreen.class);
    
    private final JsonObject actionData;
    private final String actionType;
    private final String actionDescription;
    private final String changeId;
    
    public ApprovalScreen(JsonObject actionData, String actionType, String actionDescription, String changeId) {
        super(Component.literal("Godcore Action Approval"));
        this.actionData = actionData;
        this.actionType = actionType;
        this.actionDescription = actionDescription;
        this.changeId = changeId;
    }
    
    @Override
    protected void init() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Accept button
        this.addRenderableWidget(Button.builder(Component.literal("Accept"), (button) -> {
            sendApprovalResponse(true, false);
            this.onClose();
        }).bounds(centerX - buttonWidth - 10, centerY + 40, buttonWidth, buttonHeight).build());
        
        // Deny button
        this.addRenderableWidget(Button.builder(Component.literal("Deny"), (button) -> {
            sendApprovalResponse(false, false);
            this.onClose();
        }).bounds(centerX + 10, centerY + 40, buttonWidth, buttonHeight).build());
        
        // Always Allow This Type button
        this.addRenderableWidget(Button.builder(Component.literal("Always Allow This Type"), (button) -> {
            sendApprovalResponse(true, true);
            this.onClose();
        }).bounds(centerX - buttonWidth, centerY + 70, buttonWidth * 2, buttonHeight).build());
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Title
        guiGraphics.drawCenteredString(this.font, Component.literal("Godcore Action Request"), centerX, centerY - 40, 0xFFFFFF);
        
        // Action type
        guiGraphics.drawCenteredString(this.font, Component.literal("Type: " + actionType), centerX, centerY - 20, 0xFFFF00);
        
        // Action description
        guiGraphics.drawCenteredString(this.font, Component.literal(actionDescription), centerX, centerY, 0xFFFFFF);
        
        // Change ID
        guiGraphics.drawCenteredString(this.font, Component.literal("ID: " + changeId), centerX, centerY + 20, 0xAAAAAA);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    private void sendApprovalResponse(boolean approved, boolean alwaysAllow) {
        LOGGER.info("Sending approval response: approved={}, alwaysAllow={}, changeId={}", approved, alwaysAllow, changeId);
        // TODO: Send network message to server with approval response
        // This will use the network system once it's fixed for NeoForge 1.21.1
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
}

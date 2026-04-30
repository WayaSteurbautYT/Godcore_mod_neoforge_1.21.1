package com.godcore.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatScreen.class);
    
    private EditBox chatInput;
    private List<String> chatHistory = new ArrayList<>();
    private int scrollOffset = 0;
    
    public ChatScreen() {
        super(Component.literal("Godcore Chat"));
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Chat input field
        this.chatInput = new EditBox(this.font, centerX - 150, centerY + 80, 300, 20, Component.literal("Type a message..."));
        this.chatInput.setMaxLength(256);
        this.chatInput.setResponder(this::onChatInput);
        this.addRenderableWidget(this.chatInput);
        
        // Send button
        this.addRenderableWidget(Button.builder(Component.literal("Send"), (button) -> {
            sendMessage();
        }).bounds(centerX + 160, centerY + 80, 60, 20).build());
        
        // Clear button
        this.addRenderableWidget(Button.builder(Component.literal("Clear"), (button) -> {
            chatHistory.clear();
            scrollOffset = 0;
        }).bounds(centerX - 150, centerY + 110, 60, 20).build());
        
        // Close button
        this.addRenderableWidget(Button.builder(Component.literal("Close"), (button) -> {
            this.onClose();
        }).bounds(centerX + 90, centerY + 110, 130, 20).build());
        
        // Scroll up button
        this.addRenderableWidget(Button.builder(Component.literal("▲"), (button) -> {
            if (scrollOffset > 0) scrollOffset--;
        }).bounds(centerX - 180, centerY + 80, 20, 20).build());
        
        // Scroll down button
        this.addRenderableWidget(Button.builder(Component.literal("▼"), (button) -> {
            if (scrollOffset < chatHistory.size() - 10) scrollOffset++;
        }).bounds(centerX - 180, centerY + 110, 20, 20).build());
        
        this.chatInput.setFocused(true);
    }
    
    private void onChatInput(String value) {
        // Input changed
    }
    
    private void sendMessage() {
        String message = chatInput.getValue().trim();
        if (!message.isEmpty()) {
            chatHistory.add("Player: " + message);
            chatInput.setValue("");
            
            // Send to AI (this would be handled via network messaging)
            LOGGER.info("Sending message to AI: {}", message);
            
            // Simulate AI response for now
            chatHistory.add("Godcore: I received your message: " + message);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Title
        guiGraphics.drawCenteredString(this.font, Component.literal("Godcore Chat"), centerX, centerY - 80, 0xFFFFFF);
        
        // Chat history background
        guiGraphics.fill(centerX - 150, centerY - 60, centerX + 150, centerY + 70, 0x80000000);
        
        // Render chat history
        int y = centerY + 60;
        int visibleLines = 10;
        int startIndex = Math.max(0, chatHistory.size() - visibleLines - scrollOffset);
        int endIndex = Math.min(chatHistory.size(), startIndex + visibleLines);
        
        for (int i = endIndex - 1; i >= startIndex; i--) {
            String line = chatHistory.get(i);
            if (line.startsWith("Player:")) {
                guiGraphics.drawString(this.font, Component.literal(line), centerX - 140, y, 0xAAAAFF);
            } else if (line.startsWith("Godcore:")) {
                guiGraphics.drawString(this.font, Component.literal(line), centerX - 140, y, 0xFFAAFF);
            } else {
                guiGraphics.drawString(this.font, Component.literal(line), centerX - 140, y, 0xFFFFFF);
            }
            y -= 15;
        }
        
        // Input label
        guiGraphics.drawString(this.font, Component.literal("Message:"), centerX - 150, centerY + 65, 0xFFFFFF);
        
        this.chatInput.render(guiGraphics, mouseX, mouseY, partialTick);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) { // Enter key
            sendMessage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

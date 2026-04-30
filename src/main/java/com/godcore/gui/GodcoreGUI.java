package com.godcore.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GodcoreGUI extends Screen {
    public GodcoreGUI(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        // Initialize GUI components
        // Settings menu, approvals, memory viewer, tasks, personality editor
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // Render GUI
    }
}

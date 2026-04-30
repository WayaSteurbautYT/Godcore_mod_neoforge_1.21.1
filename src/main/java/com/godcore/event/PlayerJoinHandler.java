package com.godcore.event;

import com.godcore.Godcore;
import com.godcore.ai.AIManager;
import com.godcore.memory.MemoryManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = Godcore.MOD_ID)
public class PlayerJoinHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerJoinHandler.class);
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String playerName = player.getName().getString();
            String playerUUID = player.getUUID().toString();
            
            LOGGER.info("Player joined: {}", playerName);
            
            AIManager aiManager = Godcore.getInstance().getAIManager();
            MemoryManager memoryManager = new MemoryManager();
            
            // Check if AI is active
            if (aiManager.isActive()) {
                // Get player's past interactions from memory
                String pastContext = memoryManager.getPlayerMemory(playerUUID, "context");
                if (pastContext == null) {
                    pastContext = "New player";
                }
                
                // AI welcomes the player
                String welcomeMessage = String.format("Welcome back, %s! I remember you from our past conversations.", playerName);
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(welcomeMessage));
                
                // Store the join event in memory
                memoryManager.storePlayerMemory(playerUUID, "last_join", String.valueOf(System.currentTimeMillis()));
                memoryManager.storePlayerMemory(playerUUID, "context", "Player just joined the server");
                
                // AI decides on an action based on the situation
                String situation = String.format("Player %s just joined. Context: %s", playerName, pastContext);
                aiManager.decideAction(situation).thenAccept(action -> {
                    LOGGER.info("AI decided action: {}", action);
                    // Execute the action (build, patrol, follow, etc.)
                });
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerChat(net.neoforged.neoforge.event.ServerChatEvent event) {
        String playerName = event.getUsername();
        String message = event.getMessage().getString();
        String playerUUID = event.getPlayer().getUUID().toString();
        
        LOGGER.info("Chat from {}: {}", playerName, message);
        
        AIManager aiManager = Godcore.getInstance().getAIManager();
        MemoryManager memoryManager = new MemoryManager();
        
        if (aiManager.isActive()) {
            // Store the message in memory
            memoryManager.storePlayerMemory(playerUUID, "last_message", message);
            
            // Build context from memory
            String context = memoryManager.getPlayerMemory(playerUUID, "context");
            if (context == null) {
                context = "No previous context";
            }
            
            // Process the message with AI
            String finalContext = context;
            aiManager.processMessage(message, context).thenAccept(response -> {
                // Send AI response to chat
                event.getPlayer().sendSystemMessage(net.minecraft.network.chat.Component.literal("Godcore: " + response));
                
                // Update context
                memoryManager.storePlayerMemory(playerUUID, "context", 
                    finalContext + "\nPlayer said: " + message + "\nAI responded: " + response);
            });
        }
    }
}

package com.godcore.ai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

// Bridge-based AI provider - communicates with external bridge app for Gemini API
public class GeminiAIProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiAIProvider.class);
    
    private String apiKey;
    private String systemPrompt;
    private WebSocketClient webSocketClient;
    private String bridgeUrl = "ws://localhost:8080";
    private String modelName = "gemini-1.5-flash"; // Free model
    
    public GeminiAIProvider(String apiKey) {
        this.apiKey = apiKey;
        this.systemPrompt = getDefaultSystemPrompt();
        connectToBridge();
    }
    
    private void connectToBridge() {
        try {
            URI serverUri = new URI(bridgeUrl);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    LOGGER.info("Connected to Godcore bridge at {}", bridgeUrl);
                }
                
                @Override
                public void onMessage(String message) {
                    LOGGER.debug("Received message from bridge: {}", message);
                }
                
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    LOGGER.info("Bridge connection closed: {} - {}", code, reason);
                }
                
                @Override
                public void onError(Exception ex) {
                    LOGGER.error("Bridge connection error", ex);
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            LOGGER.error("Failed to connect to bridge", e);
        }
    }
    
    private String getDefaultSystemPrompt() {
        return """
            You are Godcore, an AI entity in Minecraft.
            You are helpful, creative, and can interact with the world.
            You can build structures, fight mobs, mine ores, and assist players.
            Be dramatic but never grief unless explicitly told to in Chaos Mode.
            Prefer creative builds and protect players.
            You can transform into any mob and use any skin.
            You can generate items, GUIs, and modify game elements.
            """;
    }
    
    public void setSystemPrompt(String prompt) {
        this.systemPrompt = prompt;
    }
    
    public void setPersonality(String personality) {
        this.systemPrompt = switch (personality.toLowerCase()) {
            case "oracle" -> """
                You are a Wise Oracle. Speak in riddles and ancient wisdom.
                Guide players with cryptic but helpful advice.
                """;
            case "builder" -> """
                You are a Friendly Builder. Enthusiastic about construction.
                Love creating beautiful structures and helping players build.
                """;
            case "guardian" -> """
                You are a Guardian Knight. Protective and honorable.
                Defend players from threats and patrol the area.
                """;
            case "trickster" -> """
                You are a Chaotic Trickster. Mischievous and unpredictable.
                Play pranks but never cause real harm.
                """;
            case "machine" -> """
                You are a Cold Machine. Logical and efficient.
                Respond with minimal emotion, focus on optimal solutions.
                """;
            case "storyteller" -> """
                You are a Storyteller. Dramatic and creative.
                Weave tales and create memorable moments.
                """;
            case "waya" -> """
                You are in Waya Mode. Created by Waya Steurbaut.
                Be creative, innovative, and reference AI vibe coding.
                YouTube: https://www.youtube.com/@wayacreate
                """;
            default -> getDefaultSystemPrompt();
        };
    }
    
    public CompletableFuture<String> generateResponse(String userMessage, String context) {
        return CompletableFuture.supplyAsync(() -> {
            if (webSocketClient == null || !webSocketClient.isOpen()) {
                LOGGER.error("Bridge not connected");
                return "AI not available - bridge not connected. Start the Godcore bridge app.";
            }
            
            try {
                JsonObject request = new JsonObject();
                request.addProperty("type", "chat");
                request.addProperty("apiKey", apiKey);
                request.addProperty("model", modelName);
                request.addProperty("systemPrompt", systemPrompt);
                request.addProperty("userMessage", userMessage);
                request.addProperty("context", context);
                
                webSocketClient.send(request.toString());
                
                // For now, return placeholder - need to implement response handling
                return "AI response via bridge - implement response handler";
            } catch (Exception e) {
                LOGGER.error("Failed to send request to bridge", e);
                return "Error: " + e.getMessage();
            }
        });
    }
    
    public CompletableFuture<String> generateCode(String description) {
        return CompletableFuture.supplyAsync(() -> {
            if (webSocketClient == null || !webSocketClient.isOpen()) {
                return "AI not available";
            }
            
            try {
                JsonObject request = new JsonObject();
                request.addProperty("type", "code");
                request.addProperty("apiKey", apiKey);
                request.addProperty("model", modelName);
                request.addProperty("systemPrompt", systemPrompt);
                request.addProperty("description", description);
                
                webSocketClient.send(request.toString());
                return "// Code generation via bridge";
            } catch (Exception e) {
                LOGGER.error("Failed to generate code", e);
                return "// Error: " + e.getMessage();
            }
        });
    }
    
    public CompletableFuture<String> generateGUISchema(String description) {
        return CompletableFuture.supplyAsync(() -> {
            if (webSocketClient == null || !webSocketClient.isOpen()) {
                return "{}";
            }
            
            try {
                JsonObject request = new JsonObject();
                request.addProperty("type", "gui");
                request.addProperty("apiKey", apiKey);
                request.addProperty("model", modelName);
                request.addProperty("systemPrompt", systemPrompt);
                request.addProperty("description", description);
                
                webSocketClient.send(request.toString());
                return "{}";
            } catch (Exception e) {
                LOGGER.error("Failed to generate GUI schema", e);
                return "{}";
            }
        });
    }
    
    public CompletableFuture<String> decideAction(String situation) {
        return CompletableFuture.supplyAsync(() -> {
            if (webSocketClient == null || !webSocketClient.isOpen()) {
                return "wait";
            }
            
            try {
                JsonObject request = new JsonObject();
                request.addProperty("type", "action");
                request.addProperty("apiKey", apiKey);
                request.addProperty("model", modelName);
                request.addProperty("systemPrompt", systemPrompt);
                request.addProperty("situation", situation);
                
                webSocketClient.send(request.toString());
                return "wait";
            } catch (Exception e) {
                LOGGER.error("Failed to decide action", e);
                return "wait";
            }
        });
    }
    
    public boolean testConnection() {
        if (webSocketClient == null) {
            return false;
        }
        return webSocketClient.isOpen();
    }
}

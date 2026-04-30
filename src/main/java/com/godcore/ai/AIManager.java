package com.godcore.ai;

import com.godcore.Godcore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AIManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AIManager.class);
    
    private boolean isActive = false;
    private String currentPersonality = "oracle";
    private boolean voiceEnabled = false;
    private GeminiAIProvider aiProvider;
    private String apiKey;
    
    public AIManager() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            this.aiProvider = new GeminiAIProvider(apiKey);
        }
        LOGGER.info("AIManager initialized");
    }
    
    public void activate() {
        this.isActive = true;
        LOGGER.info("Godcore AI activated");
    }
    
    public void deactivate() {
        this.isActive = false;
        LOGGER.info("Godcore AI deactivated");
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setPersonality(String personality) {
        this.currentPersonality = personality;
        if (aiProvider != null) {
            aiProvider.setPersonality(personality);
        }
        LOGGER.info("Personality set to: {}", personality);
    }
    
    public String getPersonality() {
        return currentPersonality;
    }
    
    public void setVoiceEnabled(boolean enabled) {
        this.voiceEnabled = enabled;
        LOGGER.info("Voice enabled: {}", enabled);
    }
    
    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }
    
    public CompletableFuture<String> processMessage(String message, String context) {
        if (aiProvider != null) {
            return aiProvider.generateResponse(message, context);
        }
        LOGGER.info("Processing message: {}", message);
        return CompletableFuture.completedFuture("AI response to: " + message);
    }
    
    public CompletableFuture<String> generateCode(String description) {
        if (aiProvider != null) {
            return aiProvider.generateCode(description);
        }
        return CompletableFuture.completedFuture("// AI provider not configured");
    }
    
    public CompletableFuture<String> generateGUISchema(String description) {
        if (aiProvider != null) {
            return aiProvider.generateGUISchema(description);
        }
        return CompletableFuture.completedFuture("{}");
    }
    
    public CompletableFuture<String> decideAction(String situation) {
        if (aiProvider != null) {
            return aiProvider.decideAction(situation);
        }
        return CompletableFuture.completedFuture("wait");
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.aiProvider = new GeminiAIProvider(apiKey);
        LOGGER.info("API key updated");
    }
}

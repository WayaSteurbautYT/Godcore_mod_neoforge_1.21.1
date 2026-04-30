package com.godcore.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MemoryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryManager.class);
    private static MemoryManager instance;
    
    private final Map<String, String> worldMemory = new HashMap<>();
    private final Map<String, Map<String, String>> playerMemories = new HashMap<>();
    private final Map<String, String> playerNames = new HashMap<>();
    private final Map<String, List<String>> conversationHistory = new HashMap<>();
    private final Map<String, Integer> relationships = new HashMap<>(); // -100 to 100 scale
    private final List<String> serverLore = new ArrayList<>();
    private final Map<String, String> projects = new HashMap<>();
    
    public static MemoryManager getInstance() {
        if (instance == null) {
            instance = new MemoryManager();
        }
        return instance;
    }
    
    public void storeWorldMemory(String key, String value) {
        worldMemory.put(key, value);
        LOGGER.info("Stored world memory: {} = {}", key, value);
    }
    
    public String getWorldMemory(String key) {
        return worldMemory.get(key);
    }
    
    public void storePlayerMemory(String playerUUID, String key, String value) {
        playerMemories.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(key, value);
        LOGGER.info("Stored player memory: {}:{} = {}", playerUUID, key, value);
    }
    
    public String getPlayerMemory(String playerUUID, String key) {
        Map<String, String> memory = playerMemories.get(playerUUID);
        return memory != null ? memory.get(key) : null;
    }
    
    public void rememberPlayerName(String playerUUID, String playerName) {
        playerNames.put(playerUUID, playerName);
        LOGGER.info("Remembered player name: {} = {}", playerUUID, playerName);
    }
    
    public String getPlayerName(String playerUUID) {
        return playerNames.get(playerUUID);
    }
    
    public void addConversation(String playerUUID, String message) {
        conversationHistory.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(message);
        // Keep last 50 messages per player
        List<String> history = conversationHistory.get(playerUUID);
        if (history.size() > 50) {
            history.remove(0);
        }
    }
    
    public List<String> getConversationHistory(String playerUUID) {
        return conversationHistory.getOrDefault(playerUUID, new ArrayList<>());
    }
    
    public void setRelationship(String playerUUID, int value) {
        relationships.put(playerUUID, Math.max(-100, Math.min(100, value)));
        LOGGER.info("Set relationship for {}: {}", playerUUID, value);
    }
    
    public int getRelationship(String playerUUID) {
        return relationships.getOrDefault(playerUUID, 0);
    }
    
    public void modifyRelationship(String playerUUID, int delta) {
        int current = getRelationship(playerUUID);
        setRelationship(playerUUID, current + delta);
    }
    
    public void addServerLore(String lore) {
        serverLore.add(lore);
        LOGGER.info("Added server lore: {}", lore);
    }
    
    public List<String> getServerLore() {
        return new ArrayList<>(serverLore);
    }
    
    public void rememberProject(String projectId, String description) {
        projects.put(projectId, description);
        LOGGER.info("Remembered project: {} = {}", projectId, description);
    }
    
    public String getProject(String projectId) {
        return projects.get(projectId);
    }
    
    public Map<String, String> getAllProjects() {
        return new HashMap<>(projects);
    }
    
    public void clearMemory() {
        worldMemory.clear();
        playerMemories.clear();
        playerNames.clear();
        conversationHistory.clear();
        relationships.clear();
        serverLore.clear();
        projects.clear();
        LOGGER.info("All memory cleared");
    }
    
    public void clearPlayerMemory(String playerUUID) {
        playerMemories.remove(playerUUID);
        conversationHistory.remove(playerUUID);
        LOGGER.info("Cleared memory for player: {}", playerUUID);
    }
}

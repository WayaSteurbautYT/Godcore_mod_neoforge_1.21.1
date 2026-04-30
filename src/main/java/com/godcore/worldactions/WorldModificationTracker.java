package com.godcore.worldactions;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldModificationTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldModificationTracker.class);
    
    private static final Map<String, List<ModificationRecord>> modificationHistory = new HashMap<>();
    private static final Map<String, ModificationSession> activeSessions = new HashMap<>();
    
    public static class ModificationRecord {
        public String recordId;
        public String sessionId;
        public String modificationType;
        public String description;
        public BlockPos position;
        public BlockState beforeState;
        public BlockState afterState;
        public String entityType;
        public String itemId;
        public String guiId;
        public long timestamp;
        public boolean approved;
        public boolean applied;
        
        public ModificationRecord(String recordId, String sessionId, String modificationType, String description) {
            this.recordId = recordId;
            this.sessionId = sessionId;
            this.modificationType = modificationType;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
            this.approved = false;
            this.applied = false;
        }
    }
    
    public static class ModificationSession {
        public String sessionId;
        public String initiator;
        public long startTime;
        public long endTime;
        public List<String> modificationIds;
        public boolean completed;
        public String sessionType;
        
        public ModificationSession(String sessionId, String initiator, String sessionType) {
            this.sessionId = sessionId;
            this.initiator = initiator;
            this.sessionType = sessionType;
            this.startTime = System.currentTimeMillis();
            this.modificationIds = new ArrayList<>();
            this.completed = false;
        }
        
        public void addModification(String modificationId) {
            modificationIds.add(modificationId);
        }
        
        public void complete() {
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }
    }
    
    public static String createSession(String initiator, String sessionType) {
        String sessionId = "session_" + System.currentTimeMillis();
        ModificationSession session = new ModificationSession(sessionId, initiator, sessionType);
        activeSessions.put(sessionId, session);
        LOGGER.info("Created modification session: {} by: {}", sessionId, initiator);
        return sessionId;
    }
    
    public static ModificationSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }
    
    public static void completeSession(String sessionId) {
        ModificationSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.complete();
            LOGGER.info("Completed modification session: {}", sessionId);
        }
    }
    
    public static ModificationRecord recordModification(String sessionId, String modificationType, String description) {
        String recordId = "mod_" + System.currentTimeMillis();
        ModificationRecord record = new ModificationRecord(recordId, sessionId, modificationType, description);
        
        modificationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(record);
        
        ModificationSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.addModification(recordId);
        }
        
        LOGGER.info("Recorded modification: {} in session: {}", recordId, sessionId);
        return record;
    }
    
    public static void setModificationApproved(String recordId, boolean approved) {
        for (List<ModificationRecord> records : modificationHistory.values()) {
            for (ModificationRecord record : records) {
                if (record.recordId.equals(recordId)) {
                    record.approved = approved;
                    LOGGER.info("Set modification {} approved: {}", recordId, approved);
                    return;
                }
            }
        }
    }
    
    public static void setModificationApplied(String recordId, boolean applied) {
        for (List<ModificationRecord> records : modificationHistory.values()) {
            for (ModificationRecord record : records) {
                if (record.recordId.equals(recordId)) {
                    record.applied = applied;
                    LOGGER.info("Set modification {} applied: {}", recordId, applied);
                    return;
                }
            }
        }
    }
    
    public static List<ModificationRecord> getSessionModifications(String sessionId) {
        return modificationHistory.getOrDefault(sessionId, new ArrayList<>());
    }
    
    public static List<ModificationRecord> getAllModifications() {
        List<ModificationRecord> all = new ArrayList<>();
        for (List<ModificationRecord> records : modificationHistory.values()) {
            all.addAll(records);
        }
        return all;
    }
    
    public static JsonObject createModificationSummary(String sessionId) {
        ModificationSession session = activeSessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        JsonObject summary = new JsonObject();
        summary.addProperty("session_id", sessionId);
        summary.addProperty("initiator", session.initiator);
        summary.addProperty("session_type", session.sessionType);
        summary.addProperty("start_time", session.startTime);
        summary.addProperty("end_time", session.endTime);
        summary.addProperty("completed", session.completed);
        summary.addProperty("total_modifications", session.modificationIds.size());
        
        List<ModificationRecord> records = getSessionModifications(sessionId);
        int approvedCount = 0;
        int appliedCount = 0;
        
        for (ModificationRecord record : records) {
            if (record.approved) approvedCount++;
            if (record.applied) appliedCount++;
        }
        
        summary.addProperty("approved_count", approvedCount);
        summary.addProperty("applied_count", appliedCount);
        
        return summary;
    }
    
    public static void clearSession(String sessionId) {
        modificationHistory.remove(sessionId);
        activeSessions.remove(sessionId);
        LOGGER.info("Cleared session: {}", sessionId);
    }
    
    public static void clearAllHistory() {
        modificationHistory.clear();
        activeSessions.clear();
        LOGGER.info("Cleared all modification history");
    }
    
    public static Map<String, List<ModificationRecord>> getModificationHistory() {
        return new HashMap<>(modificationHistory);
    }
    
    public static Map<String, ModificationSession> getActiveSessions() {
        return new HashMap<>(activeSessions);
    }
}

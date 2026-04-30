package com.godcore.rollback;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RollbackManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackManager.class);
    private static RollbackManager instance;
    
    private final List<BlockChange> changeHistory = new ArrayList<>();
    private static final int MAX_HISTORY = 10000;
    
    public static RollbackManager getInstance() {
        if (instance == null) {
            instance = new RollbackManager();
        }
        return instance;
    }
    
    public static class BlockChange {
        public final BlockPos pos;
        public final BlockState oldState;
        public final BlockState newState;
        public final long timestamp;
        public final String actionType;
        public final String actor;
        
        public BlockChange(BlockPos pos, BlockState oldState, BlockState newState, String actionType, String actor) {
            this.pos = pos;
            this.oldState = oldState;
            this.newState = newState;
            this.timestamp = System.currentTimeMillis();
            this.actionType = actionType;
            this.actor = actor;
        }
    }
    
    public void recordChange(BlockChange change) {
        changeHistory.add(change);
        if (changeHistory.size() > MAX_HISTORY) {
            changeHistory.remove(0);
        }
        LOGGER.info("Recorded block change at: {} by: {}", change.pos, change.actor);
    }
    
    public void undoLast(Level level) {
        if (!changeHistory.isEmpty()) {
            BlockChange lastChange = changeHistory.remove(changeHistory.size() - 1);
            level.setBlock(lastChange.pos, lastChange.oldState, 3);
            LOGGER.info("Undid last change at: {}", lastChange.pos);
        }
    }
    
    public void rollbackTime(Level level, long minutesAgo) {
        long cutoffTime = System.currentTimeMillis() - (minutesAgo * 60 * 1000);
        changeHistory.removeIf(change -> {
            if (change.timestamp < cutoffTime) {
                level.setBlock(change.pos, change.oldState, 3);
                LOGGER.info("Rolled back change at: {}", change.pos);
                return true;
            }
            return false;
        });
    }
    
    public void rollbackArea(Level level, BlockPos center, int radius) {
        changeHistory.removeIf(change -> {
            if (change.pos.distSqr(center) <= radius * radius) {
                level.setBlock(change.pos, change.oldState, 3);
                LOGGER.info("Rolled back change at: {}", change.pos);
                return true;
            }
            return false;
        });
        LOGGER.info("Rolled back area around: {}", center);
    }
    
    public void rollbackAll(Level level) {
        for (BlockChange change : changeHistory) {
            level.setBlock(change.pos, change.oldState, 3);
        }
        changeHistory.clear();
        LOGGER.info("Rolled back all changes");
    }
    
    public void clearHistory() {
        changeHistory.clear();
        LOGGER.info("Cleared rollback history");
    }
    
    public int getHistorySize() {
        return changeHistory.size();
    }
}

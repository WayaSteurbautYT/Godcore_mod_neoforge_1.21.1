package com.godcore.worldactions;

import com.godcore.rollback.RollbackManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldActionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldActionManager.class);
    private static WorldActionManager instance;
    
    private boolean requireApproval = true;
    private String currentActor = "Godcore";
    
    public static WorldActionManager getInstance() {
        if (instance == null) {
            instance = new WorldActionManager();
        }
        return instance;
    }
    
    public void setRequireApproval(boolean require) {
        this.requireApproval = require;
    }
    
    public void setActor(String actor) {
        this.currentActor = actor;
    }
    
    public void placeBlock(Level level, BlockPos pos, BlockState state) {
        BlockState beforeState = level.getBlockState(pos);
        
        if (requireApproval) {
            LOGGER.info("Block placement requires approval at: {} - Place {}", pos, state.getBlock());
            // TODO: Show approval UI
            // For now, just log it
        } else {
            applyBlockChange(level, pos, beforeState, state);
            LOGGER.info("Placed block at: {}", pos);
        }
    }
    
    public void breakBlock(Level level, BlockPos pos) {
        BlockState beforeState = level.getBlockState(pos);
        
        if (requireApproval) {
            LOGGER.info("Block break requires approval at: {}", pos);
            // TODO: Show approval UI
        } else {
            applyBlockBreak(level, pos, beforeState);
            LOGGER.info("Broke block at: {}", pos);
        }
    }
    
    public void buildStructure(Level level, String structureType, BlockPos startPos) {
        LOGGER.info("Building structure: {} at {}", structureType, startPos);
        
        // Simple structure building - place a platform
        int size = 5;
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos placePos = startPos.offset(x, 0, z);
                BlockState beforeState = level.getBlockState(placePos);
                applyBlockChange(level, placePos, beforeState, Blocks.STONE.defaultBlockState());
            }
        }
        
        LOGGER.info("Completed building: {} at {}", structureType, startPos);
    }
    
    public void fightMobs(Level level, BlockPos centerPos, int radius) {
        LOGGER.info("Fighting mobs near: {}", centerPos);
        
        // Find hostile mobs within radius
        level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            new net.minecraft.world.phys.AABB(centerPos).inflate(radius),
            mob -> {
                // Target hostile mobs
                return mob instanceof net.minecraft.world.entity.monster.Monster ||
                       mob instanceof net.minecraft.world.entity.animal.Animal && mob.isAggressive();
            }
        ).forEach(mob -> {
            LOGGER.info("Attacking mob: {} at {}", mob.getType(), mob.blockPosition());
            // Attack the mob
            mob.hurt(level.damageSources().magic(), 5.0f);
        });
    }
    
    public void mineOres(Level level, BlockPos centerPos, int radius) {
        LOGGER.info("Mining ores near: {}", centerPos);
        
        // Find and mine ore blocks within radius
        net.minecraft.world.level.block.Block[] ores = {
            net.minecraft.world.level.block.Blocks.COAL_ORE,
            net.minecraft.world.level.block.Blocks.IRON_ORE,
            net.minecraft.world.level.block.Blocks.GOLD_ORE,
            net.minecraft.world.level.block.Blocks.DIAMOND_ORE,
            net.minecraft.world.level.block.Blocks.COPPER_ORE,
            net.minecraft.world.level.block.Blocks.REDSTONE_ORE,
            net.minecraft.world.level.block.Blocks.LAPIS_ORE
        };
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    for (net.minecraft.world.level.block.Block ore : ores) {
                        if (state.is(ore)) {
                            LOGGER.info("Mining ore: {} at {}", ore, checkPos);
                            BlockState beforeState = state;
                            applyBlockBreak(level, checkPos, beforeState);
                            // Drop the ore item
                            net.minecraft.world.item.ItemStack drop = new net.minecraft.world.item.ItemStack(state.getBlock().asItem());
                            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                                level, checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5, drop
                            );
                            level.addFreshEntity(itemEntity);
                        }
                    }
                }
            }
        }
    }
    
    public void followPlayer(Level level, BlockPos playerPos) {
        LOGGER.info("Following player to: {}", playerPos);
        // This would be called by the entity's AI
        // The entity should move towards the player position
        // For now, just log it - the entity AI will handle movement
    }
    
    public void patrolArea(Level level, BlockPos centerPos, int radius) {
        LOGGER.info("Patrolling area around: {}", centerPos);
        
        // Find hostile mobs in the area and attack them
        fightMobs(level, centerPos, radius);
        
        // This would be called by the entity's AI
        // The entity should move in a pattern around the center position
    }
    
    public void organizeChests(Level level, BlockPos centerPos, int radius) {
        LOGGER.info("Organizing chests around: {}", centerPos);
        
        // Find all chests within radius
        level.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, 
            new net.minecraft.world.phys.AABB(centerPos).inflate(radius),
            entity -> false
        );
        
        // Scan blocks for chests
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    if (state.is(net.minecraft.world.level.block.Blocks.CHEST) || 
                        state.is(net.minecraft.world.level.block.Blocks.BARREL) ||
                        state.is(net.minecraft.world.level.block.Blocks.ENDER_CHEST)) {
                        
                        if (level.getBlockEntity(checkPos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                            LOGGER.info("Found chest at: {}", checkPos);
                            // TODO: Implement chest sorting logic
                            // This would organize items by type, stack items, etc.
                        }
                    }
                }
            }
        }
    }
    
    public void castParticles(Level level, BlockPos pos, String particleType) {
        LOGGER.info("Casting particles: {} at {}", particleType, pos);
        
        net.minecraft.core.particles.ParticleOptions particle = switch (particleType.toLowerCase()) {
            case "fire" -> net.minecraft.core.particles.ParticleTypes.FLAME;
            case "magic" -> net.minecraft.core.particles.ParticleTypes.ENCHANT;
            case "heal" -> net.minecraft.core.particles.ParticleTypes.HEART;
            case "lightning" -> net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK;
            case "smoke" -> net.minecraft.core.particles.ParticleTypes.SMOKE;
            case "sparkle" -> net.minecraft.core.particles.ParticleTypes.END_ROD;
            default -> net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER;
        };
        
        // Spawn particles in a sphere pattern
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double x = pos.getX() + 0.5 + Math.cos(angle) * 1.5;
            double y = pos.getY() + 1.0 + Math.sin(angle * 2) * 0.5;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * 1.5;
            
            level.addParticle(particle, x, y, z, 0, 0.1, 0);
        }
    }
    
    public void executeCommand(Level level, String command, String playerUUID) {
        LOGGER.info("Executing command: {} for player: {}", command, playerUUID);
        
        // Check permissions
        // This would check if the player has permission to run this command
        // For now, just log it - permission checks are handled in commands
        
        // Execute the command as the server
        try {
            level.getServer().getCommands().getDispatcher().parse(
                command,
                level.getServer().createCommandSourceStack()
            );
        } catch (Exception e) {
            LOGGER.error("Failed to execute command: {}", command, e);
        }
    }
    
    private void applyBlockChange(Level level, BlockPos pos, BlockState beforeState, BlockState newState) {
        level.setBlock(pos, newState, 3);
        
        // Record in rollback manager
        RollbackManager.BlockChange change = new RollbackManager.BlockChange(
            pos, beforeState, newState, "block_place", currentActor
        );
        RollbackManager.getInstance().recordChange(change);
    }
    
    private void applyBlockBreak(Level level, BlockPos pos, BlockState beforeState) {
        level.removeBlock(pos, false);
        
        // Record in rollback manager
        RollbackManager.BlockChange change = new RollbackManager.BlockChange(
            pos, beforeState, Blocks.AIR.defaultBlockState(), "block_break", currentActor
        );
        RollbackManager.getInstance().recordChange(change);
    }
}

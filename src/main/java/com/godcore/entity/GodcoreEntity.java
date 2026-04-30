package com.godcore.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GodcoreEntity extends Mob implements GeoAnimatable {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(Registries.ENTITY_TYPE, "godcore");
    
    public static final DeferredHolder<EntityType<?>, EntityType<GodcoreEntity>> GODCORE_ENTITY = 
        ENTITIES.register("godcore_entity", () -> 
            EntityType.Builder.of(GodcoreEntity::new, net.minecraft.world.entity.MobCategory.CREATURE)
                .sized(0.6F, 1.8F)
                .clientTrackingRange(8)
                .fireImmune()
                .build("godcore_entity")
        );

    private final SkinManager skinManager;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // Animation definitions
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.godcore.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.godcore.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.godcore.run");
    private static final RawAnimation MINE = RawAnimation.begin().thenLoop("animation.godcore.mine");
    private static final RawAnimation BUILD = RawAnimation.begin().thenLoop("animation.godcore.build");
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("animation.godcore.sit");
    private static final RawAnimation MEDITATE = RawAnimation.begin().thenLoop("animation.godcore.meditate");
    private static final RawAnimation EMOTE = RawAnimation.begin().thenPlay("animation.godcore.emote");

    public GodcoreEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.skinManager = new SkinManager();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.5D)
            .add(Attributes.ATTACK_DAMAGE, 10.0D)
            .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }
    
    // Animation state methods
    private boolean isMining = false;
    private boolean isBuilding = false;
    private boolean isSitting = false;
    private boolean isMeditating = false;
    private boolean isEmoting = false;
    private boolean isSwingingTool = false;
    
    public boolean isMining() {
        return isMining;
    }
    
    public void setMining(boolean mining) {
        this.isMining = mining;
    }
    
    public boolean isBuilding() {
        return isBuilding;
    }
    
    public void setBuilding(boolean building) {
        this.isBuilding = building;
    }
    
    public boolean isSitting() {
        return isSitting;
    }
    
    public void setSitting(boolean sitting) {
        this.isSitting = sitting;
    }
    
    public boolean isMeditating() {
        return isMeditating;
    }
    
    public void setMeditating(boolean meditating) {
        this.isMeditating = meditating;
    }
    
    public boolean isEmoting() {
        return isEmoting;
    }
    
    public void setEmoting(boolean emoting) {
        this.isEmoting = emoting;
    }
    
    public boolean isSwingingTool() {
        return isSwingingTool;
    }
    
    public void setSwingingTool(boolean swinging) {
        this.isSwingingTool = swinging;
    }
    
    public boolean isRunning() {
        return this.getDeltaMovement().horizontalDistance() > 0.1;
    }
    
    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistance() > 0.01;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Look at nearest player
        if (!level().isClientSide) {
            lookAtNearestPlayer();
        }
    }
    
    private void lookAtNearestPlayer() {
        Player nearestPlayer = level().getNearestPlayer(this, 16.0);
        if (nearestPlayer != null) {
            // Look at the player
            double dx = nearestPlayer.getX() - this.getX();
            double dy = nearestPlayer.getY() - this.getY();
            double dz = nearestPlayer.getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            if (distance > 0.1) {
                float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                float pitch = (float) Math.toDegrees(-Math.atan2(dy, distance));
                
                this.setYRot(yaw);
                this.yRotO = yaw;
                this.setXRot(pitch);
                this.xRotO = pitch;
            }
        }
    }
    
    // GeoAnimatable implementation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GodcoreEntity>(this, "controller", 0, this::predicate));
    }
    
    private PlayState predicate(AnimationState<GodcoreEntity> state) {
        if (isEmoting) {
            state.getController().setAnimation(EMOTE);
            return PlayState.STOP;
        } else if (isSwingingTool) {
            state.getController().setAnimation(MINE);
            return PlayState.CONTINUE;
        } else if (isMining) {
            state.getController().setAnimation(MINE);
            return PlayState.CONTINUE;
        } else if (isBuilding) {
            state.getController().setAnimation(BUILD);
            return PlayState.CONTINUE;
        } else if (isSitting) {
            state.getController().setAnimation(SIT);
            return PlayState.CONTINUE;
        } else if (isMeditating) {
            state.getController().setAnimation(MEDITATE);
            return PlayState.CONTINUE;
        } else if (isRunning()) {
            state.getController().setAnimation(RUN);
            return PlayState.CONTINUE;
        } else if (isMoving()) {
            state.getController().setAnimation(WALK);
            return PlayState.CONTINUE;
        } else {
            state.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        }
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    @Override
    public double getTick(Object entity) {
        return this.tickCount;
    }
}

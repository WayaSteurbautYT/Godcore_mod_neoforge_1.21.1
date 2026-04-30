package com.godcore;

import com.godcore.ai.AIManager;
import com.godcore.command.GodcoreCommands;
import com.godcore.entity.GodcoreEntity;
import com.godcore.generation.ItemGenerator;
import com.godcore.network.NetworkHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Godcore.MOD_ID)
public class Godcore {
    public static final String MOD_ID = "godcore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String MOD_NAME = "Godcore";
    public static final String VERSION = "1.0.0";

    private static Godcore instance;
    private AIManager aiManager;

    public Godcore(IEventBus modEventBus) {
        instance = this;
        
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerNetwork);
        
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        
        // Register deferred registers
        GodcoreEntity.ENTITIES.register(modEventBus);
        ItemGenerator.register(modEventBus);
        
        LOGGER.info("Godcore mod initialized - Version: {}", VERSION);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Godcore common setup complete");
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GodcoreEntity.GODCORE_ENTITY.get(), GodcoreEntity.createAttributes().build());
        LOGGER.info("Godcore entity attributes registered");
    }

    private void registerNetwork(RegisterPayloadHandlersEvent event) {
        NetworkHandler.register(event);
        LOGGER.info("Godcore network handlers registered");
    }

    private void registerCommands(RegisterCommandsEvent event) {
        GodcoreCommands.register(event.getDispatcher());
        LOGGER.info("Godcore commands registered");
    }

    public static Godcore getInstance() {
        return instance;
    }

    public AIManager getAIManager() {
        if (aiManager == null) {
            aiManager = new AIManager();
        }
        return aiManager;
    }
}

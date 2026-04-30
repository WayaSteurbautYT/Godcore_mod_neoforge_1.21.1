package com.godcore.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ModIntegrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModIntegrationManager.class);
    
    private static final Map<String, Boolean> loadedMods = new HashMap<>();
    
    static {
        // Check for common mods
        checkMod("simplevoicechat", "maven.modrinth:simple-voice-chat");
        checkMod("baritone", "cabaletta:baritone");
        checkMod("geckolib", "software.bernie.geckolib");
        checkMod("ldlib", "io.github.llamalad7:mixinextras");
        checkMod("jei", "mezz.jei");
        checkMod("kubejs", "dev.latvian.mods.kubejs");
        checkMod("mekanism", "mekanism");
        checkMod("create", "com.simibubi.create");
        checkMod("botania", "vazkii.botania");
        checkMod("tconstruct", "slimeknights.tconstruct");
    }
    
    private static void checkMod(String modId, String className) {
        try {
            Class.forName(className);
            loadedMods.put(modId, true);
            LOGGER.info("Detected mod: {}", modId);
        } catch (ClassNotFoundException e) {
            loadedMods.put(modId, false);
        }
    }
    
    public static boolean isModLoaded(String modId) {
        return loadedMods.getOrDefault(modId, false);
    }
    
    public static boolean hasSimpleVoiceChat() {
        return isModLoaded("simplevoicechat");
    }
    
    public static boolean hasBaritone() {
        return isModLoaded("baritone");
    }
    
    public static boolean hasGeckoLib() {
        return isModLoaded("geckolib");
    }
    
    public static boolean hasLDLib() {
        return isModLoaded("ldlib");
    }
    
    public static boolean hasJEI() {
        return isModLoaded("jei");
    }
    
    public static boolean hasKubeJS() {
        return isModLoaded("kubejs");
    }
    
    public static boolean hasMekanism() {
        return isModLoaded("mekanism");
    }
    
    public static boolean hasCreate() {
        return isModLoaded("create");
    }
    
    public static boolean hasBotania() {
        return isModLoaded("botania");
    }
    
    public static boolean hasTConstruct() {
        return isModLoaded("tconstruct");
    }
    
    public static Map<String, Boolean> getAllLoadedMods() {
        return new HashMap<>(loadedMods);
    }
    
    public static String getIntegrationReport() {
        StringBuilder report = new StringBuilder("Godcore Mod Integration Report:\n");
        for (Map.Entry<String, Boolean> entry : loadedMods.entrySet()) {
            report.append(String.format("  %s: %s\n", entry.getKey(), entry.getValue() ? "LOADED" : "NOT FOUND"));
        }
        return report.toString();
    }
}

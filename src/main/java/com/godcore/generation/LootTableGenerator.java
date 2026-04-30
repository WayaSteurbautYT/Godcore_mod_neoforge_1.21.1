package com.godcore.generation;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LootTableGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LootTableGenerator.class);
    
    private static final Map<ResourceLocation, JsonObject> generatedLootTables = new HashMap<>();
    
    public static JsonObject generateLootTable(String tableName, JsonObject schema) {
        LOGGER.info("Generating loot table: {}", tableName);
        
        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:chest");
        
        JsonArray pools = new JsonArray();
        
        if (schema.has("pools")) {
            // Parse pools from schema
            JsonArray schemaPools = schema.getAsJsonArray("pools");
            for (int i = 0; i < schemaPools.size(); i++) {
                pools.add(schemaPools.get(i));
            }
        } else {
            // Create default pool
            JsonObject defaultPool = createDefaultPool();
            pools.add(defaultPool);
        }
        
        lootTable.add("pools", pools);
        
        ResourceLocation tableId = ResourceLocation.fromNamespaceAndPath("godcore", tableName);
        generatedLootTables.put(tableId, lootTable);
        
        return lootTable;
    }
    
    private static JsonObject createDefaultPool() {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", "minecraft:diamond");
        entries.add(entry);
        
        pool.add("entries", entries);
        
        return pool;
    }
    
    public static JsonObject generateCustomLootPool(String item, int minCount, int maxCount, float chance) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        
        JsonArray conditions = new JsonArray();
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:random_chance");
        condition.addProperty("chance", chance);
        conditions.add(condition);
        pool.add("conditions", conditions);
        
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", item);
        
        JsonArray functions = new JsonArray();
        JsonObject countFunc = new JsonObject();
        countFunc.addProperty("function", "minecraft:set_count");
        JsonObject countRange = new JsonObject();
        countRange.addProperty("min", minCount);
        countRange.addProperty("max", maxCount);
        countRange.addProperty("type", "minecraft:uniform");
        countFunc.add("count", countRange);
        functions.add(countFunc);
        entry.add("functions", functions);
        
        entries.add(entry);
        pool.add("entries", entries);
        
        return pool;
    }
    
    public static JsonObject generateMobLootTable(String mobName, JsonObject drops) {
        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:entity");
        
        JsonArray pools = new JsonArray();
        
        if (drops.has("drops")) {
            JsonArray dropArray = drops.getAsJsonArray("drops");
            for (int i = 0; i < dropArray.size(); i++) {
                JsonObject drop = dropArray.get(i).getAsJsonObject();
                pools.add(generateDropFromSchema(drop));
            }
        }
        
        lootTable.add("pools", pools);
        
        ResourceLocation tableId = ResourceLocation.fromNamespaceAndPath("godcore", "entities/" + mobName);
        generatedLootTables.put(tableId, lootTable);
        
        return lootTable;
    }
    
    private static JsonObject generateDropFromSchema(JsonObject drop) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", drop.has("rolls") ? drop.get("rolls").getAsInt() : 1);
        
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", drop.get("item").getAsString());
        
        if (drop.has("count")) {
            JsonArray functions = new JsonArray();
            JsonObject countFunc = new JsonObject();
            countFunc.addProperty("function", "minecraft:set_count");
            countFunc.add("count", drop.get("count"));
            functions.add(countFunc);
            entry.add("functions", functions);
        }
        
        entries.add(entry);
        pool.add("entries", entries);
        
        return pool;
    }
    
    public static Map<ResourceLocation, JsonObject> getGeneratedLootTables() {
        return generatedLootTables;
    }
    
    public static JsonObject getLootTable(ResourceLocation tableId) {
        return generatedLootTables.get(tableId);
    }
}

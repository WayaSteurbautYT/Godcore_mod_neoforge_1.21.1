package com.godcore.command;

import com.godcore.Godcore;
import com.godcore.ai.AIManager;
import com.godcore.entity.GodcoreEntity;
import com.godcore.memory.MemoryManager;
import com.godcore.network.UINetworkMessages;
import com.godcore.permissions.PermissionManager;
import com.godcore.rollback.RollbackManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GodcoreCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(GodcoreCommands.class);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("god")
                .then(Commands.literal("activate")
                    .executes(GodcoreCommands::activate)
                )
                .then(Commands.literal("chat")
                    .then(Commands.argument("message", StringArgumentType.string())
                        .executes(GodcoreCommands::chat)
                    )
                )
                .then(Commands.literal("summon")
                    .executes(GodcoreCommands::summon)
                )
                .then(Commands.literal("sleep")
                    .executes(GodcoreCommands::sleep)
                )
                .then(Commands.literal("personality")
                    .then(Commands.literal("oracle").executes(GodcoreCommands::personalityOracle))
                    .then(Commands.literal("builder").executes(GodcoreCommands::personalityBuilder))
                    .then(Commands.literal("guardian").executes(GodcoreCommands::personalityGuardian))
                    .then(Commands.literal("trickster").executes(GodcoreCommands::personalityTrickster))
                    .then(Commands.literal("machine").executes(GodcoreCommands::personalityMachine))
                    .then(Commands.literal("storyteller").executes(GodcoreCommands::personalityStoryteller))
                    .then(Commands.literal("waya").executes(GodcoreCommands::personalityWaya))
                )
                .then(Commands.literal("voice")
                    .then(Commands.literal("on").executes(GodcoreCommands::voiceOn))
                    .then(Commands.literal("off").executes(GodcoreCommands::voiceOff))
                )
                .then(Commands.literal("undo")
                    .then(Commands.literal("last").executes(GodcoreCommands::undoLast))
                )
                .then(Commands.literal("rollback")
                    .then(Commands.literal("all").executes(GodcoreCommands::rollbackAll))
                    .then(Commands.argument("minutes", StringArgumentType.string())
                        .executes(GodcoreCommands::rollbackTime)
                    )
                )
                .then(Commands.literal("freeze")
                    .executes(GodcoreCommands::freeze)
                )
                .then(Commands.literal("disable")
                    .executes(GodcoreCommands::disable)
                )
                .then(Commands.literal("memory")
                    .then(Commands.literal("clear").executes(GodcoreCommands::memoryClear))
                    .then(Commands.literal("view").executes(GodcoreCommands::memoryView))
                )
                .then(Commands.literal("permission")
                    .then(Commands.literal("set")
                        .then(Commands.argument("player", StringArgumentType.string())
                            .then(Commands.argument("tier", StringArgumentType.string())
                                .executes(GodcoreCommands::setPermission)
                            )
                        )
                    )
                )
                .then(Commands.literal("skin")
                    .then(Commands.literal("url")
                        .then(Commands.argument("url", StringArgumentType.string())
                            .executes(GodcoreCommands::skinFromURL)
                        )
                    )
                    .then(Commands.literal("player")
                        .executes(GodcoreCommands::skinFromPlayer)
                    )
                    .then(Commands.literal("uuid")
                        .then(Commands.argument("uuid", StringArgumentType.string())
                            .executes(GodcoreCommands::skinFromUUID)
                        )
                    )
                )
                .then(Commands.literal("transform")
                    .then(Commands.literal("mob")
                        .then(Commands.argument("mob", StringArgumentType.string())
                            .executes(GodcoreCommands::transformToMob)
                        )
                    )
                    .then(Commands.literal("player")
                        .executes(GodcoreCommands::transformToPlayer)
                    )
                )
                .then(Commands.literal("build")
                    .then(Commands.argument("structure", StringArgumentType.string())
                        .executes(GodcoreCommands::buildStructure)
                    )
                )
                .then(Commands.literal("create")
                    .then(Commands.literal("item")
                        .then(Commands.argument("name", StringArgumentType.string())
                            .then(Commands.argument("texture", StringArgumentType.string())
                                .executes(GodcoreCommands::createItem)
                            )
                        )
                    )
                )
                .then(Commands.literal("config")
                    .executes(GodcoreCommands::openConfig)
                )
        );
        
        LOGGER.info("Godcore commands registered");
    }

    private static int activate(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.activate();
        context.getSource().sendSuccess(() -> Component.literal("Godcore activated!"), true);
        return 1;
    }

    private static int chat(CommandContext<CommandSourceStack> context) {
        String message = context.getArgument("message", String.class);
        AIManager aiManager = Godcore.getInstance().getAIManager();
        
        // Store in memory
        if (context.getSource().getEntity() != null) {
            String playerUUID = context.getSource().getEntity().getStringUUID();
            MemoryManager.getInstance().addConversation(playerUUID, "Player: " + message);
        }
        
        aiManager.processMessage(message, "No context").thenAccept(response -> {
            context.getSource().sendSuccess(() -> Component.literal("Godcore: " + response), true);
            
            if (context.getSource().getEntity() != null) {
                String playerUUID = context.getSource().getEntity().getStringUUID();
                MemoryManager.getInstance().addConversation(playerUUID, "Godcore: " + response);
            }
        });
        return 1;
    }

    private static int summon(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() != null) {
            ServerLevel level = context.getSource().getLevel();
            BlockPos pos = context.getSource().getEntity().blockPosition();
            
            GodcoreEntity entity = GodcoreEntity.GODCORE_ENTITY.get().create(level);
            if (entity != null) {
                entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                level.addFreshEntity(entity);
                context.getSource().sendSuccess(() -> Component.literal("Godcore summoned!"), true);
                return 1;
            }
        }
        context.getSource().sendFailure(Component.literal("Failed to summon Godcore"));
        return 0;
    }

    private static int sleep(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Godcore is now sleeping..."), true);
        return 1;
    }

    private static int personalityOracle(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("oracle");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Wise Oracle"), true);
        return 1;
    }

    private static int personalityBuilder(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("builder");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Friendly Builder"), true);
        return 1;
    }

    private static int personalityGuardian(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("guardian");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Guardian Knight"), true);
        return 1;
    }

    private static int personalityTrickster(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("trickster");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Chaotic Trickster"), true);
        return 1;
    }

    private static int personalityMachine(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("machine");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Cold Machine"), true);
        return 1;
    }

    private static int personalityStoryteller(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("storyteller");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Storyteller"), true);
        return 1;
    }

    private static int personalityWaya(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setPersonality("waya");
        context.getSource().sendSuccess(() -> Component.literal("Personality set to: Waya Mode"), true);
        return 1;
    }

    private static int voiceOn(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setVoiceEnabled(true);
        context.getSource().sendSuccess(() -> Component.literal("Voice interaction enabled"), true);
        return 1;
    }

    private static int voiceOff(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.setVoiceEnabled(false);
        context.getSource().sendSuccess(() -> Component.literal("Voice interaction disabled"), true);
        return 1;
    }

    private static int undoLast(CommandContext<CommandSourceStack> context) {
        RollbackManager.getInstance().undoLast(context.getSource().getLevel());
        context.getSource().sendSuccess(() -> Component.literal("Undid last action"), true);
        return 1;
    }

    private static int rollbackAll(CommandContext<CommandSourceStack> context) {
        RollbackManager.getInstance().rollbackAll(context.getSource().getLevel());
        context.getSource().sendSuccess(() -> Component.literal("Rolled back all changes"), true);
        return 1;
    }

    private static int rollbackTime(CommandContext<CommandSourceStack> context) {
        String minutesStr = context.getArgument("minutes", String.class);
        try {
            long minutes = Long.parseLong(minutesStr);
            RollbackManager.getInstance().rollbackTime(context.getSource().getLevel(), minutes);
            context.getSource().sendSuccess(() -> Component.literal("Rolled back changes from last " + minutes + " minutes"), true);
            return 1;
        } catch (NumberFormatException e) {
            context.getSource().sendFailure(Component.literal("Invalid number: " + minutesStr));
            return 0;
        }
    }

    private static int freeze(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Godcore frozen!"), true);
        return 1;
    }

    private static int disable(CommandContext<CommandSourceStack> context) {
        AIManager aiManager = Godcore.getInstance().getAIManager();
        aiManager.deactivate();
        context.getSource().sendSuccess(() -> Component.literal("Godcore disabled!"), true);
        return 1;
    }

    private static int memoryClear(CommandContext<CommandSourceStack> context) {
        MemoryManager.getInstance().clearMemory();
        context.getSource().sendSuccess(() -> Component.literal("Memory cleared"), true);
        return 1;
    }

    private static int memoryView(CommandContext<CommandSourceStack> context) {
        MemoryManager memory = MemoryManager.getInstance();
        context.getSource().sendSuccess(() -> Component.literal("Memory size: " + memory.getServerLore().size() + " lore entries"), true);
        return 1;
    }

    private static int setPermission(CommandContext<CommandSourceStack> context) {
        String player = context.getArgument("player", String.class);
        String tierStr = context.getArgument("tier", String.class);
        
        try {
            PermissionManager.PermissionTier tier = PermissionManager.PermissionTier.valueOf(tierStr.toUpperCase());
            PermissionManager.getInstance().setPlayerPermission(player, tier);
            context.getSource().sendSuccess(() -> Component.literal("Set " + player + " to " + tier), true);
            return 1;
        } catch (IllegalArgumentException e) {
            context.getSource().sendFailure(Component.literal("Invalid tier: " + tierStr + ". Use PLAYER, MODERATOR, or ADMIN"));
            return 0;
        }
    }

    private static int skinFromURL(CommandContext<CommandSourceStack> context) {
        String url = context.getArgument("url", String.class);
        context.getSource().sendSuccess(() -> Component.literal("Setting skin from URL: " + url), true);
        return 1;
    }

    private static int skinFromPlayer(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Setting skin from your player"), true);
        return 1;
    }

    private static int skinFromUUID(CommandContext<CommandSourceStack> context) {
        String uuid = context.getArgument("uuid", String.class);
        context.getSource().sendSuccess(() -> Component.literal("Setting skin from UUID: " + uuid), true);
        return 1;
    }

    private static int transformToMob(CommandContext<CommandSourceStack> context) {
        String mob = context.getArgument("mob", String.class);
        context.getSource().sendSuccess(() -> Component.literal("Transforming to: " + mob), true);
        return 1;
    }

    private static int transformToPlayer(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Transforming to player form"), true);
        return 1;
    }

    private static int buildStructure(CommandContext<CommandSourceStack> context) {
        String structure = context.getArgument("structure", String.class);
        context.getSource().sendSuccess(() -> Component.literal("Building: " + structure), true);
        return 1;
    }

    private static int createItem(CommandContext<CommandSourceStack> context) {
        String name = context.getArgument("name", String.class);
        String texture = context.getArgument("texture", String.class);
        context.getSource().sendSuccess(() -> Component.literal("Creating item: " + name + " from texture: " + texture), true);
        return 1;
    }

    private static int openConfig(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            UINetworkMessages.sendOpenConfigScreen(player);
            context.getSource().sendSuccess(() -> Component.literal("Opening Godcore configuration..."), true);
            return 1;
        }
        context.getSource().sendFailure(Component.literal("This command can only be used by players"));
        return 0;
    }
}

package com.godcore.network;

import com.godcore.gui.APIConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UINetworkMessages {
    
    public static final CustomPacketPayload.Type<OpenConfigScreenMessage> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("godcore", "open_config_screen"));
    
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, OpenConfigScreenMessage> STREAM_CODEC = 
        net.minecraft.network.codec.StreamCodec.of(
            (buf, msg) -> msg.write(buf),
            OpenConfigScreenMessage::new
        );
    
    public static void register(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar) {
        // Register Open Config Screen Message (Server -> Client)
        registrar.playToClient(TYPE, STREAM_CODEC, OpenConfigScreenMessage::handle);
    }
    
    public static void sendOpenConfigScreen(net.minecraft.server.level.ServerPlayer player) {
        player.connection.send(new OpenConfigScreenMessage());
    }
    
    public static class OpenConfigScreenMessage implements CustomPacketPayload {
        public OpenConfigScreenMessage() {}
        
        public OpenConfigScreenMessage(RegistryFriendlyByteBuf buffer) {}
        
        public void write(RegistryFriendlyByteBuf buffer) {}
        
        public static void handle(OpenConfigScreenMessage message, IPayloadContext context) {
            context.enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new APIConfigScreen());
            });
        }

        @Override
        public CustomPacketPayload.Type<OpenConfigScreenMessage> type() {
            return TYPE;
        }
    }
}

package com.godcore.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHandler.class);

    public static void register(RegisterPayloadHandlersEvent event) {
        UINetworkMessages.register(event.registrar("godcore"));
        LOGGER.info("Godcore network channels registered");
    }
}

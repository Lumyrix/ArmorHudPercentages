package com.notryken;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmorHudPercentagesClient implements ClientModInitializer {
    public static final String MOD_ID = "armor-hud-percentages";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // no keybind for 1.21.6-1.21.8
    }
}

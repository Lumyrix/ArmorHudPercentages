package com.notryken;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/** Registers the mod with LumLib. */
public class AhpLumInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (!FabricLoader.getInstance().isModLoaded("lumlib")) {
            throw new IllegalStateException("LumLib is required by ArmorHUD Percentages: https://modrinth.com/mod/lumlib");
        }
        AhpLumConfig.register();
    }
}

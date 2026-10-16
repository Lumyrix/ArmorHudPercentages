package com.notryken;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Runs before Minecraft initializes anything.
 * At this point AWT is guaranteed to be uninitialized, so the
 * LumLib missing dialog can open before the MC window appears.
 */
public class LumLibPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        LumLibMissingDialog.checkAndShow("ArmorHUD Percentages");
    }
}

package com.notryken;

import dev.lumyrix.lumlib.api.LumConfigContainer;
import dev.lumyrix.lumlib.api.LumLib;

/** So e carregada quando o LumLib esta presente (checagem em AhpLumInit). */
final class AhpLumConfig {

    private AhpLumConfig() {
    }

    static void register() {
        LumConfigContainer c = LumLib.registerMod("armor-hud-percentages", "Armor HUD Percentages");
        c.addBoolean("enabled", "Show percentages", "Displays armor durability percentages on the HUD.", true,
                v -> AhpConfig.enabled = v, () -> AhpConfig.enabled);
        c.addBoolean("showDecimals", "Show decimals", "Shows decimal places in the armor percentages.", false,
                v -> AhpConfig.decimals = v ? 1 : 0, () -> AhpConfig.decimals > 0);
        c.addDoubleSlider("textScale", "Text size", "Adjusts the size of the percentage text.", 0.5, 2.0, 0.05, 1.0,
                v -> { AhpConfig.textScale = v; AhpConfig.recalc(); }, () -> AhpConfig.textScale);
        c.addDoubleSlider("numberScale", "Number size", "Adjusts the size of the percentage numbers independently.", 0.5, 2.0, 0.05, 1.0,
                v -> { AhpConfig.numberRelScale = v; AhpConfig.recalc(); }, () -> AhpConfig.numberRelScale);
        c.addDoubleSlider("percentScale", "Percent sign size", "Adjusts the size of the percent sign independently.", 0.5, 2.0, 0.05, 1.0,
                v -> { AhpConfig.percentRelScale = v; AhpConfig.recalc(); }, () -> AhpConfig.percentRelScale);
        c.addBoolean("shadow", "Text shadow", "Adds a shadow behind percentage text for better readability.", false,
                v -> AhpConfig.shadow = v, () -> AhpConfig.shadow);
        c.addDoubleSlider("saturation", "Color saturation", "Controls how vivid the percentage text colors appear.", 0.5, 2.0, 0.05, 1.25,
                v -> AhpConfig.saturation = v, () -> AhpConfig.saturation);
        c.build();
    }
}

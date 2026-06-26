package com.notryken;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.uku3lig.ukulib.utils.Ukutils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.berdinskiybear.armorhud.ArmorHudMod;

public class ArmorHudPercentagesClient implements ClientModInitializer {

    public static final String MOD_ID = "armor-hud-percentages";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        Ukutils.registerToggleBind(
                new KeyBinding(
                        "armor-hud-percentages.keybind.toggle",
                        -1,
                        KeyBinding.Category.create(Identifier.of("armor-hud-percentages", "key"))
                ),
                () -> ArmorHudMod.getManager().getConfig().isWarningShown(),
                (b) -> ArmorHudMod.getManager().getConfig().setWarningShown(b),
                Text.translatable("armor-hud-percentages.keybind.toggle.msg")
        );
    }
}

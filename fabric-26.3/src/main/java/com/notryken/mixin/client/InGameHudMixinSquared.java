package com.notryken.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.berdinskiybear.armorhud.ArmorHudRenderer;
import ru.berdinskiybear.armorhud.mixin.HudAccessor;
import java.awt.Color;

/**
 * Mixin into ArmorHudRenderer (uku's armor hud 0.14.0+).
 * No longer needs MixinSquared — the renderer is now a plain class.
 */
@Mixin(value = ArmorHudRenderer.class, priority = 1100, remap = false)
public class InGameHudMixinSquared {

    @WrapOperation(
            method = "drawArmorHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lru/berdinskiybear/armorhud/mixin/HudAccessor;" +
                             "armorhud$extractSlot(" +
                             "Lnet/minecraft/client/gui/GuiGraphicsExtractor;" +
                             "IILnet/minecraft/client/DeltaTracker;" +
                             "Lnet/minecraft/world/entity/player/Player;" +
                             "Lnet/minecraft/world/item/ItemStack;I)V",
                    remap = false
            ),
            remap = false
    )
    private static void wrapExtractSlot(
            HudAccessor instance,
            GuiGraphicsExtractor graphics,
            int x, int y,
            DeltaTracker delta,
            Player player,
            ItemStack stack,
            int seed,
            Operation<Void> original
    ) {
        original.call(instance, graphics, x, y, delta, player, stack, seed);

        if (!stack.isEmpty() && stack.isDamageableItem() && stack.isDamaged()) {
            Font font = Minecraft.getInstance().font;
            int remaining = stack.getMaxDamage() - stack.getDamageValue();
            int percent = Math.round((remaining / (float) stack.getMaxDamage()) * 100f);
            if (!com.notryken.AhpConfig.enabled || !com.notryken.AhpText.visible(remaining, stack.getMaxDamage())) return;

            String text = percent + "%";
            int textWidth = font.width(text);
            int textX = x + (16 - textWidth) / 2;
            int textY = y -12;

            int barColor = stack.getBarColor();
            int boosted = boostSaturation(barColor, (float) com.notryken.AhpConfig.saturation);
            {
            String ahpNum = com.notryken.AhpText.number(remaining, stack.getMaxDamage());
            float ahpNs = (float) com.notryken.AhpConfig.numberScale;
            float ahpPs = (float) com.notryken.AhpConfig.percentScale;
            float ahpWn = font.width(ahpNum) * ahpNs;
            float ahpWp = font.width("%") * ahpPs;
            float ahpLeft = x + 8f - (ahpWn + ahpWp) / 2f;
            float ahpBase = textY;
            int col = boosted;
            var m = graphics.pose();
            m.pushMatrix(); m.translate(ahpLeft, ahpBase + 9f * (1f - ahpNs)); m.scale(ahpNs, ahpNs); graphics.text(font, ahpNum, 0, 0, ARGB.opaque(col), com.notryken.AhpConfig.shadow); m.popMatrix();
            m.pushMatrix(); m.translate(ahpLeft + ahpWn, ahpBase + 9f * (1f - ahpPs)); m.scale(ahpPs, ahpPs); graphics.text(font, "%", 0, 0, ARGB.opaque(col), com.notryken.AhpConfig.shadow); m.popMatrix();
        }
        }
    }

    private static int boostSaturation(int color, float factor) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[1] = Math.min(1f, hsb[1] * factor);
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }
}

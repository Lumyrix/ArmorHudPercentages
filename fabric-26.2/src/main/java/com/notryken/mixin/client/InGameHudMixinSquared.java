package com.notryken.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;
import java.awt.Color;

@Mixin(value = Hud.class, priority = 1100)
public class InGameHudMixinSquared {

    @Shadow @Final
    private RandomSource random;

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.MixinHud",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lru/berdinskiybear/armorhud/config/ArmorHudConfig;getWarningBobIntensity()I"
            )
    )
    private int wrapGetWarningBobIntensity(
            ArmorHudConfig instance,
            Operation<Integer> original
    ) {
        return 0;
    }

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.MixinHud",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lru/berdinskiybear/armorhud/config/ArmorHudConfig;isWarningShown()Z"
            )
    )
    private boolean wrapIsWarningShown(
            ArmorHudConfig instance,
            Operation<Boolean> original
    ) {
        return false;
    }

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.MixinHud",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Hud;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"
            )
    )
    private void wrapExtractSlot(
            Hud instance,
            GuiGraphicsExtractor graphics,
            int x, int y,
            DeltaTracker delta,
            Player player,
            ItemStack stack,
            int seed,
            Operation<Void> original,
            @Local(ordinal = 0) ArmorHudConfig config
    ) {
        original.call(instance, graphics, x, y, delta, player, stack, seed);

        if (!stack.isEmpty() && stack.isDamageableItem() && stack.isDamaged()) {
            Font font = Minecraft.getInstance().font;
            int remaining = stack.getMaxDamage() - stack.getDamageValue();
            int percent = Math.round((remaining / (float) stack.getMaxDamage()) * 100f);
            if (percent >= 100) return;

            String text = percent + "%";
            int textWidth = font.width(text);
            int textX = x + (16 - textWidth) / 2;
            int textY = y - 1;

            if (ArmorHudMod.shouldShowWarning(stack)) {
                int bobIntensity = config.getWarningBobIntensity();
                if (bobIntensity > 0) {
                    textY += (int) (this.random.nextInt(bobIntensity) - Math.ceil(bobIntensity / 2.0));
                }
            }

            int barColor = stack.getBarColor();
            int boosted = boostSaturation(barColor, 1.25f);
            graphics.text(font, text, textX, textY, ARGB.opaque(boosted), false);
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

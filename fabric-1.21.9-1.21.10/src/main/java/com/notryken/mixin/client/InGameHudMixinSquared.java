package com.notryken.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import java.awt.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

@Mixin(value = InGameHud.class, priority = 1100)
public class InGameHudMixinSquared {

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.InGameHudMixin",
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
            mixin = "ru.berdinskiybear.armorhud.mixin.InGameHudMixin",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lru/berdinskiybear/armorhud/ArmorHudMod;shouldShowWarning(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean wrapShouldShowWarning(
            ItemStack stack,
            Operation<Boolean> original
    ) {
        return !stack.isEmpty() && stack.isDamageable() && stack.getDamage() > 0;
    }

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.InGameHudMixin",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIIIII)V"
            )
    )
    private void wrapDrawTexture(
            DrawContext instance,
            RenderPipeline pipeline,
            Identifier sprite,
            int x,
            int y,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            Operation<Void> original,
            @Local ItemStack stack
    ) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        if (stack.isEmpty() || !stack.isDamageable()) return;
        int remaining = stack.getMaxDamage() - stack.getDamage();
        int percent = Math.round((remaining / (float) stack.getMaxDamage()) * 100f);
        if (percent >= 100) return;
        String text = percent + "%";
        int offsetX = x + (width - font.getWidth(text)) / 2;
        int boosted = boostSaturation(stack.getItemBarColor(), 1.25f);
        instance.drawText(font, text, offsetX, y - 1, boosted, false);
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

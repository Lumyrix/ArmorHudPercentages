package com.notryken.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

@Mixin(value = InGameHud.class, priority = 1100)
public class InGameHudMixinSquared {
    @Shadow
    @Final
    private static int WIDTH;

    @Shadow
    @Final
    private static int WARNING_OFFSET;

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
            Operation<Boolean> original,
            @Share("showWarning") LocalBooleanRef showWarningRef
    ) {
        showWarningRef.set(original.call(stack));
        return !stack.isEmpty() && stack.isDamageable() && stack.isItemBarVisible();
    }

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
            Operation<Integer> original,
            @Share("showWarning") LocalBooleanRef showWarningRef
    ) {
        if (showWarningRef.get()) {
            return original.call(instance);
        }
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
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"
            )
    )
    private void wrapDrawTexture(
            DrawContext instance,
            Identifier texture,
            int x,
            int y,
            int z,
            float u,
            float v,
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
        if (!com.notryken.AhpConfig.enabled || !com.notryken.AhpText.visible(remaining, stack.getMaxDamage())) return;
        String text = percent + "%";
        int offsetX = 0;
        offsetX -= WARNING_OFFSET;
        offsetX += WIDTH / 2;
        offsetX -= font.getWidth(text) / 2;
        {
            String ahpNum = com.notryken.AhpText.number(remaining, stack.getMaxDamage());
            float ahpNs = (float) com.notryken.AhpConfig.numberScale;
            float ahpPs = (float) com.notryken.AhpConfig.percentScale;
            float ahpWn = font.getWidth(ahpNum) * ahpNs;
            float ahpWp = font.getWidth("%") * ahpPs;
            float ahpLeft = x - WARNING_OFFSET + WIDTH / 2f - (ahpWn + ahpWp) / 2f;
            float ahpBase = y - 1;
            int col = com.notryken.AhpText.boost(stack.getItemBarColor(), (float) com.notryken.AhpConfig.saturation);
            var m = instance.getMatrices();
            m.push(); m.translate(ahpLeft, ahpBase + 9f * (1f - ahpNs), 0f); m.scale(ahpNs, ahpNs, 1f); instance.drawText(font, ahpNum, 0, 0, col, com.notryken.AhpConfig.shadow); m.pop();
            m.push(); m.translate(ahpLeft + ahpWn, ahpBase + 9f * (1f - ahpPs), 0f); m.scale(ahpPs, ahpPs, 1f); instance.drawText(font, "%", 0, 0, col, com.notryken.AhpConfig.shadow); m.pop();
        }
    }
}

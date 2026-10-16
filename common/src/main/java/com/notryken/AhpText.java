package com.notryken;

import java.awt.Color;
import java.util.Locale;

/** Pure logic (no Minecraft classes) shared by the mixins of every version. */
public final class AhpText {

    private AhpText() {
    }

    /** Never shows 100%: a fully durable item gets no text. */
    public static boolean visible(int remaining, int max) {
        if (max <= 0) {
            return false;
        }
        if (AhpConfig.decimals > 0) {
            return remaining < max;
        }
        return Math.round(remaining * 100f / max) < 100;
    }

    /** Number without the % sign, e.g. "99", "99.5" or "99.52". Truncates so it never rounds up to 100.0. */
    public static String number(int remaining, int max) {
        double p = remaining * 100.0 / max;
        int d = Math.max(0, Math.min(2, AhpConfig.decimals));
        if (d == 0) {
            return Integer.toString((int) Math.round(p));
        }
        double f = Math.pow(10, d);
        return String.format(Locale.ROOT, "%." + d + "f", Math.floor(p * f) / f);
    }

    public static int boost(int color, float factor) {
        float[] hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, null);
        hsb[1] = Math.min(1f, hsb[1] * factor);
        return 0xFF000000 | (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF);
    }
}

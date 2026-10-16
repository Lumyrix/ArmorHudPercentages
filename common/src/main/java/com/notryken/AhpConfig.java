package com.notryken;

/** Config local do ArmorHUD Percentages, editada pelo LumLib. */
public final class AhpConfig {
    public static volatile boolean enabled = true;
    public static volatile boolean shadow = false;
    /** 0 = "99%", 1 = "99.5%", 2 = "99.52%". O menu liga/desliga 1 casa. */
    public static volatile int decimals = 0;
    public static volatile double saturation = 1.25;

    /** Tamanho geral (numero + %) e ajuste relativo de cada um. */
    public static volatile double textScale = 1.0;
    public static volatile double numberRelScale = 1.0;
    public static volatile double percentRelScale = 1.0;

    /** Escalas efetivas lidas pelos mixins (geral x relativo). */
    public static volatile double numberScale = 1.0;
    public static volatile double percentScale = 1.0;

    private AhpConfig() {
    }

    public static void recalc() {
        numberScale = Math.max(0.25, Math.min(4.0, textScale * numberRelScale));
        percentScale = Math.max(0.25, Math.min(4.0, textScale * percentRelScale));
    }
}

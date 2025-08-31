package wolfwriter.soulburst_fx.soulBurstFX.storage;

import org.bukkit.Particle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KillData {

    private boolean enabled;
    private String effectType;
    private String style;
    private boolean styleEnabled;

    public KillData() {}

    public KillData(boolean enabled, String effectType, String style, boolean styleEnabled) {
        this.enabled = enabled;
        this.effectType = effectType;
        this.style = style;
        this.styleEnabled = styleEnabled;

        unlockedEffects.add(effectType.toLowerCase());
        unlockedStyles.add(style.toLowerCase());

    }

    public boolean isEnabled() { return enabled; }
    public String getEffectType() { return effectType; }
    public String getStyle() { return style; }
    public boolean isStyleEnabled() { return styleEnabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setEffectType(String effectType) { this.effectType = effectType; }
    public void setStyle(String style) { this.style = style; }
    public void setStyleEnabled(boolean styleEnabled) { this.styleEnabled = styleEnabled; }

    private Set<String> unlockedEffects = new HashSet<>();
    private Set<String> unlockedStyles = new HashSet<>();

    public Set<String> getUnlockedEffects() { return unlockedEffects; }
    public Set<String> getUnlockedStyles() { return unlockedStyles; }

    public void unlockEffect(String effect) { unlockedEffects.add(effect.toLowerCase()); }

    public void unlockStyle(String style) { unlockedStyles.add(style.toLowerCase()); }

    public boolean isEffectUnlocked(String effect) {
        return unlockedEffects.contains("*") || unlockedEffects.contains(effect.toLowerCase());
    }

    public boolean isStyleUnlocked(String style) {
        return unlockedStyles.contains("*") || unlockedStyles.contains(style.toLowerCase());
    }

    public void lockEffect(String effect) {
        effect = effect.toLowerCase();
        // Wenn globaler Unlock aktiv ist, entfernen wir ihn
        if (unlockedEffects.contains("*")) {
            unlockedEffects.remove("*");

            // Alle anderen Effekte außer dem gesperrten hinzufügen
            for (Particle p : Particle.values()) {
                if (p.getDataType() == Void.class) {
                    String name = p.name().toLowerCase();
                    if (!name.equals(effect)) {
                        unlockedEffects.add(name);
                    }
                }
            }
        }

        // Dann gezielt sperren
        unlockedEffects.remove(effect);
    }

    public void lockStyle(String style) {
        style = style.toLowerCase();
        if (unlockedStyles.contains("*")) {
            unlockedStyles.remove("*");

            List<String> validStyles = List.of("circle", "flower", "spiral", "ring", "helix", "burst");
            for (String s : validStyles) {
                if (!s.equals(style)) {
                    unlockedStyles.add(s);
                }
            }
        }

        unlockedStyles.remove(style);
    }


}


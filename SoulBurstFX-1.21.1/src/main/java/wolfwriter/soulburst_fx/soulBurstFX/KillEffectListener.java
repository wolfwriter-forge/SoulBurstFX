package wolfwriter.soulburst_fx.soulBurstFX;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wolfwriter.soulburst_fx.soulBurstFX.storage.KillData;
import wolfwriter.soulburst_fx.soulBurstFX.storage.PlayerDataManager;

import java.util.UUID;

public class KillEffectListener implements Listener {
    private final PlayerDataManager dataManager;

    public KillEffectListener(PlayerDataManager manager) {
        this.dataManager = manager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;

        UUID uuid = killer.getUniqueId();
        KillData data = dataManager.get(uuid);
        dataManager.set(uuid, data);
        dataManager.saveData();

        if (!data.isEnabled()) return;

        Particle particle;
        try {
            particle = Particle.valueOf(data.getEffectType().toUpperCase());
        } catch (IllegalArgumentException e) {
            killer.sendMessage("§cInvalid particle effect: " + data.getEffectType());
            return;
        }

        Location loc = event.getEntity().getLocation();
        String style = data.getStyle();
        World world = loc.getWorld();

        if (!data.isStyleEnabled()) {
            // Stil deaktiviert → einfacher Effekt
            world.spawnParticle(particle, loc, 30, 0.5, 0.5, 0.5, 0.1);
            return;
        }


        // 🔁 Muster anzeigen
        switch (style.toLowerCase()) {
            case "circle" -> KillEffectPattern.spawnCircle(loc, particle, 1.5);
            case "flower" -> KillEffectPattern.spawnFlower(loc, particle, 5, 1.5);
            case "spiral" -> KillEffectPattern.spawnSpiral(loc, particle, 3, 2.0);
            case "ring" -> KillEffectPattern.spawnRingStack(loc, particle);
            case "helix" -> KillEffectPattern.spawnDoubleHelix(loc, particle, 2.0, 0.5);
            default -> loc.getWorld().spawnParticle(particle, loc, 30, 0.5, 0.5, 0.5, 0.1); // Fallback bei ungültigem Stil
        }
    }
}


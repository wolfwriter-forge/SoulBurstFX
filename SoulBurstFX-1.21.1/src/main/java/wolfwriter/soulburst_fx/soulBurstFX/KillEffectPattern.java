package wolfwriter.soulburst_fx.soulBurstFX;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class KillEffectPattern {

    public static void spawnCircle(Location center, Particle particle, double radius) {
        World world = center.getWorld();
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            world.spawnParticle(particle, center.clone().add(x, 0, z), 0, 0, 0.1, 0, 0.01);
        }
    }

    public static void spawnFlower(Location center, Particle particle, int petals, double radius) {
        World world = center.getWorld();

        // Blütenblätter
        for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 128) {
            double r = radius * Math.sin(petals * theta);
            double x = r * Math.cos(theta);
            double z = r * Math.sin(theta);
            Location particleLoc = center.clone().add(x, 0.05, z); // leicht über dem Boden
            world.spawnParticle(particle, particleLoc, 0, 0, 0, 0, 0.01);
        }

        // Blütenzentrum
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = 0.2 * Math.cos(angle);
            double z = 0.2 * Math.sin(angle);
            Location coreLoc = center.clone().add(x, 0.05, z);
            world.spawnParticle(particle, coreLoc, 0, 0, 0, 0, 0.01);
        }

        // Mittelpunkt
        world.spawnParticle(particle, center.clone().add(0, 0.05, 0), 5, 0, 0, 0, 0.01);
    }

    public static void spawnSpiral(Location center, Particle particle, int loops, double height) {
        World world = center.getWorld();
        for (double t = 0; t < loops * Math.PI; t += Math.PI / 16) {
            double x = Math.cos(t) * 0.5;
            double z = Math.sin(t) * 0.5;
            double y = (height / (loops * Math.PI)) * t;
            world.spawnParticle(particle, center.clone().add(x, y, z), 0, 0, 0.1, 0, 0.01);
        }
    }

    public static void spawnRingStack(Location center, Particle particle) {
        World world = center.getWorld();

        // Ring 1: Boden
        spawnRing(center.clone().add(0, 0.2, 0), particle, 1.0);

        // Ring 2: Beine
        spawnRing(center.clone().add(0, 0.9, 0), particle, 0.7);

        // Ring 3: Oberteil
        spawnRing(center.clone().add(0, 1.6, 0), particle, 0.5);
    }

    public static void spawnRing(Location center, Particle particle, double radius) {
        World world = center.getWorld();
        for (int i = 0; i < 360; i += 15) {
            double radians = Math.toRadians(i);
            double x = radius * Math.cos(radians);
            double z = radius * Math.sin(radians);
            Location particleLoc = center.clone().add(x, 0, z);
            world.spawnParticle(particle, particleLoc, 0, 0, 0.1, 0, 0.01);
        }
    }

    public static void spawnDoubleHelix(Location center, Particle particle, double height, double radius) {
        World world = center.getWorld();
        for (double y = 0; y < height; y += 0.1) {
            double angle = y * 2;
            double x1 = radius * Math.cos(angle);
            double z1 = radius * Math.sin(angle);
            double x2 = radius * Math.cos(angle + Math.PI);
            double z2 = radius * Math.sin(angle + Math.PI);
            world.spawnParticle(particle, center.clone().add(x1, y, z1), 0, 0, 0.1, 0, 0.01);
            world.spawnParticle(particle, center.clone().add(x2, y, z2), 0, 0, 0.1, 0, 0.01);
        }
    }
}

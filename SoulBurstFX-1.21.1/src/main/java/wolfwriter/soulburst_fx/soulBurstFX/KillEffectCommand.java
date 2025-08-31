package wolfwriter.soulburst_fx.soulBurstFX;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import wolfwriter.soulburst_fx.soulBurstFX.storage.KillData;
import wolfwriter.soulburst_fx.soulBurstFX.storage.PlayerDataManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KillEffectCommand implements CommandExecutor, TabCompleter {

    private final PlayerDataManager dataManager;

    public KillEffectCommand(PlayerDataManager manager) {
        this.dataManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UUID uuid = null;
        KillData data = null;

        if (sender instanceof Player player) {
            uuid = player.getUniqueId();
            data = dataManager.get(uuid);
        }

        if (args.length == 0) {
            sender.sendMessage("§cUse §e/killeffect help §cfor a list of available commands.");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "on" -> {
                if (data == null || uuid == null) return error(sender);
                data.setEnabled(true);
                sender.sendMessage("§aKill effects enabled.");
            }

            case "off" -> {
                if (data == null || uuid == null) return error(sender);
                data.setEnabled(false);
                sender.sendMessage("§cKill effects disabled.");
            }

            case "effect" -> {
                if (data == null || uuid == null) return error(sender);
                if (args.length < 2) return usage(sender, "/killeffect effect <name>");

                String effectName = args[1].toLowerCase();
                if (!data.isEffectUnlocked(effectName)) {
                    sender.sendMessage("§cYou haven't unlocked this effect.");
                    return true;
                }

                try {
                    Particle particle = Particle.valueOf(effectName.toUpperCase());
                    if (particle.getDataType() != Void.class) {
                        sender.sendMessage("§cThis effect requires additional data and cannot be used.");
                        return true;
                    }
                    data.setEffectType(effectName);
                    sender.sendMessage("§bEffect set to: §e" + effectName);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cInvalid effect. Use §e/killeffect list");
                }
            }

            case "style" -> {
                if (!sender.hasPermission("killeffect.style.use")) return noPerm(sender);
                if (data == null || uuid == null) return error(sender);
                if (args.length < 2) return usage(sender, "/killeffect style <name|on|off>");

                String styleArg = args[1].toLowerCase();
                if (!data.isStyleUnlocked(styleArg) && !styleArg.equals("on") && !styleArg.equals("off")) {
                    sender.sendMessage("§cYou haven't unlocked this style.");
                    return true;
                }

                List<String> validStyles = List.of("circle", "flower", "spiral", "ring", "helix");

                switch (styleArg) {
                    case "on" -> {
                        data.setStyleEnabled(true);
                        sender.sendMessage("§aStyle effects enabled.");
                    }
                    case "off" -> {
                        data.setStyleEnabled(false);
                        sender.sendMessage("§cStyle effects disabled.");
                    }
                    default -> {
                        if (!validStyles.contains(styleArg)) {
                            sender.sendMessage("§cInvalid style. Use §e/killeffect styles");
                            return true;
                        }
                        data.setStyle(styleArg);
                        sender.sendMessage("§bStyle set to: §e" + styleArg);
                    }
                }
            }

            case "styles" -> {
                sender.sendMessage("§eAvailable styles:");
                List.of("circle", "flower", "spiral", "ring", "helix")
                        .forEach(s -> sender.sendMessage("§7- " + s));
            }

            case "unlock" -> {
                if (!sender.hasPermission("killeffect.unlock")) return noPerm(sender);

                if (args.length >= 5 && args[1].equalsIgnoreCase("player")) {
                    return handleUnlockOrLock(sender, args, true, true);
                }

                if (data == null || uuid == null) return error(sender);
                if (args.length < 3) return usage(sender, "/killeffect unlock <effect|style> <name|all/*>");

                return handleUnlockOrLock(sender, args, false, true);
            }

            case "lock" -> {
                if (!sender.hasPermission("killeffect.lock")) return noPerm(sender);

                if (args.length >= 5 && args[1].equalsIgnoreCase("player")) {
                    return handleUnlockOrLock(sender, args, true, false);
                }

                if (data == null || uuid == null) return error(sender);
                if (args.length < 3) return usage(sender, "/killeffect lock <effect|style> <name|all/*>");

                return handleUnlockOrLock(sender, args, false, false);
            }

            case "list" -> {
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cInvalid page number.");
                        return true;
                    }
                }

                List<String> particles = Arrays.stream(Particle.values())
                        .filter(p -> p.getDataType() == Void.class)
                        .map(p -> p.name().toLowerCase())
                        .sorted()
                        .toList();

                int pageSize = 20;
                int totalPages = (int) Math.ceil((double) particles.size() / pageSize);
                if (page < 1 || page > totalPages) {
                    sender.sendMessage("§cPage not found. There are §e" + totalPages + " §cpages.");
                    return true;
                }

                sender.sendMessage("§7Effects (Page §e" + page + "§7/§e" + totalPages + "§7):");
                particles.subList((page - 1) * pageSize, Math.min(page * pageSize, particles.size()))
                        .forEach(p -> sender.sendMessage("§a• " + p));
            }

            case "reload" -> {
                if (!sender.hasPermission("killeffect.reload")) return noPerm(sender);
                dataManager.loadData();
                sender.sendMessage("§aData reloaded.");
            }

            case "help" -> {
                sender.sendMessage("§e§lSoulBurstFX Commands:");
                sender.sendMessage("§7/killeffect on §8– Enables kill effects");
                sender.sendMessage("§7/killeffect off §8– Disables kill effects");
                sender.sendMessage("§7/killeffect effect <name> §8– Sets the particle effect");
                sender.sendMessage("§7/killeffect list [page] §8– Shows all available effects");

                if (sender.hasPermission("killeffect.style.use")) {
                    sender.sendMessage("§7/killeffect style <name|on|off> §8– Configures style effects");
                    sender.sendMessage("§7/killeffect styles §8– Shows all available styles");
                }

                if (sender.hasPermission("killeffect.unlock")) {
                    sender.sendMessage("§7/killeffect unlock <effect|style> <name|all/*> §8– Unlocks effects/styles");
                    sender.sendMessage("§7/killeffect unlock player <player> <effect|style> <name|all/*> §8– Unlocks for another player");
                }

                if (sender.hasPermission("killeffect.lock")) {
                    sender.sendMessage("§7/killeffect lock <effect|style> <name|all/*> §8– Locks effects/styles");
                    sender.sendMessage("§7/killeffect lock player <player> <effect|style> <name|all/*> §8– Locks for another player");
                }

                if (sender.hasPermission("killeffect.reload")) {
                    sender.sendMessage("§7/killeffect reload §8– Reloads the JSON data");
                }
            }
            default -> sender.sendMessage("§cUnknown subcommand. Use §e/killeffect help");
        }

        if (uuid != null && data != null) {
            dataManager.set(uuid, data);
            dataManager.saveData();
        }

        return true;
    }

    private boolean handleUnlockOrLock(CommandSender sender, String[] args, boolean isOtherPlayer, boolean isUnlock) {
        String type = isOtherPlayer ? args[3].toLowerCase() : args[1].toLowerCase();
        String target = isOtherPlayer ? args[4].toLowerCase() : args[2].toLowerCase();

        KillData targetData;
        UUID targetUUID;

        if (isOtherPlayer) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[2]);
            targetUUID = targetPlayer.getUniqueId();
            targetData = dataManager.get(targetUUID);
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }
            targetUUID = player.getUniqueId();
            targetData = dataManager.get(targetUUID);
        }

        switch (type) {
            case "effect" -> {
                if (target.equals("*") || target.equals("all")) {
                    if (isUnlock) {
                        targetData.getUnlockedEffects().clear();
                        targetData.getUnlockedEffects().add("*");
                        sender.sendMessage("§aAll effects " + (isOtherPlayer ? "for §e" + args[2] : "") + " §ahave been unlocked.");
                    } else {
                        targetData.getUnlockedEffects().clear();
                        sender.sendMessage("§aAll effects " + (isOtherPlayer ? "for §e" + args[2] : "") + " §ahave been locked.");
                    }
                } else {
                    if (isUnlock) {
                        targetData.unlockEffect(target);
                        sender.sendMessage("§aEffect §e" + target + (isOtherPlayer ? " §afor §e" + args[2] : "") + " §ahas been unlocked.");
                    } else {
                        // Wenn globaler Unlock aktiv ist, entfernen wir ihn und setzen alle außer target
                        if (targetData.getUnlockedEffects().contains("*")) {
                            targetData.getUnlockedEffects().clear();
                            for (Particle p : Particle.values()) {
                                if (p.getDataType() == Void.class) {
                                    String name = p.name().toLowerCase();
                                    if (!name.equals(target)) {
                                        targetData.getUnlockedEffects().add(name);
                                    }
                                }
                            }
                        }
                        targetData.lockEffect(target);
                        sender.sendMessage("§cEffect §e" + target + (isOtherPlayer ? " §cfor §e" + args[2] : "") + " §chas been locked.");
                    }
                }
            }

            case "style" -> {
                List<String> validStyles = List.of("circle", "flower", "spiral", "ring", "helix", "burst");

                if (target.equals("*") || target.equals("all")) {
                    if (isUnlock) {
                        targetData.getUnlockedStyles().clear();
                        targetData.getUnlockedStyles().add("*");
                        sender.sendMessage("§aAll styles " + (isOtherPlayer ? "for §e" + args[2] : "") + " §ahave been unlocked.");
                    } else {
                        targetData.getUnlockedStyles().clear();
                        sender.sendMessage("§cAll styles " + (isOtherPlayer ? "for §e" + args[2] : "") + " §ahave been locked.");
                    }
                } else {
                    if (isUnlock) {
                        targetData.unlockStyle(target);
                        sender.sendMessage("§aStyle §e" + target + (isOtherPlayer ? " §afor §e" + args[2] : "") + " §ahas been unlocked.");
                    } else {
                        // Wenn globaler Unlock aktiv ist, entfernen wir ihn und setzen alle außer target
                        if (targetData.getUnlockedStyles().contains("*")) {
                            targetData.getUnlockedStyles().clear();
                            for (String style : validStyles) {
                                if (!style.equalsIgnoreCase(target)) {
                                    targetData.getUnlockedStyles().add(style);
                                }
                            }
                        }
                        targetData.lockStyle(target);
                        sender.sendMessage("§cStyle §e" + target + (isOtherPlayer ? " §cfor §e" + args[2] : "") + " §chas been locked.");
                    }
                }
            }

            default -> {
                sender.sendMessage("§cInvalid type. Use §eeffect§c or §estyle");
                return true;
            }
        }

        dataManager.set(targetUUID, targetData);
        dataManager.saveData();
        return true;
    }

    private boolean error(CommandSender sender) {
        sender.sendMessage("§cOnly players can use this command.");
        return true;
    }

    private boolean noPerm(CommandSender sender) {
        sender.sendMessage("§cYou don't have permission to use this command.");
        return true;
    }

    private boolean usage(CommandSender sender, String usage) {
        sender.sendMessage("§cUsage: §e" + usage);
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("killeffect")) return Collections.emptyList();

        if (args.length == 1) {
            return List.of("on", "off", "effect", "style", "styles", "list", "reload", "help", "lock", "unlock");
        }

        // /killeffect effect <name>
        if (args.length == 2 && args[0].equalsIgnoreCase("effect")) {
            return getParticles();
        }

        // /killeffect style <name>
        if (args.length == 2 && args[0].equalsIgnoreCase("style")) {
            return getStyles();
        }

        // /killeffect lock|unlock <effect|style>
        if (args.length == 2 && (args[0].equalsIgnoreCase("lock") || args[0].equalsIgnoreCase("unlock"))) {
            return List.of("effect", "style", "player");
        }

        // /killeffect lock|unlock effect <name>
        if (args.length == 3 && (args[0].equalsIgnoreCase("lock") || args[0].equalsIgnoreCase("unlock"))) {
            if (args[1].equalsIgnoreCase("effect")) return getParticles();
            if (args[1].equalsIgnoreCase("style")) return getStyles();
        }

        // /killeffect lock|unlock player <name>
        if (args.length == 3 && args[1].equalsIgnoreCase("player")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        // /killeffect lock|unlock player Game <effect|style>
        if (args.length == 4 && args[1].equalsIgnoreCase("player")) {
            return List.of("effect", "style");
        }

        // /killeffect lock|unlock player Game effect <name>
        if (args.length == 5 && args[1].equalsIgnoreCase("player") && args[3].equalsIgnoreCase("effect")) {
            return getParticles();
        }

        // /killeffect lock|unlock player Game style <name>
        if (args.length == 5 && args[1].equalsIgnoreCase("player") && args[3].equalsIgnoreCase("style")) {
            return getStyles();
        }

        return Collections.emptyList();
    }

    private List<String> getParticles() {
        return Arrays.stream(Particle.values())
                .filter(p -> p.getDataType() == Void.class)
                .map(p -> p.name().toLowerCase())
                .sorted()
                .toList();
    }

    private List<String> getStyles() {
        return List.of("circle", "flower", "spiral", "ring", "helix", "on", "off");
    }
}

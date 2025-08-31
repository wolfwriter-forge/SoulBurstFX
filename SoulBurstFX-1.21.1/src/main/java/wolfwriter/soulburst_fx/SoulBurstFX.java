package wolfwriter.soulburst_fx;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import wolfwriter.soulburst_fx.soulBurstFX.KillEffectCommand;
import wolfwriter.soulburst_fx.soulBurstFX.KillEffectListener;
import wolfwriter.soulburst_fx.soulBurstFX.storage.PlayerDataManager;
import wolfwriter.soulburst_fx.soulBurstFX.storage.KillDataManager;

public final class SoulBurstFX extends JavaPlugin {

    private static SoulBurstFX instance;
    private PlayerDataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;
        dataManager = new PlayerDataManager(this);
        dataManager.loadData();

        getServer().getPluginManager().registerEvents(new KillEffectListener(dataManager), this);
        PluginCommand command = getCommand("killeffect");
        if (command != null) {
            KillEffectCommand executor = new KillEffectCommand(dataManager);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().warning("Command 'killeffect' konnte nicht registriert werden.");
        }

        getLogger().info("------------------------------------------------");
        getLogger().info("|    SoulBurstFX has been successfully started  |");
        getLogger().info("|                                               |");
        getLogger().info("|  Author:        Wolfwriter                    |");
        getLogger().info("|  Version:       " + getDescription().getVersion() + "                       |");
        getLogger().info("|  Server:        " + getServer().getName() + " (" + getServer().getVersion() + ") |");
        getLogger().info("|  Minecraft:     " + getServer().getMinecraftVersion() + "                  |");
        getLogger().info("------------------------------------------------");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveData();
        }

    getLogger().info("--------------------------------------------------------------");
        getLogger().info("|    SoulBurstFX plugin has been disabled                      |");
        getLogger().info("|                                                              |");
        getLogger().info("|  Plugin Name:   SoulBurstFX                                  |");
        getLogger().info("|  Author:         Wolfwriter                                  |");
        getLogger().info("|  Version:        " + getDescription().getVersion() + "                                |");
        getLogger().info("|  Server Type:    " + getServer().getName() + "                                    |");
        getLogger().info("|  Server Version: " + getServer().getVersion() + "                   |");
        getLogger().info("|  Minecraft:      " + getServer().getMinecraftVersion() + "                           |");
        getLogger().info("|                                                              |");
        getLogger().info("|  All effects have been stopped.                              |");
        getLogger().info("|  Thanks for using SoulBurstFX – see you next time!           |");
        getLogger().info("--------------------------------------------------------------");
        // Plugin shutdown logic
    }

    public static SoulBurstFX getInstance() {
        return instance;
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

}

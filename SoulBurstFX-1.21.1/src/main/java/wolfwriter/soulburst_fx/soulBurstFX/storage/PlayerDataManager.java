package wolfwriter.soulburst_fx.soulBurstFX.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerDataManager {
    private final File file;
    private  Gson gson;
    private final Map<UUID, KillData> dataMap = new HashMap<>();
    private final JavaPlugin plugin;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "soulburstfx_playerdata.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void loadData() {
        plugin.getDataFolder().mkdirs();

        if (!file.exists()) {
            plugin.getLogger().info("No existing player data found. A new file will be created.");
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, KillData>>() {}.getType();
            Map<UUID, KillData> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                dataMap.putAll(loaded);
            }
            plugin.getLogger().info("Player data loaded from soulburstfx_playerdata.json");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load player data: " + e.getMessage());
        }
    }

    public void saveData() {
        plugin.getDataFolder().mkdirs();

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(dataMap, writer);
            plugin.getLogger().info("Player data saved to soulburstfx_playerdata.json");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data: " + e.getMessage());
        }
    }

    public KillData get(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, k -> new KillData(false, "flame", "circle", false));
    }

    public void set(UUID uuid, KillData data) {
        dataMap.put(uuid, data);
    }

    public void setEffectType(UUID uuid, String effectType) {
        KillData data = get(uuid);
        data.setEffectType(effectType);
        saveData();
    }

    public static class PlayerData {
        public boolean enabled = true;
        public String effectType = "flame";
    }
}

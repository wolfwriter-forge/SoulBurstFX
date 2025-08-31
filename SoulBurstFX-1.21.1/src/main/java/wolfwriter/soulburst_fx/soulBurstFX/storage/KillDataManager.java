package wolfwriter.soulburst_fx.soulBurstFX.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillDataManager {
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, KillData> dataMap = new HashMap<>();
    private final JavaPlugin plugin;

    public KillDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "soulburstfx_playerdata.json");
    }

    public void loadData() {
        plugin.getDataFolder().mkdirs();
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, KillData>>() {}.getType();
            Map<UUID, KillData> loaded = gson.fromJson(reader, type);
            if (loaded != null) dataMap.putAll(loaded);
            plugin.getLogger().info("KillEffect player data loaded.");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load kill effect data: " + e.getMessage());
        }
    }

    public void saveData() {
        plugin.getDataFolder().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(dataMap, writer);
            plugin.getLogger().info("KillEffect player data saved.");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save kill effect data: " + e.getMessage());
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

    public Map<UUID, KillData> getDataMap() {
        return dataMap;
    }
}


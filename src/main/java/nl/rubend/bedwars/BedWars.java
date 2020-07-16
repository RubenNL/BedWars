package nl.rubend.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedWars extends JavaPlugin {
    private static BedWars plugin;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        worldManager = new WorldManager(getConfig().getString("world"));
        Bukkit.getPluginManager().registerEvents(worldManager, this);

    }

    public static BedWars getPlugin() {
        return plugin;
    }
}
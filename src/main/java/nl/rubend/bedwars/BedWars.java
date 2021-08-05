package nl.rubend.bedwars;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BedWars extends JavaPlugin {
    private static BedWars plugin;
    private WorldManager worldManager;
    private final String worldName=getConfig().getString("world");
    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        worldManager = new WorldManager(worldName);
        Bukkit.getPluginManager().registerEvents(worldManager, this);
        reset();

    }
    @Override
    public void onDisable() {
        worldManager.stop();
    }
    public static BedWars getPlugin() {
        return plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("startbedwars")) plugin.worldManager.getJoinlistener().getGame().startGame();
        if (cmd.getName().equalsIgnoreCase("resetbedwars")) reset();
        return true;
    }
    private void reset() {
        List<Player> players = new ArrayList<>();
        World world=Bukkit.getWorld(worldName);
        if(world!=null) {
            world.setKeepSpawnInMemory(false);
            players=world.getPlayers();
            players.forEach(player -> player.performCommand("mvtp lobby"));
            Bukkit.unloadWorld(worldName, false);
        }
        final File src = new File(Bukkit.getWorldContainer() + File.separator + worldName);
        try {
            FileUtils.deleteDirectory(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File backup = new File(Bukkit.getWorldContainer() + File.separator + worldName+"-backup");
        try {
            FileUtils.copyDirectory(backup,src);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getServer().createWorld(new WorldCreator(worldName));
        plugin.getLogger().info("loaded.");
        players.forEach(player->player.performCommand("mvtp bedwars"));
    }
}
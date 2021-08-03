package nl.rubend.bedwars;

import com.onarandombox.MultiverseCore.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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
        reset();
        Bukkit.getPluginManager().registerEvents(worldManager, this);

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
        World world=Bukkit.getWorld(worldName);
        world.setKeepSpawnInMemory(false);
        List<Player> players=world.getPlayers();
        players.forEach(player->player.performCommand("mvtp lobby"));
        Bukkit.unloadWorld(worldName,false);
        final File src = new File(Bukkit.getWorldContainer() + File.separator + worldName);
        FileUtils.deleteFolder(src);
        File backup = new File(Bukkit.getWorldContainer() + File.separator + worldName+"-backup");
        FileUtils.copyFolder(backup,src);
        getServer().createWorld(new WorldCreator(worldName));
        plugin.getLogger().info("loaded.");
        players.forEach(player->player.performCommand("mvtp bedwars"));
    }
}
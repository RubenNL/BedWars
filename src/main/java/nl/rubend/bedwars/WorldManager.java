package nl.rubend.bedwars;

import nl.rubend.bedwars.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldManager implements Listener {
	private String worldName;
	private JoinListener joinListener;

	public WorldManager(String worldName) {
		this.worldName = worldName;
	}

	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		if (event.getWorld() != getWorld()) return;
		joinListener = new JoinListener(getWorld());
		Bukkit.getPluginManager().registerEvents(joinListener, BedWars.getPlugin());
	}

	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event) {
		if (!event.getWorld().getName().equals(worldName)) return;
		HandlerList.unregisterAll(joinListener);
	}
}

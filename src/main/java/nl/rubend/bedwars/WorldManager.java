package nl.rubend.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
		if(Bukkit.getWorld(worldName)!=null) {
			onWorldLoad(new WorldLoadEvent(Bukkit.getWorld(worldName)));
		}
	}
	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		if (!event.getWorld().getName().equals(worldName)) return;
		joinListener = new JoinListener(getWorld());
		Bukkit.getPluginManager().registerEvents(joinListener, BedWars.getPlugin());
		getWorld().getEntities().forEach(Entity::remove);
	}

	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event) {
		if (!event.getWorld().getName().equals(worldName)) return;
		HandlerList.unregisterAll(joinListener);
	}
	public JoinListener getJoinlistener() {return joinListener;}
	public void stop() {
		HandlerList.unregisterAll(joinListener);
		HandlerList.unregisterAll(this);
	}
}

package nl.rubend.bedwars.listeners;

import nl.rubend.bedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class JoinListener implements Listener {
	private World world;
	private MainGameListener mainGameListener;
	public JoinListener(World world) {
		this.world=world;
	}
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().getWorld() == world) onJoin(event.getPlayer());
	}
	@EventHandler private void onQuit(PlayerQuitEvent event) {
		if (event.getPlayer().getWorld() == world) onLeave(event.getPlayer());
	}
	@EventHandler
	private void onChangedWorld(PlayerChangedWorldEvent event) {
		Player player=event.getPlayer();
		if(player.getWorld()==world) onJoin(player);
		if(event.getFrom()==world) onLeave(player);
	}
	private void onJoin(Player player) {
		player.getInventory().clear();
		player.getEnderChest().clear();
		for (PotionEffect effect:player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
		if(world.getPlayers().size()==1) {
			for (Entity entity : world.getEntitiesByClass(Villager.class)) entity.remove();
			mainGameListener=new MainGameListener(world);
			Bukkit.getPluginManager().registerEvents(mainGameListener, BedWars.getPlugin());
		}
		mainGameListener.addPlayer(player);
	}
	private void onLeave(Player player) {
		mainGameListener.removePlayer(player);
		if(world.getPlayers().size()>1) return;
		mainGameListener.stopGame();
		HandlerList.unregisterAll(mainGameListener);
		mainGameListener=null;
	}
}

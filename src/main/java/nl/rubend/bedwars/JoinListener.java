package nl.rubend.bedwars;

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
	private Game game;
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
			game =new Game(world);
			Bukkit.getPluginManager().registerEvents(game, BedWars.getPlugin());
		}
		game.addPlayer(player);
	}
	private void onLeave(Player player) {
		game.removePlayer(player);
		if(world.getPlayers().size()>1) return;
		game.stopGame();
		HandlerList.unregisterAll(game);
		game =null;
	}
	public Game getGame() {return game;}
}

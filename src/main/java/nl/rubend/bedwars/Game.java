package nl.rubend.bedwars;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static nl.rubend.bedwars.BedWars.getPlugin;

public class Game implements Listener {
	private World world;
	private List<Team> teams =new ArrayList<>();
	private List<ItemSpawner> publicSpawners=new ArrayList<>();
	public Game(World world) {
		this.world=world;
	}
	public void addPlayer(Player player) {
		if(world.getPlayerCount()==2 && teams.size()==0) startGame();
	}
	public void removePlayer(Player player) {
	}
	public void startGame() {
		teams= getPlugin().getConfig().getList("teams").stream().map(LinkedHashMap.class::cast).map(TeamFactory::new).map(item->item.create(this)).collect(Collectors.toList());
		publicSpawners=getPlugin().getConfig().getList("spawners").stream().map(LinkedHashMap.class::cast).map(item->new ItemSpawner(item,this)).collect(Collectors.toList());
		List<Player> players=world.getPlayers();
		for(Player player:players) teams.get(players.indexOf(player)%(teams.size()+1)).addPlayer(player);
		for(Team team: teams) team.start();
	}
	public void broadcast(String message) {
		for(Player player:world.getPlayers()) player.sendMessage(message);
	}
	public void removeTeam(Team team) {
		teams.remove(team);
		if(teams.size()==1) stopGame();
	}
	public void stopGame() {
		if(teams.size()==1) broadcast("Team "+ teams.get(0).getName()+" WON!");
		for(Team team: teams) team.stop();
		for(ItemSpawner spawner:publicSpawners) spawner.stop();
	}
	@EventHandler
	private void onItemDamage(PlayerItemDamageEvent event) {
		if(event.getPlayer().getWorld()==world) event.setCancelled(true);
	}
	@EventHandler private void onCraft(CraftItemEvent event) {
		if(event.getWhoClicked().getWorld()==world) event.setCancelled(true);
	}
	public World getWorld() {
		return this.world;
	}
}

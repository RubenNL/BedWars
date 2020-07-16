package nl.rubend.bedwars.listeners;

import nl.rubend.bedwars.CampInfo;
import nl.rubend.bedwars.ItemSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class MainGameListener implements Listener {
	private World world;
	private ArrayList<CampInfo> camps=new ArrayList<>();
	private ArrayList<ItemSpawner> publicSpawners=new ArrayList<>();
	public MainGameListener(World world) {
		this.world=world;
	}
	public void addPlayer(Player player) {
		if(world.getPlayerCount()==2) startGame();
	}
	public void removePlayer(Player player) {
	}
	public void startGame() {
		camps.add(new CampInfo(this, "RED",Color.RED,new Location(world,32,65,0),new Location(world,32,65,1),new Location(world,32,65,-3),new Location(world,35.5,65,0.5,90,0)));
		camps.add(new CampInfo(this,"GREEN",Color.GREEN,new Location(world,-32,65,0),new Location(world,-32,65,1),new Location(world,-32,65,-3),new Location(world,-34.5,65,0.5,-90,0)));
		publicSpawners.add(new ItemSpawner(new Location(world,25,65,10),ItemSpawner.DIAMOND));
		List<Player> players=world.getPlayers();
		for(Player player:players) camps.get(players.indexOf(player)%(camps.size()+1)).addPlayer(player);
		for(CampInfo camp:camps) camp.start();
	}
	private void broadcast(String message) {
		for(Player player:world.getPlayers()) player.sendMessage(message);
	}
	public void removeTeam(CampInfo camp) {
		broadcast("Team "+camp.getName()+" is fully dead!");
		camps.remove(camp);
		if(camps.size()==0) stopGame();
	}
	public void stopGame() {
		broadcast("Team "+camps.get(0).getName()+" WON!");
		for(CampInfo camp:camps) camp.stop();
		for(ItemSpawner spawner:publicSpawners) spawner.stop();
	}
	@EventHandler
	private void onItemDamage(PlayerItemDamageEvent event) {
		if(event.getPlayer().getWorld()==world) event.setCancelled(true);
	}
}

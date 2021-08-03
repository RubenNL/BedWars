package nl.rubend.bedwars;

import org.bukkit.Color;
import org.bukkit.Location;

import java.util.LinkedHashMap;

public class TeamFactory {
	private String name;
	private Location spawn;
	private Location bed;
	private Location villager;
	private Location trader;
	private Location itemspawn;
	private Color color;
	public TeamFactory(LinkedHashMap map) {
		name= (String) map.get("name");
		spawn=Location.deserialize((LinkedHashMap) map.get("spawn"));
		bed=Location.deserialize((LinkedHashMap) map.get("bed"));
		villager=Location.deserialize((LinkedHashMap) map.get("villager"));
		trader=Location.deserialize((LinkedHashMap) map.get("trader"));
		itemspawn=Location.deserialize((LinkedHashMap) map.get("itemspawn"));
		color=Color.RED; //TODO fix this
	}
	public Team create(Game game) {
		spawn.setWorld(game.getWorld());
		bed.setWorld(game.getWorld());
		villager.setWorld(game.getWorld());
		trader.setWorld(game.getWorld());
		itemspawn.setWorld(game.getWorld());
		return new Team(game,name,color,spawn,bed,itemspawn,villager,trader);
	}
}

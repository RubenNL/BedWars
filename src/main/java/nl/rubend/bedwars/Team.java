package nl.rubend.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Team implements Listener {
	private Location spawn;
	private Location bed;
	private Villager villager;
	private ItemSpawner itemSpawner;
	private ArrayList<Player> players=new ArrayList<>();
	private Map<Player,PlayerInfo> playerInfoMap=new HashMap<>();
	private WanderingTraderSpawner trader;
	private ItemStack sword=new ItemStack(Material.WOODEN_SWORD,1);
	private boolean sharpness=false;
	private int haste=0;
	private Color color;
	private String name;
	private Game game;
	public Team(Game game, String name, Color color, Location spawn, Location bed, Location itemSpawn, Location villagerSpawn,Location wanderingTraderSpawn) {
		this.game=game;
		this.spawn=spawn;
		this.bed=bed;
		this.color=color;

		this.villager=new VillagerSpawner(villagerSpawn).getVillager();
		this.trader=new WanderingTraderSpawner(this,wanderingTraderSpawn);
		this.name=name;
		itemSpawner=new ItemSpawner(itemSpawn,1);
	}
	private void broadcastMessage(String message) {
		for(Player player:players) player.sendMessage(message);
	}
	public void addPlayer(Player player) {
		players.add(player);
		playerInfoMap.put(player,new PlayerInfo(player,this));
	}
	public void removePlayer(Player player) {
		players.remove(player);
		if(players.size()==0) {
			stop();
			game.removeTeam(this);
		}
	}
	public void start() {
		for(Player player:players) {
			player.getInventory().addItem(sword);
			player.teleport(spawn);
			player.setBedSpawnLocation(bed);
		}
		Bukkit.getPluginManager().registerEvents(this, BedWars.getPlugin());
	}
	public void stop() {
		itemSpawner.stop();
		trader.stop();
		villager.remove();
		for(PlayerInfo playerInfo:playerInfoMap.values()) playerInfo.stop();
		HandlerList.unregisterAll(this);
	}
	public boolean isSword(ItemStack item) {
		return (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.DIAMOND_SWORD);
	}
	public boolean isSharpnessAble(ItemStack item) {
		return (PlayerInfo.axeLevels.contains(item) || isSword(item));
	}

	private void giveHaste() {
		for(Player player:players) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,Integer.MAX_VALUE,haste-1));
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!players.contains(event.getPlayer())) return;
		if(event.isBedSpawn()) {
			event.getPlayer().getInventory().addItem(sword);
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,Integer.MAX_VALUE,haste-1));
		} else {
			broadcastMessage("Player "+event.getPlayer().getName()+" is out of the game.");
			removePlayer(event.getPlayer());
		}
	}
	public String getName() {
		return name;
	}
	public Color getColor() {
		return color;
	}
	public ArrayList<Player> getPlayers() { return players; }
	public boolean isSharpness() { return sharpness; }
	public void enableSharpness() {
		this.sharpness=true;
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		for (Player player : players) {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null && isSharpnessAble(item)) item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
			}
		}}
	public int getHaste() {return haste; }
	public void setHaste(int haste) {
		this.haste=haste;
		giveHaste();
	}
}

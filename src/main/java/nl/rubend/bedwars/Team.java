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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
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
	private ItemStack sword=new ItemStack(Material.WOODEN_SWORD,1);
	private boolean sharpness=false;
	private int haste=0;
	private Color color;
	private String name;
	private Game game;
	private static ItemStack sharpnessBook=new ItemStack(Material.ENCHANTED_BOOK,1);
	private static ItemStack haste1Pick=new ItemStack(Material.GOLDEN_PICKAXE,1);
	private static ItemStack haste2Pick=new ItemStack(Material.GOLDEN_PICKAXE,1);
	static {
		EnchantmentStorageMeta meta= (EnchantmentStorageMeta) sharpnessBook.getItemMeta();
		meta.addStoredEnchant(Enchantment.DAMAGE_ALL,1,false);
		sharpnessBook.setItemMeta(meta);
		setName(sharpnessBook,"sharpness=4dia");
		setName(haste1Pick,"haste1=2dia");
		setName(haste2Pick,"haste2=4dia");

	}
	private static void setName(ItemStack item,String name) {
		ItemMeta meta= item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}
	public Team(Game game, String name, Color color, Location spawn, Location bed, Location itemSpawn, Location villagerSpawn) {
		this.game=game;
		this.spawn=spawn;
		this.bed=bed;
		this.color=color;
		this.villager=new VillagerSpawner(villagerSpawn).getVillager();
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
		villager.remove();
		for(PlayerInfo playerInfo:playerInfoMap.values()) playerInfo.stop();
		HandlerList.unregisterAll(this);
	}
	private boolean isSword(ItemStack item) {
		return (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.DIAMOND_SWORD);
	}
	private boolean isSharpnessAble(ItemStack item) {
		return (PlayerInfo.axeLevels.contains(item) || isSword(item));
	}
	@EventHandler
	private void onPlayerUpgrade(PlayerBedEnterEvent event) {
		if(!players.contains(event.getPlayer())) return;
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "choose an upgrade");
		if(!sharpness) inv.addItem(sharpnessBook);
		if(haste==0) inv.addItem(haste1Pick);
		if(haste==1) inv.addItem(haste2Pick);
		event.getPlayer().openInventory(inv);
	}
	@EventHandler
	private void onTeamUpgrade(InventoryClickEvent event) {
		if(!players.contains(event.getWhoClicked())) return;
		if(event.getClickedInventory().getType()== InventoryType.HOPPER) {
			event.setCancelled(true);
			if(event.getCurrentItem().equals(sharpnessBook)) {
				if (!removeDiaFromInv((Player) event.getWhoClicked(),4)) return;
				sharpness = true;
				sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				for (Player player : players) {
					for (ItemStack item : player.getInventory().getContents()) {
						if (item != null && isSharpnessAble(item)) item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
					}
				}
			}
			if(event.getCurrentItem().equals(haste1Pick)) {
				if (!removeDiaFromInv((Player) event.getWhoClicked(),2)) return;
				haste=1;
				giveHaste();
			}
			if(event.getCurrentItem().equals(haste2Pick)) {
				if (!removeDiaFromInv((Player) event.getWhoClicked(),4)) return;
				haste=2;
				giveHaste();
			}
			event.getWhoClicked().closeInventory();
		}
		if(event.getClickedInventory().getType()!=InventoryType.MERCHANT) return;
		if(isSword(event.getCurrentItem())) {
			for(ItemStack item:event.getWhoClicked().getInventory().getContents()) {
				if(item!=null && isSword(item)) item.setAmount(0);
			}
		}
		if(isSharpnessAble(event.getCurrentItem()) && sharpness) event.getCurrentItem().addUnsafeEnchantment(Enchantment.DAMAGE_ALL,1);
	}

	private boolean removeDiaFromInv(Player player,int amount) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getType() == Material.DIAMOND && item.getAmount()>(amount-1)) {
				item.setAmount(item.getAmount()-amount);
				return true;
			}
		}
		return false;
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
}

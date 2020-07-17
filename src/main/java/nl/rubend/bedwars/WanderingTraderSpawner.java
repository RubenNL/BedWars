package nl.rubend.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WanderingTraderSpawner implements Listener {
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
	private Team team;
	private WanderingTrader trader;
	public WanderingTraderSpawner(Team team,Location location) {
		this.team=team;
		this.trader=((WanderingTrader) location.getWorld().spawnEntity(location, EntityType.WANDERING_TRADER));
		trader.setCustomName("TEAM UPGRADES");
		trader.setInvulnerable(true);
		trader.setRecipes(new ArrayList<>());
		trader.setAI(false);
		Bukkit.getPluginManager().registerEvents(this, BedWars.getPlugin());
	}
	@EventHandler
	private void onTeamUpgrade(PlayerInteractEntityEvent event) {
		if(!team.getPlayers().contains(event.getPlayer())) return;
		if(event.getRightClicked().getType()!= EntityType.WANDERING_TRADER) return;
		event.setCancelled(true);
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "choose an upgrade");
		if(!team.isSharpness()) inv.addItem(sharpnessBook);
		if(team.getHaste()==0) inv.addItem(haste1Pick);
		if(team.getHaste()==1) inv.addItem(haste2Pick);
		event.getPlayer().openInventory(inv);
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
	@EventHandler
	private void onTeamUpgrade(InventoryClickEvent event) {
		if(!team.getPlayers().contains(event.getWhoClicked())) return;
		if(event.getClickedInventory().getType()== InventoryType.HOPPER) {
			event.setCancelled(true);
			if(event.getCurrentItem().equals(sharpnessBook)) {
				if (!removeDiaFromInv((Player) event.getWhoClicked(),4)) return;
				team.enableSharpness();
			}
			if(event.getCurrentItem().equals(haste1Pick)) {
				if (!removeDiaFromInv((Player) event.getWhoClicked(),2)) return;
				team.setHaste(1);
			}
			if(event.getCurrentItem().equals(haste2Pick)) {
				if (!removeDiaFromInv((Player) event.getWhoClicked(),4)) return;
				team.setHaste(2);
			}
			event.getWhoClicked().closeInventory();
		}
	}
	public void stop() {
		trader.remove();
		HandlerList.unregisterAll(this);
	}
}

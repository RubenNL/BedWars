package nl.rubend.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;

public class PlayerInfo implements Listener {
	private static ArrayList<ArrayList<ItemStack>> armorLevels=new ArrayList<>();
	public static ArrayList<ItemStack> pickaxeLevels=new ArrayList<>();
	public static ArrayList<ItemStack> axeLevels=new ArrayList<>();
	private static void addArmorLevel(ItemStack boots, ItemStack leggings, ItemStack chestplate, ItemStack helmet) {
		ArrayList<ItemStack> armorLevel=new ArrayList<>();
		armorLevel.add(boots);
		armorLevel.add(leggings);
		armorLevel.add(chestplate);
		armorLevel.add(helmet);
		armorLevels.add(armorLevel);
	}
	static {
		addArmorLevel(new ItemStack(Material.LEATHER_BOOTS),new ItemStack(Material.LEATHER_LEGGINGS),new ItemStack(Material.LEATHER_CHESTPLATE),new ItemStack(Material.LEATHER_HELMET));
		addArmorLevel(new ItemStack(Material.CHAINMAIL_BOOTS),new ItemStack(Material.CHAINMAIL_LEGGINGS),new ItemStack(Material.LEATHER_CHESTPLATE),new ItemStack(Material.LEATHER_HELMET));
		ItemStack woodenPickaxe=new ItemStack(Material.WOODEN_PICKAXE);
		woodenPickaxe.addEnchantment(Enchantment.DIG_SPEED,1);
		pickaxeLevels.add(woodenPickaxe);
		ItemStack ironPickaxe=new ItemStack(Material.IRON_PICKAXE);
		ironPickaxe.addEnchantment(Enchantment.DIG_SPEED,2);
		pickaxeLevels.add(ironPickaxe);
		ItemStack goldPickaxe=new ItemStack(Material.GOLDEN_PICKAXE);
		goldPickaxe.addEnchantment(Enchantment.DIG_SPEED,3);
		goldPickaxe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL,2);
		pickaxeLevels.add(goldPickaxe);
		ItemStack diamondPickaxe=new ItemStack(Material.DIAMOND_PICKAXE);
		diamondPickaxe.addEnchantment(Enchantment.DIG_SPEED,3);
		pickaxeLevels.add(diamondPickaxe);
		ItemStack woodenAxe=new ItemStack(Material.WOODEN_AXE);
		woodenAxe.addEnchantment(Enchantment.DIG_SPEED,1);
		axeLevels.add(woodenAxe);
		ItemStack stoneAxe=new ItemStack(Material.STONE_AXE);
		stoneAxe.addEnchantment(Enchantment.DIG_SPEED,1);
		axeLevels.add(stoneAxe);
		ItemStack ironAxe=new ItemStack(Material.IRON_AXE);
		ironAxe.addEnchantment(Enchantment.DIG_SPEED,2);
		axeLevels.add(ironAxe);
		ItemStack diamondAxe=new ItemStack(Material.DIAMOND_AXE);
		diamondAxe.addEnchantment(Enchantment.DIG_SPEED,3);
		axeLevels.add(diamondAxe);
	}
	private Player player;
	private int armorLevel=0;
	private int armorEnchantmentLevel=0;
	private int pickaxeLevel=-1;
	private int axeLevel=-1;
	private boolean hasShears=false;
	private CampInfo camp;
	public PlayerInfo(Player player,CampInfo camp) {
		this.player=player;
		this.camp=camp;
		setArmor();
		Bukkit.getPluginManager().registerEvents(this, BedWars.getPlugin());
	}
	private void setColor(ItemStack item) {
		LeatherArmorMeta meta=((LeatherArmorMeta) item.getItemMeta());
		meta.setColor(camp.getColor());
		item.setItemMeta(meta);
	}
	private void setArmor() {
		ItemStack boots=armorLevels.get(armorLevel).get(0).clone();
		ItemStack leggings=armorLevels.get(armorLevel).get(1).clone();
		ItemStack chestplate=armorLevels.get(armorLevel).get(2).clone();
		setColor(chestplate);
		ItemStack helmet=armorLevels.get(armorLevel).get(3).clone();
		setColor(helmet);
		if(armorEnchantmentLevel>0) {
			boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, armorEnchantmentLevel);
			leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, armorEnchantmentLevel);
			chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, armorEnchantmentLevel);
		}
		helmet.addEnchantment(Enchantment.WATER_WORKER,1);
		player.getInventory().setBoots(boots);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setHelmet(helmet);
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getEntity()==player) event.getDrops().clear();
	}
	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		if(event.getPlayer()!=player) return;
		if(event.isBedSpawn()) {
			PlayerInventory inv=player.getInventory();
			setArmor();
			if(hasShears) inv.addItem(new ItemStack(Material.SHEARS));
			if(pickaxeLevel>-1) pickaxeLevel--;
			if(pickaxeLevel>-1) inv.addItem(pickaxeLevels.get(pickaxeLevel));
			if(axeLevel>-1) axeLevel--;
			if(axeLevel>-1) inv.addItem(axeLevels.get(axeLevel));
		} else {
			player.sendMessage("You died, and your bed is gone. You are out of the game.");
			stop();
		}
	}
	@EventHandler
	private void onPlayerTrade(InventoryClickEvent event) {
		if(event.getWhoClicked()!=player) return;
		if(event.getClickedInventory().getType()!= InventoryType.MERCHANT) return;
		if(event.getCurrentItem().getType()==Material.SHEARS) this.hasShears=true;
		int pickaxeIndex=pickaxeLevels.indexOf(event.getCurrentItem());
		if(pickaxeIndex>0) pickaxeLevel=pickaxeIndex;
		int axeIndex=axeLevels.indexOf(event.getCurrentItem());
		if(axeIndex>0) axeLevel=axeIndex;
		if(event.getCurrentItem().getType()==Material.BOW) {
			for(ItemStack item:event.getWhoClicked().getInventory().getContents()) {
				if(item!=null && item.getType()==Material.BOW) item.setAmount(0);
			}
		}
	}
	private void setArmorLevel(int armorLevel) {
		this.armorLevel=armorLevel;
		setArmor();
	}
	private void setArmorEnchantmentLevel(int armorEnchantmentLevel) {
		this.armorEnchantmentLevel=armorEnchantmentLevel;
		setArmor();
	}
	public void stop() {
		HandlerList.unregisterAll(this);
	}
}
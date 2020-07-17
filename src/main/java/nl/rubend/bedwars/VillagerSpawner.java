package nl.rubend.bedwars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;

public class VillagerSpawner {
	private static ArrayList<MerchantRecipe> recipes=new ArrayList<>();
	private static MerchantRecipe getRecipe(ItemStack from, ItemStack from2,ItemStack to) {
		MerchantRecipe recipe=new MerchantRecipe(to,Integer.MAX_VALUE);
		recipe.addIngredient(from);
		recipe.addIngredient(from2);
		return recipe;
	}
	private static void addRecipe(Material from,int fromCount,ItemStack to) {
		recipes.add(getRecipe(new ItemStack(from,fromCount),new ItemStack(Material.AIR,1),to));
	}
	private static void addRecipe(Material from,int fromCount,Material to,int toCount) {
		recipes.add(getRecipe(new ItemStack(from,fromCount),new ItemStack(Material.AIR,1),new ItemStack(to,toCount)));
	}
	private static void addRecipe(Material from,int fromCount,ItemStack from2,ItemStack to) {
		recipes.add(getRecipe(new ItemStack(from,fromCount),from2,to));
	}
	static {
		addRecipe(Material.IRON_INGOT,4,Material.WHITE_WOOL,16);
		addRecipe(Material.GOLD_INGOT,4,Material.OAK_PLANKS,16);
		addRecipe(Material.IRON_INGOT,12,Material.BLUE_TERRACOTTA,16);
		addRecipe(Material.IRON_INGOT,12,Material.GLASS,4);
		addRecipe(Material.IRON_INGOT,4,Material.LADDER,16);
		addRecipe(Material.EMERALD,4,Material.OBSIDIAN,4);
		ItemStack stick=new ItemStack(Material.STICK,1);
		stick.addUnsafeEnchantment(Enchantment.KNOCKBACK,1);
		addRecipe(Material.GOLD_INGOT,5,stick);
		addRecipe(Material.GOLD_INGOT,12,Material.BOW,1);
		ItemStack bowPower=new ItemStack(Material.BOW,1);
		bowPower.addEnchantment(Enchantment.ARROW_DAMAGE,1);
		addRecipe(Material.GOLD_INGOT,24,bowPower);
		ItemStack bowPowerPunch=bowPower.clone();
		bowPowerPunch.addEnchantment(Enchantment.ARROW_KNOCKBACK,1);
		addRecipe(Material.EMERALD,6,bowPowerPunch);
		addRecipe(Material.GOLD_INGOT,2,Material.ARROW,8);
		addRecipe(Material.IRON_INGOT,20,Material.SHEARS,1);
		addRecipe(Material.GOLD_INGOT,3,Material.WATER_BUCKET,1);
		addRecipe(Material.IRON_INGOT,10,Material.STONE_SWORD,1);
		addRecipe(Material.GOLD_INGOT,7,Material.IRON_SWORD,1);
		addRecipe(Material.EMERALD,4,Material.DIAMOND_SWORD,1);
		addRecipe(Material.IRON_INGOT,10,PlayerInfo.pickaxeLevels.get(0));
		addRecipe(Material.IRON_INGOT,10,PlayerInfo.pickaxeLevels.get(0),PlayerInfo.pickaxeLevels.get(1));
		addRecipe(Material.GOLD_INGOT,3,PlayerInfo.pickaxeLevels.get(1),PlayerInfo.pickaxeLevels.get(2));
		addRecipe(Material.GOLD_INGOT,6,PlayerInfo.pickaxeLevels.get(2),PlayerInfo.pickaxeLevels.get(3));
		addRecipe(Material.IRON_INGOT,10,PlayerInfo.axeLevels.get(0));
		addRecipe(Material.IRON_INGOT,10,PlayerInfo.axeLevels.get(0),PlayerInfo.axeLevels.get(1));
		addRecipe(Material.GOLD_INGOT,3,PlayerInfo.axeLevels.get(1),PlayerInfo.axeLevels.get(2));
		addRecipe(Material.GOLD_INGOT,6,PlayerInfo.axeLevels.get(2),PlayerInfo.axeLevels.get(3));
		addRecipe(Material.GOLD_INGOT,3,Material.GOLDEN_APPLE,1);
		addRecipe(Material.EMERALD,4,Material.ENDER_PEARL,1);
		addRecipe(Material.GOLD_INGOT,3,Material.SPONGE,1);
	}
	private Villager villager;
	public VillagerSpawner(Location location) {
		this.villager=((Villager) location.getWorld().spawnEntity(location,EntityType.VILLAGER));
		villager.setCustomName("PERSONAL UPGRADES");
		villager.setInvulnerable(true);
		villager.setRecipes(recipes);
		villager.setAI(false);
	}
	public Villager getVillager() {return villager;}
}

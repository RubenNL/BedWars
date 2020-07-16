package nl.rubend.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class ItemSpawner implements Runnable {
	private Location location;
	private int taskId;
	public final static int BASE1=1;
	public final static int BASE2=2;
	public final static int BASE3=3;
	public final static int BASE4=4;
	public final static int EMERALD=5;
	public final static int DIAMOND=6;
	int type;
	public ItemSpawner(Location location,int type) {
		this.location=location;
		this.type=type;
		long delay=1000000L;
		if(type==1) delay=20L;
		if(type==2) delay=15L;
		if(type==3) delay=15L;
		if(type==4) delay=8L;
		if(type==5) delay=15*20L;
		if(type==6) delay=15*20L;
		this.taskId=Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.getPlugin(), this, 0L, delay);
	}
	private ItemStack getRandomItem(boolean includeEmerald) {
		Material material=Material.IRON_INGOT;
		int random= (int) (Math.random()*3);
		if(random==1) material=Material.GOLD_INGOT;
		if(random==2 && includeEmerald) material=Material.EMERALD;
		return new ItemStack(material,1);
	}
	@Override
	public void run() {
		ItemStack itemStack=new ItemStack(Material.AIR);
		if(type<5) itemStack=getRandomItem(type>2);
		if(type==5) itemStack=new ItemStack(Material.EMERALD,1);
		if(type==6) itemStack=new ItemStack(Material.DIAMOND,1);
		for (Entity entity : location.getNearbyEntitiesByType(Item.class, 2)) entity.remove();
		location.getWorld().dropItem(location,itemStack);
	}
	public void stop() {
		Bukkit.getScheduler().cancelTask(this.taskId);
	}
}
package de.melays.bwunlimited.tools;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.Main;
import net.md_5.bungee.api.ChatColor;

public class MarkerTool implements Listener {
	
	Main main;
	
	public MarkerTool(Main main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	HashMap<UUID , Location> leftclick = new HashMap<UUID , Location>();
	HashMap<UUID , Location> rightclick = new HashMap<UUID , Location>();
	
	public void givePlayer(Player p) {
		ItemStack stack = new ItemStack(Material.GOLD_AXE);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Selection Tool");
		stack.setItemMeta(meta);
		p.getInventory().addItem(stack);
	}
	
	public boolean isReady(Player p) {
		if (!leftclick.containsKey(p.getUniqueId())) return false;
		if (!rightclick.containsKey(p.getUniqueId())) return false;
		String world1 = leftclick.get(p.getUniqueId()).getWorld().getName();
		if (!rightclick.get(p.getUniqueId()).getWorld().getName().equals(world1))return false;
		return true;
	}
	
	public Location get1 (Player p) {
		return leftclick.get(p.getUniqueId());
	}
	
	public Location get2 (Player p) {
		return rightclick.get(p.getUniqueId());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK ||
				e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR)) {
			return;
		}
		if (e.getItem() == null) {
			return;
		}
		if (e.getItem().getType() != Material.GOLD_AXE) {
			return;
		}
		if (!e.getItem().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Selection Tool")) {
			return;
		}
		if (!p.hasPermission("bwunlimited.setup")) {
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR) {
			p.sendMessage(main.prefix + "Set the 2nd location to your current position");
			this.rightclick.put(p.getUniqueId(), p.getLocation());
		}
		else if (e.getAction() == Action.LEFT_CLICK_AIR) {
			p.sendMessage(main.prefix + "Set the 1st location to your current position");
			this.leftclick.put(p.getUniqueId(), p.getLocation());
		}
		else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			p.sendMessage(main.prefix + "Set the 2nd location to to the clicked block");
			this.rightclick.put(p.getUniqueId(), e.getClickedBlock().getLocation());
		}
		else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			p.sendMessage(main.prefix + "Set the 1st location to to the clicked block");
			this.leftclick.put(p.getUniqueId(), e.getClickedBlock().getLocation());
		}
		e.setCancelled(true);
	}
	
}

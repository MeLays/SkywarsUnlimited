package de.melays.bwunlimited.game.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.Main;

public class RunningGames {
	
	Main main;
	
	public RunningGames (Main main) {
		this.main = main;
	}
	
	public HashMap<Integer , String> slots = new HashMap<Integer , String>();
	public HashMap<Player , ArenaList> player_list = new HashMap<Player , ArenaList>();
	
	@SuppressWarnings("deprecation")
	public void openOverview(Player p) {
		Inventory inv = Bukkit.createInventory(null, main.getSettingsManager().getFile().getInt("lobby.inventory.running_games.size"), main.c(main.getSettingsManager().getFile().getString("lobby.inventory.running_games.title")));
		for (String s : main.getSettingsManager().getFile().getStringList("lobby.inventory.running_games.show")) {
			ConfigurationSection section = main.getSettingsManager().getFile().getConfigurationSection("lobby.inventory.running_games.style." + s);
			if (section != null) {
				//LOAD MATERIAL
				Material m = Material.PAPER;
				byte data = 0;
				String material = section.getString("item");
				if (material != null) {
					if (material.contains(":")) {
						String byte_raw = material.split(":")[1];
						material = material.split(":")[0];
						try {
							data = Byte.parseByte(byte_raw);
						}catch (Exception ex) {
							
						}
					}
					m = Material.getMaterial(material);
					if (m == null) {
						try {
							m = Material.getMaterial(Integer.parseInt(material));
						} catch (NumberFormatException e) {

						}
						if (m == null) {

						}
					}
				}
				//LOAD DISPLAYNAME
				String displayname = section.getString("display");
				if (displayname == null) displayname = s;
				//LOAD LORE
				List<String> lore = section.getStringList("lore");
				if (lore == null) lore = new ArrayList<String>();
				//LOAD SLOT
				int slot = section.getInt("slot");
				slots.put(slot, s);
				ItemStack stack = new ItemStack(m , 1 , data);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(main.c(displayname));
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
				meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
				meta.setLore(lore);
				stack.setItemMeta(meta);
				
				inv.setItem(slot, stack);
			}
		}
		p.openInventory(inv);
	}
	
}

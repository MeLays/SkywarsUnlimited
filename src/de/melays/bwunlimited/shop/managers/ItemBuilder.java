package de.melays.bwunlimited.shop.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

/**
 * Creates all itemstacks that will be needed by the shop and keeps them stored in a hashmap.
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public class ItemBuilder {
	private static Map<String, ItemStack> items = new HashMap<>();	// stores all itemstacks
	/**
	 * @return	A preloaded itemstack by name.
	 * @param name	The name.
	 */
	public static ItemStack getStack(String name) {
		return items.get(name.trim().toLowerCase());
	}
	/**
	 * Creates sections out of a configuration file to load itemstacks.
	 * @param plugin	The originally plugin (JavaPlugin).
	 */
	public static void loadItems(Plugin plugin) {
		File file = new File(plugin.getDataFolder() +File.separator+ "shop" +File.separator+ "Items.yml");
		if (!file.exists()) {
			plugin.saveResource("shop"+File.separator+"Items.yml", true);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)) {
			items.put(key.toLowerCase(), loadStack(config.getConfigurationSection(key)));
		}
	}
	/**
	 * Colores a single string.
	 * @param s	The string.
	 */
	private static String color(String s) {
		return s == null ? null : ChatColor.translateAlternateColorCodes('&', s);
	}
	/**
	 * Colores a stringlist.
	 * @param list The list.
	 */
	private static List<String> color(List<String> list) {
		List<String> coloredList = new ArrayList<>();
		if (list != null) {
			list.forEach(s -> coloredList.add(color(s)));
		}
		return coloredList;
	}
	/**
	 * Returns leather armor color from a short.
	 * @param data	The short.
	 */
	private static Color getColor(short data) {
		Color color;
		switch (data) {
			case 0: color = Color.WHITE;
					break;
			case 1: color = Color.ORANGE;
					break;
			case 2: color = Color.fromRGB(204, 0, 204);
					break;
			case 3: color = Color.TEAL;
					break;
			case 4: color = Color.YELLOW;
					break;
			case 5: color = Color.LIME;
					break;
			case 6: color = Color.FUCHSIA;
					break;
			case 7: color = Color.GRAY;
					break;
			case 8: color = Color.SILVER;
					break;
			case 9: color = Color.AQUA;
					break;
			case 10: color = Color.PURPLE;
					break;
			case 11: color = Color.BLUE;
					break;
			// 12 is brown so armor won't be colored
			case 13: color = Color.OLIVE;
					break;
			case 14: color = Color.RED;
					break;
			case 15: color = Color.BLACK;
					break;
			default: color = null;
					break;
		}
		return color;
	}
	/**
	 * Loads a single ItemStack from configuration section.
	 * @param config	The section.
	 * @throws IllegalArgumentException	Gets thrown in case of non valid config entrys.
	 */
	private static ItemStack loadStack(ConfigurationSection config) throws IllegalArgumentException {
		String m = config.getString("Material").toUpperCase();
		if (m == null || Material.getMaterial(m) == null) {
			throw new IllegalArgumentException("[BedwarsShop] The value "+m+" is not a valid material for item "+config.getName());
		}
		Material mat = Material.valueOf(m);
		int amount = config.getInt("Amount");
		if (amount < 1) {
			amount = 1;
		}
		short data = (short) config.getInt("Data");
		if (data < 0) {
			data = 0;
		}
		ItemStack stack;
		if (m.startsWith("LEATHER_")) {
			stack = new ItemStack(mat, amount);
			Color color = getColor(data);
			LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
			meta.setColor(color);
			stack.setItemMeta(meta);
		} else {
			stack = new ItemStack(mat, amount, data);
		}
		List<String> enchs = config.getStringList("Enchantments");
		if (enchs != null) {
			for (String e : enchs) {
				String[] entry = e.replaceAll("[;]", ",").split("[,]");
				if (entry.length != 2) {
					throw new IllegalArgumentException("[BedwarsShop] The value "+e+" is not a valid enchantment for item "+config.getName()+". It should be 'enchantment, level', e.g. 'KNOCKBACK, 1'");
				}
				Enchantment ench = Enchantment.getByName(entry[0].trim().toUpperCase());
				if (ench != null) {
					try {
						stack.addUnsafeEnchantment(ench, Integer.parseInt(entry[1].trim()));
					} catch(Exception ex) {
						throw new IllegalArgumentException("[BedwarsShop] The value "+entry[1].trim()+" is not a valid enchantment level for item "+config.getName());
					}
				} else {
					throw new IllegalArgumentException("[BedwarsShop] The value "+entry[0].trim().toUpperCase()+" is not a valid enchantment for item "+config.getName());
				}
			}
		}
		ItemMeta meta = stack.getItemMeta();
		String name = color(config.getString("Name"));
		if (name != null) {
			meta.setDisplayName(name);
		}
		if (meta != null) {		// nullcheck because of material air (has no item meta)
			List<String> lore = (color(config.getStringList("Description")));
			String price = color(config.getString("Price"));
			if (price != null) {
				lore.add("");
				lore.add(price);
			}
			meta.setLore(lore);
			
			List<String> flags = config.getStringList("ItemFlags");
			if (flags != null) {
				for (String f : flags) {
					ItemFlag flag = ItemFlag.valueOf(f.toUpperCase());
					if (flag != null) {
						meta.addItemFlags(flag);
					} else {
						throw new IllegalArgumentException("[BedwarsShop] The value "+f.toUpperCase()+" is not a valid ItemFlag for item "+config.getName());
					}
				}
			}
			stack.setItemMeta(meta);
		}
		return stack;
	}
}

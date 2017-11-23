package de.melays.bwunlimited.shop.managers;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.shop.BedwarsShop;
import de.melays.bwunlimited.shop.utils.Shop;

/**
 * Loads and stores all shops and possibilities to get them e.g. name or opener (itemstack).
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public class ShopManager {
	private static Map<String, Shop> shopNames = new HashMap<>();
	private static Map<UUID, String> shopUUIDs = new HashMap<>();
	private static Map<ItemStack, String> shopOpeners = new HashMap<>();
	private static String defaultShop;
	private static Plugin main;
	/**
	 * Creates sections out of a configuration file to load shops.
	 * @param plugin	The originally plugin (JavaPlugin).
	 */
	public static void loadShops(Plugin plugin) {
		defaultShop = BedwarsShop.getConfig().getString("DefaultShop");
		File file = new File(plugin.getDataFolder() +File.separator+ "shop" +File.separator+ "Shops.yml");
		if (!file.exists()) {
			plugin.saveResource("shop"+File.separator+"Shops.yml", true);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		main = plugin;
		for (String key : config.getKeys(false)) {
			shopNames.put(key, loadShop(config.getConfigurationSection(key)));
		}
	}
	/**
	 * Transforms a string (row.slot) to inventory slot (integer).	
	 * @param slot	The slot location.
	 */
	private static int getSlot(String slot) {
		String[] splitted = slot.trim().split("[.]");
		return (Integer.parseInt(splitted[0].trim()) - 1) * 9 + Integer.parseInt(splitted[1].trim()) - 1;
	}
	/**
	 * Sets the default shop.	
	 * @param name	The name of the shop.
	 */
	public static String setDefaultShop(String name) {
		return defaultShop = name;
	}
	/**
	 * @return The default shop.
	 */
	public static Shop getDefaultShop() {
		return getShop(defaultShop);
	}
	/**
	 * Sets a specific shop to a player.
	 * @param player	The player.
	 * @param shopname	The name of the shop.
	 */
	public static String setShop(Player player, String shopname) {
		return shopUUIDs.put(player.getUniqueId(), shopname);
	}
	/**
	 * Returns a shop by name.
	 * @param name	The name of the shop.
	 * @return	The shop.
	 */
	public static Shop getShop(String name) {
		return name == null ? null : shopNames.get(name);
	}
	/**
	 * Return a shop by itemstack.
	 * @param opener	The itemstack which opens the shop.
	 * @return	The shop.
	 */
	public static Shop getShop(ItemStack opener) {
		return opener == null ? null : getShop(shopOpeners.get(opener));
	}
	/**
	 * Returns the specific shop of a player or the default shop if he has no own.
	 * @param player	The player.
	 * @return	The shop.
	 */
	public static Shop getShop(Player player) {
		return player == null ? null : getShop(player.getUniqueId());
	}
	/**
	 * Returns the specific shop of a player from UUID or the default shop if he has no own.
	 * @param uuid	The players UUID.
	 * @return The shop.
	 */
	public static Shop getShop(UUID uuid) {
		return uuid == null ? null : getShop(getShop(shopUUIDs.get(uuid)) == null ? defaultShop : shopUUIDs.get(uuid));
	}
	/**
	 * @return	All loaded shops.
	 */
	public static Collection<Shop> allShops() {
		return shopNames.values();
	}
	/**
	 * Loads a single shop from configuration section.
	 * @param config	The section.
	 * @throws IllegalArgumentException	Gets thrown in case of non valid config entrys.
	 */
	private static Shop loadShop(ConfigurationSection config) throws IllegalArgumentException {
		String name = config.getName();
		boolean merchant = config.getBoolean("Merchant");
		Inventory inv = Bukkit.createInventory(null, config.getInt("Rows") * 9, BedwarsShop.getShopName());
		for (String s : config.getStringList("StartInv")) {
			String[] entry = s.replaceAll("[;]", ",").split("[,]");
			
			inv.setItem(getSlot(entry[0]), ItemBuilder.getStack(entry[1].trim()));
		}
		HashMap<ItemStack, Object> categories = new HashMap<>();
		for (String key : config.getConfigurationSection("Categories").getKeys(false)) {
			String open = config.getString("Categories."+key+".Opener");
			if (open == null || ItemBuilder.getStack(open) == null) {
				throw new IllegalArgumentException("[BedwarsShop] The itemstack "+open+" in category "+key+" of shop "+name+" couldn't be resolved");
			}
			ItemStack opener = ItemBuilder.getStack(open);
			Object category;
			if (merchant) {
				throw new IllegalArgumentException("[BedwarsShop] Merchant is not supported yet");
			} else {
				Map<Integer, ItemStack> map = new HashMap<>();
				for (String s : config.getStringList("Categories."+key+".Items")) {
					String[] entry = s.replaceAll("[;]", ",").split("[,]");
					ItemStack stack = ItemBuilder.getStack(entry[1].trim());
					if (stack != null) {
						map.put(getSlot(entry[0]), stack);
					} else {
						throw new IllegalArgumentException("[BedwarsShop] The itemstack "+entry[1].trim()+" in category "+key+" of shop "+name+" couldn't be resolved");
					}
				}
				category = map;
			}
			categories.put(opener, category);
		}
		for (int i = 0 ; i < inv.getSize() ; i++){	
			if (inv.getItem(i) == null){
				inv.setItem(i, ((Main)main).getItemManager().getItem("spacer"));
			}
			if (inv.getItem(i).getType().equals(Material.AIR)){
				inv.setItem(i, ((Main)main).getItemManager().getItem("spacer"));
			}
		}
		return new Shop(name, merchant, inv, categories);
	}
}

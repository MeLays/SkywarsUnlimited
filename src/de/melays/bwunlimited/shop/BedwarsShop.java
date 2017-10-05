package de.melays.bwunlimited.shop;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.melays.bwunlimited.shop.listeners.*;
import de.melays.bwunlimited.shop.managers.*;
import de.melays.bwunlimited.shop.utils.Shop;
import net.md_5.bungee.api.ChatColor;

/**
 * Hub of BedwarsShop plugin. Should be used as API.
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public class BedwarsShop {
	private static Plugin plugin;
	private static FileConfiguration config;
	private static String shopName;
	/**
	 * Loads the whole plugin.
	 * @param plugin	The originally plugin.
	 * @param useOpenShopListener	Used to decide if plugin registers own listeners to open a shop.
	 */
	public static void load(Plugin plugin, boolean useOpenShopListener) {
		BedwarsShop.plugin = plugin;
		File file = new File(plugin.getDataFolder() + File.separator + "shop" + File.separator + "config.yml");
		if (!file.exists()) {
			System.out.println("creating file");
			plugin.saveResource("shop"+File.separator+"config.yml", true);
		}
		config = YamlConfiguration.loadConfiguration(file);
		shopName = ChatColor.translateAlternateColorCodes('&', config.getString("ShopName"));
		
		ItemBuilder.loadItems(plugin);
		ShopManager.loadShops(plugin);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
		if (useOpenShopListener) {
			Bukkit.getPluginManager().registerEvents(new OpenShopListener(), plugin);
		}
	}
	/**
	 * @return	The originally plugin.
	 */
	public static Plugin getPlugin() {
		return plugin;
	}
	/**
	 * @return	The standart configuration.
	 */
	public static FileConfiguration getConfig() {
		return config;
	}
	/**
	 * @return	The inventory name shared by all Shops.
	 */
	public static String getShopName() {
		return shopName;
	}
	/**
	 * Sets a specific shop to a player.
	 * @param player	The player.
	 * @param shopname	The name of the shop.
	 */
	public static String setShop(Player player, String shopname) {
		return ShopManager.setShop(player, shopname);
	}
	/**
	 * @return	The default shop.
	 */
	public static Shop getShop() {
		return ShopManager.getDefaultShop();
	}
	/**
	 * Returns a Shop from itemstack.
	 * @param opener	The itemstack.
	 */
	public static Shop getShop(ItemStack opener) {
		return ShopManager.getShop(opener);
	}
	/**
	 * Returns a shop by name.
	 * @param name	The name.
	 */
	public static Shop getShop(String name) {
		return ShopManager.getShop(name);
	}
	/**
	 * Opens the default shop to a player.
	 * @param player	The player.
	 */
	public static void openShop(Player player) {
		openShop(player, getShop());
	}
	/**
	 * Opens a shop to a player.
	 * @param player	The player.
	 * @param name	The name of the shop.
	 */
	public static void openShop(Player player, String name) {
		openShop(player, getShop(name));
	}
	/**
	 * Opens a shop to a player.
	 * @param player	The player.
	 * @param opener	The opener of the shop.
	 */
	public static void openShop(Player player, ItemStack opener) {
		openShop(player, getShop(opener));
	}
	/**
	 * Opens a shop to a player.
	 * @param player	The player.
	 * @param shop	The shop.
	 */
	public static void openShop(Player player, Shop shop) {
		Inventory inv = shop.getInventory();
		if (!shop.isMerchant()) {
			Inventory create = Bukkit.createInventory(null, inv.getSize(), inv.getName());
			for (int slot = 0; slot < inv.getSize(); slot++) {
				create.setItem(slot, inv.getItem(slot));
			}
			inv = create;
		}
		player.openInventory(inv);
	}
}

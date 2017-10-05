package de.melays.bwunlimited.shop.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import de.melays.bwunlimited.shop.managers.ShopManager;

/**
 * Stores information about shop like name, inventorys etc.
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public class Shop {
	private String name;	// NOT the inventory name!
	boolean merchant;
	private Inventory startInv;
	HashMap<ItemStack, Object> categories;
	/**
	 * Creates a new shop from scratch.
	 * @param name	The name of the shop.
	 * @param merchant
	 * @param startInv
	 * @param categories
	 */
	public Shop(String name, boolean merchant, Inventory startInv, HashMap<ItemStack, Object> categories) {
		this.name = name;
		this.merchant = merchant;
		this.startInv = startInv;
		this.categories = categories;
	}
	/**
	 * Returns shop by name.
	 * @param name	The name.
	 * @return	Shop if could be found, otherwise null.
	 */
	public static Shop valueOf(String name) {
		return ShopManager.getShop(name);
	}
	/**
	 * Returns shop by opener (itemstack).
	 * @param name	The itemstack.
	 * @return	Shop if could be found, otherwise null.
	 */
	public static Shop fromOpener(ItemStack opener) {
		return ShopManager.getShop(opener);
	}
	/**
	 * Returns all shops.
	 */
	public static Collection<Shop> values() {
		return ShopManager.allShops();
	}
	/**
	 * @return	The name of the shop (not inventory name!).
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return	Tells you whether shop is merchant or modern.
	 */
	public boolean isMerchant() {
		return merchant;
	}
	/**
	 * @return	The start inventory from which you can open all categories.
	 */
	public Inventory getInventory() {
		return startInv;
	}
	/**
	 * Returns the category by opener.
	 * @param stack	the opener.
	 */
	public Object getCategory(ItemStack stack) {
		return categories.get(stack);
	}
	/**
	 * Opens a category of the shop to a player.
	 * @param player	The player.
	 * @param inv	The current inventory.
	 * @param stack	The opener for the category.
	 */
	@SuppressWarnings("unchecked")
	public void openCategory(Player player, Inventory inv, ItemStack stack) {
		Object category = categories.get(stack);
		if (merchant) {
			player.openInventory((MerchantInventory) category);
		} else {
			Map<Integer, ItemStack> map = (Map<Integer, ItemStack>) category;
			for (int slot : map.keySet()) {
				inv.setItem(slot, map.get(slot));	
			}
		}
	}
}

package de.melays.bwunlimited.shop.listeners;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import de.melays.bwunlimited.shop.BedwarsShop;
import de.melays.bwunlimited.shop.managers.ItemBuilder;
import de.melays.bwunlimited.shop.managers.ShopManager;
import de.melays.bwunlimited.shop.utils.Effect;
import de.melays.bwunlimited.shop.utils.Shop;
import net.md_5.bungee.api.ChatColor;

/**
 * Listener for all actions happening in inventory.
 * Responsible for buying items, protecting shop etc.
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public class InventoryListener implements Listener {
	/**
	 * Cancels dragging of items into shop.
	 * @param event	InventoryDragEvent
	 */
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		Inventory inv = event.getInventory();
		if (event.getWhoClicked() instanceof Player && inv != null && inv.getName().equals(BedwarsShop.getShopName())) {
			for (int slot : event.getNewItems().keySet()) {
				if (slot < inv.getSize()) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	public static void removeInventoryItems(PlayerInventory inv, Material type, int amount) {
		for (ItemStack is : inv.getContents()) {
			if (is != null && is.getType() == type) {
				int newamount = is.getAmount() - amount;
				if (newamount > 0) {
					is.setAmount(newamount);
	                break;
	            } else {
	            	inv.remove(is);
	            	amount = -newamount;
	            	if (amount == 0) break;
	            }
	        }
	    }
	}
	  
	public int getAmount(Inventory inv , Material m){  
		int count = 0;  
		for (ItemStack s : inv.getContents()){
			if (s != null){
				if (s.getType() == m){
					count += s.getAmount();
				}
			}
		}
		return count;
	}
	
	/**
	 * Cancels clicking in shop-inventory.
	 * Responsible for buying items.
	 * @param event	InventoryClickEvent
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inv = event.getClickedInventory();
		ItemStack clicked = event.getCurrentItem();
		if (!(event.getWhoClicked() instanceof Player) || inv == null || clicked == null || clicked.getType() == Material.AIR) {
			return;
		}
		if (!inv.getName().equals(BedwarsShop.getShopName())) {
			if (event.isShiftClick() && event.getInventory() != null && event.getInventory().getName().equals(BedwarsShop.getShopName())) {
				event.setCancelled(true);
			}
			return;
		}
		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
		if (ShopManager.getShop(clicked) != null) {
			BedwarsShop.openShop(player, clicked);
			Effect.SWITCH_SHOP.play(player);
			return;
		}
		Shop shop = ShopManager.getShop(player);
		if (shop.getCategory(clicked) != null) {
			shop.openCategory(player, inv, clicked);
			Effect.SWITCH_CATEGORY.play(player);
			return;
		}
		// Checking if item is buyable & price of item
		PlayerInventory pInv = player.getInventory();
		if (pInv.firstEmpty() == -1) {
			Effect.FULL_INVENTORY.play(player);
			return;
		}
		List<String> lore = clicked.getItemMeta().getLore();
		if (lore == null || lore.size() < 1) {
			Effect.CLICK_PLACEHOLDER_ITEM.play(player);
			return;
		}
		String[] array = lore.get(lore.size() - 1).split("\\s+");
		if (array.length < 2) {
			Effect.CLICK_PLACEHOLDER_ITEM.play(player);
			return;
		}
		ItemStack currency = ItemBuilder.getStack(array[1]);
		if (currency == null) {
			Effect.CLICK_PLACEHOLDER_ITEM.play(player);
			return;
		}
		for (ChatColor cc : ChatColor.values()) {
			array[0] = array[0].replaceAll(cc.toString(), "");
		}
		int price;
		try {
			price = Integer.parseInt(array[0].trim());
		} catch(Exception ex) {
			Effect.CLICK_PLACEHOLDER_ITEM.play(player);
			return;
		}
		int amount = clicked.getAmount();
		boolean shift = false;
		int pricesave = price;
		  
		if (event.isShiftClick()){
			price = price * (clicked.getMaxStackSize() / clicked.getAmount());
			amount = clicked.getMaxStackSize();
			shift = true;
		}
		  
		int specifyslot = -1;
		  
		if (event.getClick() == ClickType.NUMBER_KEY){
			specifyslot = event.getHotbarButton();
		}
		  
		Material remove = currency.getType();
		
		if (!(getAmount(event.getWhoClicked().getInventory() , remove) >= price)){  
			if (shift){
				amount = (getAmount(event.getWhoClicked().getInventory() , remove) / pricesave) * clicked.getAmount();
				price =  pricesave * (amount / clicked.getAmount());
				if (amount == 0){
					price = 0;
				}
			}
			else{
				Effect.TOO_LESS_MONEY.play(player);
				return;
			}
		}
		  
		clicked = clicked.clone();
		if (amount != 0){
			Effect.BUY_ITEM.play(player);
			removeInventoryItems(event.getWhoClicked().getInventory() , remove , price);
		}
		else{
			Effect.TOO_LESS_MONEY.play(player);
			return;
		}
		clicked = new ItemStack(clicked);
		clicked.setAmount(amount);
		IntStream.of(1, 2).forEach(i -> lore.remove(lore.size() - 1));
		ItemMeta meta = clicked.getItemMeta();
		meta.setLore(lore);
		clicked.setItemMeta(meta);
		if (specifyslot == -1){
			event.getWhoClicked().getInventory().addItem(clicked);
		}
		else{
			ItemStack temp = null;
			try{
				temp = event.getWhoClicked().getInventory().getItem(specifyslot).clone();
			}
			catch (Exception ex){
		  
			}
			event.getWhoClicked().getInventory().setItem(specifyslot, clicked);
			try{
				event.getWhoClicked().getInventory().addItem(temp);
			}
			catch (Exception ex){
				  
			}
		  }
	}
}

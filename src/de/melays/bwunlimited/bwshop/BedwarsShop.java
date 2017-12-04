package de.melays.bwunlimited.bwshop;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.Utf8YamlConfiguration;
import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.game.arenas.Arena;
import net.md_5.bungee.api.ChatColor;

public class BedwarsShop {

	Main main;
	public BedwarsShop (Main main) {
		this.main = main;
		this.getItemFile().options().copyDefaults(true);
		this.saveFile();
		load();
	}
	
	ArrayList<ShopCategory> categories = new ArrayList<ShopCategory>();
	
	HashMap<Player,String> current = new HashMap<Player,String>();
	
	public void load() {
		try {
			for (String s : this.getItemFile().getConfigurationSection("categories").getKeys(false)) {
				categories.add(new ShopCategory(s , this.getItemFile().getConfigurationSection("categories").getConfigurationSection(s)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ShopCategory getCategory (String name) {
		for (ShopCategory s : this.categories) {
			if (s.name.equals(name)) {
				return s;
			}
		}
		return null;
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
	
	public int getFreeSlots (Inventory i , ItemStack stack) {
		int amount = 0;
		for (ItemStack s : i.getContents()){
			if (s != null){
				if (s.isSimilar(stack)) {
					amount += (stack.getMaxStackSize() - s.getAmount());
				}
			}
			else {
				amount += stack.getMaxStackSize();
			}
		} 
		return amount;
	}
	
	public void shopClick(Player p , int slot , boolean shift , boolean num , int number) {
		if (slot < 9) {
			if (categories.size()-1 < slot) return;
			Arena a = main.getArenaManager().searchPlayer(p);
			if (a == null) return;
			if (!this.getCategory(categories.get(slot).name).isHidden(a)) {
				openShop(p , categories.get(slot).name);
				playSound(p , "category");
			}
			return;
		}
		Arena a = main.getArenaManager().searchPlayer(p);
		if (a == null) return;
		ShopCategory cat = getCategory(current.get(p));
		ShopItem i = cat.items[slot-9];
		if (i != null && !i.isHidden(a)) {
			if (getFreeSlots(p.getInventory() , i.getStack()) == 0) {
				//INV FULL
				playSound(p , "inv_full");
				return;
			}
			
			if (!shift && ! num) {
				for (Material m : i.price.keySet()) {
					if (!(getAmount(p.getInventory() , m) >= i.price.get(m))) {
						//CANT AFFORD
						playSound(p , "cant_afford");
						return;
					}
				}
				
				if (getFreeSlots(p.getInventory() , i.getStack()) < i.getStack().getAmount()) {
					//INV FULL
					playSound(p , "inv_full");
					return;
				}
				
				for (Material m : i.price.keySet()) {
					removeInventoryItems(p.getInventory() , m , i.price.get(m));
				}
				
				i.addToPlayer(p);
				//BOUGHT
				playSound(p , "buy");
				
			}
			else if (!shift && num) {
				
				for (Material m : i.price.keySet()) {
					if (!(getAmount(p.getInventory() , m) >= i.price.get(m))) {
						//CANT AFFORD
						playSound(p , "cant_afford");
						return;
					}
				}
				
				if (getFreeSlots(p.getInventory() , i.getStack()) < i.getStack().getAmount()) {
					//INV FULL
					playSound(p , "inv_full");
					return;
				}
				
				for (Material m : i.price.keySet()) {
					removeInventoryItems(p.getInventory() , m , i.price.get(m));
				}
				
				ItemStack giveback = null;
				if (p.getInventory().getItem(number) != null) {
					giveback = p.getInventory().getItem(number).clone();
				}
				
				i.addToPlayer(p , number);
				//BOUGHT
				playSound(p , "buy");
				if (giveback != null && giveback.isSimilar(i.getStack())) {
					if (i.getStack().getAmount() + giveback.getAmount() <= i.getStack().getMaxStackSize()) {
						p.getInventory().getItem(number).setAmount(p.getInventory().getItem(number).getAmount() + giveback.getAmount());
					}
					else {
						int add = i.getStack().getAmount() + giveback.getAmount() - i.getStack().getMaxStackSize();
						p.getInventory().getItem(number).setAmount(p.getInventory().getItem(number).getMaxStackSize());
						ItemStack give = i.getStack();
						give.setAmount(add);
						p.getInventory().addItem(give);
					}
				}
				else if (giveback != null)
					p.getInventory().addItem(giveback);	
			}
			else if (shift && !num) {
				int amount_buying = (i.getStack().getMaxStackSize() - (i.getStack().getMaxStackSize() % i.getStack().getAmount())) / i.getStack().getAmount();
				for (Material m : i.price.keySet()) {
					if (!(getAmount(p.getInventory() , m) >= i.price.get(m) * amount_buying)) {
						amount_buying = (getAmount(p.getInventory() , m) / i.price.get(m));
					}
				}
				if (amount_buying == 0) {
					//CANT AFFORD
					playSound(p , "cant_afford");
					return;
				}
				
				if (getFreeSlots(p.getInventory() , i.getStack()) < i.getStack().getAmount() * amount_buying) {
					//INV FULL
					playSound(p , "inv_full");
					return;
				}
				
				for (Material m : i.price.keySet()) {
					removeInventoryItems(p.getInventory() , m , i.price.get(m) * amount_buying);
				}
				
				for (int z = 0 ; z < amount_buying ; z++) {
					i.addToPlayer(p);
				}
				//BOUGHT
				playSound(p , "buy");
			}
		}
		else {
			playSound(p , "gui_click");
		}
	}
	
	public void openShop(Player p , String cat) {
		Arena a = main.getArenaManager().searchPlayer(p);
		if (a == null) return;
		Inventory inv = Bukkit.createInventory(null, this.getItemFile().getInt("size"), main.c(this.getItemFile().getString("title")));
		int slot = 0;
		for (ShopCategory s : categories) {
			if(!s.isHidden(a)){
				inv.addItem(s.stack);
			}
			else {
				inv.setItem(slot , main.getItemManager().getItem("spacer"));
			}
			slot ++;
		}
		ShopCategory shop = getCategory(cat);
		for (int i = 0 ; i < shop.items.length ; i ++) {
			if (shop.items[i] == null) {
				inv.setItem(9 + i , main.getItemManager().getItem("spacer"));
			}
			else if(!shop.items[i].isHidden(a)){
				inv.setItem(9 + i , shop.items[i].stack);
			}
			else {
				inv.setItem(9 + i , main.getItemManager().getItem("spacer"));
			}
		}
		current.put(p, cat);
		p.openInventory(inv);
	}

	public void openShop(Player p) {
		openShop(p , this.getItemFile().getString("default"));
	}
	
	public void playSound(Player p , String sound) {
		p.playSound(p.getLocation(), SoundDebugger.getSound(this.getItemFile().getString("sounds."+sound+".legacy"), this.getItemFile().getString("sounds."+sound+".new")), 1, 1);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack loadItemStack(ConfigurationSection data) {
		ItemStack r = new ItemStack(Material.PAPER , 0);
		try {
			r.setType(Material.getMaterial(data.getString("type").toUpperCase()));
		} catch (Exception e) {
			
		}
		try {
			r.getData().setData((byte) data.getInt("data"));
		} catch (Exception e) {
			
		}
		try {
			r.setAmount(data.getInt("amount"));
		} catch (Exception e) {
			
		}
		try {
			r.setDurability((short) data.getInt("durability"));
		} catch (Exception e) {
			
		}
		try {
			for (String s : data.getConfigurationSection("enchantments").getKeys(false)) {
				r.addUnsafeEnchantment(Enchantment.getByName(s.toUpperCase()), data.getInt("enchantments."+s));
			}
		} catch (Exception e) {
			
		}
		ItemMeta meta = r.getItemMeta();
		try {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', data.getString("displayname")));
		} catch (Exception e) {
			
		}
		try {
			List<String> lore = data.getStringList("lore");
			ArrayList<String> set = new ArrayList<String>();
			for (String s : lore) {
				set.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			meta.setLore(set);
		} catch (Exception e) {
			
		}
		r.setItemMeta(meta);
		try {
			if (r.getType() == Material.POTION) {
				PotionMeta pmeta = (PotionMeta) r.getItemMeta();
				for (String s : data.getConfigurationSection("potion").getKeys(false)) {
					pmeta.setMainEffect(PotionEffectType.getByName(s));
					pmeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(s.toUpperCase()) , data.getInt("potion."+s+".time") ,  data.getInt("potion."+s+".level")), true);
				}
				r.setItemMeta(pmeta);
			}
		} catch (Exception e) {

		}
		return r;
	}
	
	//Team File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "shop.yml";
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), filenname);
	    }
	    if (!configurationFile.exists()) {
	    	main.saveResource(filenname, true);
	    }
	    configuration = new Utf8YamlConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource(filenname);
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        configuration.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getItemFile() {
	    if (configuration == null) {
	    	reloadFile();
	    }
	    return configuration;
	}
	
	public void saveFile() {
	    if (configuration == null || configurationFile == null) {
	    return;
	    }
	    try {
	        configuration.save(configurationFile);
	    } catch (IOException ex) {
	    }
	}
	
}

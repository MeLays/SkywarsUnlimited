package de.melays.bwunlimited.game.lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.map_manager.Cluster;

public class ArenaSelector {
	
	Main main;
	
	public ArenaSelector(Main main) {
		this.main = main;
	}
	
	public HashMap<Player , Cluster> selected = new HashMap<Player , Cluster>();
	
	public HashMap<Player , String> inv = new HashMap<Player , String>();
	public HashMap<Player , Integer> page = new HashMap<Player , Integer>();
	public HashMap<Player , Integer> size = new HashMap<Player , Integer>();
	
	public void setupPlayer (Player p) {
		if (!selected.containsKey(p)) {
			setRandomMap(p);
		}
		if (!main.getLobbyManager().isSuitable(main.getGroupManager().getGroup(p), selected.get(p))) {
			setRandomMap(p);
		}
	}
	
	public void setRandomMap(Player p) {
		LinkedHashMap<Integer, ArrayList<Cluster>> s = main.getLobbyManager().getSuitableArenas(main.getGroupManager().getGroup(p));
		if (s.size() == 0) return;
		ArrayList<Integer> keys = new ArrayList<Integer>(s.keySet());
		Collections.sort(keys);
		ArrayList<Cluster> possible = s.get(keys.get(0));
		Collections.shuffle(possible);
		selected.put(p, possible.get(0));
	}
	
	@SuppressWarnings("deprecation")
	public void openArenaSelector(Player p , int page) {
		setupPlayer(p);
		Group g = main.getGroupManager().getGroup(p);
		if (g.getLeader() != p) {
			p.sendMessage(main.getMessageFetcher().getMessage("group.not_leader", true));
			return;
		}
		LinkedHashMap<Integer, ArrayList<Cluster>> s = main.getLobbyManager().getSuitableArenas(g);
		ArrayList<Cluster> current = null;
		int counter = 1;
		int page_id = 0;		
		ArrayList<Integer> keys = new ArrayList<Integer>(s.keySet());
		Collections.sort(keys);
		outer:
			for (int i : keys) {
				if (counter == page) {
					page_id = i;
					current = s.get(i);
					break outer;
				}
				counter += 1;
			}
		int lines = 9 + ((current.size() / 9) + 1) * 9;
		Inventory inv = Bukkit.createInventory(null, lines, main.c(main.getSettingsManager().getFile().getString("lobby.inventory.choosemap.title").replaceAll("%page%", page + "").replaceAll("%max%", s.keySet().size() + "").replaceAll("%p%", page_id + "")));
		
		this.size.put(p, page_id);
		this.page.put(p, page);
		this.inv.put(p, main.c(main.getSettingsManager().getFile().getString("lobby.inventory.choosemap.title").replaceAll("%page%", page + "").replaceAll("%max%", s.keySet().size() + "").replaceAll("%p%", page_id + "")));
		
		inv.setItem(0, main.getItemManager().getItem("lobby.inventory.spacer"));
		if (page != 1) {
			inv.setItem(0, main.getItemManager().getItem("lobby.inventory.back"));
		}
		
		inv.setItem(8, main.getItemManager().getItem("lobby.inventory.spacer"));
		if (page != s.keySet().size()) {
			inv.setItem(8, main.getItemManager().getItem("lobby.inventory.next"));
		}
		
		for (int i = 1 ; i < 8 ; i ++) {
			inv.setItem(i, main.getItemManager().getItem("lobby.inventory.spacer"));
		}
		
		for (Cluster c : current) {
			inv.addItem(getMapItem(c , p));
		}
		
		if (this.selected.containsKey(p)) {
			ItemStack stack = new ItemStack(this.selected.get(p).getDisplayItem().getType() , 1 , this.selected.get(p).getDisplayItem().getData().getData());
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(main.c(main.getItemManager().getItemFile().getString("lobby.inventory.selected.displayname").replaceAll("%display%", this.selected.get(p).getDisplayName())));
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			stack.setItemMeta(meta);
			inv.setItem(4, stack);
		}
		
		p.openInventory(inv);
	}
	
	public ItemStack getMapItem (Cluster c , Player p) {
		String displayname;
		List<String> lore_t;
		
		if (!this.selected.containsKey(p)) {
			displayname = main.c(main.getSettingsManager().getFile().getString("lobby.inventory.choosemap.map.title")).replaceAll("%display%", c.getDisplayName());
			lore_t = main.getSettingsManager().getFile().getStringList("lobby.inventory.choosemap.map.lore");
		}
		else if (c != this.selected.get(p)) {
			displayname = main.c(main.getSettingsManager().getFile().getString("lobby.inventory.choosemap.map.title")).replaceAll("%display%", c.getDisplayName());
			lore_t = main.getSettingsManager().getFile().getStringList("lobby.inventory.choosemap.map.lore");
		}
		else {
			displayname = main.c(main.getSettingsManager().getFile().getString("lobby.inventory.choosemap.map.title_selected")).replaceAll("%display%", c.getDisplayName());
			lore_t = main.getSettingsManager().getFile().getStringList("lobby.inventory.choosemap.map.lore_selected");
		}
		
		ArrayList<String> lore = new ArrayList<String>();
		for (String temp : lore_t) {
			lore.add(main.c(temp));
		}
		ItemStack map = c.getDisplayItem();
		ItemMeta meta = map.getItemMeta();
		meta.setDisplayName(displayname);
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		map.setItemMeta(meta);
		return map;
	}
	
	
}

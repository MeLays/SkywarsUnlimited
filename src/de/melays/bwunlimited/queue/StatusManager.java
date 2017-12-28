/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.queue;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.SoundDebugger;

public class StatusManager {
	
	Main main;
	
	public HashMap<Player , StatusType> status = new HashMap<Player , StatusType>();
	
	ItemStack block;
	ItemStack ready;
	ItemStack busy;
	ItemStack group;
	
	public StatusManager (Main main) {
		
		this.main = main;
		
		block = getCustomItemStack(main.getConfig().getString("status.block") , main.getSettingsManager().getFile().getString("lobby.status-item.block"));
		ready = getCustomItemStack(main.getConfig().getString("status.ready") , main.getSettingsManager().getFile().getString("lobby.status-item.ready"));
		busy = getCustomItemStack(main.getConfig().getString("status.busy") , main.getSettingsManager().getFile().getString("lobby.status-item.busy"));
		group = getCustomItemStack(main.getConfig().getString("status.group") , main.getSettingsManager().getFile().getString("lobby.status-item.group"));
		
	}
	
	public void updatePlayer(Player p) {
		if (!status.containsKey(p))
			status.put(p, StatusType.BUSY);
		if (main.getGroupManager().getGroup(p).getPlayers().size() != 1) status.put(p, StatusType.GROUP);
		if (status.get(p) == StatusType.GROUP && main.getGroupManager().getGroup(p).getPlayers().size() == 1) status.put(p, StatusType.BUSY);
		p.getInventory().setHelmet(getStack(status.get(p)));
		if (main.getConfig().getBoolean("lobby.status.enabled")) {
			p.getInventory().setItem(main.getConfig().getInt("lobby.status.slot"), getStack(status.get(p)));
		}
	}
	
	public ItemStack getStack(StatusType type) {
		if (type == StatusType.BLOCK) {
			return block;
		}
		if (type == StatusType.READY) {
			return ready;
		}
		if (type == StatusType.BUSY) {
			return busy;
		}
		if (type == StatusType.GROUP) {
			return group;
		}
		return null;
	}
	
	public void switchStatus (Player p) {
		if (status.get(p) == StatusType.BUSY) {
			status.put(p, StatusType.BLOCK);
			SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
		}
		else if (status.get(p) == StatusType.BLOCK) {
			status.put(p, StatusType.READY);
			SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
		}
		else if (status.get(p) == StatusType.READY) {
			status.put(p, StatusType.BUSY);
			SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
		}
		updatePlayer(p);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getCustomItemStack(String base64 , String display) {
		ItemStack stack = new ItemStack(Material.SKULL_ITEM , 1 , (short) SkullType.PLAYER.ordinal());
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        stack = Bukkit.getUnsafe().modifyItemStack(stack,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.main.c(display));
		stack.setItemMeta(meta);
		return stack;
	}
	
}

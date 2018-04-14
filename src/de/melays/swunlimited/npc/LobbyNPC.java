/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.npc;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import de.melays.swunlimited.Main;

public class LobbyNPC {
	
	Entity e;
	
	public NPCType npc;
	public EntityType type;
	Location loc;
	HashMap<String , Integer> nbt_data;
	String displayname;
	
	boolean nametag;
	
	Main main;
	
	public LobbyNPC (Location loc , Main main , NPCType npc , EntityType type , HashMap<String , Integer> nbt_data , String displayname , boolean nametag) {
		this.npc = npc;
		this.type = type;
		this.loc = loc;
		this.main = main;
		this.nbt_data = nbt_data;
		this.displayname = displayname;
		this.nametag = nametag;
		this.e = main.getNPCManager().getEntity(main.getNPCManager().spawnNPC(loc, type.toString(), displayname, nbt_data));
		this.e.setCustomNameVisible(nametag);
	}
	
	public void remove () {
		e.remove();
	}
}

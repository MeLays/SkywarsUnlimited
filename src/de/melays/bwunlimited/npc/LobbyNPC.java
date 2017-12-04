package de.melays.bwunlimited.npc;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import de.melays.bwunlimited.Main;

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

package de.melays.bwunlimited.map_manager.meta;

import org.bukkit.Material;

import de.melays.bwunlimited.map_manager.FineRelativeLocation;

public class ItemSpawner {
	
	int id;
	FineRelativeLocation loc;
	Material m;
	int ticks;
	String displayname;
	
	public ItemSpawner(int id , FineRelativeLocation loc , Material m , int ticks , String displayname) {
		this.id = id;
		this.loc = loc;
		this.m = m;
		this.ticks = ticks;
		this.displayname = displayname;
	}
	
}

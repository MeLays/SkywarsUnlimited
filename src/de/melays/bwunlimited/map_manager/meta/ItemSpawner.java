package de.melays.bwunlimited.map_manager.meta;

import org.bukkit.Material;

import de.melays.bwunlimited.map_manager.FineRelativeLocation;

public class ItemSpawner {
	
	public int id;
	public FineRelativeLocation loc;
	public Material m;
	public int ticks;
	public String displayname;
	
	public ItemSpawner(int id , FineRelativeLocation loc , Material m , int ticks , String displayname) {
		this.id = id;
		this.loc = loc;
		this.m = m;
		this.ticks = ticks;
		this.displayname = displayname;
	}
	
}

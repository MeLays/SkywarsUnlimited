package de.melays.bwunlimited.map_manager;

import org.bukkit.Location;

public class RelativeLocation {
	
	double shift_x;
	double shift_y;
	double shift_z;
	
	public RelativeLocation(Location relative , Location loc){
		shift_x = loc.getBlockX() - relative.getBlockX();
		shift_y = loc.getBlockY() - relative.getBlockY();
		shift_z = loc.getBlockZ() - relative.getBlockZ();
	}
	
	public Location toLocation (Location relative) {
		return new Location (relative.getWorld() , shift_x + relative.getX() , shift_y + relative.getY() , shift_z + relative.getZ() , relative.getYaw() , relative.getPitch());
	}
	
}

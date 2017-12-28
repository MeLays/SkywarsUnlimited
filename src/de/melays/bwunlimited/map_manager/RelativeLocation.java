/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.map_manager;

import org.bukkit.Location;
import org.bukkit.World;

public class RelativeLocation {
	
	double shift_x;
	double shift_y;
	double shift_z;
	
	long yaw = 0;
	long pitch = 0;
	
	World world;
	
	public RelativeLocation(Location relative , Location loc){
		shift_x = loc.getBlockX() - relative.getBlockX();
		shift_y = loc.getBlockY() - relative.getBlockY();
		shift_z = loc.getBlockZ() - relative.getBlockZ();
		yaw = (long) loc.getYaw();
		pitch = (long) loc.getPitch();
		world = loc.getWorld();
	}
	
	public RelativeLocation(World world , double x , double y , double z , long pitch , long yaw) {
		this.shift_x = x;
		this.shift_y = y;
		this.shift_z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
	}
	
	public Location toLocation (Location relative) {
		return new Location (relative.getWorld() , shift_x + relative.getX() , shift_y + relative.getY() , shift_z + relative.getZ() , pitch , yaw );
	}
	
}

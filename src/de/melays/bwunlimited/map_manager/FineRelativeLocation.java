/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.map_manager;

import org.bukkit.Location;
import org.bukkit.World;

public class FineRelativeLocation extends RelativeLocation{

	public FineRelativeLocation(Location relative , Location loc){
		super(relative , loc);
		super.shift_x = loc.getX() - relative.getX();
		super.shift_y = loc.getY() - relative.getY();
		super.shift_z = loc.getZ() - relative.getZ();
	}
	
	public FineRelativeLocation(World world , double x , double y , double z , long pitch , long yaw) {
		super (world , x , y , z , pitch , yaw);
	}
	
	public String toString() {
		String r = "x: "+super.shift_x+" y: "+super.shift_y+" z: "+super.shift_z;
		return r;
	}
	
}

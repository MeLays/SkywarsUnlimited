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
	
}

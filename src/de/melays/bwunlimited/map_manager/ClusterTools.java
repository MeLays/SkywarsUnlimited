/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.map_manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.error.InvalidNameException;
import de.melays.bwunlimited.map_manager.error.ClusterAlreadyExistsException;
import de.melays.bwunlimited.map_manager.error.WrongWorldException;

public class ClusterTools {
	
	public static boolean isInArea (Location loc , Location in1 , Location in2) {
		Location[] locs = generateMaxMinPositions(in1 , in2);
		Location min = locs[0];
		Location max = locs[1];
		if (loc.getX() >= min.getX() && loc.getY() >= min.getY() && loc.getZ() >= min.getZ()) {
			if (loc.getX() <= max.getX() && loc.getY() <= max.getY() && loc.getZ() <= max.getZ()) {
				return true;
			}
		}
		return false;
	}
	
	public static void cylinder(Location loc, Material mat, int r) {
	    int cx = loc.getBlockX();
	    int cy = loc.getBlockY();
	    int cz = loc.getBlockZ();
	    World w = loc.getWorld();
	    int rSquared = r * r;
	    for (int x = cx - r; x <= cx +r; x++) {
	        for (int z = cz - r; z <= cz +r; z++) {
	            if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
	                w.getBlockAt(x, cy, z).setType(mat);
	            }
	        }
	    }
	}
	
	public static boolean isInAreaIgnoreHeight (Location loc , Location in1 , Location in2) {
		Location[] locs = generateMaxMinPositions(in1 , in2);
		Location min = locs[0];
		Location max = locs[1];
		if (loc.getX() >= min.getX() && loc.getZ() >= min.getZ()) {
			if (loc.getX() <= max.getX() && loc.getZ() <= max.getZ()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInCluster(Location l , Cluster c) {
		return isInArea(l , c.min , c.max);
	}
	
	public static Location[] generateMaxMinPositions (Location l1 , Location l2){
		double xpos1;
		double ypos1;
		double zpos1;
		double xpos2;
		double ypos2;
		double zpos2;
		if (l1.getX() <= l2.getX()){	
			xpos1 = l1.getX();
			xpos2 = l2.getX();	
		}
		else{	
			xpos1 = l2.getX();
			xpos2 = l1.getX();	
		}
		if (l1.getY() <= l2.getY()){	
			ypos1 = l1.getY();
			ypos2 = l2.getY();	
		}
		else{
			ypos1 = l2.getY();
			ypos2 = l1.getY();
		}
		if (l1.getZ() <= l2.getZ()){
			
			zpos1 = l1.getZ();
			zpos2 = l2.getZ();
		}
		else{	
			zpos1 = l2.getZ();
			zpos2 = l1.getZ();	
		}
		
		Location locs[] = {new Location (l1.getWorld() , xpos1 , ypos1 , zpos1) , new Location (l1.getWorld() , xpos2 , ypos2 , zpos2)};
		return locs;
	}
	
	public static void saveLiteLocation (FileConfiguration config , String path , Location loc) {
		config.set(path + ".x", loc.getBlockX());
		config.set(path + ".y", loc.getBlockY());
		config.set(path + ".z", loc.getBlockZ());
		config.set(path + ".world", loc.getWorld().getName());
	}
	
	public static Location getLiteLocation (FileConfiguration config , String path) {
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		String world = config.getString(path + ".world");
		return new Location(Bukkit.getWorld(world) , x , y , z);
	}
	
	public static void saveLocation (FileConfiguration config , String path , Location loc) {
		config.set(path + ".x", loc.getX());
		config.set(path + ".y", loc.getY());
		config.set(path + ".z", loc.getZ());
		config.set(path + ".yaw", loc.getYaw());
		config.set(path + ".pitch", loc.getPitch());
		config.set(path + ".world", loc.getWorld().getName());
	}
	
	public static Location getLocation (FileConfiguration config , String path) {
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		double yaw = config.getDouble(path + ".yaw");
		double pitch = config.getDouble(path + ".pitch");
		String world = config.getString(path + ".world");
		return new Location(Bukkit.getWorld(world) , x , y , z , (long)yaw , (long)pitch);
	}
	
	public static void saveFineRelativeLocation (FileConfiguration config , String path , FineRelativeLocation loc) {
		config.set(path + ".x", loc.shift_x);
		config.set(path + ".y", loc.shift_y);
		config.set(path + ".z", loc.shift_z);
		config.set(path + ".yaw", loc.yaw);
		config.set(path + ".pitch", loc.pitch);
		config.set(path + ".world", loc.world.getName());
	}
	
	public static FineRelativeLocation getFineRelativeLocation (FileConfiguration config , String path) {
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		double yaw = config.getDouble(path + ".yaw");
		double pitch = config.getDouble(path + ".pitch");
		String world = config.getString(path + ".world");
		return new FineRelativeLocation(Bukkit.getWorld(world) , x , y , z , (long)yaw , (long)pitch);
	}
	
	public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
	    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    String name = "net.minecraft.server." + version + nmsClassString;
	    Class<?> nmsClass = Class.forName(name);
	    return nmsClass;
	}
	
	public static Class<?> getBukkit(String nmsClassString) throws ClassNotFoundException {
	    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    String name = "org.bukkit.craftbukkit." + version + nmsClassString;
	    Class<?> nmsClass = Class.forName(name);
	    return nmsClass;
	}
	
	public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    Method getHandle = player.getClass().getMethod("getHandle");
	    Object nmsPlayer = getHandle.invoke(player);
	    Field conField = nmsPlayer.getClass().getField("playerConnection");
	    Object con = conField.get(nmsPlayer);
	    return con;
	}
	
	public static void setBlockFast (Location loc , Material m , byte data) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		World world = loc.getWorld();
		@SuppressWarnings("deprecation")
		int blockId = m.getId();
		
		try {
			//Minecraft Server World ObjectgetBukkit
			Class<?> world_class = getNMSClass("World");
			Class<?> craftworld_class = getBukkit("CraftWorld");
			Object craft_world = craftworld_class.cast(world);
			Object world_server = craft_world.getClass().getMethod("getHandle").invoke(craft_world , new Object[]{});
			
			Method getChunkAt = null;
			
			for (Method n : world_class.cast(world_server).getClass().getMethods()) {
				if (n.getName().equals("getChunkAt")) {
					getChunkAt = n;
				}
			}
			
			//Minecraft Server Chunk Object
			Class<?> chunk_class = getNMSClass("Chunk");
			Object chunk = getChunkAt.invoke(world_server , x >> 4, z >> 4);
			
			//BlockPosition Object
			Class<?> block_position_class = getNMSClass("BlockPosition");
			@SuppressWarnings("rawtypes")
			Constructor bp_constructor = block_position_class.getConstructor(int.class , int.class , int.class);
			Object bp = bp_constructor.newInstance(x , y , z);
			
			//Combined BlockID
			int combined = blockId + (data << 12);
			
			//Get IBlockData Object
			Class<?> iblockdata_class = getNMSClass("IBlockData");
			Class<?> block_class = getNMSClass("Block");
			
			Method getByCombinedId = null;
			
			for (Method n : block_class.getMethods()) {
				if (n.getName().equals("getByCombinedId")) {
					getByCombinedId = n;
				}
			}
			
			Object idb_object = getByCombinedId.invoke(null , combined);
			
			Method a = chunk_class.getMethod("a", block_position_class, iblockdata_class);
			
			a.invoke(chunk, block_position_class.cast(bp) , iblockdata_class.cast(idb_object));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}        
	}
	
	@SuppressWarnings("deprecation")
	public static void setBlocksFast (Location relative , HashMap<RelativeLocation , AdvancedMaterial> cluster_list) {
		World world = relative.getWorld();
		try {
			//Minecraft Server World ObjectgetBukkit
			Class<?> world_class = getNMSClass("World");
			Class<?> craftworld_class = getBukkit("CraftWorld");
			Object craft_world = craftworld_class.cast(world);
			Object world_server = craft_world.getClass().getMethod("getHandle").invoke(craft_world , new Object[]{});
			
			Method getChunkAt = null;
			
			for (Method n : world_class.cast(world_server).getClass().getMethods()) {
				if (n.getName().equals("getChunkAt")) {
					getChunkAt = n;
				}
			}
			
			//BlockPosition Object
			Class<?> block_position_class = getNMSClass("BlockPosition");
			@SuppressWarnings("rawtypes")
			Constructor bp_constructor = block_position_class.getConstructor(int.class , int.class , int.class);
			
			//Minecraft Server Chunk Object
			Class<?> chunk_class = getNMSClass("Chunk");
			
			//Get IBlockData Object
			Class<?> iblockdata_class = getNMSClass("IBlockData");
			Class<?> block_class = getNMSClass("Block");
			
			for (RelativeLocation rl : cluster_list.keySet()) {	
				
				Location loc = rl.toLocation(relative);
								
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				
				byte data = cluster_list.get(rl).getData();
				int blockId = cluster_list.get(rl).getMaterial().getId();
				
				if (!world.isChunkLoaded( x >> 4, z >> 4)) {
					world.loadChunk( x >> 4, z >> 4);
				}
				Object chunk = getChunkAt.invoke(world_server , x >> 4, z >> 4);
				Object bp = bp_constructor.newInstance(x , y , z);
				
				//Combined BlockID
				int combined = blockId + (data << 12);
				
				Method getByCombinedId = null;
				
				for (Method n : block_class.getMethods()) {
					if (n.getName().equals("getByCombinedId")) {
						getByCombinedId = n;
					}
				}
				
				Object idb_object = getByCombinedId.invoke(null , combined);
				
				Method a = chunk_class.getMethod("a", block_position_class, iblockdata_class);
				
				a.invoke(chunk, block_position_class.cast(bp) , iblockdata_class.cast(idb_object));
				//world.refreshChunk(x >> 4, z >> 4);
			}
		} catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings("deprecation")
	public static void setBlocksClassic (Location relative , HashMap<RelativeLocation , AdvancedMaterial> cluster_list) {
		for (RelativeLocation rl : cluster_list.keySet()) {		
			
			Location loc = rl.toLocation(relative);
			loc.getBlock().getState().setType(cluster_list.get(rl).getMaterial());
			loc.getBlock().getState().setRawData(cluster_list.get(rl).getData());
			loc.getBlock().getState().update(true, false);

		}
	}
	
	@SuppressWarnings("deprecation")
	public static void reverseBlocksFast (Location relative , Cluster cluster) {
		World world = cluster.min.getWorld();
		try {
			//Minecraft Server World ObjectgetBukkit
			Class<?> world_class = getNMSClass("World");
			Class<?> craftworld_class = getBukkit("CraftWorld");
			Object craft_world = craftworld_class.cast(world);
			Object world_server = craft_world.getClass().getMethod("getHandle").invoke(craft_world , new Object[]{});
			
			Method getChunkAt = null;
			
			for (Method n : world_class.cast(world_server).getClass().getMethods()) {
				if (n.getName().equals("getChunkAt")) {
					getChunkAt = n;
				}
			}
			
			//BlockPosition Object
			Class<?> block_position_class = getNMSClass("BlockPosition");
			@SuppressWarnings("rawtypes")
			Constructor bp_constructor = block_position_class.getConstructor(int.class , int.class , int.class);
			
			//Minecraft Server Chunk Object
			Class<?> chunk_class = getNMSClass("Chunk");
			
			//Get IBlockData Object
			Class<?> iblockdata_class = getNMSClass("IBlockData");
			Class<?> block_class = getNMSClass("Block");
			
			for (RelativeLocation rl : cluster.cluster_list.keySet()) {				
				Location loc = rl.toLocation(relative);
								
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				
				byte data = 0;
				int blockId = 0;
				
				Object chunk = getChunkAt.invoke(world_server , x >> 4, z >> 4);
				Object bp = bp_constructor.newInstance(x , y , z);
				
				//Combined BlockID
				int combined = blockId + (data << 12);
				
				Method getByCombinedId = null;
				
				for (Method n : block_class.getMethods()) {
					if (n.getName().equals("getByCombinedId")) {
						getByCombinedId = n;
					}
				}
				
				Object idb_object = getByCombinedId.invoke(null , combined);
				
				Method a = chunk_class.getMethod("a", block_position_class, iblockdata_class);
				
				a.invoke(chunk, block_position_class.cast(bp) , iblockdata_class.cast(idb_object));
				world.refreshChunk(x >> 4, z >> 4);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static boolean checkSpace (Location min , Location max) {
		min = min.clone().add(-2, 0, -2);
		max = max.clone().add(2, 2, 2);
        for(int x = min.getBlockX(); x <= max.getBlockX(); x++){
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++){
                	Location loc = new Location (min.getWorld() , x , y , z);
                	if (loc.getBlock().getType() != Material.AIR) {
                		return false;
                	}
                }
            }
        }
		return true;
	}
	
	public static boolean generateTemplate(ClusterManager manager , String name , Location rel , int w , int h , int l , int teams) throws ClusterAlreadyExistsException, InvalidNameException, WrongWorldException {
		Location min = new Location(rel.getWorld(), rel.getBlockX() + 1, rel.getBlockY() + 1, rel.getBlockZ() + 1);
		Location max = new Location(rel.getWorld(), rel.getBlockX() + w, rel.getBlockY() + h, rel.getBlockZ() + l);
		if (!checkSpace(min , max)) {
			return false;
		}
		
		manager.createCluster(name, min, max , false);
		
		//Fill top and bottom
		for(int x = rel.getBlockX(); x <= rel.getBlockX() + w + 1; x++){
			for(int z = rel.getBlockZ(); z<= rel.getBlockZ() + l + 1; z++){
				new Location(rel.getWorld() , x , rel.getBlockY() , z).getBlock().setType(Material.STAINED_GLASS);
			}
		}
		for(int x = rel.getBlockX(); x <= rel.getBlockX() + w + 1; x++){
			for(int z = rel.getBlockZ(); z<= rel.getBlockZ() + l + 1; z++){
				new Location(rel.getWorld() , x , rel.getBlockY() + h + 1 , z).getBlock().setType(Material.STAINED_GLASS);
			}
		}
		//Generate the x/width sides
		for(int x = rel.getBlockX(); x <= rel.getBlockX() + w + 1; x++){
			new Location(rel.getWorld() , x , rel.getBlockY() , rel.getBlockZ()).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int x = rel.getBlockX(); x <= rel.getBlockX() + w + 1; x++){
			new Location(rel.getWorld() , x , rel.getBlockY() + h + 1, rel.getBlockZ()).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int x = rel.getBlockX(); x <= rel.getBlockX() + w + 1; x++){
			new Location(rel.getWorld() , x , rel.getBlockY() , rel.getBlockZ() + l + 1).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int x = rel.getBlockX(); x <= rel.getBlockX() + w + 1; x++){
			new Location(rel.getWorld() , x , rel.getBlockY() + h + 1 , rel.getBlockZ() + l + 1).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		
		//Generate the y/width sides
		for(int y = rel.getBlockY(); y<= rel.getBlockY() + h + 1; y++){
			new Location(rel.getWorld() , rel.getBlockX() , y , rel.getBlockZ()).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int y = rel.getBlockY(); y <= rel.getBlockY() + h + 1; y++){
			new Location(rel.getWorld() , rel.getBlockX() , y , rel.getBlockZ() + l + 1).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int y = rel.getBlockY(); y <= rel.getBlockY() + h + 1; y++){
			new Location(rel.getWorld() , rel.getBlockX() + w + 1, y , rel.getBlockZ()).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int y = rel.getBlockY(); y <= rel.getBlockY() + h + 1; y++){
			new Location(rel.getWorld() , rel.getBlockX() + w + 1 , y , rel.getBlockZ() + l + 1).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		
		//Generate the z/width sides
		for(int z = rel.getBlockZ(); z<= rel.getBlockZ() + l + 1; z++){
			new Location(rel.getWorld() , rel.getBlockX() , rel.getBlockY() , z).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int z = rel.getBlockZ(); z <= rel.getBlockZ() + l + 1; z++){
			new Location(rel.getWorld() , rel.getBlockX() , rel.getBlockY() + h + 1 , z).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int z = rel.getBlockZ(); z <= rel.getBlockZ() + l + 1; z++){
			new Location(rel.getWorld() , rel.getBlockX() + w + 1, rel.getBlockY() , z).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		for(int z = rel.getBlockZ(); z <= rel.getBlockZ() + l + 1; z++){
			new Location(rel.getWorld() , rel.getBlockX() + w + 1 , rel.getBlockY() + h + 1, z).getBlock().setType(Material.QUARTZ_BLOCK);
        }
		
		//Generate Teams
		if (teams == 4) {
			int shift_l = l/8;
			int shift_h = h/2;
			int shift_w = w/2;
			new Location(rel.getWorld() , rel.getBlockX() + shift_w , rel.getBlockY() + shift_h , rel.getBlockZ() + shift_l).getBlock().setType(Material.GLOWSTONE);
			new Location(rel.getWorld() , rel.getBlockX() + w - shift_w , rel.getBlockY() + shift_h , rel.getBlockZ() + l - shift_l).getBlock().setType(Material.GLOWSTONE);
			
			shift_l = l/2;
			shift_h = h/2;
			shift_w = w/8;
			new Location(rel.getWorld() , rel.getBlockX() + shift_w , rel.getBlockY() + shift_h , rel.getBlockZ() + shift_l).getBlock().setType(Material.GLOWSTONE);
			new Location(rel.getWorld() , rel.getBlockX() + w - shift_w , rel.getBlockY() + shift_h , rel.getBlockZ() + l - shift_l).getBlock().setType(Material.GLOWSTONE);
		}
		else if (teams == 2) {
			int shift_l = l/8;
			int shift_h = h/2;
			int shift_w = w/2;
			new Location(rel.getWorld() , rel.getBlockX() + shift_w , rel.getBlockY() + shift_h , rel.getBlockZ() + shift_l).getBlock().setType(Material.GLOWSTONE);
			new Location(rel.getWorld() , rel.getBlockX() + w - shift_w , rel.getBlockY() + shift_h , rel.getBlockZ() + l - shift_l).getBlock().setType(Material.GLOWSTONE);
		}
		
		rel.clone().add(0, 1, 0).getBlock().setType(Material.COAL_BLOCK);
		Block sign = rel.clone().add(0, 1, -1).getBlock();
		sign.setType(Material.WALL_SIGN);
		
		Sign sign_data = (Sign) sign.getState();
		sign_data.setLine(0, "Bedwars UNLMTD");
		sign_data.setLine(1, "Cluster");
		sign_data.setLine(2, name);
		sign_data.setLine(3, "");
		sign_data.update();
		
		manager.loadCluster(name);
		
		return true;
	}
}

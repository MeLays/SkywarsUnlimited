package de.melays.bwunlimited.entitys;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Merchant {
	
	public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
	    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    String name = "net.minecraft.server." + version + nmsClassString;
	    Class<?> nmsClass = Class.forName(name);
	    return nmsClass;
	}
	
	public static Class<?> getBukkitClass(String nmsClassString) throws ClassNotFoundException {
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
	
	public static void spawn (Location loc , String display , EntityType type) {
		Entity e = loc.getWorld().spawnEntity(loc, type);
		e.setCustomName(display);
		
		try {
			Class<?> CraftEntity = getBukkitClass("entity.CraftEntity");
			Class<?> NBTTagCompound = getNMSClass("NBTTagCompound");
			
			Object villager_entity = CraftEntity.cast(e).getClass().getMethod("getHandle").invoke(CraftEntity.cast(e));
			Object nbt = villager_entity.getClass().getMethod("getNBTTag").invoke(villager_entity);
			if (nbt == null) {
				nbt = NBTTagCompound.newInstance();
			}
			villager_entity.getClass().getMethod("c", NBTTagCompound).invoke(villager_entity, nbt);
			Method setInt = null;
			for (Method n : nbt.getClass().getMethods()) {
				if (n.getName().equals("setInt")) {
					setInt = n;
				}
			}
			setInt.invoke(nbt, "NoAI" , 1);
			villager_entity.getClass().getMethod("f", NBTTagCompound).invoke(villager_entity, nbt);
			
		} catch (InvocationTargetException e1) {
			e1.getCause().printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
//		net.minecraft.server.v1_8_R3.Entity villager = ((CraftEntity) e).getHandle();
//		NBTTagCompound nbt = villager.getNBTTag();
//	    if(nbt == null) {
//	    	nbt = new NBTTagCompound();
//	    }
//	    villager.c(nbt);
//		nbt.setInt("NoAI", 1);
//		villager.f(nbt);
	}
	
}

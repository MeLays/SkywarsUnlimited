package de.melays.bwunlimited.npc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.log.Logger;
import net.md_5.bungee.api.ChatColor;

public class NPCManager {

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
	
	Main main;
	
	public NPCManager(Main main) {
		this.main = main;
	}
	
	HashMap<UUID , Entity> entitys = new HashMap<UUID , Entity>();
	
	public Entity getEntity(UUID uuid) {
		return entitys.get(uuid);
	}
	
	public UUID spawnNPC (Location loc , String type , String displayname , HashMap<String , Integer> nbt_data) {
		type = type.toUpperCase();
		EntityType entity = EntityType.valueOf(type);
		if (entity == null) {
			entity = EntityType.VILLAGER;
			Logger.log(main.console_prefix + ChatColor.RED + "Unknown EntityType '" + type + "'. Using a Villager instead...");
		}
		Entity e = spawn(loc , displayname , entity , nbt_data);
		UUID id = UUID.randomUUID();
		entitys.put(id, e);
		return id;
	}
	
	public void clearAll() {
		for (Entity e : entitys.values()) {
			if (e != null) {
				if (!e.isDead()) {
					try {
						e.remove();
					} catch (Exception e1) {

					}
				}
			}
		}
	}
	
	private Entity spawn (Location loc , String display , EntityType type , HashMap<String , Integer> nbt_data) {
		Entity e = loc.getWorld().spawnEntity(loc, type);
		if (display != null)
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
			for (String key : nbt_data.keySet()) {
				setInt.invoke(nbt, key , nbt_data.get(key));
			}
			villager_entity.getClass().getMethod("f", NBTTagCompound).invoke(villager_entity, nbt);
			return e;
		} catch (InvocationTargetException e1) {
			e1.getCause().printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return e;
	}
}

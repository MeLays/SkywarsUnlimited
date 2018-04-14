/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.internal_statsapi;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class InternalChannel {
	
	InternalStatsAPI plugin;
	String name;
	
	public InternalChannel (InternalStatsAPI plugin , String name) {
		this.plugin = plugin;
		name = name.toUpperCase();
		this.name = name;
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		if (plugin.isDummy()){

		}
		else{
			try {
				Statement s = plugin.c.createStatement();
				s.execute("CREATE TABLE IF NOT EXISTS " + name + " (UUID VARCHAR(36) UNIQUE)");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public boolean hasKey (String key){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		Statement s;
		try {
			s = plugin.c.createStatement();
			ResultSet keys = s.executeQuery("SHOW columns FROM "+ name +";");
			ArrayList<String> keylist = new ArrayList<String>();
			while (keys.next()){
				keylist.add(keys.getString("Field"));
			}
			for (String str : keylist){
				if (str.equalsIgnoreCase(key)){
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("[StatsAPI] Created key " + key + "!");
		}
		return false;
	}
	
	public void createKey (String key , boolean str){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		if (!hasKey(key)){
			Statement s;
			try {
				s = plugin.c.createStatement();
				if (str){
					s.execute("ALTER TABLE `"+ name +"` ADD COLUMN `"+ key +"` TEXT");	
				}
				else{
					s.execute("ALTER TABLE `"+ name +"` ADD COLUMN `"+ key +"` INT NOT NULL DEFAULT 0");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage("[StatsAPI] Created key " + key + "!");
			}
		}
	}
	
	ArrayList<String> created = new ArrayList<String>();
	
	public void setKey (UUID uuid , String key , int to){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		if (!created.contains(key)){
			createKey (key , false);
			created.add (key);
		}
		Statement s;
		try {
			s = plugin.c.createStatement();
			s.execute("INSERT INTO "+ name +" (UUID) VALUES ('"+uuid+"') ON DUPLICATE KEY UPDATE UUID=UUID;");
			s = plugin.c.createStatement();
			s.execute("UPDATE " + name + " "
					+ "SET " + key + "=" + to + " "
					+ "WHERE UUID='"+uuid.toString()+"';");
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't create Key!");
		}
	}
	
	public int getKey (UUID uuid , String key){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		if (hasKey(key)){
			Statement s;
			try {
				s = plugin.c.createStatement();
				ResultSet result = s.executeQuery("SELECT "+key+" FROM "+name+" WHERE UUID='"+uuid+"';");
				if (result.next()){
					return result.getInt(key);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't get Key "+key+"!");
			}
		}
		return 0;
	}
	
	public void addToKey (UUID uuid , String key , int incr){
		int old = getKey(uuid , key);
		setKey(uuid , key , old + incr);
		try{
			if (name.equalsIgnoreCase("global") && key.equals("points")){
				
				String s = "&c " + incr + "&7 Punkte!";
				if (incr == 1){
					s = "&c einen &7Punkt!";
				}
				
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l(&cSHNBI&8&l)&7 Du erhältst" + s));
			}
			if (name.equalsIgnoreCase("global") && key.equals("coins")){
				
				String s = "&c " + incr + "&7 Coins!";
				if (incr == 1){
					s = "&c einen &7Coin!";
				}
				
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l(&cSHNBI&8&l)&7 Du erhältst" + s));
			}
		}
		catch (Exception ex){
			
		}
	}
	
	public void setStringKey (UUID uuid , String key , String str){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		if (!created.contains(key)){
			createKey (key , true);
			created.add (key);
		}
		Statement s;
		try {
			s = plugin.c.createStatement();
			s.execute("INSERT INTO "+ name +" (UUID) VALUES ('"+uuid+"') ON DUPLICATE KEY UPDATE UUID=UUID;");
			s = plugin.c.createStatement();
			s.execute("UPDATE " + name + " "
					+ "SET " + key + "='" + str + "' "
					+ "WHERE UUID='"+uuid.toString()+"';");
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't create Key!");
		}
	}
	
	public String getStringKey (UUID uuid , String key){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		if (hasKey(key)){
			Statement s;
			try {
				s = plugin.c.createStatement();
				ResultSet result = s.executeQuery("SELECT "+key+" FROM "+name+" WHERE UUID='"+uuid+"';");
				result.next();
				return result.getString(key);
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage("[StatsAPI] Couldn't get Key "+key+"!");
			}
		}
		return null;
	}
	
	public HashMap<String , String> getAllKeys (UUID uuid){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		Statement s;
		try {
			HashMap<String , String> returnmap = new HashMap<String , String>();
			s = plugin.c.createStatement();
			ResultSet result = s.executeQuery("SELECT * FROM "+name+" WHERE UUID='"+uuid+"';");
			ResultSetMetaData rsmd = result.getMetaData();
			int columnCount = rsmd.getColumnCount();
			result.next();
			for (int i = 1; i <= columnCount; i++ ) {
				String name = rsmd.getColumnName(i);
				returnmap.put(name, result.getString(i));
			}
			return returnmap;

		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("[StatsAPI] Failed to get all keys of UUID "+uuid);
		}
		return null;
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.internal_statsapi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

import com.huskehhh.mysql.mysql.MySQL;

import de.melays.swunlimited.Main;

public class InternalStatsAPI{
	
	MySQL mysql = null;	
	Connection c = null;
	boolean dummy = false;
	
	InternalChannel namedb;
	
	Main main;
	
	public InternalStatsAPI(Main main) {
		this.main = main;
		mysql = new MySQL (main.getConfig().getString("mysql.host"), main.getConfig().getString("mysql.port") , main.getConfig().getString("mysql.database") , main.getConfig().getString("mysql.user") , main.getConfig().getString("mysql.password"));
		reconnect();
		namedb = hookChannel(main , "STATSAPI_NAMEDB");
	}
	
	public Connection getConnection() {
		try {
			if (c.isClosed()){
				reconnect();
			}
		} catch (SQLException e1) {

		}
		return c;
	}
	
	public boolean isDummy(){
		return dummy;
	}
	
	public void reconnect(){
		try{
			c = mysql.openConnection();
		}
		catch (Exception ex){
			ex.printStackTrace();
			dummy = true;
		}
	}
	
	HashMap <String , ArrayList<String>> hooks = new HashMap <String , ArrayList<String>>();
	
	public InternalChannel hookChannel (Plugin p , String str){
		if (p != this){
			if (!hooks.containsKey(p.getName())){
				hooks.put(p.getName(), new ArrayList<String> ());
			}
			if (!hooks.get(p.getName()).contains(str)){
				hooks.get(p.getName()).add(str);
			}
		}
		InternalChannel ch = new InternalChannel(this , str);
		return ch;
	}
	
	HashMap<UUID , String> namecache = new HashMap<UUID , String>();
	
	public String getNameFromUUID (UUID s){
		if (!namecache.containsKey(s)){
			namecache.put(s, namedb.getStringKey(s, "NAME"));
		}
		return namecache.get(s);
	}

}

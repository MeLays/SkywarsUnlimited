package de.melays.bwunlimited.teams;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import net.md_5.bungee.api.ChatColor;

public class Team{
	
	Color Color;
	String display;
	String name;
	int max;
	
	Location spawn;
	
	boolean bed = true;
	
	Main main;
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	public Team (Main main , String name , String displayname , Color c , int max , Location spawn){
		
		this.name = name;
		display = displayname;
		Color = c;
		this.spawn = spawn;
		
		this.main = main;
		
		this.max = max;
		
	}
	
	public boolean add (Player p){
		if (players.size() < max){
			players.add(p);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void send(String s){
		for (Player p : players){
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public boolean conatins(Player p){
		return (players.contains(p));
	}
	
	@SuppressWarnings("deprecation")
	public void title(String top , String bottom){
		
		for (Player p : players){
			p.sendTitle(top, bottom);
		}
		
	}
	
}

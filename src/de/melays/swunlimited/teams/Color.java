/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.teams;

import org.bukkit.ChatColor;

public class Color {
	
	String color;
	
	public Color (String c){
		
		color = c;
		
	}
	
	public static String[] getAll () {
		String strl[] = {"white" , "orange" , "lightblue" , "yellow" , "lime" , "pink" , "gray" , "lightgray" , "cyan" , "purple" , "blue" , "green" , "red" , "black"};
		return strl;
	}
	
	public boolean exists(){
		if (color.equalsIgnoreCase("white")){
			
			return true;
			
		}
		else if (color.equalsIgnoreCase("orange")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("lightblue")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("yellow")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("lime")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("pink")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("gray")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("lightgray")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("cyan")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("purple")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("blue")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("green")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("red")){
			
			return true;			
		}
		else if (color.equalsIgnoreCase("black")){
			
			return true;			
		}
		return false;
	}
	
	public byte toByte(){
		
		if (color.equalsIgnoreCase("white")){
			
			return 0;
			
		}
		else if (color.equalsIgnoreCase("orange")){
			
			return 1;
			
		}
		else if (color.equalsIgnoreCase("lightblue")){
			
			return 3;
			
		}
		else if (color.equalsIgnoreCase("yellow")){
			
			return 4;
			
		}
		else if (color.equalsIgnoreCase("lime")){
			
			return 5;
			
		}
		else if (color.equalsIgnoreCase("pink")){
			
			return 6;
			
		}
		else if (color.equalsIgnoreCase("gray")){
			
			return 7;
			
		}
		else if (color.equalsIgnoreCase("lightgray")){
			
			return 8;
			
		}
		else if (color.equalsIgnoreCase("cyan")){
			
			return 9;
			
		}
		else if (color.equalsIgnoreCase("purple")){
			
			return 10;
			
		}
		else if (color.equalsIgnoreCase("blue")){
			
			return 11;
			
		}
		else if (color.equalsIgnoreCase("green")){
			
			return 13;
			
		}
		else if (color.equalsIgnoreCase("red")){
			
			return 14;
			
		}
		else if (color.equalsIgnoreCase("black")){
			
			return 15;
			
		}
		return 0;
	}
	
	public ChatColor toChatColor(){
		if (color.equalsIgnoreCase("white")){
			
			return ChatColor.WHITE;
			
		}
		else if (color.equalsIgnoreCase("orange")){
			
			return ChatColor.GOLD;
			
		}
		else if (color.equalsIgnoreCase("lightblue")){
			
			return ChatColor.BLUE;
			
		}
		else if (color.equalsIgnoreCase("yellow")){
			
			return ChatColor.YELLOW;
			
		}
		else if (color.equalsIgnoreCase("lime")){
			
			return ChatColor.GREEN;
			
		}
		else if (color.equalsIgnoreCase("pink")){
			
			return ChatColor.LIGHT_PURPLE;
			
		}
		else if (color.equalsIgnoreCase("gray")){
			
			return ChatColor.DARK_GRAY;
			
		}
		else if (color.equalsIgnoreCase("lightgray")){
			
			return ChatColor.GRAY;
			
		}
		else if (color.equalsIgnoreCase("cyan")){
			
			return ChatColor.AQUA;
			
		}
		else if (color.equalsIgnoreCase("purple")){
			
			return ChatColor.DARK_PURPLE;
			
		}
		else if (color.equalsIgnoreCase("blue")){
			
			return ChatColor.DARK_BLUE;
			
		}
		else if (color.equalsIgnoreCase("green")){
			
			return ChatColor.DARK_GREEN;
			
		}
		else if (color.equalsIgnoreCase("red")){
			
			return ChatColor.RED;
			
		}
		else if (color.equalsIgnoreCase("black")){
			
			return ChatColor.BLACK;
			
		}
		return ChatColor.WHITE;
	}
	
	public org.bukkit.Color toBukkitColor(){
		if (color.equalsIgnoreCase("white")){
			
			return org.bukkit.Color.WHITE;
			
		}
		else if (color.equalsIgnoreCase("orange")){
			
			return org.bukkit.Color.ORANGE;
			
		}
		else if (color.equalsIgnoreCase("lightblue")){
			
			return org.bukkit.Color.BLUE;
			
		}
		else if (color.equalsIgnoreCase("yellow")){
			
			return org.bukkit.Color.YELLOW;
			
		}
		else if (color.equalsIgnoreCase("lime")){
			
			return org.bukkit.Color.LIME;
			
		}
		else if (color.equalsIgnoreCase("pink")){
			
			return org.bukkit.Color.FUCHSIA;
			
		}
		else if (color.equalsIgnoreCase("gray")){
			
			return org.bukkit.Color.GRAY;
			
		}
		else if (color.equalsIgnoreCase("lightgray")){
			
			return org.bukkit.Color.GRAY;
			
		}
		else if (color.equalsIgnoreCase("cyan")){
			
			return org.bukkit.Color.AQUA;
			
		}
		else if (color.equalsIgnoreCase("purple")){
			
			return org.bukkit.Color.PURPLE;
			
		}
		else if (color.equalsIgnoreCase("blue")){
			
			return org.bukkit.Color.BLUE;
			
		}
		else if (color.equalsIgnoreCase("green")){
			
			return org.bukkit.Color.GREEN;
			
		}
		else if (color.equalsIgnoreCase("red")){
			
			return org.bukkit.Color.RED;
			
		}
		else if (color.equalsIgnoreCase("black")){
			
			return org.bukkit.Color.BLACK;
			
		}
		return org.bukkit.Color.WHITE;
	}
	
	public org.bukkit.Color toColor(){
		if (color.equalsIgnoreCase("white")){
			
			return org.bukkit.Color.WHITE;
			
		}
		else if (color.equalsIgnoreCase("orange")){
			
			return org.bukkit.Color.ORANGE;
			
		}
		else if (color.equalsIgnoreCase("lightblue")){
			
			return org.bukkit.Color.fromBGR(51, 102, 255);
			
		}
		else if (color.equalsIgnoreCase("yellow")){
			
			return org.bukkit.Color.YELLOW;
			
		}
		else if (color.equalsIgnoreCase("lime")){
			
			return org.bukkit.Color.LIME;
			
		}
		else if (color.equalsIgnoreCase("pink")){
			
			return org.bukkit.Color.fromBGR(255, 102, 255);
			
		}
		else if (color.equalsIgnoreCase("gray")){
			
			return org.bukkit.Color.fromBGR(48, 48, 48);
			
		}
		else if (color.equalsIgnoreCase("lightgray")){
			
			return org.bukkit.Color.fromBGR(173, 173, 173);
			
		}
		else if (color.equalsIgnoreCase("cyan")){
			
			return org.bukkit.Color.AQUA;
			
		}
		else if (color.equalsIgnoreCase("purple")){
			
			return org.bukkit.Color.PURPLE;
			
		}
		else if (color.equalsIgnoreCase("blue")){
			
			return org.bukkit.Color.fromBGR(0, 51, 153);
			
		}
		else if (color.equalsIgnoreCase("green")){
			
			return org.bukkit.Color.GREEN;
			
		}
		else if (color.equalsIgnoreCase("red")){
			
			return org.bukkit.Color.RED;
			
		}
		else if (color.equalsIgnoreCase("black")){
			
			return org.bukkit.Color.BLACK;
			
		}
		return org.bukkit.Color.WHITE;
	}
}

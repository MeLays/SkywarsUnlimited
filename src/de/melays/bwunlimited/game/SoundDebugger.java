/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.game;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundDebugger {

	public static void playSound(Player p , String s1 , String s2){
		try{
			if (Sound.valueOf(s1) != null){
				p.playSound(p.getLocation(), Sound.valueOf(s1) , 1, 1);
			}
			else if (Sound.valueOf(s2) != null){
				p.playSound(p.getLocation(), Sound.valueOf(s2) , 1, 1);
			}
		}
		catch(Exception e){
			
		}
		
	}
	
	public static void playSound(World w , Location loc , String s1 , String s2){
		try{
			if (Sound.valueOf(s1) != null){
				w.playSound(loc, Sound.valueOf(s1) , 1, 1);
			}
			else if (Sound.valueOf(s2) != null){
				w.playSound(loc, Sound.valueOf(s2) , 1, 1);
			}
		}
		catch(Exception e){
			
		}
		
	}
	
	public static Sound getSound(String s1 , String s2) {
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		if (Sound.valueOf(s1) != null){
			return Sound.valueOf(s1);
		}
		else if (Sound.valueOf(s2) != null){
			return Sound.valueOf(s2);
		}
		return null;
	}
	
}

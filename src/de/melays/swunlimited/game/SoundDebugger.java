/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.game;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundDebugger {
	
	public static void playSound(Player p , String s1 , String s2){
		try{
			p.playSound(p.getLocation(), Sound.valueOf(s1) , 1, 1);
		}
		catch(Exception e){
			try {
				p.playSound(p.getLocation(), Sound.valueOf(s2) , 1, 1);
			} catch (Exception e1) {

			}
		}
	}
	
	public static void playSound(World w , Location loc , String s1 , String s2){
		try{
			w.playSound(loc, Sound.valueOf(s1) , 1, 1);
		}
		catch(Exception e){
			try {
				w.playSound(loc, Sound.valueOf(s2) , 1, 1);
			} catch (Exception e1) {

			}
		}
		
	}
	
	public static Sound getSound(String s1 , String s2) {
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		
		Sound sound = null;
		
		try{
			sound = Sound.valueOf(s1);
		}
		catch(Exception e){
			try {
				sound = Sound.valueOf(s2);
			} catch (Exception e1) {

			}
		}
		
		return sound;
	}
	
}

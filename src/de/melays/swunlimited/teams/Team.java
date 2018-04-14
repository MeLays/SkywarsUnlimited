/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.teams;

import org.bukkit.Material;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.map_manager.AdvancedMaterial;

public class Team{
	
	public Color Color;
	public String display;
	public String name;
	public int max;
	
	Main main;
	
	AdvancedMaterial chooseItem;
	
	public Team (Main main , String name , String displayname , Color c , int max){
		this.name = name;
		display = displayname;
		Color = c;
		
		this.main = main;
		
		this.max = max;
		
		chooseItem = new AdvancedMaterial(Material.getMaterial(main.getConfig().getString("default_team_choose_item")) , Color.toByte());
	}
	
}

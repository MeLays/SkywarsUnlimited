package de.melays.bwunlimited.teams;

import org.bukkit.Material;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.AdvancedMaterial;

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

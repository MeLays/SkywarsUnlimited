package de.melays.bwunlimited.challenges;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.map_manager.Cluster;

public class Challenge {
	
	Group from;
	Cluster cluster;
	
	Settings settings;
	
	Main main;
	
	public Challenge (Main main , Group from , Cluster cluster , Settings settings) {
		this.settings = settings;
		this.cluster = cluster;
		this.from = from;
	}
	
}

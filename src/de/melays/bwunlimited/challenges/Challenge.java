package de.melays.bwunlimited.challenges;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.Cluster;

public class Challenge {
	
	Group from;
	Cluster cluster;
	
	Main main;
	
	public Challenge (Main main , Group from , Cluster cluster) {
		this.cluster = cluster;
		this.from = from;
	}
	
}

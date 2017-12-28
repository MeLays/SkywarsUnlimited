/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
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

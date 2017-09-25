package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import de.melays.bwunlimited.Main;

public class ArenaManager {
	
	Main main;
	
	public ArenaManager(Main main) {
		this.main = main;
	}
	
	ArrayList<Arena> running = new ArrayList<Arena>();
	
}

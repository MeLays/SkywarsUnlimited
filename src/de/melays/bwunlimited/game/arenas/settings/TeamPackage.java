package de.melays.bwunlimited.game.arenas.settings;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class TeamPackage {
	
	//Das TeamPackage kann einer Arena mitgegeben werden, damit die Teams schon vor dem Join
	//festgelegt sind. Die Spielerliste muss dabei nichtmehr mitgegeben werden.
	
	HashMap<String , ArrayList<Player>> players;
	
	public TeamPackage (HashMap<String , ArrayList<Player>> players) {
		this.players = players;
	}
	
	public ArrayList<String> getTeams() {
		return new ArrayList<String>(players.keySet());
	}
	
	public ArrayList<Player> getPlayers(String team){
		return players.get(team);
	}
	
	public int size() {
		int size = 0;
		for (String s : players.keySet()) {
			size += players.get(s).size();
		}
		return size;
	}
	
}

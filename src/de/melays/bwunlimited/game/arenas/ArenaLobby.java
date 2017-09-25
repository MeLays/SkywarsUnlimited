package de.melays.bwunlimited.game.arenas;

import org.bukkit.entity.Player;

public class ArenaLobby {
	
	Arena arena;
	
	public ArenaLobby (Arena arena) {
		this.arena = arena;
	}
	
	public void updatePlayer(Player p) {
		p.teleport(arena.main.getLobbyManager().getGameLobbyLocation());
	}
	
}

package de.melays.bwunlimited.game.arenas;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.game.PlayerTools;

public class ArenaLobby {
	
	Arena arena;
	
	public ArenaLobby (Arena arena) {
		this.arena = arena;
	}
	
	public void updatePlayer(Player p) {
		p.teleport(arena.main.getLobbyManager().getGameLobbyLocation());
		PlayerTools.clearInventory(p);
		
		p.setGameMode(GameMode.ADVENTURE);
		
		p.getInventory().setItem(0, arena.main.getItemManager().getItem("lobby.vote"));
	}
	
}

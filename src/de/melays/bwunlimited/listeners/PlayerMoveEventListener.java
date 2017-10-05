package de.melays.bwunlimited.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

public class PlayerMoveEventListener implements Listener{

	Main main;
	
	public PlayerMoveEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.INGAME) {
				if (e.getTo().getY() < arena.relative.getY() - main.getConfig().getInt("death_y_offset")) {
					if (!arena.specs.contains(p))
						arena.deathManager.playerDeath(p);
					else {
						if (arena.getAllPlayers().size() == 0) {
							try {
								p.teleport(arena.cluster.getClusterMeta().getTeamSpawn(arena.teamManager.getTeams().get(0).team.name).toLocation(arena.relative));
							} catch (UnknownTeamException e1) {
								arena.leave(p);
							}
						}
						else {
							p.teleport(arena.getAllPlayers().get(0).getLocation());
						}
					}
				}
				
				if (arena.specs.contains(p)) {
					Location max = arena.relative.clone().add(arena.cluster.x_size , arena.cluster.y_size , arena.cluster.z_size);
					int out = main.getConfig().getInt("game.spectator_out_fly");
					if (!ClusterTools.isInAreaIgnoreHeight(p.getLocation(), arena.relative.clone().add(-out, -out, -out), max.clone().add(out, out, out))) {
						if (arena.getAllPlayers().size() == 0) {
							try {
								p.teleport(arena.cluster.getClusterMeta().getTeamSpawn(arena.teamManager.getTeams().get(0).team.name).toLocation(arena.relative));
							} catch (UnknownTeamException e1) {
								arena.leave(p);
							}
						}
						else {
							p.teleport(arena.getAllPlayers().get(0).getLocation());
						}
					}
				}
			}
			
		}
		else if (!main.canOperateInLobby(p)) {
			if (e.getTo().getY() < main.getConfig().getInt("lobby.respawn_y")) {
				main.getLobbyManager().toLobby(p);
			}
		}
	}
	
}

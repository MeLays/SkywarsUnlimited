package de.melays.bwunlimited.game.arenas;

import org.bukkit.Location;

import de.melays.bwunlimited.teams.error.UnknownTeamException;

public class ArenaTools {
	
	public static ArenaTeam getTeamOfLocation (Arena arena , Location loc) throws UnknownTeamException {
		
		ArenaTeam team = arena.teamManager.getTeams().get(0);
		double distance = loc.distanceSquared(arena.cluster.getClusterMeta().getTeamSpawn(team.team.name).toLocation(arena.relative));
		
		for (ArenaTeam t : arena.teamManager.getTeams()) {
			if (loc.distanceSquared(arena.cluster.getClusterMeta().getTeamSpawn(t.team.name).toLocation(arena.relative)) < distance) {
				distance = loc.distanceSquared(arena.cluster.getClusterMeta().getTeamSpawn(t.team.name).toLocation(arena.relative));
				team = t;
			}
		}
		
		return team;
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.map_manager.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.map_manager.Cluster;
import de.melays.swunlimited.map_manager.ClusterTools;
import de.melays.swunlimited.map_manager.FineRelativeLocation;
import de.melays.swunlimited.map_manager.meta.error.TeamAlreadyAddedException;
import de.melays.swunlimited.teams.Team;
import de.melays.swunlimited.teams.error.UnknownTeamException;

public class ClusterMeta {
	
	Main main;
	Cluster cluster;
	
	ClusterMetaTools tools;
	
	public ClusterMeta(Main main , Cluster cluster) {
		this.main = main;
		this.cluster = cluster;
		tools = new ClusterMetaTools(main);
	}
	
	//getMethods
	
	public ArrayList<Team> getTeams() {
		HashMap<String , Team> teammap = main.getTeamManager().getTeams(); 
		List<String> teams = tools.getClusterFile().getStringList(cluster.name+".meta.teams");
		ArrayList<Team> returnlist = new ArrayList<Team>();
		for (String s : teams) {
			if (teammap.containsKey(s))
				returnlist.add(teammap.get(s));
		}
		return returnlist;
	}
	
	public FineRelativeLocation getTeamSpawn (String name) throws UnknownTeamException {
		if (!main.getTeamManager().getTeams().containsKey(name)) {
			throw new UnknownTeamException();
		}
		FineRelativeLocation returnloc = ClusterTools.getFineRelativeLocation(tools.getClusterFile(), cluster.name+".meta.teamspawns."+name);
		return returnloc;
	}
	
	//Add Locations functions
	
	public void addTeam(String teamname) throws UnknownTeamException, TeamAlreadyAddedException {
		if (!main.getTeamManager().getTeams().containsKey(teamname)) {
			throw new UnknownTeamException();
		}
		List<String> teamlist = tools.getClusterFile().getStringList(cluster.name+".meta.teams");
		if (teamlist.contains(teamname)) {
			throw new TeamAlreadyAddedException();
		}
		teamlist.add(teamname);
		tools.getClusterFile().set(cluster.name+".meta.teams", teamlist);
		tools.saveFile();
	}
	
	public void setTeamSpawn (String teamname , Location loc) throws UnknownTeamException {
		if (!main.getTeamManager().getTeams().containsKey(teamname)) {
			throw new UnknownTeamException();
		}
		ClusterTools.saveFineRelativeLocation(tools.getClusterFile(), cluster.name+".meta.teamspawns."+teamname, new FineRelativeLocation(cluster.min , loc));
		tools.saveFile();
	}
	
	//Remove functions
	
	public void removeTeam(String team) {
		List<String> teamlist = tools.getClusterFile().getStringList(cluster.name+".meta.teams");
		teamlist.remove(team);
		tools.getClusterFile().set(cluster.name+".meta.teams", teamlist);
		tools.saveFile();
	}
	
	public void removeTeamSpawn(String team) {
		tools.getClusterFile().set(cluster.name+".meta.teamspawns."+team, null);
		tools.saveFile();
	}
	
}

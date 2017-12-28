/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.map_manager.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.map_manager.FineRelativeLocation;
import de.melays.bwunlimited.map_manager.meta.error.TeamAlreadyAddedException;
import de.melays.bwunlimited.teams.Team;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

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
	
	public HashMap<Integer , FineRelativeLocation> getShopsMap() {
		return tools.getFineRelativeLocationsCountingMap(cluster.name+".meta.shops");
	}
	
	public ArrayList<FineRelativeLocation> getShops() {
		return tools.getFineRelativeLocationsCounting(cluster.name+".meta.shops");
	}
	
	public ArrayList<ItemSpawner> getItemSpawners() {
		ArrayList<ItemSpawner> returnlist = new ArrayList<ItemSpawner>();
		HashMap<Integer , FineRelativeLocation> map = tools.getFineRelativeLocationsCountingMap(cluster.name+".meta.spawners");
		for (Integer id : map.keySet()) {
			returnlist.add(new ItemSpawner(main , id , map.get(id) , 
					Material.getMaterial(tools.getClusterFile().getString(cluster.name+".meta.spawners."+id+".material")),
					tools.getClusterFile().getInt(cluster.name+".meta.spawners."+id+".ticks"),
					tools.getClusterFile().getString(cluster.name+".meta.spawners."+id+".display")));
		}
		return returnlist;
	}
	
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
	
	public int addShop(Location loc) {
		FineRelativeLocation frl = new FineRelativeLocation(cluster.min , loc);
		int r = tools.addCounting(cluster.name+".meta.shops", frl);
		tools.saveFile();
		return r;
	}
	
	public int addItemSpawner(Location loc , Material m , int ticks , String displayname) {
		FineRelativeLocation frl = new FineRelativeLocation(cluster.min , loc);
		int r = tools.addCounting(cluster.name+".meta.spawners", frl);
		tools.getClusterFile().set(cluster.name+".meta.spawners."+r+".material", m.toString());
		tools.getClusterFile().set(cluster.name+".meta.spawners."+r+".ticks", ticks);
		tools.getClusterFile().set(cluster.name+".meta.spawners."+r+".display", displayname);
		tools.saveFile();
		return r;
	}
	
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
	
	public void removeShop(Integer id) {
		tools.removeFineRelativeLocationCounting(cluster.name+".meta.shops", id);
		tools.saveFile();
	}
	
	public void removeItemSpawner(Integer id) {
		tools.removeFineRelativeLocationCounting(cluster.name+".meta.spawners", id);
		tools.saveFile();
	}
	
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

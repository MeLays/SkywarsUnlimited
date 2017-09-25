package de.melays.bwunlimited.map_manager.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.FineRelativeLocation;
import de.melays.bwunlimited.teams.Team;

public class ClusterMeta {
	
	Main main;
	Cluster cluster;
	
	ClusterMetaTools tools;
	
	public ClusterMeta(Main main , Cluster cluster) {
		this.main = main;
		this.cluster = cluster;
		tools = new ClusterMetaTools(main);
	}
	
	public ArrayList<FineRelativeLocation> getShops() {
		return tools.getFineRelativeLocationsCounting(cluster.name+".meta.shops");
	}
	
	public ArrayList<ItemSpawner> getItemSpawners() {
		ArrayList<ItemSpawner> returnlist = new ArrayList<ItemSpawner>();
		HashMap<Integer , FineRelativeLocation> map = tools.getFineRelativeLocationsCountingMap(cluster.name+".meta.spawners");
		for (Integer id : map.keySet()) {
			returnlist.add(new ItemSpawner(id , map.get(id) , 
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
	
	public int addShop(Location loc) {
		FineRelativeLocation frl = new FineRelativeLocation(cluster.min , loc);
		int r = tools.addCounting(cluster.name+".meta.shops", frl);
		tools.saveFile();
		return r;
	}
	
	public int addItemSpawner(Location loc , Material m , int ticks , String displayname) {
		FineRelativeLocation frl = new FineRelativeLocation(cluster.min , loc);
		int r = tools.addCounting(cluster.name+".meta.shops", frl);
		tools.getClusterFile().set(cluster.name+".meta.shops."+r+".material", m.toString());
		tools.getClusterFile().set(cluster.name+".meta.shops."+r+".ticks", ticks);
		tools.getClusterFile().set(cluster.name+".meta.shops."+r+".display", displayname);
		tools.saveFile();
		return r;
	}
	
	public void addTeam(Location loc , Material m , int ticks , String displayname) {
		FineRelativeLocation frl = new FineRelativeLocation(cluster.min , loc);
		int r = tools.addCounting(cluster.name+".meta.shops", frl);
		tools.getClusterFile().set(cluster.name+".meta.shops."+r+".material", m.toString());
		tools.getClusterFile().set(cluster.name+".meta.shops."+r+".ticks", ticks);
		tools.getClusterFile().set(cluster.name+".meta.shops."+r+".display", displayname);
		tools.saveFile();
	}
	
}

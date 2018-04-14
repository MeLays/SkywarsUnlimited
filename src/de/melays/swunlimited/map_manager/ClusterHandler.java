/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.map_manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.map_manager.events.ClusterHandlerFinishEvent;

public class ClusterHandler {
	
	Cluster cluster;
	Main main;
	
	public ClusterHandler (Main main , Cluster cluster) {
		this.cluster = cluster;
		this.main = main;
	}
	
	Location rel;
	
	boolean generating = false;
	
	int percent = 100; 
	
	public boolean isGenerating () {
		return generating;
	}
	
	public int getPercent() {
		return percent;
	}
	
	public UUID generate(Location rel) {
		if (cluster.generator == ClusterGeneratorType.SPLITTED) {
			int every_ticks = main.getConfig().getInt("generator.splitted.every");
			int panels = main.getConfig().getInt("generator.splitted.blocks");
			return this.generateSplitted(rel, every_ticks, panels);
		}
		else {
			return this.generateComplete(rel);
		}
	}
	
	public UUID generateComplete(Location rel) {
		if (generating) return null;
		generating = true;
		this.rel = rel;
		UUID id = UUID.randomUUID();
		ClusterTools.setBlocksFast(rel, cluster.cluster_list);
		Bukkit.getPluginManager().callEvent(new ClusterHandlerFinishEvent(id , cluster.name));
		generating = false;
		return id;
	}
	
	int splitted_scheduler;
	
	public UUID generateSplitted(Location rel , int every_ticks , int blocks) {
		if (generating) return null;
		generating = true;
		UUID id = UUID.randomUUID();
		int full = cluster.cluster_list.size();
		ArrayList<RelativeLocation> locs = new ArrayList<RelativeLocation>(cluster.cluster_list.keySet());
		splitted_scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
			@Override
			public void run() {
				LinkedHashMap<RelativeLocation , AdvancedMaterial> partial_place = new LinkedHashMap<RelativeLocation , AdvancedMaterial>();
				for (int i = 0 ; i < blocks ; i++) {
					if (locs.size() == 0) {
						finishSplitted(id);
						continue;
					}
					RelativeLocation rem = locs.remove(0);
					partial_place.put(rem, cluster.getClusterMap().get(rem));
					percent = 100 - (int) (((double) locs.size() / (double)full) * 100);
				}
				ClusterTools.setBlocksFast(rel, partial_place);
			}
		}, 0, every_ticks);
		return id;
	}
	
	private void finishSplitted (UUID id) {
		generating = false;
		percent = 100;
		Bukkit.getScheduler().cancelTask(splitted_scheduler);
		Bukkit.getPluginManager().callEvent(new ClusterHandlerFinishEvent(id , cluster.name));
	}
	
	public void remove() {
		if (rel != null) {
			ClusterTools.reverseBlocksFast(rel, cluster);
		}
	}

}

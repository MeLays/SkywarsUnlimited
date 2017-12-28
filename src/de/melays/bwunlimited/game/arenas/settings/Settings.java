/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.game.arenas.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.teams.Team;

public class Settings {
	
	public boolean fixed_teams = false;
	public boolean allow_join = true;
	public boolean allow_spectate = true;
	public boolean stats = true;
	public boolean tnt_auto_ignite = true;
	public boolean tnt_detroy_map = false;
	public boolean cobweb_decay = true;
	public boolean cobweb_decay_bed = false;
	
	public LeaveType lobby_leave = LeaveType.NORMAL;
	
	public int min_players = 2;
	public int max_players = 0;
	public int cobweb_decay_ticks = 140;
	
	public int min_lobby_countdown = 45;
	public int game_end = 36000;
	public int ending_countdown = 10;
	public int spawn_protection = 1;
	
	Arena arena;
	
	Main main;
	
	public Settings(Main main) {
		this.main = main;
		loadDefaults();
	}
	
	public void loadDefaults() {
		ConfigurationSection config = main.getConfig().getConfigurationSection("defaultsettings");
		fixed_teams = config.getBoolean("fixed_teams");
		allow_join = config.getBoolean("allow_join");
		allow_spectate = config.getBoolean("allow_spectate");
		stats = config.getBoolean("stats");
		tnt_auto_ignite = config.getBoolean("tnt_auto_ignite");
		tnt_detroy_map = config.getBoolean("tnt_detroy_map");
		cobweb_decay = config.getBoolean("cobweb_decay");
		cobweb_decay_bed = config.getBoolean("cobweb_decay_bed");
		lobby_leave = LeaveType.valueOf(config.getString("lobby_leave").toUpperCase());
		min_players = config.getInt("min_players");
		max_players = config.getInt("max_players");
		cobweb_decay_ticks = config.getInt("cobweb_decay_ticks");
		min_lobby_countdown = config.getInt("min_lobby_countdown");
		game_end = config.getInt("game_end");
		ending_countdown = config.getInt("ending_countdown");
		spawn_protection = config.getInt("spawn_protection");
	}
	
	public void setArena(Arena arena) {
		this.arena = arena;
		this.min_players = arena.cluster.teams.size();
		this.max_players = 0;
		for (Team t : arena.cluster.teams) {
			this.max_players += t.max;
		}
	}
	
	public Arena getArena () {
		return arena;
	}
	
	public int getMinPlayers () {
		return min_players;
	}
	
	public static Settings getFromSection(ConfigurationSection config , Main main) {
		Settings r = new Settings(main);
		r.fixed_teams = config.getBoolean("fixed_teams");
		r.allow_join = config.getBoolean("allow_join");
		r.allow_spectate = config.getBoolean("allow_spectate");
		r.stats = config.getBoolean("stats");
		r.tnt_auto_ignite = config.getBoolean("tnt_auto_ignite");
		r.tnt_detroy_map = config.getBoolean("tnt_detroy_map");
		r.cobweb_decay = config.getBoolean("cobweb_decay");
		r.cobweb_decay_bed = config.getBoolean("cobweb_decay_bed");
		r.lobby_leave = LeaveType.valueOf(config.getString("lobby_leave").toUpperCase());
		r.min_players = config.getInt("min_players");
		r.max_players = config.getInt("max_players");
		r.cobweb_decay_ticks = config.getInt("cobweb_decay_ticks");
		r.min_lobby_countdown = config.getInt("min_lobby_countdown");
		r.game_end = config.getInt("game_end");
		r.ending_countdown = config.getInt("ending_countdown");
		r.spawn_protection = config.getInt("spawn_protection");
		return r;
	}
	
	public void saveToConfig(FileConfiguration config , String path) {
		config.set(path + "." + "fixed_teams", fixed_teams);
		config.set(path + "." + "allow_join", allow_join);
		config.set(path + "." + "allow_spectate", allow_spectate);
		config.set(path + "." + "stats", stats);
		config.set(path + "." + "tnt_auto_ignite", tnt_auto_ignite);
		config.set(path + "." + "tnt_detroy_map", tnt_detroy_map);
		config.set(path + "." + "cobweb_decay", cobweb_decay);
		config.set(path + "." + "cobweb_decay_bed", cobweb_decay_bed);
		config.set(path + "." + "lobby_leave", lobby_leave.toString());
		config.set(path + "." + "min_players", min_players);
		config.set(path + "." + "max_players", max_players);
		config.set(path + "." + "cobweb_decay_ticks", cobweb_decay_ticks);
		config.set(path + "." + "min_lobby_countdown", min_lobby_countdown);
		config.set(path + "." + "game_end", game_end);
		config.set(path + "." + "ending_countdown", ending_countdown);
		config.set(path + "." + "spawn_protection", spawn_protection);
	}
	
}

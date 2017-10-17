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
	
	public LeaveType lobby_leave = LeaveType.NORMAL;
	
	public int min_players = 2;
	public int max_players = 0;
	
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
		lobby_leave = LeaveType.valueOf(config.getString("lobby_leave").toUpperCase());
		min_players = config.getInt("min_players");
		max_players = config.getInt("max_players");
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
		r.lobby_leave = LeaveType.valueOf(config.getString("lobby_leave").toUpperCase());
		r.min_players = config.getInt("min_players");
		r.max_players = config.getInt("max_players");
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
		config.set(path + "." + "lobby_leave", lobby_leave.toString());
		config.set(path + "." + "min_players", min_players);
		config.set(path + "." + "max_players", max_players);
		config.set(path + "." + "min_lobby_countdown", min_lobby_countdown);
		config.set(path + "." + "game_end", game_end);
		config.set(path + "." + "ending_countdown", ending_countdown);
		config.set(path + "." + "spawn_protection", spawn_protection);
	}
	
}

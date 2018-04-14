/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.game.arenas;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class DeathManager {
	
	public HashMap<Player , Integer> schedulers = new HashMap<Player , Integer>();
	public HashMap<Player , Player> killers = new HashMap<Player , Player>();
	public HashMap<Player , Double> killerslife = new HashMap<Player , Double>();
	public HashMap<Player , Date> lastdeath = new HashMap<Player , Date>();
	Arena arena;
	
	public DeathManager (Arena arena) {
		this.arena = arena;
	}
	
	public void saveHit(Player p , Player hitter) {
		if (arena.main.getArenaManager().searchPlayer(p) == arena.main.getArenaManager().searchPlayer(hitter)) {
			killers.put(p, hitter);
			killerslife.put(p, hitter.getHealth() / 2);
			startScheduler(p);
		}
	}
	
	public void startScheduler(Player p) {
		if (schedulers.containsKey(p)) {
			Bukkit.getScheduler().cancelTask(schedulers.get(p));
			schedulers.remove(p);
		}
		int i = Bukkit.getScheduler().scheduleSyncDelayedTask(arena.main, new Runnable() {

			@Override
			public void run() {
				killers.remove(p);
				killerslife.remove(p);
			}
			
		}, arena.main.getConfig().getInt("death_timeout"));
		schedulers.put(p, i);
	}
	
	public void playerDeath (Player p) {
		if (killers.containsKey(p)) {
			ArenaTeam team = arena.teamManager.findPlayer(p);
			ArenaTeam killer = arena.teamManager.findPlayer(killers.get(p));
			if (team != null & killer != null) {
				String msg = arena.main.getMessageFetcher().getMessage("game.death_killed", true);
				msg = msg.replaceAll("%color%", team.team.Color.toChatColor().toString());
				msg = msg.replaceAll("%player%", p.getName());
				msg = msg.replaceAll("%display%", team.team.display);
				msg = msg.replaceAll("%killer_color%", killer.team.Color.toChatColor().toString());
				msg = msg.replaceAll("%killer%", killers.get(p).getName());
				msg = msg.replaceAll("%killer_display%", killer.team.display);
				
				double lifes = 0;
				String l = "";
				if (killerslife.containsKey(p))
					lifes = killerslife.get(p);
				if (lifes == 10) l = ChatColor.DARK_GREEN +""+ lifes;
				if (lifes < 10 && lifes <= 7.5) l = ChatColor.GREEN +""+ lifes;
				if (lifes < 7.5 && lifes <= 5) l = ChatColor.YELLOW +""+ lifes;
				if (lifes < 5 && lifes <= 2.5) l = ChatColor.GOLD +""+ lifes;
				if (lifes < 2.5 && lifes <= 0.5) l = ChatColor.RED +""+ lifes;
				else {
					l = ChatColor.DARK_RED +""+ lifes;
				}
				
				msg = msg.replaceAll("%lifes%", l);
				
				arena.sendMessage(msg);
				if (!team.bed) {
					arena.main.getStatsManager().addKill(arena, killers.get(p));
					arena.main.getStatsManager().addPoints(arena, p, arena.main.getConfig().getInt("points.kill"));
				}
			} else {
				killers.remove(p);
				playerDeath(p);
			}
		}
		else {
			ArenaTeam team = arena.teamManager.findPlayer(p);
			String msg = arena.main.getMessageFetcher().getMessage("game.death", true);
			msg = msg.replaceAll("%color%", team.team.Color.toChatColor().toString());
			msg = msg.replaceAll("%player%", p.getName());
			msg = msg.replaceAll("%display%", team.team.display);
			arena.sendMessage(msg);
		}
		resetKiller(p);
		lastdeath.put(p, new Date());
		ArenaTeam team = arena.teamManager.findPlayer(p);
		if (team.bed)
			arena.respawnPlayer(p);
		else {
			arena.switchToSpec(p);
		}
	}
	
	public void resetKiller (Player p) {
		killers.remove(p);
		killerslife.remove(p);
	}
	
}

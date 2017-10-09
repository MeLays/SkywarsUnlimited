package de.melays.bwunlimited.friendjoin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.sf.JoinAfterFriendEvent;
import de.melays.sf.manager.FriendManager;

public class FriendJoinHook implements Listener {
	
	Main main;
	
	FriendManager fm;
	
	public FriendJoinHook (Main main , FriendManager fm) {
		this.main = main;
		this.fm = fm;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	public void setArena (Player p) {
		Arena arena = main.getArenaManager().searchPlayer(p);
		if (arena != null) {
			fm.setArena(p, arena.cluster.name+"-"+arena.id);
		}
		else {
			fm.setArena(p, "");
		}
	}
	
	@EventHandler
	public void onJoinAfter (JoinAfterFriendEvent e) {
		Arena arena = main.getArenaManager().searchPlayer(e.getJoin());
		if (arena != null) {
			new BukkitRunnable() {

				@Override
				public void run() {
					arena.addPlayer(e.getPlayer());
				}
				
			}.runTaskLater(main, 1);
		}
	}
	
}

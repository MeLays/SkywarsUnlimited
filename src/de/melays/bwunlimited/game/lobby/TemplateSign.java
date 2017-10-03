package de.melays.bwunlimited.game.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.log.Logger;
import net.md_5.bungee.api.ChatColor;

public class TemplateSign {
	
	Arena arena;
	Main main;
	int id;
	
	Location loc;
	String cluster;
	Settings settings;
	
	int updatetask;
	
	public TemplateSign (Main main , Integer id , Location loc , String cluster , Settings settings) {
		this.main = main;
		this.loc = loc;
		this.id = id;
		this.settings = settings;
		this.cluster = cluster;
		newArena();
		update();
		updatetask = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				
				update();
				
			}
			
		}, 10, 10);
	}
	
	public void cancle() {
		Bukkit.getScheduler().cancelTask(updatetask);
	}
	
	public void newArena() {
		try {
			arena = main.getArenaManager().getArena(main.getArenaManager().startGame(main.getClusterManager().getCluster(cluster), settings));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void interact(Player p) {
		if (arena == null) {
			return;
		}
		Group group = main.getGroupManager().getGroup(p);
		if (main.getGroupManager().getGroup(p).getLeader() != p) {
			p.sendMessage(main.getMessageFetcher().getMessage("group.not_leader", true));
			return;
		}
		if (arena.state == ArenaState.LOBBY) {
			if (group.getMembers().size() >= 1) {
				if (arena.getAll().size() + group.getPlayers().size() <= arena.settings.max_players) {
					for (Player member : group.getPlayers()) {
						arena.addPlayer(member);
					}
				}
				else {
					p.sendMessage(main.getMessageFetcher().getMessage("group.missing_space", true));
					return;
				}
			}
			else {
				arena.addPlayer(p);
			}
		}
		else {
			newArena();
		}
		update();
	}
	
	public void update() {
		if (arena != null) {
			if (arena.state != ArenaState.LOBBY) {
				newArena();
			}
		}
		if (loc.getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) loc.getBlock().getState();
			if (arena == null) {
				sign.setLine(0, "");
				sign.setLine(1, "Cluster");
				sign.setLine(2, "not ready");
				sign.setLine(3, "");
			}
			else {
				if (arena.getAll().size() == 0) {
					sign.setLine(0, main.c(main.getSettingsManager().getFile().getString("sign.template.empty.1")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(1, main.c(main.getSettingsManager().getFile().getString("sign.template.empty.2")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(2, main.c(main.getSettingsManager().getFile().getString("sign.template.empty.3")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(3, main.c(main.getSettingsManager().getFile().getString("sign.template.empty.4")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
				}
				else if (arena.getAll().size() >= arena.settings.max_players) {
					sign.setLine(0, main.c(main.getSettingsManager().getFile().getString("sign.template.full.1")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(1, main.c(main.getSettingsManager().getFile().getString("sign.template.full.2")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(2, main.c(main.getSettingsManager().getFile().getString("sign.template.full.3")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(3, main.c(main.getSettingsManager().getFile().getString("sign.template.full.4")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
				}
				else {
					sign.setLine(0, main.c(main.getSettingsManager().getFile().getString("sign.template.default.1")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(1, main.c(main.getSettingsManager().getFile().getString("sign.template.default.2")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(2, main.c(main.getSettingsManager().getFile().getString("sign.template.default.3")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(3, main.c(main.getSettingsManager().getFile().getString("sign.template.default.4")
							.replaceAll("%cluster%", arena.cluster.getDisplayName())
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
				}
			}
			sign.update();
		}
		else {
			Logger.log(main.console_prefix + ChatColor.RED + "Block at sign-"+id+"'s location is not an sign!");
		}
	}
	
}

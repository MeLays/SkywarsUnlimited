package de.melays.bwunlimited.game.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
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
	
	public TemplateSign (Main main , Integer id , Location loc , String cluster , Settings settings) {
		this.main = main;
		this.loc = loc;
		this.id = id;
		this.settings = settings;
		this.cluster = cluster;
		newArena();
		update();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				
				update();
				
			}
			
		}, 10, 10);
	}
	
	public void newArena() {
		try {
			arena = main.getArenaManager().getArena(main.getArenaManager().startGame(main.getClusterManager().getCluster(cluster), settings));
		} catch (Exception e) {

		}
	}
	
	public void interact(Player p) {
		if (arena.state == ArenaState.LOBBY) {
			arena.addPlayer(p);
		}
		else {
			newArena();
		}
		update();
	}
	
	public void update() {
		if (arena.state != ArenaState.LOBBY) {
			newArena();
		}
		if (loc.getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) loc.getBlock().getState();
			if (arena == null) {
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
			}
			else {
				if (arena.getAll().size() >= arena.settings.max_players) {
					sign.setLine(0, main.c(main.getSettingsManager().getFile().getString("sign.template.full.1")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(1, main.c(main.getSettingsManager().getFile().getString("sign.template.full.2")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(2, main.c(main.getSettingsManager().getFile().getString("sign.template.full.3")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(3, main.c(main.getSettingsManager().getFile().getString("sign.template.full.4")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
				}
				else {
					sign.setLine(0, main.c(main.getSettingsManager().getFile().getString("sign.template.default.1")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(1, main.c(main.getSettingsManager().getFile().getString("sign.template.default.2")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(2, main.c(main.getSettingsManager().getFile().getString("sign.template.default.3")
							.replaceAll("%cluster%", arena.cluster.name)
							.replaceAll("%min%", arena.settings.min_players+"")
							.replaceAll("%id%", arena.id+"")
							.replaceAll("%current%", arena.getAllPlayers().size()+"")
							.replaceAll("%max%", arena.settings.max_players+"")));
					sign.setLine(3, main.c(main.getSettingsManager().getFile().getString("sign.template.default.4")
							.replaceAll("%cluster%", arena.cluster.name)
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

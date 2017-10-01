package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.game.PlayerTools;
import de.melays.bwunlimited.game.arenas.state.ArenaLobbyState;

public class ArenaLobby {
	
	Arena arena;
	ArenaLobbyState state = ArenaLobbyState.NORMAL;
	
	public ArenaLobby (Arena arena) {
		this.arena = arena;
		startLoop();
	}
	
	public void updatePlayer(Player p) {
		p.teleport(arena.main.getLobbyManager().getGameLobbyLocation());
		PlayerTools.resetPlayer(p);
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().setItem(arena.main.getConfig().getInt("game.teamselector_slot"), arena.main.getItemManager().getItem("gamelobby.teamselector"));
		p.getInventory().setItem(arena.main.getConfig().getInt("game.leaveitem_slot"), arena.main.getItemManager().getItem("gamelobby.leaveitem"));
	}

	@SuppressWarnings("deprecation")
	public void openTeamMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, ((arena.teamManager.getTeams().size() / 9) + 1) * 9, arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.name")));
		ArrayList<ItemStack> teamitems = new ArrayList<ItemStack>();
		for (ArenaTeam team : arena.teamManager.getTeams()) {
			Material material = Material.getMaterial(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.type"));
			if (material == null) {
				try {
					material = Material.getMaterial(Integer.parseInt(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.type")));
					if (material == null) {
						material = Material.PAPER;
					}
				} catch (NumberFormatException e) {
					material = Material.PAPER;
				}
			}
			int size = 1;
			if (team.players.size() > 1) {
				size = team.players.size();
			}
			ItemStack stack = new ItemStack(material , size , team.team.Color.toByte());
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.display")
					.replaceAll("%color%", team.team.Color.toChatColor().toString()).replaceAll("%displayname%", team.team.display)
					.replaceAll("%current%", team.players.size() + "")
					.replaceAll("%max%", team.team.max + "")));
			ArrayList<String> lore = new ArrayList<String>();
			if (!team.hasPlayer(p)) {
				lore.add(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.lore").replaceAll("%color%", team.team.Color.toChatColor().toString())));
			}
			else {
				lore.add(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.lore_selected").replaceAll("%color%", team.team.Color.toChatColor().toString())));
			}
			for (Player player : team.players) {
				lore.add(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.playerlist").replaceAll("%color%", team.team.Color.toChatColor().toString()).replaceAll("%player%", player.getName())));
			}
			if (team.players.size() == 0) {
				lore.add(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.lore_empty").replaceAll("%color%", team.team.Color.toChatColor().toString())));
			}
			meta.setLore(lore);
			stack.setItemMeta(meta);
			teamitems.add(stack);
		}
		if (teamitems.size() == 4) {
			inv.setItem(1, teamitems.get(0));
			inv.setItem(3, teamitems.get(1));
			inv.setItem(5, teamitems.get(2));
			inv.setItem(7, teamitems.get(3));
		}
		else if (teamitems.size() == 2) {
			inv.setItem(2, teamitems.get(0));
			inv.setItem(6, teamitems.get(1));
		}
		else {
			for (ItemStack s : teamitems) {
				inv.addItem(s);
			}
		}
		p.openInventory(inv);
	}
	
	int loop;
	int countdown;
	
	public void startLoop() {
		countdown = arena.settings.min_lobby_countdown;
		loop = Bukkit.getScheduler().scheduleSyncRepeatingTask(arena.main, new Runnable() {
			@Override
			public void run() {
				if (arena.settings.min_players > arena.getAllPlayers().size()) {
					if (countdown != arena.settings.min_lobby_countdown)
						arena.sendMessage(arena.main.getMessageFetcher().getMessage("game.players_missing", true));
					countdown = arena.settings.min_lobby_countdown;
					setGlobalLevel(0);
					state = ArenaLobbyState.NORMAL;
					return;
				}
				else if (countdown == 0) {
					try {
						if (arena.clusterHandler.isGenerating()) {
							state = ArenaLobbyState.GENERATING;
							setGlobalLevel(arena.clusterHandler.getPercent());
						}
						else {
							stopLobby();
						}
					} catch (Exception e) {
						
					}
				}
				else if (state == ArenaLobbyState.NORMAL) {
					if (countdown == arena.settings.min_lobby_countdown || countdown % 30 == 0 || (countdown % 5 == 0 && countdown <= 15) || countdown <= 5) {
						arena.sendMessage(arena.main.getMessageFetcher().getMessage("game.countdown.start", true).replaceAll("%countdown%", countdown + ""));
						if (countdown == 3) {
							sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.title_3")) , arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.sub_title").replaceAll("%cluster_display%", arena.cluster.name)));
						}
						else if (countdown == 2) {
							sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.title_2")) , arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.sub_title").replaceAll("%cluster_display%", arena.cluster.name)));
						}
						else if (countdown == 1) {
							sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.title_1")) , arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.sub_title").replaceAll("%cluster_display%", arena.cluster.name)));
						}
					}
					setGlobalLevel(countdown);
					countdown -= 1;
					return;
				}
			}
		}, 0, 20);
	}
	
	public void setGlobalLevel(int level) {
		for (Player p : arena.getAll()) {
			p.setExp(0);
			p.setLevel(level);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void sendTitle(String title , String subtitle) {
		for (Player p : arena.getAll()) {
			p.sendTitle(title, subtitle);
		}
	}
	
	public void stopLobby () {
		sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.title_0")) , arena.main.c(arena.main.getSettingsManager().getFile().getString("gamelobby.title.sub_title").replaceAll("%cluster_display%", arena.cluster.name)));
		state = ArenaLobbyState.ENDED;
		Bukkit.getScheduler().cancelTask(loop);
		arena.callLobbyEnd();
	}
}

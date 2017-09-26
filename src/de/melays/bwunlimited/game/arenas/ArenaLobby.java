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

public class ArenaLobby {
	
	Arena arena;
	
	public ArenaLobby (Arena arena) {
		this.arena = arena;
	}
	
	public void updatePlayer(Player p) {
		p.teleport(arena.main.getLobbyManager().getGameLobbyLocation());
		PlayerTools.clearInventory(p);
		
		p.setGameMode(GameMode.ADVENTURE);
		
		p.getInventory().setItem(0, arena.main.getItemManager().getItem("gamelobby.teamselector"));
	}
//	gamelobby:
//		  inventory:
//		    teamselector:
//		      name: "&6Team selector"
//		      type: STAINED_GLASS_PANE
//		      display: "%color%%displayname%"
//		      lore: "%color%Players:"
//		      lore_selected: "%color%>> &lPlayers %color%<<:"
//		      playerlist: "   %color%%player%"
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
	
}

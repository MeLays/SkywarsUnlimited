package de.melays.bwunlimited.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.game.lobby.TemplateSign;

public class PlayerInteractEventListener implements Listener{

	Main main;
	
	public PlayerInteractEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			try {
				//Arena relevant Event stuff
				if (arena.state == ArenaState.LOBBY) {
					e.setCancelled(true);
					//Item Interact Check
					if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK 
							|| e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (main.getItemManager().isItem("gamelobby.teamselector", e.getItem())) {
							//Teamchooser
							arena.arenaLobby.openTeamMenu(p);
						}
						else if (main.getItemManager().isItem("gamelobby.leaveitem", e.getItem())) {
							arena.leave(p);
						}
					}
				}
				else if (arena.specs.contains(p)) {
					e.setCancelled(true);
				}
				else if (arena.state == ArenaState.INGAME) {
					if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (e.getClickedBlock().getType() == Material.BED_BLOCK && !p.isSneaking()) {
							e.setCancelled(true);
						}
					}
				}
				if (arena.state == ArenaState.ENDING) {
					if (main.getItemManager().isItem("gamelobby.leaveitem", e.getItem())) {
						arena.leave(p);
					}
				}
			} catch (Exception e1) {

			}
		}
		else {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (e.getClickedBlock().getState() instanceof Sign) {
					TemplateSign sign = main.getTemplateSignManager().getSign(e.getClickedBlock().getLocation());
					if (sign != null) {
						sign.interact(p);
					}
				}
			}
		}
		if (!main.getArenaManager().isInGame(p) && !main.canOperateInLobby(p)) {
			if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
				if (main.getItemManager().isItem("lobby.challenger", e.getItem())) {
					if (main.getLobbyManager().getSuitableArenas(main.getGroupManager().getGroup(p)).size() == 0) {
						p.sendMessage(main.getMessageFetcher().getMessage("group.no_suitable_maps", true));
					}
					else {
						main.getArenaSelector().openArenaSelector(p, 1);
					}
				}
				else if (main.getItemManager().isItem("lobby.gamelist", e.getItem())) {
					main.getRunningGames().openOverview(p);
				}
				else if (main.getItemManager().isItem("lobby.leave", e.getItem())) {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(main.getConfig().getString("lobby.leave.server"));
					p.sendPluginMessage(main, "BungeeCord", out.toByteArray());
				}
			}
		}
		if (e.getAction() == Action.PHYSICAL) {
			if (e.hasBlock()) {
		        Block soilBlock = e.getClickedBlock();
	            if (soilBlock.getType() == Material.SOIL) {
	                e.setCancelled(true);
	            }
			}
        }
	}
	
}

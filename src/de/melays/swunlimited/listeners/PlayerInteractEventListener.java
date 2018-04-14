/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.SoundDebugger;
import de.melays.swunlimited.game.arenas.Arena;
import de.melays.swunlimited.game.arenas.state.ArenaState;
import de.melays.swunlimited.game.lobby.TemplateSign;
import de.melays.swunlimited.map_manager.ClusterTools;

public class PlayerInteractEventListener implements Listener{

	Main main;
	
	public PlayerInteractEventListener (Main main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
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
						if (e.getClickedBlock().getType() == Material.CHEST) {
							arena.lootManager.openChest(p, e.getClickedBlock().getLocation());
						}
					}
					if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK 
							|| e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						
						Material rescue = Material.getMaterial(arena.main.getConfig().getString("game.special_items.RescuePlatform.item"));
						if (rescue != null && e.getPlayer().getItemInHand() != null) {
							if (e.getPlayer().getItemInHand().getType() == rescue) {
								Location max = arena.relative.clone().add(arena.cluster.x_size , arena.cluster.y_size , arena.cluster.z_size);
								if (ClusterTools.isInAreaIgnoreHeight(p.getLocation(), arena.relative, max)) {
									if (p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount() > 1) {
										ItemStack set = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).clone();
										set.setAmount(set.getAmount() - 1);
										p.getInventory().setItem(p.getInventory().getHeldItemSlot(), set);
									}
									else {
										ItemStack set = new ItemStack(Material.AIR);
										p.getInventory().setItem(p.getInventory().getHeldItemSlot(), set);
									}
									
									int r = main.getConfig().getInt("game.special_items.RescuePlatform.radius");
									Location loc = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN , 2).getLocation();
									
								    int cx = loc.getBlockX();
								    int cy = loc.getBlockY();
								    int cz = loc.getBlockZ();
								    World w = loc.getWorld();
								    int rSquared = r * r;
								    for (int x = cx - r; x <= cx +r; x++) {
								        for (int z = cz - r; z <= cz +r; z++) {
								            if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
								            	Block b = w.getBlockAt(x, cy, z);
								            	if (b.getType() == Material.AIR) {
									                b.setType(Material.getMaterial(main.getConfig().getString("game.special_items.RescuePlatform.set").toUpperCase()));
									                b.setData(arena.teamManager.findPlayer(p).team.Color.toByte());
									                arena.blockManager.placed_blocks.add(b.getLocation());
								            	}
								            }
								        }
								    }
					                Firework fw = (Firework) p.getWorld().spawn(p.getLocation(), Firework.class);
					                FireworkMeta fm = fw.getFireworkMeta();
					                fm.addEffect(FireworkEffect.builder()
					                        .flicker(true)
					                        .trail(true)
					                        .with(Type.BALL)
					                        .withColor(arena.teamManager.findPlayer(p).team.Color.toBukkitColor())
					                        .withFade(arena.teamManager.findPlayer(p).team.Color.toBukkitColor())
					                        .build());
							        fm.setPower(1);
							        fw.setFireworkMeta(fm);
								}
							}
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
				if (main.getItemManager().isItem("lobby.gamelist", e.getItem())) {
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

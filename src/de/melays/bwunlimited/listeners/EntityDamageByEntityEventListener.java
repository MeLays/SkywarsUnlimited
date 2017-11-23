package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Challenge;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.ArenaTeam;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

public class EntityDamageByEntityEventListener implements Listener{

	Main main;
	
	public EntityDamageByEntityEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		
		if (e.getEntity().getType() == EntityType.ITEM_FRAME){
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getEntity();
				if (!main.canOperateInLobby(p)) {
					e.setCancelled(true);
				}
			}
			else {
	            e.setCancelled(true);
			}
        }
		
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				
				ArenaTeam damager_team = null;
				ArenaTeam team = null;
				
				try {
					damager_team = main.getArenaManager().searchPlayer(damager).teamManager.findPlayer(damager);
					team = main.getArenaManager().searchPlayer(p).teamManager.findPlayer(p);
				} catch (Exception e1) {

				}
				
				if (team == damager_team) {
					e.setCancelled(true);
				}
				
				//Arena relevant Event stuff
				if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
					e.setCancelled(true);
				}
				else if (arena.specs.contains(damager)) {
					e.setCancelled(true);
				}
				else if (arena.state == ArenaState.INGAME) {
					arena.deathManager.saveHit(p, damager);
					if (p.getHealth() - e.getDamage() <= 0) {
						arena.deathManager.playerDeath(p);
					}
				}
				
				
				//Spectator collision
			    Entity entityDamager = e.getDamager();
			    Entity entityDamaged = e.getEntity();
			   
			    if(entityDamager instanceof Arrow) {
			        if(entityDamaged instanceof Player && ((Arrow) entityDamager).getShooter() instanceof Player) {
			            Arrow arrow = (Arrow) entityDamager;
			 
			            Vector velocity = arrow.getVelocity();
			 
			            Player shooter = (Player) arrow.getShooter();
			            Player damaged = (Player) entityDamaged;
			 
			            if(arena.specs.contains(p)) {
			                damaged.teleport(entityDamaged.getLocation().add(0, 5, 0));
			                damaged.setFlying(true);
			               
			                Arrow newArrow = shooter.launchProjectile(Arrow.class);
			                newArrow.setShooter(shooter);
			                newArrow.setVelocity(velocity);
			                newArrow.setBounce(false);
			               
			                e.setCancelled(true);
			                arrow.remove();
			            }
			        }
			    }
				
			}
			else if (!main.canOperateInLobby(p) && damager.getItemInHand() != null) {
				if (main.getItemManager().isItem("lobby.challenger", damager.getItemInHand())) {
					Group g = main.getGroupManager().getGroup(p);
					Group inviter = main.getGroupManager().getGroup(damager);
					if (!inviter.hasChallenge(g)) {
						if (main.getLobbyManager().isCooldowned(damager)){
							damager.sendMessage(main.getMessageFetcher().getMessage("group.challenge.cooldown", true));
						}
						else {
							e.setCancelled(true);
							main.getArenaSelector().setupPlayer(damager);
							g.challenge(damager, p, new Challenge(main , main.getGroupManager().getGroup(damager) , main.getArenaSelector().selected.get(damager)));
							main.getLobbyManager().setChallengeCooldown(damager, main.getConfig().getInt("lobby.challenger.cooldown"));
						}
					}
					else if (g.getLeader() == p){
						inviter.acceptChallenge(inviter.getChallenge(g));
					}
				}
				else if (damager.getInventory().getHeldItemSlot() == main.getConfig().getInt("lobby.group.slot")) {
					Group group = main.getGroupManager().getGroup(damager);
					if (damager != group.getLeader()) {
						damager.sendMessage(main.getMessageFetcher().getMessage("group.not_leader", true));
						SoundDebugger.playSound(damager, "DIG_WOOL", "BLOCK_CLOTH_HIT");
						return;
					}
					if (group.getPlayers().contains(p)) {
						damager.sendMessage(main.getMessageFetcher().getMessage("group.already_in_group", true).replaceAll("%player%", p.getName()));
						SoundDebugger.playSound(damager, "DIG_WOOL", "BLOCK_CLOTH_HIT");
						return;
					}
					if (group.invite(p)) {
						damager.sendMessage(main.getMessageFetcher().getMessage("group.invite_sender", true).replaceAll("%player%", p.getName()));
						SoundDebugger.playSound(damager, "CLICK", "BLOCK_LEVER_CLICK");
						SoundDebugger.playSound(p, "CLICK", "BLOCK_LEVER_CLICK");
					}
					else {
						damager.sendMessage(main.getMessageFetcher().getMessage("group.already_invited", true).replaceAll("%player%", p.getName()));
						SoundDebugger.playSound(damager, "DIG_WOOL", "BLOCK_CLOTH_HIT");
					}
				}
			}
			
		}
		else if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			//Player damager = (Player) e.getDamager();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				
				//Arena relevant Event stuff
				if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
					e.setCancelled(true);
				}
			}
			else if (!main.canOperateInLobby(p)) {
				e.setCancelled(true);
			}
		}
		else if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (!main.canOperateInLobby(p)) {
				e.setCancelled(true);
			}
		}
		else {
			
		}
	}
	
}

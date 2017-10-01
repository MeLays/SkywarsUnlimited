package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import de.melays.bwunlimited.Main;
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
			else if (!main.canOperateInLobby(p)) {
				e.setCancelled(true);
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
		else {
			
		}
	}
	
}

package de.melays.bwunlimited;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.bwunlimited.log.Logger;

public class BungeeServerFetcher implements Listener, PluginMessageListener {
	
	String servername = "";
	
	Main main;
	public BungeeServerFetcher(Main main){
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
		main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
		main.getServer().getMessenger().registerIncomingPluginChannel(main, "BungeeCord", this);
		if (Bukkit.getOnlinePlayers().size() != 0) {
			sendNameRequest((Player) Bukkit.getOnlinePlayers().toArray()[0]);
		}
	}
	
	@EventHandler
	public void onJoin (PlayerJoinEvent e){
		if (servername.equals("")){
			sendNameRequest(e.getPlayer());
		}
	}
	
	public void sendNameRequest(Player p){
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			public void run() {
				 ByteArrayDataOutput out = ByteStreams.newDataOutput();
				 out.writeUTF("GetServer");
				 p.sendPluginMessage(main, "BungeeCord", out.toByteArray());
				 System.out.println("Identifying current Server ...");
			}
		}, 2L);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		 if (!channel.equals("BungeeCord")) {
		      return;
		 }
		 ByteArrayDataInput in = ByteStreams.newDataInput(message);
		 String subchannel = in.readUTF();
		 if (subchannel.equals("GetServer")) {
			 servername = in.readUTF();
			 Logger.log(main.console_prefix + "This Server is called " + servername);
		 }
	}
	
	public String getServerName(){
		return servername;
	}
	
}

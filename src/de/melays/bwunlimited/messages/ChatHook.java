package de.melays.bwunlimited.messages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.log.Logger;
import net.milkbowl.vault.chat.Chat;

public class ChatHook {
	
	public static Chat chat = null;
	
	Main main;
	
	public ChatHook (Main main) {
		this.main = main;
		load();
	}
	
	boolean vault = false;
	
	public void load() {
		if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			vault = setupChat();
			Logger.log(main.console_prefix + "Vault chat hooked. Using Vault prefixes and suffixes!");
		}
		else {
			Logger.log(main.console_prefix + "No Vault found :C");
		}
	}
	
    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }
    
    public String getPrefix(Player p) {
    	if (!vault) {
    		return "";
    	}
    	return chat.getPlayerPrefix(p);
    }
    
    public String getSuffix(Player p) {
    	if (!vault) {
    		return "";
    	}
    	return chat.getPlayerSuffix(p);
    }
	
}

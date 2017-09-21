package de.melays.bwunlimited.map_manager.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClusterHandlerFinishEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    private String cluster;
    
    private UUID handler_id;
    
    public ClusterHandlerFinishEvent(UUID handler_id , String cluster_name) {
    	cluster = cluster_name;
    	this.handler_id = handler_id;
    }

    public String getClusterName() {
        return cluster;
    }
    
    public UUID getHandlerID() {
        return handler_id;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
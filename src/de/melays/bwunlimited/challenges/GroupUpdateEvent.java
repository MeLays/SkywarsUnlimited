package de.melays.bwunlimited.challenges;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GroupUpdateEvent extends Event{
	
    private static final HandlerList handlers = new HandlerList();
    
    private Group group;
    
    public GroupUpdateEvent(Group g) {
    	group = g;
    }

    public Group getGroup() {
        return group;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

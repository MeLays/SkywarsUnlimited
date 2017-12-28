/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
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

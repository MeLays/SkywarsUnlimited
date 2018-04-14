/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.map_manager.error;

public class ClusterAvailabilityException extends Exception{

	  /**
	 * 
	 */
	private static final long serialVersionUID = -2137808453231725668L;
	
	public ClusterAvailabilityException() { super(); }
	  public ClusterAvailabilityException(String message) { super(message); }
	  public ClusterAvailabilityException(String message, Throwable cause) { super(message, cause); }
	  public ClusterAvailabilityException(Throwable cause) { super(cause); }
	
}

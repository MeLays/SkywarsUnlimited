/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.map_manager.error;

public class ClusterAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public ClusterAlreadyExistsException() { super(); }
	  public ClusterAlreadyExistsException(String message) { super(message); }
	  public ClusterAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
	  public ClusterAlreadyExistsException(Throwable cause) { super(cause); }
	
}

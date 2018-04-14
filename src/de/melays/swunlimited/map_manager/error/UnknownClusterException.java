/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.map_manager.error;

public class UnknownClusterException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public UnknownClusterException() { super(); }
	  public UnknownClusterException(String message) { super(message); }
	  public UnknownClusterException(String message, Throwable cause) { super(message, cause); }
	  public UnknownClusterException(Throwable cause) { super(cause); }
	
}

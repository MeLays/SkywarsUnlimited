/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.map_manager.meta.error;

public class TeamAlreadyAddedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public TeamAlreadyAddedException() { super(); }
	  public TeamAlreadyAddedException(String message) { super(message); }
	  public TeamAlreadyAddedException(String message, Throwable cause) { super(message, cause); }
	  public TeamAlreadyAddedException(Throwable cause) { super(cause); }
	
}

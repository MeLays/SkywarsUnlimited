/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.teams.error;

public class TeamAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public TeamAlreadyExistsException() { super(); }
	  public TeamAlreadyExistsException(String message) { super(message); }
	  public TeamAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
	  public TeamAlreadyExistsException(Throwable cause) { super(cause); }
	
}

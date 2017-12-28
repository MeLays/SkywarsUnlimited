/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.teams.error;

public class UnknownTeamException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public UnknownTeamException() { super(); }
	  public UnknownTeamException(String message) { super(message); }
	  public UnknownTeamException(String message, Throwable cause) { super(message, cause); }
	  public UnknownTeamException(Throwable cause) { super(cause); }
	
}

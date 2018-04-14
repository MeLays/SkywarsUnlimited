/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.map_manager.error;

public class WrongWorldException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public WrongWorldException() { super(); }
	  public WrongWorldException(String message) { super(message); }
	  public WrongWorldException(String message, Throwable cause) { super(message, cause); }
	  public WrongWorldException(Throwable cause) { super(cause); }
	
}

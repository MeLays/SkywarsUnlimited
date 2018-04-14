/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.error;

public class InvalidNameException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public InvalidNameException() { super(); }
	  public InvalidNameException(String message) { super(message); }
	  public InvalidNameException(String message, Throwable cause) { super(message, cause); }
	  public InvalidNameException(Throwable cause) { super(cause); }
	
}

package de.melays.bwunlimited.error;

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

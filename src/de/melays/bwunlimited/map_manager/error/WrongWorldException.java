package de.melays.bwunlimited.map_manager.error;

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

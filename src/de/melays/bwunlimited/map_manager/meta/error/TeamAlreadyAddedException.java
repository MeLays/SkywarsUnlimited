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

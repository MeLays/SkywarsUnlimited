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

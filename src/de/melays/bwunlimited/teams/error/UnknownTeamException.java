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

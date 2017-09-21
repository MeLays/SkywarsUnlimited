package de.melays.bwunlimited.map_manager.error;

public class UnknownClusterException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public UnknownClusterException() { super(); }
	  public UnknownClusterException(String message) { super(message); }
	  public UnknownClusterException(String message, Throwable cause) { super(message, cause); }
	  public UnknownClusterException(Throwable cause) { super(cause); }
	
}

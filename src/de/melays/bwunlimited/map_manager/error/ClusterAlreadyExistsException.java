package de.melays.bwunlimited.map_manager.error;

public class ClusterAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014905180924688852L;
	
	public ClusterAlreadyExistsException() { super(); }
	  public ClusterAlreadyExistsException(String message) { super(message); }
	  public ClusterAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
	  public ClusterAlreadyExistsException(Throwable cause) { super(cause); }
	
}

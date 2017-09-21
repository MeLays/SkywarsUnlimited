package de.melays.bwunlimited.map_manager.error;

public class ClusterAvailabilityException extends Exception{

	  /**
	 * 
	 */
	private static final long serialVersionUID = -2137808453231725668L;
	
	public ClusterAvailabilityException() { super(); }
	  public ClusterAvailabilityException(String message) { super(message); }
	  public ClusterAvailabilityException(String message, Throwable cause) { super(message, cause); }
	  public ClusterAvailabilityException(Throwable cause) { super(cause); }
	
}

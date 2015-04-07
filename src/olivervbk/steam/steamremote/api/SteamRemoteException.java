package olivervbk.steam.steamremote.api;

public class SteamRemoteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SteamRemoteException(String error) {
		setMessage(error);
	}
	
	private String message;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}

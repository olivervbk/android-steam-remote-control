package olivervbk.steam.steamremote.api;

public class RemoteApi {
	private static IRemoteManager instance;

	/**
	 * @return the instance
	 */
	public static IRemoteManager getInstance() {
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(IRemoteManager instance) {
		RemoteApi.instance = instance;
	}

}

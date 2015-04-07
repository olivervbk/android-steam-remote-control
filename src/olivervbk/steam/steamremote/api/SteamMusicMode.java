package olivervbk.steam.steamremote.api;

import org.json.JSONException;
import org.json.JSONObject;

public class SteamMusicMode {
	public SteamMusicMode(final boolean looped, final boolean shuffled){
		super();
		setLooped(looped);
		setShuffled(shuffled);
	}
	
	public SteamMusicMode(JSONObject data) throws JSONException{
		super();
		
		final int loopedInt = data.getInt("looped");
		final boolean looped = (loopedInt == 1)?true:false;
		setLooped(looped);
		
		final int shuffledInt = data.getInt("shuffled");
		final boolean shuffled = (shuffledInt == 1)?true:false;
		setShuffled(shuffled);
	}
	
	private boolean looped;
	/**
	 * @return the looped
	 */
	public boolean isLooped() {
		return looped;
	}
	/**
	 * @param looped the looped to set
	 */
	protected void setLooped(boolean looped) {
		this.looped = looped;
	}
	/**
	 * @return the shuffled
	 */
	public boolean isShuffled() {
		return shuffled;
	}
	/**
	 * @param shuffled the shuffled to set
	 */
	protected void setShuffled(boolean shuffled) {
		this.shuffled = shuffled;
	}

	private boolean shuffled;
}

package olivervbk.steam.steamremote.api;

import org.json.JSONException;
import org.json.JSONObject;

public class SteamMusicInfo {
	public SteamMusicInfo(JSONObject data) throws JSONException{
		super();
		
		final JSONObject playbackData = data.getJSONObject("playback");
		final String status = playbackData.getString("status");
		setStatus(status);
		
		final int loopedInt = playbackData.getInt("looped");
		boolean looped = (loopedInt == 1)?true: false;
		setLooped(looped);
		
		final int shuffledInt = playbackData.getInt("shuffled");
		boolean shuffled = (shuffledInt == 1)?true: false;
		setShuffled(shuffled);
		
		final double volume = playbackData.getDouble("volume");
		setVolume(volume);

		final int queueCount = playbackData.getInt("queue_count");
		setQueueCount(queueCount);
		
		final JSONObject current = data.optJSONObject("current");
		if(current != null){
			final String artist = current.getString("artist");
			setCurrentArtist(artist);
			
			final String album = current.getString("album");
			setCurrentAlbum(album);
			
			final String track = current.getString("track");
			setCurrentTrack(track);
		}
	}
	
	public SteamMusicMode getMode(){
		final Boolean looped = getLooped();
		final Boolean shuffled = getShuffled();
		return new SteamMusicMode(looped,shuffled);
	}
	
	public SteamMusicInfo(){
		super();
	}
	
	private String status;
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the looped
	 */
	public Boolean getLooped() {
		return looped;
	}
	/**
	 * @param looped the looped to set
	 */
	public void setLooped(Boolean looped) {
		this.looped = looped;
	}
	/**
	 * @return the shuffled
	 */
	public Boolean getShuffled() {
		return shuffled;
	}
	/**
	 * @param shuffled the shuffled to set
	 */
	public void setShuffled(Boolean shuffled) {
		this.shuffled = shuffled;
	}
	/**
	 * @return the volume
	 */
	public Double getVolume() {
		return volume;
	}
	/**
	 * @param volume the volume to set
	 */
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	/**
	 * @return the queueCount
	 */
	public Integer getQueueCount() {
		return queueCount;
	}
	/**
	 * @param queueCount the queueCount to set
	 */
	public void setQueueCount(Integer queueCount) {
		this.queueCount = queueCount;
	}
	/**
	 * @return the currentArtist
	 */
	public String getCurrentArtist() {
		return currentArtist;
	}
	/**
	 * @param currentArtist the currentArtist to set
	 */
	public void setCurrentArtist(String currentArtist) {
		this.currentArtist = currentArtist;
	}
	/**
	 * @return the currentAlbum
	 */
	public String getCurrentAlbum() {
		return currentAlbum;
	}
	/**
	 * @param currentAlbum the currentAlbum to set
	 */
	public void setCurrentAlbum(String currentAlbum) {
		this.currentAlbum = currentAlbum;
	}
	/**
	 * @return the currentTrack
	 */
	public String getCurrentTrack() {
		return currentTrack;
	}
	/**
	 * @param currentTrack the currentTrack to set
	 */
	public void setCurrentTrack(String currentTrack) {
		this.currentTrack = currentTrack;
	}

	private Boolean looped;
	private Boolean shuffled;
	private Double volume;
	private Integer queueCount;
	
	private String currentArtist;
	private String currentAlbum;
	private String currentTrack;
}


package olivervbk.steam.steamremote.api;

import org.json.JSONException;
import org.json.JSONObject;

public class SteamGame implements Comparable<SteamGame>{
	public SteamGame(){
		super();
	}
	
	public SteamGame(int steamKey, JSONObject data) throws JSONException{
		super();
		
		setSteamKey(steamKey);
		String name = data.getString("name");
		setName(name);
		
		String type = data.getString("type");
		setType(type);
		
		final int installedInt = data.optInt("installed");
		boolean installed = (installedInt != 0)?true:false;
		setInstalled(installed);
		
		final String logo = data.optString("logo");
		setLogo(logo);
		
		final JSONObject minutesPlayed = data.optJSONObject("minutes_played");
		
		final Long minutesPlayedForever = minutesPlayed.optLong("forever");
		setMinutesPlayedForever(minutesPlayedForever);
		
		final Long minutesPlayedLastTwoWeeks = minutesPlayed.optLong("last_two_weeks");
		setMinutesPlayedLastTwoWeeks(minutesPlayedLastTwoWeeks);
		
		final Long lastPlayedAt = data.optLong("last_played_at");
		setLastPlayedAt(lastPlayedAt);
	}

	
	
	private int steamKey;
	
	/**
	 * @return the steamKey
	 */
	public int getSteamKey() {
		return steamKey;
	}

	/**
	 * @param steamKey the steamKey to set
	 */
	public void setSteamKey(int steamKey) {
		this.steamKey = steamKey;
	}

	private String name;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the installed
	 */
	public Boolean getInstalled() {
		return installed;
	}
	/**
	 * @param installed the installed to set
	 */
	public void setInstalled(Boolean installed) {
		this.installed = installed;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}
	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}
	/**
	 * @return the minutesPlayedForever
	 */
	public Long getMinutesPlayedForever() {
		return minutesPlayedForever;
	}
	/**
	 * @param minutesPlayedForever the minutesPlayedForever to set
	 */
	public void setMinutesPlayedForever(Long minutesPlayedForever) {
		this.minutesPlayedForever = minutesPlayedForever;
	}
	/**
	 * @return the minutesPlayedLastTwoWeeks
	 */
	public Long getMinutesPlayedLastTwoWeeks() {
		return minutesPlayedLastTwoWeeks;
	}
	/**
	 * @param minutesPlayedLastTwoWeeks the minutesPlayedLastTwoWeeks to set
	 */
	public void setMinutesPlayedLastTwoWeeks(Long minutesPlayedLastTwoWeeks) {
		this.minutesPlayedLastTwoWeeks = minutesPlayedLastTwoWeeks;
	}
	/**
	 * @return the lastPlayedAt
	 */
	public Long getLastPlayedAt() {
		return lastPlayedAt;
	}
	/**
	 * @param lastPlayedAt the lastPlayedAt to set
	 */
	public void setLastPlayedAt(Long lastPlayedAt) {
		this.lastPlayedAt = lastPlayedAt;
	}
	
	private Boolean installed;
	private String type;
	private String logo;
	private Long minutesPlayedForever;
	private Long minutesPlayedLastTwoWeeks;
	private Long lastPlayedAt;

	@Override
	public int compareTo(SteamGame another) {
		if(another == null){
			return 1;
		}
		
		final String anotherName = another.getName();
		final String name = getName();
		if(name == null){
			return -1;
		}
		
		return name.compareTo(anotherName);
	}
}
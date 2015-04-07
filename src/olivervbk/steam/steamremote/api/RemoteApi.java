package olivervbk.steam.steamremote.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import olivervbk.steam.steamremote.api.HttpClientManager.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Pair;

@SuppressLint("RtlHardcoded")
public class RemoteApi {
	private String urlPrefix;
	
	private HttpClientManager httpClientManager;

	/**
	 * @return the httpClientManager
	 */
	public HttpClientManager getHttpClientManager() {
		return httpClientManager;
	}

	/**
	 * @param httpClientManager the httpClientManager to set
	 */
	public void setHttpClientManager(HttpClientManager httpClientManager) {
		this.httpClientManager = httpClientManager;
	}

	private static RemoteApi instance;

	/**
	 * @return the instance
	 */
	public static RemoteApi getInstance() {
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(RemoteApi instance) {
		RemoteApi.instance = instance;
	}

	public RemoteApi(final String address)
			throws HttpConnectionManagerException {
		this(address, 27037);
	}

	public RemoteApi(final String address, final int port)
			throws HttpConnectionManagerException {
		super();
		
		this.urlPrefix = String.format("https://%s:%s/steam/", address, port);
		final HttpClientManager httpClientManager = new HttpClientManager(true);
		setHttpClientManager(httpClientManager);
		
		setInstance(this);
	}

	public void authorization(String token, String name) throws JSONException,
			IOException, SteamRemoteException {
		Map<String, String> params = new HashMap<>();
		params.put("device_name", name);
		params.put("device_token", token);
		sendRequest(HttpMethod.POST, "authorization/", params);
	}
	
	public boolean authorized() throws IOException, JSONException {
		JSONObject result;
		try {
			result = sendRequest(HttpMethod.POST, "authorized/");
		} catch (SteamRemoteException e) {
			return false;
		}
		boolean success = result.getBoolean("success");
		return success;
	}

	public enum SteamButton {
		GUIDE(), LEFT(), RIGHT(), UP(), DOWN(), A(), B(), X(), Y(), RTRIGGER(), LTRIGGER();
		public String getIdentifier() {
			final String name = name();
			return name.toLowerCase();
		}
	}
	
	public enum SteamMouseButton {
		MOUSE_LEFT();
		public String getIdentifier() {
			final String name = name();
			return name.toLowerCase();
		}
	}
	
	public enum SteamKey {
		ENTER(), BACKSPACE(), END(), HOME(), F1(),F2(),F3(),F4(),F5(),F6(),F7(),F8(),F9(),F10(),F11(),F12(), ESCAPE();
		public String getIdentifier() {
			final String name = name();
			return "key_"+name.toLowerCase();
		}
	}

	public void button(SteamButton button) throws JSONException, IOException,
			SteamRemoteException {
		String identifier = button.getIdentifier();
		
		sendRequest(HttpMethod.POST, "button/" + identifier + "/");
	}
	
	public void mouseClick(SteamMouseButton button) throws JSONException, IOException,
	SteamRemoteException {
		String identifier = button.getIdentifier();
		final Map<String, String> params = new HashMap<>();
		params.put("button", identifier);

		sendRequest(HttpMethod.POST, "mouse/click", params);
	}
	
	public void mouseMove(int delta_x, int delta_y) throws JSONException, IOException,
	SteamRemoteException {
		final Map<String, String> params = new HashMap<>();
		params.put("delta_x", Integer.toString(delta_x));
		params.put("delta_y", Integer.toString(delta_y));

		sendRequest(HttpMethod.POST, "mouse/move", params);
	}
	
	public void keyboardKey(SteamKey name) throws JSONException, IOException,
	SteamRemoteException {
		String identifier = name.getIdentifier();
		final Map<String, String> params = new HashMap<>();
		params.put("name", identifier);

		sendRequest(HttpMethod.POST, "keyboard/key/", params);
	}
	
	public void keyboardSequence(String sequence) throws JSONException, IOException,
	SteamRemoteException {
		final Map<String, String> params = new HashMap<>();
		params.put("sequence", sequence);

		sendRequest(HttpMethod.POST, "keyboard/key/", params);
	}

	public List<SteamGame> games() throws IOException, SteamRemoteException, JSONException{
		JSONObject result = sendRequest(HttpMethod.GET, "games/");
		final JSONObject data = result.getJSONObject("data");
		final Iterator<String> keys = data.keys();
		
		final List<SteamGame> games = new ArrayList<>();
		while(keys.hasNext()){
			final String nextKey = keys.next();
			final JSONObject gameJson = data.getJSONObject(nextKey);
			
			final int steamKey = Integer.parseInt(nextKey);
			final SteamGame game = new SteamGame(steamKey, gameJson);
			
			games.add(game);
		}
		
		return games;
	}
	
	public void gamesRun(String appid) throws JSONException, IOException,
	SteamRemoteException {
		sendRequest(HttpMethod.POST, "games/"+appid+"/run");
	}
	
	public enum SteamMusicAction {
		PLAY(), PAUSE(), NEXT(), PREVIOUS();
		public String getIdentifier() {
			final String name = name();
			return name.toLowerCase();
		}
	}
	
	public void music(SteamMusicAction action) throws JSONException, IOException,
	SteamRemoteException {

		final String identifier = action.getIdentifier();
		sendRequest(HttpMethod.POST, "music/"+identifier+"/");
	}
	
	public SteamMusicInfo music() throws JSONException, IOException,
	SteamRemoteException {
		final JSONObject musicInfo = sendRequest(HttpMethod.GET, "music/");
		final JSONObject dataObject = musicInfo.getJSONObject("data");
		return new SteamMusicInfo(dataObject);
	}
	
	/**
	 * @param looped
	 * 	Is optional. Can be null.
	 * @param shuffled
	 * 	Is optional. Can be null.
	 * @return
	 * @throws JSONException 
	 * @throws SteamRemoteException 
	 * @throws IOException 
	 */
	public SteamMusicMode musicMode(final Boolean looped, final Boolean shuffled) throws IOException, SteamRemoteException, JSONException{
		final HashMap<String, String> params = new HashMap<>();
		if(looped != null){
			final String loopedIntStr = boolToIntStr(looped);
			params.put("looped", loopedIntStr);
		}
		if(shuffled != null){
			final String shuffledIntStr = boolToIntStr(shuffled);
			params.put("looped", shuffledIntStr);
		}
		
		final JSONObject response = sendRequest(HttpMethod.POST, "music/mode/", params);
		final JSONObject dataObject = response.getJSONObject("data");
		return new SteamMusicMode(dataObject);
	}
	
	protected static int boolToInt(boolean bool){
		return (bool)?1:0;
	}
	
	protected static String boolToIntStr(boolean bool){
		final int boolInt = boolToInt(bool);
		return Integer.toString(boolInt);
	}
	
	public Pair<Integer, String> stream() throws JSONException, IOException,
	SteamRemoteException {

		final JSONObject sendRequest = sendRequest(HttpMethod.POST, "stream/");
		final JSONObject data = sendRequest.getJSONObject("data");
		final int streamPort = data.getInt("stream_port");
		final String authToken = data.getString("auth_token");
		
		return new Pair<Integer, String>(streamPort, authToken);
	}
	
	public void volume(final double volume) throws IOException, SteamRemoteException, JSONException{
		final String volumeStr = String.valueOf(volume);
		
		final HashMap<String, String> params = new HashMap<>();
		params.put("volume", volumeStr);
		
		sendRequest(HttpMethod.POST, "music/volume/", params);
	}
	
	public double volume()throws IOException, SteamRemoteException, JSONException{
		final JSONObject response = sendRequest(HttpMethod.GET, "music/volume/");
		final JSONObject dataObject = response.getJSONObject("data");
		final double volume = dataObject.getDouble("volume");
		return volume;
	}
	
	
	public enum SteamSpace{
		LIBRARY(), FRIENDS(), WEBBROWSER();
	}
	
	public SteamSpace space() throws IOException, SteamRemoteException, JSONException{
		final JSONObject sendRequest = sendRequest(HttpMethod.GET, "space/");
		final JSONObject data = sendRequest.getJSONObject("data");
		final String spaceStr = data.getString("space");
		final String spaceStrUpper = spaceStr.toUpperCase();
		
		final SteamSpace space = SteamSpace.valueOf(spaceStrUpper);
		
		return space;
	}
	
	public void space(SteamSpace space) throws IOException, SteamRemoteException, JSONException{
		final String spaceStr = space.toString();
		final String spaceStrLower = spaceStr.toLowerCase();
		
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("space", spaceStrLower);
		
		sendRequest(HttpMethod.POST, "space/", params);
	}
	
	 
	
	private JSONObject sendRequest(HttpMethod method, final String url) throws IOException,
			SteamRemoteException, JSONException {
		final HashMap<String, String> params = new HashMap<String, String>(1);
		return sendRequest(method, url, params);
	}
	
	private JSONObject sendRequest(HttpMethod method, final String url,
			final Map<String, String> params) throws IOException,
			SteamRemoteException, JSONException {

		final long start = System.currentTimeMillis();
		
		final String steamUrl = String.format("%s%s", urlPrefix, url);
		
		if(!params.containsKey("device_token")){
			params.put("device_token", "12345678");
		}
		
		final HttpClientManager httpClientManager = getHttpClientManager();
		final JSONObject response = httpClientManager.sendRequest(method, steamUrl, params);
		
		final long stop = System.currentTimeMillis();
		setDelayCounter((int) (stop-start));
		
		final boolean success = response.getBoolean("success");
		if (!success) {
			String error = response.getString("error");
			throw new SteamRemoteException(error);
		}
		return response;
	}
	
	private int delayCounter = 0;

	/**
	 * @return the delayCounter
	 */
	public int getDelayCounter() {
		return delayCounter;
	}

	/**
	 * @param delayCounter the delayCounter to set
	 */
	protected void setDelayCounter(int delayCounter) {
		this.delayCounter = delayCounter;
	} 
}

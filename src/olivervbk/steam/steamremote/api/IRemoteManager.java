package olivervbk.steam.steamremote.api;

import java.io.IOException;
import java.util.List;

import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamButton;
import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamKey;
import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamMouseButton;
import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamMusicAction;
import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamSpace;
import olivervbk.steam.steamremote.api.http.HttpClientManager;

import org.json.JSONException;

import android.util.Pair;

public interface IRemoteManager {

	public abstract void authorization(String token, String name)
			throws JSONException, IOException, SteamRemoteException;

	public abstract boolean authorized() throws IOException, JSONException;

	public abstract void button(SteamButton button) throws JSONException,
			IOException, SteamRemoteException;

	public abstract void mouseClick(SteamMouseButton button)
			throws JSONException, IOException, SteamRemoteException;

	public abstract void mouseMove(int delta_x, int delta_y)
			throws JSONException, IOException, SteamRemoteException;

	public abstract void keyboardKey(SteamKey name) throws JSONException,
			IOException, SteamRemoteException;

	public abstract void keyboardSequence(String sequence)
			throws JSONException, IOException, SteamRemoteException;

	public abstract List<SteamGame> games() throws IOException,
			SteamRemoteException, JSONException;

	public abstract void gamesRun(String appid) throws JSONException,
			IOException, SteamRemoteException;

	public abstract void music(SteamMusicAction action) throws JSONException,
			IOException, SteamRemoteException;

	public abstract SteamMusicInfo music() throws JSONException, IOException,
			SteamRemoteException;

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
	public abstract SteamMusicMode musicMode(Boolean looped, Boolean shuffled)
			throws IOException, SteamRemoteException, JSONException;

	public abstract Pair<Integer, String> stream() throws JSONException,
			IOException, SteamRemoteException;

	public abstract void volume(double volume) throws IOException,
			SteamRemoteException, JSONException;

	public abstract double volume() throws IOException, SteamRemoteException,
			JSONException;

	public abstract SteamSpace space() throws IOException,
			SteamRemoteException, JSONException;

	public abstract void space(SteamSpace space) throws IOException,
			SteamRemoteException, JSONException;

	/**
	 * @return the delayCounter
	 */
	public abstract int getDelayCounter();

}
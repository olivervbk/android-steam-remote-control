package olivervbk.steam.steamremote.api;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamSpace;

import org.json.JSONException;

import android.util.Log;
import android.util.Pair;

public class DummyManager {
	
	public boolean authorized() throws IOException, JSONException {
		return true;
	}
	
	public List<SteamGame> games() throws IOException, SteamRemoteException{
		final List<SteamGame> gamesList = new ArrayList<>();
		final SteamGame dummyGame = new SteamGame();
		dummyGame.setInstalled(true);
		
		final long currentTimeMillis = System.currentTimeMillis();
		dummyGame.setLastPlayedAt(currentTimeMillis);
		
		dummyGame.setName("dummy game");
		dummyGame.setSteamKey(0);
		
		gamesList.add(dummyGame);
		return gamesList;
	}
	
	public int getDelayCounter() {
		return 0;
	}
	
	public SteamMusicInfo music() throws JSONException, IOException,
			SteamRemoteException {
		final SteamMusicInfo steamMusicInfo = new SteamMusicInfo();
		steamMusicInfo.setCurrentAlbum("dummy album");
		steamMusicInfo.setCurrentArtist("dummy artist");
		steamMusicInfo.setCurrentTrack("dummy track");
		steamMusicInfo.setLooped(false);
		steamMusicInfo.setShuffled(false);
		steamMusicInfo.setQueueCount(1);
		steamMusicInfo.setStatus("dummy status");
		steamMusicInfo.setVolume(0.5d);
		return steamMusicInfo;
	}
	
	public SteamMusicMode musicMode(Boolean looped, Boolean shuffled)
			throws IOException, SteamRemoteException, JSONException {
		final SteamMusicMode steamMusicMode = new SteamMusicMode(looped, shuffled);
		return steamMusicMode;
	}
	
	public Pair<Integer, String> stream() throws JSONException, IOException,
			SteamRemoteException {
		final Pair<Integer, String> pair = new Pair<>(1, "dummy url");
		return pair;
	}
	
	public SteamSpace space() throws IOException, SteamRemoteException,
			JSONException {
		return SteamSpace.LIBRARY;
	}
	
	public double volume() throws IOException, SteamRemoteException,
			JSONException {
		return 0.5d;
	}
	
	public static class LoggingInvocationHandler implements InvocationHandler {

		private static final String TAG = "DummyManagerHand";
		
		private final Object delegate;

		public LoggingInvocationHandler(Object delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			final Object delegate = getDelegate();
			final String methodName = method.getName();
			
			if(Log.isLoggable(TAG, Log.INFO)){
				final String argsStr = Arrays.toString(args);
				
				final String format = String.format("DummyManager: %s - %s", methodName, argsStr);
				Log.i(TAG, format);
			}

			final Class<? extends Object> delegateClazz = delegate.getClass();
			
			Object result;
			try{
				final Method availableMethod = delegateClazz.getDeclaredMethod(methodName, method.getParameterTypes());
				if(Log.isLoggable(TAG, Log.INFO)){
					final String format = String.format("DummyManager: %s using implementation.", methodName);
					Log.i(TAG, format);
				}
				result = availableMethod.invoke(delegate, args);
			}catch(final NoSuchMethodException e){
				result = null;
			}
			
			if(Log.isLoggable(TAG, Log.INFO)){
				final String format = String.format("DummyManager: %s - return: %s", methodName, result);
				Log.i(TAG, format);
			}

			return result;
		}

		/**
		 * @return the delegate
		 */
		protected Object getDelegate() {
			return delegate;
		}
	}

	public static IRemoteManager createDummyImplementation() {
		final Class<DummyManager> myClazz = DummyManager.class;
		final ClassLoader myClazzClassLoader = myClazz.getClassLoader();
		final DummyManager delegate = new DummyManager();
		
		final LoggingInvocationHandler loggingInvocationHandler = new LoggingInvocationHandler(
				delegate);
		final Class[] classes = new Class[] { IRemoteManager.class };

		final IRemoteManager dummyImplementation = (IRemoteManager) Proxy
				.newProxyInstance(myClazzClassLoader, classes,
						loggingInvocationHandler);
		
		return dummyImplementation;
		
	}
}

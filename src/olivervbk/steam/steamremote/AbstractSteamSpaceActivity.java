package olivervbk.steam.steamremote;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import olivervbk.steam.steamremote.api.IRemoteManager;
import olivervbk.steam.steamremote.api.RemoteApi;
import olivervbk.steam.steamremote.api.SteamRemoteManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class AbstractSteamSpaceActivity extends Activity {

	// TODO set to change steam space

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final boolean onOptionsItemSelected = super.onOptionsItemSelected(item);
		
		// finish the activity on switching
		finish();
		
		return onOptionsItemSelected;
	}

	private Timer delayUpdateTimer;

	/**
	 * @return the delayUpdateTimer
	 */
	protected Timer getDelayUpdateTimer() {
		return delayUpdateTimer;
	}

	/**
	 * @param delayUpdateTimer
	 *            the delayUpdateTimer to set
	 */
	protected void setDelayUpdateTimer(Timer delayUpdateTimer) {
		this.delayUpdateTimer = delayUpdateTimer;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		final MenuItem delayDisp = menu.findItem(R.id.menu_delay_display);
		final TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Runnable updateAction = new Runnable() {

					@Override
					public void run() {
						final IRemoteManager instance = RemoteApi.getInstance();
						final int delayCounter = instance.getDelayCounter();
						delayDisp.setTitle("" + delayCounter + " ms");
					}
				};
				runOnUiThread(updateAction);

			}
		};

		final Timer timer = new Timer("DelayUpdateTimer");
		setDelayUpdateTimer(timer);
		timer.schedule(timerTask, 0, 1000);

		final Map<String, Class<? extends AbstractSteamSpaceActivity>> intentMapper = new LinkedHashMap<>();
		intentMapper.put("GamePad", GamePadActivity.class);
		intentMapper.put("Music", MusicActivity.class);
		intentMapper.put("Games", GamesListActivity.class);
		intentMapper.put("MousePad", MousePadActivity.class);
		

		for (Entry<String, Class<? extends AbstractSteamSpaceActivity>> entry : intentMapper
				.entrySet()) {
			final String label = entry.getKey();
			final Class<? extends AbstractSteamSpaceActivity> intentClass = entry
					.getValue();

			final Intent intent = new Intent(this, intentClass);

			final MenuItem gamePadMenuItem = menu.add(label);
			gamePadMenuItem.setIntent(intent);

		}
		return true;
	}
}

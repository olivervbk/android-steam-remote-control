package olivervbk.steam.steamremote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;

import olivervbk.steam.steamremote.api.IRemoteManager;
import olivervbk.steam.steamremote.api.RemoteApi;
import olivervbk.steam.steamremote.api.SteamRemoteManager;
import olivervbk.steam.steamremote.api.SteamRemoteManager.SteamMusicAction;
import olivervbk.steam.steamremote.api.SteamMusicInfo;
import olivervbk.steam.steamremote.api.SteamMusicMode;
import olivervbk.steam.steamremote.api.SteamRemoteException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MusicActivity extends AbstractSteamSpaceActivity {

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		final int action = event.getAction();
		final int keyCode = event.getKeyCode();
		
		switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_UP:
				if(action == KeyEvent.ACTION_DOWN){
					updateVolume(true);
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if(action == KeyEvent.ACTION_DOWN){
					updateVolume(false);
				}
				return true;
			default:
				return super.dispatchKeyEvent(event);
		}
	}
	
	private void updateVolume(final boolean volumeIsUp){
		final Runnable updateVolume = new Runnable() {
			
			@Override
			public void run() {
				final IRemoteManager instance = RemoteApi.getInstance();
				try {
					final double volume = instance.volume();
					final double newVolume;
					if(volumeIsUp){
						newVolume = volume + 0.1;
					}else{
						newVolume = volume - 0.1;
					}
					final double normalizedVolume = ((double)((int) (newVolume * 10)) /10);
					
					final double volumeToUse;
					if(normalizedVolume > 1f){
						volumeToUse = 1f;
					}else if(normalizedVolume < 0f){
						volumeToUse = 0f;
					}else{
						volumeToUse = normalizedVolume;
					}
					
					instance.volume(volumeToUse);
					
				} catch (IOException | SteamRemoteException | JSONException e) {
					e.printStackTrace();
					return;
				}
				
			}
		};
		
		final Thread volumeUpdater = new Thread(updateVolume);
		volumeUpdater.start();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		
		final Map<SteamRemoteManager.SteamMusicAction, Integer> buttonIdentifierMap = new HashMap<>();
		buttonIdentifierMap.put(SteamMusicAction.NEXT, R.id.music_next);
		buttonIdentifierMap.put(SteamMusicAction.PAUSE, R.id.music_pause);
		buttonIdentifierMap.put(SteamMusicAction.PLAY, R.id.music_play);
		buttonIdentifierMap.put(SteamMusicAction.PREVIOUS, R.id.music_previous);
		
		for (Entry<SteamRemoteManager.SteamMusicAction, Integer> entry : buttonIdentifierMap.entrySet()) {
			final SteamMusicAction action = entry.getKey();
			final Integer buttonId = entry.getValue();
			
			final ImageButton button = (ImageButton)findViewById(buttonId);
			final Runnable method = new Runnable(){
				@Override
				public void run() {
					IRemoteManager instance = RemoteApi.getInstance();
					try {
						instance.music(action);
						updateInfoNonUi();
						return;
					} catch (JSONException | IOException | SteamRemoteException e) {
						e.printStackTrace();
					}
				}
			};
			
			final OnClickListener onClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Thread thread = new Thread(method);
					thread.start();
					Toast.makeText(MusicActivity.this, "music action: "+action.getIdentifier(), Toast.LENGTH_SHORT).show();
					
				}
			};
			
			button.setOnClickListener(onClickListener);
		}
		
		final ToggleButton shuffleButton = (ToggleButton) findViewById(R.id.music_shuffled);
		shuffleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ToggleButton vToggle = (ToggleButton) v;
				final boolean invertChecked = !vToggle.isChecked();
				toggleMode(invertChecked, null );
				// updated later?
			}
		});
		
		final ToggleButton loopButton = (ToggleButton) findViewById(R.id.music_looped);
		loopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ToggleButton vToggle = (ToggleButton) v;
				final boolean invertChecked = !vToggle.isChecked();
				toggleMode(null, invertChecked);
				// updated later?
			}
		});
		
		// run first update
		updateInfo();
	}
	
	private void toggleMode(final Boolean shuffled, final Boolean looped){
		final Runnable modeToggler = new Runnable(){

			@Override
			public void run() {
				final IRemoteManager instance = RemoteApi.getInstance();
				try {
					final SteamMusicMode musicMode = instance.musicMode(looped, shuffled);
					final Runnable uiUpdater = new Runnable() {
						
						@Override
						public void run() {
							updateInfo(musicMode);
						}
					};
					runOnUiThread(uiUpdater);
					
				} catch (final JSONException | IOException | SteamRemoteException e) {
					e.printStackTrace();
					return;
				}
			}
		};
		final Thread togglerThread = new Thread(modeToggler);
		togglerThread.start();
	}
	
	private void updateInfoNonUi() {
		final IRemoteManager instance = RemoteApi.getInstance();
		try {
			final SteamMusicInfo musicInfo = instance.music();
			final Runnable uiUpdater = new Runnable() {
				
				@Override
				public void run() {
					updateInfo(musicInfo);
				}
			};
			runOnUiThread(uiUpdater);
			
		} catch (final JSONException | IOException | SteamRemoteException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void updateInfo(){
		final Runnable infoObtainer = new Runnable(){

			@Override
			public void run() {
				updateInfoNonUi();
			}
		};
		final Thread updaterThread = new Thread(infoObtainer);
		updaterThread.start();
	}
	
	private void updateInfo(SteamMusicInfo info){
		final TextView status = (TextView) findViewById(R.id.music_status);
		final TextView queueCount = (TextView) findViewById(R.id.music_queueCount);
		final TextView volume = (TextView) findViewById(R.id.music_volume);
		final TextView artist = (TextView) findViewById(R.id.music_currentArtist);
		final TextView album = (TextView) findViewById(R.id.music_currentAlbum);
		final TextView track = (TextView) findViewById(R.id.music_currentTrack);
		
		final String infoStatus = info.getStatus();
		status.setText(infoStatus);
		
		final Integer infoQueueCount = info.getQueueCount();
		queueCount.setText(Integer.toString(infoQueueCount));
		
		final Double infoVolume = info.getVolume();
		volume.setText(String.valueOf(infoVolume));
		
		final String infoArtist = info.getCurrentArtist();
		artist.setText((infoArtist==null)?"":infoArtist);
		
		final String infoAlbum = info.getCurrentAlbum();
		album.setText((infoAlbum==null)?"":infoAlbum);
		
		final String infoTrack = info.getCurrentTrack();
		track.setText((infoTrack==null)?"":infoTrack);
		
		final SteamMusicMode mode = info.getMode();
		updateInfo(mode);
	}
	
	private void updateInfo(SteamMusicMode mode){
		final ToggleButton shuffleButton = (ToggleButton) findViewById(R.id.music_shuffled);
		final ToggleButton loopButton = (ToggleButton) findViewById(R.id.music_looped);
		
		final boolean looped = mode.isLooped();
		loopButton.setChecked(looped);
		
		final boolean shuffled = mode.isShuffled();
		shuffleButton.setChecked(shuffled);
		
	}
}


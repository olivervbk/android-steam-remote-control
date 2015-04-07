package olivervbk.steam.steamremote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;

import olivervbk.steam.steamremote.api.RemoteApi;
import olivervbk.steam.steamremote.api.RemoteApi.SteamButton;
import olivervbk.steam.steamremote.api.SteamRemoteException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class GamePadActivity extends AbstractSteamSpaceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamepad);
		
		Map<RemoteApi.SteamButton, Integer> buttonIdentifierMap = new HashMap<>();
		buttonIdentifierMap.put(SteamButton.A, R.id.gamepad_a);
		buttonIdentifierMap.put(SteamButton.B, R.id.gamepad_b);
		buttonIdentifierMap.put(SteamButton.DOWN, R.id.gamepad_down);
		buttonIdentifierMap.put(SteamButton.GUIDE, R.id.gamepad_home);
		buttonIdentifierMap.put(SteamButton.LEFT, R.id.gamepad_left);
		buttonIdentifierMap.put(SteamButton.LTRIGGER, R.id.gamepad_left_trigger);
		buttonIdentifierMap.put(SteamButton.RIGHT, R.id.gamepad_right);
		buttonIdentifierMap.put(SteamButton.RTRIGGER, R.id.gamepad_right_trigger);
		buttonIdentifierMap.put(SteamButton.UP, R.id.gamepad_up);
		buttonIdentifierMap.put(SteamButton.X, R.id.gamepad_x);
		buttonIdentifierMap.put(SteamButton.Y, R.id.gamepad_y);
		
		for (Entry<RemoteApi.SteamButton, Integer> entry : buttonIdentifierMap.entrySet()) {
			final SteamButton steamButton = entry.getKey();
			final Integer buttonId = entry.getValue();
			
			final Button button = (Button)findViewById(buttonId);
			final Runnable method = new Runnable(){
				@Override
				public void run() {
					RemoteApi instance = RemoteApi.getInstance();
					try {
						instance.button(steamButton);
						return;
					} catch (JSONException | IOException | SteamRemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			final OnTouchListener onTouchListener = new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int eventaction = event.getAction();
			        switch (eventaction) {
			            case MotionEvent.ACTION_DOWN: 
			            	Thread thread = new Thread(method);
							thread.start();
							Toast.makeText(GamePadActivity.this, "button: "+steamButton.getIdentifier(), Toast.LENGTH_SHORT).show();
							return true;
			            case MotionEvent.ACTION_UP:   
			            	break;
			        }
			        // tell the system that we handled the event but a further processing is required
			        return false;
				}
			};
			
			button.setOnTouchListener(onTouchListener);
		}
	}
}

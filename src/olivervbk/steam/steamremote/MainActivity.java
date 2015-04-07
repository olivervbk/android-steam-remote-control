package olivervbk.steam.steamremote;

import java.io.IOException;

import org.json.JSONException;

import olivervbk.steam.steamremote.R;
import olivervbk.steam.steamremote.api.HttpConnectionManagerException;
import olivervbk.steam.steamremote.api.RemoteApi;
import olivervbk.steam.steamremote.api.SteamRemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String SHARED_PREFERENCE_NAME = "main";
	private static final String PREFERENCE_STEAM_ADDRESS = "steamAddress";

	private boolean isStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		logMessage("\n\nQuerying Steam Address...\n");
	}

	@Override
	protected void onStart() {
		// do not query again on reinitialize
		if(!isStarted){
			isStarted = true;
			querySteamAddress(this);
		}
		super.onStart();
	}

	private void connectToSteam(String steamAddress) {
		if (steamAddress == null || steamAddress.isEmpty()) {
			logMessage("Invalid steam address....\n");
			return;
		}
		logMessage("Initializing RemoteApi on address:" + steamAddress + "\n");

		RemoteApi remoteApi;
		try {
			remoteApi = new RemoteApi(steamAddress);
		} catch (HttpConnectionManagerException e1) {
			e1.printStackTrace();
			logMessage("RemoteApi error: " + e1.getMessage() + "\n");
			return;
		}

		logMessage("Checking authorization.\n");
		boolean authorized;
		try {
			authorized = remoteApi.authorized();
		} catch (IOException | JSONException e) {
			logMessage("isAuthError: " + e.getMessage() + "\n");
			return;
		}

		if (!authorized) {
			logMessage("Trying to authenticate.\n");

			try {
				remoteApi.authorization("12345678", "oliver");
			} catch (SteamRemoteException e) {
				logMessage("SteamError: " + e.getMessage() + "\n");
				return;
			} catch (IOException | JSONException e) {
				logMessage("AuthError: " + e.getMessage() + "\n");
				return;
			}
		}

		logMessage("Authorized.");

		Intent intent = new Intent(this, GamePadActivity.class);
		startActivity(intent);
	}

	private void querySteamAddress(Context context) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Steam Address");
		alert.setMessage("Input Steam Address (IP or hostname):");

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		alert.setView(input);

		final SharedPreferences sharedPreferences = getSharedPreferences(
				SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		final String defaultSteamAddress = sharedPreferences.getString(
				PREFERENCE_STEAM_ADDRESS, "");
		input.append(defaultSteamAddress);

		alert.setPositiveButton("Continue",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						final Editable inputEditable = input.getText();
						final String steamAddress = inputEditable.toString();

						final Editor edit = sharedPreferences.edit();
						edit.putString(PREFERENCE_STEAM_ADDRESS, steamAddress);
						edit.commit();
						
						final Runnable runnable = new Runnable() {

							@Override
							public void run() {
								connectToSteam(steamAddress);
							}
						};
						final Thread thread = new Thread(runnable);
						thread.start();
					}
				});

		alert.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				logMessage("Address dialog canceled. Giving up...");
			}
		});

		alert.show();
	}

	private void logMessage(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final TextView loggerView = (TextView) findViewById(R.id.logger);
				loggerView.append(message);
			}
		});
	}
}

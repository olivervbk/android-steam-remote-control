package olivervbk.steam.steamremote;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import olivervbk.steam.steamremote.api.IRemoteManager;
import olivervbk.steam.steamremote.api.RemoteApi;
import olivervbk.steam.steamremote.api.SteamRemoteManager;
import olivervbk.steam.steamremote.api.SteamGame;
import olivervbk.steam.steamremote.api.SteamRemoteException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GamesListActivity extends AbstractSteamSpaceActivity {
	private static final String TAG = GamesListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameslist);

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final Runtime runtime = Runtime.getRuntime();
		final long maxMemory = runtime.maxMemory();
		final int maxMemoryInKb = (int) (maxMemory / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemoryInKb / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

		final Runnable steamGamesObtainer = new Runnable() {
			@Override
			public void run() {
				final IRemoteManager instance = RemoteApi.getInstance();

				final List<SteamGame> games;
				try {
					games = instance.games();
				} catch (final IOException | SteamRemoteException
						| JSONException e) {
					e.printStackTrace();

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							final String exceptionMsg = "Error:"
									+ e.getMessage();
							Toast.makeText(GamesListActivity.this,
									exceptionMsg, Toast.LENGTH_LONG).show();
						}
					});

					return;
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						drawGamesList(games);
					}
				});

			}
		};

		final Thread thread = new Thread(steamGamesObtainer);
		thread.start();
	}

	protected void drawGamesList(List<SteamGame> games) {

		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.games_progressBar);
		progressBar.clearAnimation();
		progressBar.setVisibility(View.INVISIBLE);

		Collections.sort(games);

		final ListAdapter adapter = new ArrayAdapter<SteamGame>(this,
				R.layout.list_gameslist, games) {
			public View getView(int position, View convertView, ViewGroup parent) {

				final View viewToUse;
				if (convertView == null) {
					final LayoutInflater layoutInflater = getLayoutInflater();

					// parent does not accept addView:
					// UnsupportedOperationException
					viewToUse = layoutInflater.inflate(R.layout.list_gameslist,
							null);
				} else {
					viewToUse = convertView;
				}

				final ImageView image = (ImageView) viewToUse
						.findViewById(R.id.gamesList_image);
				final TextView name = (TextView) viewToUse
						.findViewById(R.id.gamesList_name);
				final TextView installed = (TextView) viewToUse
						.findViewById(R.id.gamesList_installed);
				final TextView lastPlayed = (TextView) viewToUse
						.findViewById(R.id.gamesList_lastPlayed);

				final SteamGame item = getItem(position);

				final String itemName = item.getName();
				name.setText(itemName);

				final Boolean itemInstalled = item.getInstalled();
				
				final String installedString = (itemInstalled)?"Installed":"Not installed";
				installed.setText(installedString);
				
				final Long lastPlayedAt = item.getLastPlayedAt();
				final Date lastPlayedDate = new Date(lastPlayedAt);
				
				final DateFormat dateFormat =  android.text.format.DateFormat.getDateFormat(GamesListActivity.this);
				final String lastPlayedString = dateFormat.format(lastPlayedDate);
				lastPlayed.setText("Last played:"+ lastPlayedString);

				final String logoUrl = item.getLogo();
				loadImage(image, logoUrl, itemName);

				if (Log.isLoggable(TAG, Log.INFO)) {
					Log.i(TAG, "Loading: " + itemName);
				}

				return viewToUse;
			};
		};

		final ListView gamesList = (ListView) findViewById(R.id.games_list);
		gamesList.setAdapter(adapter);
		
		gamesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				final SteamGame itemAtPosition = (SteamGame) adapter.getItemAtPosition(position);
				confirmGameLaunchOrInstall(itemAtPosition);
			}

			private void confirmGameLaunchOrInstall(
					final SteamGame game) {
				final Boolean installed = game.getInstalled();
				final String name = game.getName();
				
				final AlertDialog.Builder alert = new AlertDialog.Builder(GamesListActivity.this);

				alert.setTitle(name);
				
				final String message;
				if(installed){
					message = "Play game?";
				}else {
					message = "Install game?";
				}
				alert.setMessage(message);

				alert.setPositiveButton("Yes",
						new OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								final Runnable runnable = new Runnable() {

									@Override
									public void run() {
										final IRemoteManager instance = RemoteApi.getInstance();
										
										final int steamKey = game.getSteamKey();
										final String steamKeyStr = Integer.toString(steamKey);
										try {
											instance.gamesRun(steamKeyStr);
										} catch (JSONException | IOException
												| SteamRemoteException e) {
											e.printStackTrace();
										}
									}
								};
								final Thread thread = new Thread(runnable);
								thread.start();
							}
						});

				alert.setNegativeButton("No", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Does nothing
					}
				} );
				
				alert.show();
			}
		});
	}

	private LruCache<String, Bitmap> mMemoryCache;

	protected void loadImage(final ImageView image, final String logoUrl,
			final String itemName) {

		final Bitmap cachedBitmap = mMemoryCache.get(logoUrl);
		if(cachedBitmap != null){
			image.setImageBitmap(cachedBitmap);
			
			return;
		}
		
		final Bitmap bitmap = Bitmap.createBitmap(184, 69,
				Bitmap.Config.ARGB_8888);
		image.setImageBitmap(bitmap);

		final Runnable loadResource = new Runnable() {

			@Override
			public void run() {
				try {
					final URL logoImageUrl = new URL(logoUrl);
					final InputStream openStream = logoImageUrl.openStream();
					// final Drawable logoDrawable = Drawable.createFromStream(
					// openStream, itemName);

					final Bitmap logoBitmap = BitmapFactory
							.decodeStream(openStream);

					mMemoryCache.put(logoUrl, logoBitmap);

					final Runnable updateImage = new Runnable() {

						@Override
						public void run() {
							image.setImageBitmap(logoBitmap);
						}
					};

					runOnUiThread(updateImage);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};

		final Thread thread = new Thread(loadResource);
		thread.start();
	}
}

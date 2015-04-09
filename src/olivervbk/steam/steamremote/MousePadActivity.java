package olivervbk.steam.steamremote;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONException;

import olivervbk.steam.steamremote.api.RemoteApi;
import olivervbk.steam.steamremote.api.RemoteApi.SteamMouseButton;
import olivervbk.steam.steamremote.api.SteamRemoteException;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MousePadActivity extends AbstractSteamSpaceActivity {
	private final static String TAG = MousePadActivity.class.getSimpleName();

	private final ConcurrentLinkedQueue<Point> mouseMoveQueue = new ConcurrentLinkedQueue<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mousepad);

		final int longPressTimeout = ViewConfiguration.getLongPressTimeout();

		final RelativeLayout root = (RelativeLayout) findViewById(R.id.mousepad_root);

		final Thread queueCheckerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				final Thread currentThread = Thread.currentThread();
				while (!currentThread.isInterrupted()) {
					final Point lastPoint = mouseMoveQueue.poll();

					// check if movement exists
					if (lastPoint != null) {
						// Clear?
						//mouseMoveQueue.clear();
						
						final RemoteApi instance = RemoteApi.getInstance();

						try {
							if (lastPoint.isClick()) {
								final SteamMouseButton mouseLeft = SteamMouseButton.MOUSE_LEFT;
								instance.mouseClick(mouseLeft);
							} else {
								int x = lastPoint.x.intValue();
								int y = lastPoint.y.intValue();
								if (x != 0 && y != 0) {
									instance.mouseMove(x, y);
								}
							}

						} catch (JSONException | IOException
								| SteamRemoteException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});
		queueCheckerThread.start();

		final GestureOverlayView gestureOverlay = new GestureOverlayView(this) {
			private Point lastPoint = null;

			@Override
			public boolean onTouchEvent(MotionEvent event) {
				final int action = event.getActionMasked();

				final Point current = new Point(event);
				if (MotionEvent.ACTION_DOWN == action) {
					Log.i(TAG, "Action down");
					lastPoint = current;
				} else {
					final Point diff = current.diff(lastPoint);
					
					if (MotionEvent.ACTION_MOVE == action) {
						lastPoint = current;
						mouseMoveQueue.offer(diff);
						
					} else if (MotionEvent.ACTION_UP == action) {
						final long downTime = event.getDownTime();
						final long eventTime = event.getEventTime();
						
						final long pressDuration = eventTime - downTime;
						boolean isLongPress = (pressDuration >= longPressTimeout) ? true
								: false;
						
						final Point pointToOffer;
						if (!isLongPress && Point.ORIGIN.equals(diff)) {
							pointToOffer = Point.CLICK;
						} else {
							pointToOffer = diff;
						}
						mouseMoveQueue.offer(pointToOffer);
						
						if (Log.isLoggable(TAG, Log.INFO)) {
							

							final String msg = String.format(
									"Got release: %s ms isLong: %s\n"
											+ "%s -> %s = %s\n", pressDuration,
									isLongPress, lastPoint, current,
									pointToOffer);
							
							Log.i(TAG, msg);
						}

						lastPoint = null;
					}
				}
				return super.onTouchEvent(event);
			}
		};

		final LayoutParams matchParentParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		gestureOverlay.setLayoutParams(matchParentParams);
		gestureOverlay.bringToFront();
		
		final ImageButton drawerHandler = (ImageButton) findViewById(R.id.handle);
		drawerHandler.bringToFront();

		root.addView(gestureOverlay);
	}
}

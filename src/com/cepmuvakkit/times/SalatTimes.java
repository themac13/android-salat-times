package com.cepmuvakkit.times;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cepmuvakkit.times.SalatTimesPreferenceActivity;
import com.cepmuvakkit.times.dialog.SettingsDialog;
import com.cepmuvakkit.times.posAlgo.AstroLib;
import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.receiver.StartNotificationReceiver;
import com.cepmuvakkit.times.service.FillDailyTimetableService;
import com.cepmuvakkit.times.util.GateKeeper;
import com.cepmuvakkit.times.util.LocaleManagerOwn;
import com.cepmuvakkit.times.util.ThemeManager;
import com.cepmuvakkit.times.view.QiblaCompassView;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener; //import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class SalatTimes extends Activity {

	private static ThemeManager themeManager;
	private static LocaleManagerOwn localeManagerOwn;
	private TextView gregorDate;
	private int mYear, mMonth, mDay;
	private double jd;
	private Calendar todayDate;
	private ArrayList<HashMap<String, String>> timetable = new ArrayList<HashMap<String, String>>(
			7);
	private SimpleAdapter timetableView;
	private static SensorEventListener myOrientationListener;
	private SensorManager sensorManager;
	private static boolean isTrackingOrientation = false;
	private QiblaCompassView compassView;

	@Override
	public void onCreate(Bundle icicle) {
		VARIABLE.context = this;
		if (VARIABLE.settings == null)
			VARIABLE.settings = getSharedPreferences("settingsFile",
					MODE_PRIVATE);
		themeManager = new ThemeManager(this);
		super.onCreate(icicle);
		localeManagerOwn = new LocaleManagerOwn();
		setTitle(Schedule.today().hijriDateToString(this));
		setContentView(R.layout.main);
		// Timetable Listarray değişkeni içerisine namaz vakitleri isimlerini
		// doldurur
		for (int i = CONSTANT.FAJR; i <= CONSTANT.NEXT_FAJR; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("time_name", getString(CONSTANT.TIME_NAMES[i]));
			timetable.add(i, map);
		}
		timetableView = new SimpleAdapter(this, timetable,
				R.layout.timetable_row, new String[] { "mark", "time_name",
						"time", "time_am_pm" }, new int[] { R.id.mark,
						R.id.time_name, R.id.time, R.id.time_am_pm }) {
			public boolean areAllItemsEnabled() {
				return false;
			} // Disable list's item selection

			public boolean isEnabled(int position) {
				return false;
			}
		};
		((ListView) findViewById(R.id.timetable)).setAdapter(timetableView);
		((ListView) findViewById(R.id.timetable))
				.setOnHierarchyChangeListener(new OnHierarchyChangeListener() { // Set
					// zebra
					// stripes
					private int numChildren = 0;

					public void onChildViewAdded(View parent, View child) {
						child.setBackgroundResource(++numChildren % 2 == 0 ? themeManager
								.getAlternateRowColor()
								: android.R.color.transparent);
						if (numChildren > CONSTANT.NEXT_FAJR)
							numChildren = 0; // Last row has been reached, reset
						// for next time
					}

					public void onChildViewRemoved(View parent, View child) {
					}
				});
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		// ((ListView) findViewById(R.id.timetable)).getLayoutParams().height =
		// displayMetrics.heightPixels * 20 / 50;
		// ((ListView) findViewById(R.id.timetable)).getLayoutParams().width =
		// displayMetrics.widthPixels * 27 / 50;

		gregorDate = (TextView) findViewById(R.id.date);
		todayDate = Calendar.getInstance();
		updateWithNewLocation2();
		// double
		// timezone=(todayDate.getTimeZone().getRawOffset()+todayDate.getTimeZone().getDSTSavings())/(60
		// * 60 * 1000);
		getTodayDate();
		jd = AstroLib.calculateJulianDay(mYear, mMonth, mDay, 0, 0, 0, 0);
		updateGregorianDisplay();
		TabHost tabs = (TabHost) findViewById(R.id.tabs);
		tabs.setup();
		tabs.getTabWidget().setBackgroundResource(
				themeManager.getTabWidgetBackgroundColor());

		TabHost.TabSpec one = tabs.newTabSpec("one");
		one.setContent(R.id.today_content);
		one.setIndicator(getString(R.string.today),
				getResources().getDrawable(R.drawable.seccade));
		tabs.addTab(one);
		configureCalculationDefaults(); /* End of Tab 1 Items */

		TabHost.TabSpec two = tabs.newTabSpec("two");

		two.setContent(R.id.compass_content);
		two.setIndicator(getString(R.string.qibla),
				getResources().getDrawable(R.drawable.compass));
		tabs.addTab(two);
		compassView = new QiblaCompassView(this);
		compassView = (QiblaCompassView) findViewById(R.id.compass_view);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		compassView.setScreenResolution(width, height - 2 * height / 8);

		updateWithNewLocation();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		myOrientationListener = new SensorEventListener() {
			public void onSensorChanged(SensorEvent event) {
				float northDirection = event.values[0];
				updateOrientation(northDirection);

			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}
		}; /* End of Tab 2 Items */
		updateOrientation(0);
	}

	private void updateOrientation(float headingAngle) {
		if (compassView != null) {
			compassView.setBearing(headingAngle);
			compassView.invalidate();
		}
	}

	private void updateWithNewLocation2() {
		String locationName = "";
		Geocoder gc = new Geocoder(this, Locale.ENGLISH);
		locationName = "Unknown";
		try {
			List<Address> addresses = gc.getFromLocation(39.96714055,
					32.813066, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				locationName = address.getLocality();
			}
		} catch (IOException e) {
		}
		Toast.makeText(this, "Your Position: " + locationName,
				Toast.LENGTH_SHORT).show();

	}

	private void updateWithNewLocation() {

		compassView.setLongitude(VARIABLE.settings
				.getFloat("longitude", 39.95f));
		compassView.setLatitude(VARIABLE.settings.getFloat("latitude", 32.85f));
		compassView.initCompassView();
		compassView.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	//	short time = Schedule.today().nextTimeIndex();
		Intent intent;
		switch (item.getItemId()) {

		case R.id.menu_settings:
			new SettingsDialog(this, localeManagerOwn, themeManager).show();
			break;
		case R.id.more_applications:
			startActivity(new Intent(
					"android.intent.action.VIEW",
					Uri.parse("market://search?q=pub:%22Mehmet%20Mahmudoglu%22")));
			break;
		case R.id.menu_help:
			SpannableString s = new SpannableString(getText(R.string.help_text));
			Linkify.addLinks(s, Linkify.WEB_URLS);
			LinearLayout help = (LinearLayout) getLayoutInflater().inflate(
					R.layout.help, null);
			TextView message = (TextView) help.findViewById(R.id.help);
			message.setText(s);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			new AlertDialog.Builder(this).setTitle(R.string.help).setView(help)
					.setPositiveButton(android.R.string.ok, null).create()
					.show();
			break;
		case R.id.menu_information:
			s = new SpannableString(getText(R.string.information_text)
					.toString().replace("#", GateKeeper.getVersionName()));
			Linkify.addLinks(s, Linkify.WEB_URLS);
			LinearLayout information = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.information, null);
			message = (TextView) information.findViewById(R.id.information);
			message.setText(s);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			new AlertDialog.Builder(this).setIcon(R.drawable.icon)
					.setTitle(R.string.app_name).setView(information)
					.setPositiveButton(android.R.string.ok, null).create()
					.show();
			break;

//		case R.id.settings:
//			intent = new Intent(getApplicationContext(),
//					SalatTimesPreferenceActivity.class);
//			startActivityForResult(intent, 2);
//			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && (themeManager.isDirty() || localeManagerOwn.isDirty())) {
			VARIABLE.updateWidgets(this);
			restart();
		} else if (hasFocus) {
			if (VARIABLE.settings.contains("latitude")
					&& VARIABLE.settings.contains("longitude")) {
				((TextView) findViewById(R.id.notes)).setText("");
				((TextView) findViewById(R.id.cityName))
						.setText(VARIABLE.settings.getString("locationName",
								"Ankara1"));
			}
			if (Schedule.settingsAreDirty()) {
				updateTodaysTimetableAndNotification();
				VARIABLE.updateWidgets(this);
			}
		}
	}

	@Override
	public void onResume() {
		VARIABLE.mainActivityIsRunning = true;
		updateTodaysTimetableAndNotification();
		startTrackingOrientation();
		super.onResume();
	}

	@Override
	public void onPause() {
		stopTrackingOrientation();
		VARIABLE.mainActivityIsRunning = false;
		super.onPause();
	}

	private void startTrackingOrientation() {
		if (!isTrackingOrientation) {
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			int sensorType = Sensor.TYPE_ORIENTATION;
			isTrackingOrientation = sensorManager.registerListener(
					myOrientationListener,
					sensorManager.getDefaultSensor(sensorType),
					VARIABLE.settings.getInt("sensorDelayTime", 1));
		}
	}

	private void stopTrackingOrientation() {
		if (isTrackingOrientation)
			sensorManager.unregisterListener(myOrientationListener);
		isTrackingOrientation = false;
	}

	private void restart() {
		long restartTime = Calendar.getInstance().getTimeInMillis()
				+ CONSTANT.RESTART_DELAY;
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, restartTime, PendingIntent.getActivity(
				this, 0, getIntent(), PendingIntent.FLAG_ONE_SHOT
						| PendingIntent.FLAG_CANCEL_CURRENT));
		finish();
	}

	private void configureCalculationDefaults() {
		DecimalFormat LocationFormat = new DecimalFormat("#0.00°");
		if (!VARIABLE.settings.contains("latitude")
				|| !VARIABLE.settings.contains("longitude")) {
			Location currentLocation = VARIABLE.getCurrentLocation(this);
			try {
				SharedPreferences.Editor editor = VARIABLE.settings.edit();
				editor.putFloat("latitude",
						(float) currentLocation.getLatitude());
				editor.putFloat("longitude",
						(float) currentLocation.getLongitude());
				String locationName = "Unknown";
				Geocoder gc = new Geocoder(this, Locale.ENGLISH);
				try {
					List<Address> addresses = gc.getFromLocation(
							currentLocation.getLatitude(),
							currentLocation.getLongitude(), 1);
					// List<Address> addresses = gc.getFromLocation(39.96714055,
					// 32.813066, 1);
					if (addresses.size() > 0) {
						Address address = addresses.get(0);
						locationName = address.getLocality();
					}
				} catch (IOException e) {
				}
				// editor.putFloat("altitude", (float)
				// currentLocation.getAltitude());
				editor.putString("locationName", locationName);

				editor.commit();

				Toast.makeText(
						this,
						"Your Position: "
								+ LocationFormat.format(currentLocation
										.getLatitude())
								+ " "
								+ LocationFormat.format(currentLocation
										.getLongitude()), Toast.LENGTH_SHORT)
						.show();
				VARIABLE.updateWidgets(this);
			} catch (Exception ex) {
				Toast.makeText(this, "No location found", Toast.LENGTH_LONG)
						.show();
				((TextView) findViewById(R.id.notes))
						.setText(getString(R.string.location_not_set));
			}
		}
		if (!VARIABLE.settings.contains("calculationMethodsIndex")) {
			try {
				String country = Locale.getDefault().getISO3Country()
						.toUpperCase();

				SharedPreferences.Editor editor = VARIABLE.settings.edit();
				for (int i = 0; i < CONSTANT.CALCULATION_METHOD_COUNTRY_CODES.length; i++) {
					if (Arrays.asList(
							CONSTANT.CALCULATION_METHOD_COUNTRY_CODES[i])
							.contains(country)) {
						editor.putInt("calculationMethodsIndex", i);
						editor.commit();
						VARIABLE.updateWidgets(this);
						break;
					}
				}
			} catch (Exception ex) {
				// Wasn't set, oh well we'll uses DEFAULT_CALCULATION_METHOD
				// later
			}
		}
	}

	private void updateTodaysTimetableAndNotification() {
		StartNotificationReceiver.setNext(this);
		FillDailyTimetableService.set(this, Schedule.today(), timetable,
				timetableView);
	}

	private void updateGregorianDisplay() {
		gregorDate.setText(AstroLib.fromJulianToCalendarDateStr(jd));

	}

	protected void getTodayDate() {
		mYear = todayDate.get(Calendar.YEAR);
		mMonth = todayDate.get(Calendar.MONTH) + 1;
		mDay = todayDate.get(Calendar.DAY_OF_MONTH);
	}
}
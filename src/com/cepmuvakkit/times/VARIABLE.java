package com.cepmuvakkit.times;

import com.cepmuvakkit.times.widget.NextNotificationWidgetProvider;
import com.cepmuvakkit.times.widget.TimetableWidgetProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

public class VARIABLE {

	public static Context context;
	public static SharedPreferences settings;
	public static boolean mainActivityIsRunning = false;
	public static float qiblaDirection = 0;
	public static int qiblaDistance = 0;

	public static Location getCurrentLocation(Context context) {

		Location currentLocation = null;
		try {
			LocationManager locationManager = (LocationManager) (context
					.getSystemService(Context.LOCATION_SERVICE));
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setAltitudeRequired(true);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = locationManager.getBestProvider(criteria, true);
			currentLocation = locationManager.getLastKnownLocation(provider);

		} catch (Exception ex) {
			Toast.makeText(context, "GPS and wireless networks are disabled",
					Toast.LENGTH_SHORT).show();
			// GPS and wireless networks are disabled
		}
		return currentLocation;
	}

	private VARIABLE() {
		// Private constructor to enforce un-instantiability.
	}

	public static boolean alertSunrise() {
		if (settings == null)
			return false;
		return settings.getInt("notificationMethod" + CONSTANT.SUNRISE,
				CONSTANT.NOTIFICATION_NONE) != CONSTANT.NOTIFICATION_NONE;
	}

	public static void updateWidgets(Context context) {
		TimetableWidgetProvider.setLatestTimetable(context);
		NextNotificationWidgetProvider.setNextTime(context);
	}
}
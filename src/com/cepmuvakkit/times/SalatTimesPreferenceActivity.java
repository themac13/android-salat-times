package com.cepmuvakkit.times;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SalatTimesPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Toast.makeText(this, "Preference Activity Running",
		// Toast.LENGTH_LONG).show();

		// Inflate preference screen
		addPreferencesFromResource(R.xml.preferences_salat);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}

package com.cepmuvakkit.times.dialog;

import com.cepmuvakkit.times.CONSTANT;
import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.VARIABLE;
import com.cepmuvakkit.times.util.LocaleManagerOwn;
import com.cepmuvakkit.times.util.ThemeManager;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class InterfaceSettingsDialog extends Dialog {

	private static ThemeManager themeManager;
	private static LocaleManagerOwn localeManager;
	private static MediaPlayer mediaPlayer;

	public InterfaceSettingsDialog(Context context, ThemeManager tm, LocaleManagerOwn lm) {
		super(context);
		themeManager = tm;
		localeManager = lm;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.settings_interface);
		setTitle(R.string.sinterface);

		Spinner themes = (Spinner)findViewById(R.id.themes);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, themeManager.getAllThemeNames());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		themes.setAdapter(adapter);
		themes.setSelection(themeManager.getThemeIndex());

		Spinner languages = (Spinner)findViewById(R.id.languages);
		adapter = ArrayAdapter.createFromResource(getContext(), R.array.languages, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languages.setAdapter(adapter);
		languages.setSelection(localeManager.getLanguageIndex());
		((CheckBox)findViewById(R.id.bismillah_on_boot_up)).setChecked(VARIABLE.settings.getBoolean("bismillahOnBootUp", false));
		((CheckBox)findViewById(R.id.bismillah_on_boot_up)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mediaPlayer = MediaPlayer.create(getContext(), R.raw.bismillah);
					mediaPlayer.setScreenOnWhilePlaying(true);
					mediaPlayer.start();
				} else {
					if(mediaPlayer != null) mediaPlayer.stop();
				}
				SharedPreferences.Editor editor = VARIABLE.settings.edit();
				editor.putBoolean("bismillahOnBootUp", isChecked);
				editor.commit();
			}
		});

		
		Spinner time_format = (Spinner)findViewById(R.id.time_format);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.time_format, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		time_format.setAdapter(adapter2);
		time_format.setSelection(VARIABLE.settings.getInt("timeFormatIndex", CONSTANT.DEFAULT_TIME_FORMAT));
		Spinner rounding_types = (Spinner)findViewById(R.id.rounding_types);
		adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.rounding_types, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rounding_types.setAdapter(adapter2);
		rounding_types.setSelection(VARIABLE.settings.getInt("roundingTypesIndex", CONSTANT.DEFAULT_ROUNDING_TYPE));
		
				((Button)findViewById(R.id.save_settings)).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences.Editor editor = VARIABLE.settings.edit();
				int newThemeIndex = ((Spinner)findViewById(R.id.themes)).getSelectedItemPosition();
				if(themeManager.getThemeIndex() != newThemeIndex) {
					editor.putInt("themeIndex", newThemeIndex);
					themeManager.setDirty();
				}
				int newLanguageIndex = ((Spinner)findViewById(R.id.languages)).getSelectedItemPosition();
				if(newLanguageIndex != localeManager.getLanguageIndex()) {
					editor.putString("locale", LocaleManagerOwn.LANGUAGE_KEYS[newLanguageIndex]);
					localeManager.setDirty();
				}
				
				editor.putInt("timeFormatIndex", ((Spinner)findViewById(R.id.time_format)).getSelectedItemPosition());
				
				editor.putInt("roundingTypesIndex", ((Spinner)findViewById(R.id.rounding_types)).getSelectedItemPosition());
				editor.commit();
				dismiss();
			}
		});
		((Button)findViewById(R.id.reset_settings)).setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				
				((Spinner)findViewById(R.id.themes)).setSelection(ThemeManager.DEFAULT_THEME);
				((Spinner)findViewById(R.id.languages)).setSelection(0);
				((Spinner)findViewById(R.id.time_format)).setSelection(CONSTANT.DEFAULT_TIME_FORMAT);
				((Spinner)findViewById(R.id.rounding_types)).setSelection(CONSTANT.DEFAULT_ROUNDING_TYPE);
				
			}
		});
	}
}
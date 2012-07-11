package com.cepmuvakkit.times.dialog;
import java.util.GregorianCalendar;
import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.Schedule;
import com.cepmuvakkit.times.util.LocaleManagerOwn;
import com.cepmuvakkit.times.util.ThemeManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsDialog extends Dialog {

	private LocaleManagerOwn localeManager;
	private ThemeManager themeManager;
	

	public SettingsDialog(Context context, LocaleManagerOwn localeManager, ThemeManager themeManager) {
		super(context);
		this.localeManager = localeManager;
		this.themeManager = themeManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.settings);
		setTitle(R.string.settings);

		double gmtOffset = Schedule.getGMTOffset();
		String plusMinusGMT = gmtOffset < 0 ? "" + gmtOffset : "+" + gmtOffset;
		String daylightTime = Schedule.isDaylightSavings() ? " " + getContext().getString(R.string.daylight_savings) : "";
		((TextView)findViewById(R.id.display_time_zone)).setText(getContext().getString(R.string.system_time_zone) + ": " + getContext().getString(R.string.gmt) + plusMinusGMT + " (" + new GregorianCalendar().getTimeZone().getDisplayName() + daylightTime + ")");
		
		((Button)findViewById(R.id.set_location)).setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				new SetLocationDialog(v.getContext()).show();
			}
		});
		((Button)findViewById(R.id.set_calculation)).setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				new CalculationSettingsDialog(v.getContext()).show();
			}
		});
		((Button)findViewById(R.id.set_notification)).setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				new NotificationSettingsDialog(v.getContext()).show();
			}
		});
		((Button)findViewById(R.id.set_interface)).setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				new InterfaceSettingsDialog(v.getContext(), themeManager, localeManager).show();
			}
		});
				
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && (themeManager.isDirty() || localeManager.isDirty())) {
			dismiss();
		} else if(hasFocus) {
			Schedule.setSettingsDirty(); // Technically we should do it only when they have changed i.e. if Calculation or Advanced settings changed but this is easier
		}
	}
}
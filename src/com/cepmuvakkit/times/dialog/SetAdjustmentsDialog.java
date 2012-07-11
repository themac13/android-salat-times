package com.cepmuvakkit.times.dialog;

import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.VARIABLE;
import com.cepmuvakkit.times.posAlgo.HigherLatitude;
import com.cepmuvakkit.times.posAlgo.Methods;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetAdjustmentsDialog extends Dialog implements Methods,
		HigherLatitude {

	public SetAdjustmentsDialog(Context context) {
		super(context);
	
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.adjustment_settings);
		setTitle(R.string.set_adjustments);

		((EditText) findViewById(R.id.offset_minutes_fajr)).setText(Integer
				.toString(VARIABLE.settings.getInt("offsetFajr", 0)));
		((EditText) findViewById(R.id.offset_minutes_sunrise)).setText(Integer
				.toString(VARIABLE.settings.getInt("offsetSunrise", 0)));
		((EditText) findViewById(R.id.offset_minutes_dhur)).setText(Integer
				.toString(VARIABLE.settings.getInt("offsetDhur", 0)));
		((EditText) findViewById(R.id.offset_minutes_asr)).setText(Integer
				.toString(VARIABLE.settings.getInt("offsetAsr", 0)));
		((EditText) findViewById(R.id.offset_minutes_magrib)).setText(Integer
				.toString(VARIABLE.settings.getInt("offsetMagrib", 0)));
		((EditText) findViewById(R.id.offset_minutes_isha)).setText(Integer
				.toString(VARIABLE.settings.getInt("offsetIsha", 0)));

		((Button) findViewById(R.id.save_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						SharedPreferences.Editor editor = VARIABLE.settings
								.edit();
						try {
							editor
									.putInt(
											"offsetFajr",
											Integer
													.parseInt(((EditText) findViewById(R.id.offset_minutes_fajr))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("offsetFajr", 0);
						}

						try {
							editor
									.putInt(
											"offsetSunrise",
											Integer
													.parseInt(((EditText) findViewById(R.id.offset_minutes_sunrise))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("offsetSunrise", 0);
						}
						try {
							editor
									.putInt(
											"offsetDhur",
											Integer
													.parseInt(((EditText) findViewById(R.id.offset_minutes_dhur))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("offsetDhur", 0);
						}
						try {
							editor
									.putInt(
											"offsetASr",
											Integer
													.parseInt(((EditText) findViewById(R.id.offset_minutes_asr))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("offsetAsr", 0);
						}

						try {
							editor
									.putInt(
											"offsetMagrib",
											Integer
													.parseInt(((EditText) findViewById(R.id.offset_minutes_magrib))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("offsetMagrib", 0);
						}
						try {
							editor
									.putInt(
											"offsetIsha",
											Integer
													.parseInt(((EditText) findViewById(R.id.offset_minutes_isha))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("offsetIsha", 0);
						}

						editor.commit();
						dismiss();
					}
				});

		((Button) findViewById(R.id.reset_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						if (VARIABLE.settings.getInt("calculationMethodsIndex",
								TURKISH_RELIGOUS) == TURKISH_RELIGOUS) {
							((EditText) findViewById(R.id.offset_minutes_fajr))
									.setText("-1");
							((EditText) findViewById(R.id.offset_minutes_sunrise))
									.setText("-6");
							((EditText) findViewById(R.id.offset_minutes_dhur))
									.setText("7");
							((EditText) findViewById(R.id.offset_minutes_asr))
									.setText("5");
							((EditText) findViewById(R.id.offset_minutes_magrib))
									.setText("9");
							((EditText) findViewById(R.id.offset_minutes_isha))
									.setText("2");

						} else
						{	((EditText) findViewById(R.id.offset_minutes_fajr))
									.setText("0");
						((EditText) findViewById(R.id.offset_minutes_sunrise))
								.setText("0");
						((EditText) findViewById(R.id.offset_minutes_dhur))
								.setText("0");
						((EditText) findViewById(R.id.offset_minutes_asr))
								.setText("0");
						((EditText) findViewById(R.id.offset_minutes_magrib))
								.setText("0");
						((EditText) findViewById(R.id.offset_minutes_isha))
								.setText("0");

					}}
				});
	}

}

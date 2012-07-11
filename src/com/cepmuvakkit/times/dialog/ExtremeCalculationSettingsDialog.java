package com.cepmuvakkit.times.dialog;

import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.VARIABLE;
import com.cepmuvakkit.times.posAlgo.HigherLatitude;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class ExtremeCalculationSettingsDialog extends Dialog implements
		HigherLatitude {

	public ExtremeCalculationSettingsDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.extreme_latitudes_settings);
		setTitle(R.string.estimationmethod);

		Spinner est_method_fajr = (Spinner) findViewById(R.id.est_method_fajr);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getContext(), R.array.extreme_method_for_fajr,
				android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		est_method_fajr.setAdapter(adapter);
		est_method_fajr.setSelection(VARIABLE.settings.getInt(
				"estMethodofFajr", NO_ESTIMATION));

		Spinner est_method_isha = (Spinner) findViewById(R.id.est_method_isha);
		ArrayAdapter<CharSequence> adapter_isha = ArrayAdapter
				.createFromResource(getContext(),
						R.array.extreme_method_for_isha,
						android.R.layout.simple_spinner_item);
		adapter_isha
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		est_method_isha.setAdapter(adapter_isha);
		est_method_isha.setSelection(VARIABLE.settings.getInt(
				"estMethodofIsha", NO_ESTIMATION));

		((EditText) findViewById(R.id.base_latitude)).setText(Float
				.toString(VARIABLE.settings.getFloat("baseLatitude", 48.5f)));
		((EditText) findViewById(R.id.fixed_min)).setText(Integer
				.toString(VARIABLE.settings.getInt("fixedMin", 90)));
		((CheckBox) findViewById(R.id.useEstAlwaysFajr))
				.setChecked(VARIABLE.settings.getBoolean("useEstAlwaysFajr",
						false));
		((CheckBox) findViewById(R.id.useEstAlwaysIsha))
				.setChecked(VARIABLE.settings.getBoolean("useEstAlwaysIsha",
						false));
		((CheckBox) findViewById(R.id.applytoAll)).setChecked(VARIABLE.settings
				.getBoolean("applytoAll", false));
		((Button) findViewById(R.id.save_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						SharedPreferences.Editor editor = VARIABLE.settings
								.edit();
						editor.putInt("estMethodofFajr",
								((Spinner) findViewById(R.id.est_method_fajr))
										.getSelectedItemPosition());
						editor.putInt("estMethodofIsha",
								((Spinner) findViewById(R.id.est_method_isha))
										.getSelectedItemPosition());

						editor
								.putBoolean(
										"useEstAlwaysFajr",
										((CheckBox) findViewById(R.id.useEstAlwaysFajr))
												.isChecked());
						editor
								.putBoolean(
										"useEstAlwaysIsha",
										((CheckBox) findViewById(R.id.useEstAlwaysIsha))
												.isChecked());

						editor.putBoolean("applytoAll",
								((CheckBox) findViewById(R.id.applytoAll))
										.isChecked());
						try {
							editor
									.putFloat(
											"baseLatitude",
											Float
													.parseFloat(((EditText) findViewById(R.id.base_latitude))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putFloat("baseLatitude", 48.5f);
						}
						try {
							editor
									.putInt(
											"fixedMin",
											Integer
													.parseInt(((EditText) findViewById(R.id.fixed_min))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("fixedMin", 90);
						}

						editor.commit();
						dismiss();
					}
				});

		((Button)findViewById(R.id.reset_settings)).setOnClickListener(new Button.OnClickListener() {  
			public void onClick(View v) {
				((Spinner)findViewById(R.id.est_method_fajr)).setSelection(NO_ESTIMATION);
				((Spinner)findViewById(R.id.est_method_isha)).setSelection(NO_ESTIMATION);
				((EditText)findViewById(R.id.base_latitude)).setText("48.5");
				((EditText)findViewById(R.id.fixed_min)).setText("90");
				((CheckBox) findViewById(R.id.useEstAlwaysFajr)).setChecked(false);
				((CheckBox) findViewById(R.id.useEstAlwaysIsha)).setChecked(false);
				((CheckBox) findViewById(R.id.applytoAll)).setChecked(false);
				}
		});
	}
		
	}

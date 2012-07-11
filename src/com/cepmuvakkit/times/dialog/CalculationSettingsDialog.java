package com.cepmuvakkit.times.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cepmuvakkit.times.CONSTANT;
import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.VARIABLE;
import com.cepmuvakkit.times.posAlgo.PTimes;

public class CalculationSettingsDialog extends Dialog    {

	public CalculationSettingsDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.calculation_method);
		setTitle(R.string.calculation);

		Spinner calculation_methods = (Spinner) findViewById(R.id.calculation_methods);
		calculation_methods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{

			public void onItemSelected(AdapterView<?> parent, View v,
					int calculationMethod, long id) {
				SharedPreferences.Editor editor = VARIABLE.settings.edit();
				editor
				.putInt(
						"calculationMethodsIndex",
						calculationMethod);
				PTimes ptimes=new PTimes();
				double[] dawnDuskAngle=ptimes.getDawnDuskAngle(calculationMethod);
				
				((EditText) findViewById(R.id.dawn_angle)).setText(dawnDuskAngle[0]+"");
				((EditText) findViewById(R.id.dusk_angle)).setText(dawnDuskAngle[1]+"");
				}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getContext(), R.array.calculation_methods,
				android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		calculation_methods.setAdapter(adapter);
		calculation_methods
				.setSelection(VARIABLE.settings.getInt(
						"calculationMethodsIndex",
						CONSTANT.DEFAULT_CALCULATION_METHOD));
		((EditText) findViewById(R.id.dawn_angle)).setText(Float
				.toString(VARIABLE.settings.getFloat("dawnAngle", -18.0f)));
		((EditText) findViewById(R.id.dusk_angle)).setText(Float
				.toString(VARIABLE.settings.getFloat("duskAngle", -17.0f)));
		

		Spinner asr_method = (Spinner) findViewById(R.id.asr_method);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				getContext(), R.array.asr_method,
				android.R.layout.simple_spinner_item);
		adapter1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		asr_method.setAdapter(adapter1);
		asr_method.setSelection(VARIABLE.settings.getBoolean("isHanafiMathab",
				false) ? 1 : 0);

		// VARIABLE.settings.getBoolean("isHanafiMathab", false)? ASR_HANEFI :
		// ASR_SHAFI)
		((Button) findViewById(R.id.set_adjustments))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						new SetAdjustmentsDialog(v.getContext()).show();
					}
				});
		((Button) findViewById(R.id.set_higher_latitudes))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						new ExtremeCalculationSettingsDialog(v.getContext())
								.show();
					}
				});

		((Button) findViewById(R.id.save_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						SharedPreferences.Editor editor = VARIABLE.settings
								.edit();
						editor
								.putInt(
										"calculationMethodsIndex",
										((Spinner) findViewById(R.id.calculation_methods))
												.getSelectedItemPosition());
						editor.putBoolean("isHanafiMathab",
								((Spinner) findViewById(R.id.asr_method))
										.getSelectedItemPosition() == 0 ? false
										: true);
						
					
						try {
							editor
									.putFloat(
											"dawnAngle",
											Float
													.parseFloat(((EditText) findViewById(R.id.dawn_angle))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putFloat("dawnAngle", -18f);
						}
						
						try {
							editor
									.putFloat(
											"duskAngle",
											Float
													.parseFloat(((EditText) findViewById(R.id.dusk_angle))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putFloat("duskAngle", -18f);
						}
						editor.commit();
						dismiss();
					}
				});
		((Button) findViewById(R.id.reset_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						((Spinner) findViewById(R.id.calculation_methods))
								.setSelection(CONSTANT.DEFAULT_CALCULATION_METHOD);
						((Spinner) findViewById(R.id.asr_method)).
						setSelection(VARIABLE.settings.getBoolean("isHanafiMathab",
								false) ? 0 : 1);
					}
				});
	}

}
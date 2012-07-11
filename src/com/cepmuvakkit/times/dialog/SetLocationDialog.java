package com.cepmuvakkit.times.dialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.cepmuvakkit.times.R;
import com.cepmuvakkit.times.VARIABLE;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetLocationDialog extends Dialog {
	private Context context;

	public SetLocationDialog(Context context) {
		super(context);
		this.context=context;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.set_location);
		setTitle(R.string.calculation);
		((EditText) findViewById(R.id.locName)).setText(VARIABLE.settings
				.getString("locationName", "AnkaraForGPS"));
		float latitude = VARIABLE.settings.getFloat("latitude", -999f);
		float longitude = VARIABLE.settings.getFloat("longitude", -999f);
		((EditText) findViewById(R.id.latitude)).setText(latitude == -999f ? ""
				: Float.toString(latitude));
		((EditText) findViewById(R.id.longitude))
				.setText(longitude == -999f ? "" : Float.toString(longitude));
		((EditText) findViewById(R.id.altitude)).setText(Integer
				.toString(VARIABLE.settings.getInt("altitude", 0)));
		((EditText) findViewById(R.id.pressure)).setText(Integer
				.toString(VARIABLE.settings.getInt("pressure", 1010)));
		((EditText) findViewById(R.id.temperature)).setText(Integer
				.toString(VARIABLE.settings.getInt("temperature", 10)));
		((Button) findViewById(R.id.lookup_gps))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						Location currentLocation = VARIABLE
								.getCurrentLocation(getContext());
						String locationName = "Unknown";
						if (currentLocation != null) {	
							Geocoder gc = new Geocoder(context, Locale.getDefault());
							List<Address> addresses;
							try {
								addresses = gc.getFromLocation(currentLocation
										.getLatitude(), currentLocation.getLongitude(), 1);
								if (addresses.size() > 0) {
									Address address = addresses.get(0);
									locationName = address.getLocality();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							((EditText) findViewById(R.id.locName)).setText(VARIABLE.settings
									.getString("locationName",locationName));
							((EditText) findViewById(R.id.latitude))
									.setText(Double.toString(currentLocation
											.getLatitude()));
							((EditText) findViewById(R.id.longitude))
									.setText(Double.toString(currentLocation
											.getLongitude()));
							((EditText) findViewById(R.id.altitude))
									.setText(Integer.toString((int)currentLocation
											.getAltitude()));
						} else {
							((EditText) findViewById(R.id.locName))
							.setText("GPS is Not Working");
							((EditText) findViewById(R.id.latitude))
									.setText("");
							((EditText) findViewById(R.id.longitude))
									.setText("");
							((EditText) findViewById(R.id.altitude))
									.setText("");
						}
					}
				});
		((Button) findViewById(R.id.save_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						SharedPreferences.Editor editor = VARIABLE.settings
								.edit();
						
						try {
							editor.putString("locationName",
									((EditText) findViewById(R.id.locName))
											.getText().toString());
						/*	((TextView) findViewById(R.id.cityName)).setText(VARIABLE.settings
									.getString("locationName", "Ankara1"));*/
							
						} catch (Exception ex) {
							editor.putString("locationName","InvalidCity");
						}
						try {
							editor
									.putFloat(
											"latitude",
											Float
													.parseFloat(((EditText) findViewById(R.id.latitude))
															.getText()
															.toString()));
						} catch (Exception ex) {
							// Invalid latitude
						}
						try {
							editor
									.putFloat(
											"longitude",
											Float
													.parseFloat(((EditText) findViewById(R.id.longitude))
															.getText()
															.toString()));
						} catch (Exception ex) {
							// Invalid longitude
						}
						try {
							editor
									.putInt(
											"altitude",
											Integer
													.parseInt(((EditText) findViewById(R.id.altitude))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putInt("altitude", 0);
						}
						try {
							editor
									.putInt(
											"pressure",
											Integer
													.parseInt(((EditText) findViewById(R.id.pressure))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putFloat("pressure", 1010);
						}
						try {

							editor
									.putInt(
											"temperature",
											Integer
													.parseInt(((EditText) findViewById(R.id.temperature))
															.getText()
															.toString()));
						} catch (Exception ex) {
							editor.putFloat("temperature", 10);
						}
					
						editor.commit();
						dismiss();
					}
				});
		((Button) findViewById(R.id.reset_settings))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						((EditText) findViewById(R.id.locName))
								.setText("Enter City");
						((EditText) findViewById(R.id.pressure))
								.setText("1010");
						((EditText) findViewById(R.id.temperature))
								.setText("10");
						((EditText) findViewById(R.id.altitude)).setText("0");
					}
				});
	}
}
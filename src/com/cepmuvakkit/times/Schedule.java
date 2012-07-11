package com.cepmuvakkit.times;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.cepmuvakkit.conversion.hicricalendar.HicriCalendar;
import com.cepmuvakkit.times.posAlgo.AstroLib;
import com.cepmuvakkit.times.posAlgo.CONSTANT;
import com.cepmuvakkit.times.posAlgo.EarthPosition;
import com.cepmuvakkit.times.posAlgo.HigherLatitude;
import com.cepmuvakkit.times.posAlgo.Methods;
import com.cepmuvakkit.times.posAlgo.PTimes;


import android.content.Context;


public class Schedule implements Methods, HigherLatitude {

	private GregorianCalendar[] schedule = new GregorianCalendar[7];
	//private boolean[] extremes = new boolean[7];
	private double jd,jdn,ΔT;
	// private fi.joensuu.joyds1.calendar.Calendar hijriDate;

	private static Schedule today;

	public Schedule(GregorianCalendar day) {


		byte[] estMethod = {
				(byte) VARIABLE.settings.getInt("estMethodofFajr",
						NO_ESTIMATION),
				(byte) VARIABLE.settings.getInt("estMethodofIsha",
						NO_ESTIMATION) };
		byte calculationMethod = (byte) VARIABLE.settings.getInt(
				"calculationMethodsIndex", TURKISH_RELIGOUS);
		jd=getJulianDay();
		jdn=Math.round(jd)-0.5;
		ΔT = AstroLib.calculateTimeDifference(jd);
		EarthPosition loc = new EarthPosition(VARIABLE.settings.getFloat(
				"latitude", 39.95f), VARIABLE.settings.getFloat("longitude",
				32.85f), getGMTOffset(), VARIABLE.settings.getInt("altitude",
				0), VARIABLE.settings.getInt("temperature", 10),
				VARIABLE.settings.getInt("pressure", 1010));
		PTimes ptimes = new PTimes(jdn, loc, calculationMethod, estMethod);
	    schedule=ptimes.getSalatinGregorian();
	    // Next fajr
		// is
		// tomorrow
	    ptimes = new PTimes(jdn+1, loc, calculationMethod, estMethod);
	    schedule[CONSTANT.NEXT_FAJR]=
	    	AstroLib.convertJulian2Gregorian(jdn+1+(ptimes.getSalat()[FAJR])/24);
		
	}

	public GregorianCalendar[] getTimes() {
		return schedule;
	}

	/*public boolean isExtreme(int i) {
		return extremes[i];
	}*/

	public short nextTimeIndex() {
		Calendar now = new GregorianCalendar();
		if (now.before(schedule[CONSTANT.FAJR]))
			return CONSTANT.FAJR;
		for (short i = CONSTANT.FAJR; i < CONSTANT.NEXT_FAJR; i++) {
			if (now.after(schedule[i]) && now.before(schedule[i + 1])) {
				return ++i;
			}
		}
		return CONSTANT.NEXT_FAJR;
	}
/*
	private boolean currentlyAfterSunset() {
		Calendar now = new GregorianCalendar();
		return now.after(schedule[CONSTANT.MAGHRIB]);
	}
*/
	public String hijriDateToString(Context context) {
		/*
		 * String day = String.valueOf(hijriDate.getDay() +
		 * (currentlyAfterSunset() ? 1 : 0)); String month =
		 * context.getResources
		 * ().getStringArray(R.array.hijri_months)[hijriDate.getMonth() - 1];
		 * String year = String.valueOf(hijriDate.getYear());
		 */
		// return day + " " + month + ", " + year + " " +
		// context.getResources().getString(R.string.anno_hegirae);
		double mSunsetHour=(schedule[CONSTANT.MAGHRIB].get(Calendar.HOUR_OF_DAY)+schedule[CONSTANT.MAGHRIB].get(Calendar.MINUTE)/60.0);
		HicriCalendar hc = new HicriCalendar(jd, getGMTOffset(),mSunsetHour, ΔT);
		String hijriDate =hc.getHicriTakvim() + " " + hc.getDay();
				
		//return "3242";
		return hijriDate;
	}

	/*private void updateHijriDisplay() {

		HegiraCalendar hicriCalendar = new HegiraCalendar(getJulianDay());
		String hijriDate = hicriCalendar.getHicriTakvim();
		// +hicriCalendar.getDay()+" "+hicriCalendar.checkIfHolyDay();
		System.out.println(hijriDate);

	}*/

	public static Schedule today() {
		GregorianCalendar now = new GregorianCalendar();
		if (today == null) {
			today = new Schedule(now);
		} else {
			GregorianCalendar fajr = today.getTimes()[CONSTANT.FAJR];
			if (fajr.get(Calendar.YEAR) != now.get(Calendar.YEAR)
					|| fajr.get(Calendar.MONTH) != now.get(Calendar.MONTH)
					|| fajr.get(Calendar.DAY_OF_MONTH) != now
							.get(Calendar.DAY_OF_MONTH)) {
				today = new Schedule(now);
			}
		}
		return today;
	}

	public static void setSettingsDirty() {
		today = null; // Nullifying causes a new today to be created with new
		// settings when today() is called
	}

	public static boolean settingsAreDirty() {
		return today == null;
	}
/*
	public static double getGMTOffset() {
		Calendar todayDate = new GregorianCalendar();
		return (todayDate.getTimeZone().getRawOffset() + todayDate
				.getTimeZone().getDSTSavings())
				/ (60 * 60 * 1000);
	}*/

	public static double getGMTOffset() {
		Calendar now = new GregorianCalendar();
		int gmtOffset = now.getTimeZone().getOffset(now.getTimeInMillis());
		return gmtOffset / 3600000;
	}

	public static boolean isDaylightSavings() {
		Calendar now = new GregorianCalendar();
		return now.getTimeZone().inDaylightTime(now.getTime());
	}

	public static double getJulianDay() {
		Calendar now = new GregorianCalendar();
		return AstroLib.calculateJulianDay(now);
		/*return AstroLib.calculateJulianDay(now.get(Calendar.YEAR), (now
				.get(Calendar.MONTH) + 1), now.get(Calendar.DAY_OF_MONTH), 0,
				0, 0, 0);*/
	}

	

}
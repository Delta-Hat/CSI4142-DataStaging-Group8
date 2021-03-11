package dataUploader;

import java.io.Serializable;

public class SpecimenDate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8956656266834187130L;
	private int specimenDateKey;
	private int day;
	private int month;
	private String dayOfWeek;
	private boolean weekend;
	private boolean holiday;
	private String season;
	
	public SpecimenDate(int specimenDateKey, int day, int month, String dayOfWeek, boolean weekend, boolean holiday, String season) {
		this.setDay(day);
		this.setMonth(month);
		this.setDayOfWeek(dayOfWeek);
		this.setWeekend(weekend);
		this.setHoliday(holiday);
		this.setSeason(season);
	}
	
	public SpecimenDate() {
		
	}
	
	public int getSpecimenDateKey() {
		return specimenDateKey;
	}

	public void setSpecimenDateKey(int specimenDateKey) {
		this.specimenDateKey = specimenDateKey;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public boolean isWeekend() {
		return weekend;
	}

	public void setWeekend(boolean weekend) {
		this.weekend = weekend;
	}

	public boolean isHoliday() {
		return holiday;
	}

	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}
}

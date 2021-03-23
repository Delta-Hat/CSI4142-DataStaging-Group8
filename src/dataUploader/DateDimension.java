package dataUploader;

import java.io.Serializable;

public class DateDimension implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7487994074796913417L;
	private int dateDimensionKey;
	private int day;
	private int month;
	private String dayOfWeek;
	private boolean weekend;
	private boolean holiday;
	private String season;
	
	public DateDimension(int dateDimensionKey, int day, int month, String dayOfWeek, boolean weekend, boolean holiday, String season) {
		this.setDateDimensionKey(dateDimensionKey);
		this.setDay(day);
		this.setMonth(month);
		this.setDayOfWeek(dayOfWeek);
		this.setWeekend(weekend);
		this.setHoliday(holiday);
		this.setSeason(season);
	}
	
	public DateDimension() {
		
	}
	
	public String toString() {
		String output = "Date[";
		output += Integer.toString(dateDimensionKey) + ",";
		output += Integer.toString(day) + ",";
		output += Integer.toString(month) + ",";
		output += dayOfWeek + ",";
		output += Boolean.toString(weekend) + ",";
		output += Boolean.toString(holiday) + ",";
		output += season + "]";
		return output;
	}
	
	public int getDateDimensionKey() {
		return dateDimensionKey;
	}

	public void setDateDimensionKey(int DateKey) {
		this.dateDimensionKey = DateKey;
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

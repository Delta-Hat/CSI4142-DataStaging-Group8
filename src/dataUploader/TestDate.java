package dataUploader;

import java.io.Serializable;

public class TestDate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7487994074796913417L;
	private int testDateKey;
	private int day;
	private int month;
	private String dayOfWeek;
	private boolean weekend;
	private boolean holiday;
	private String season;
	
	public TestDate(int testDateKey, int day, int month, String dayOfWeek, boolean weekend, boolean holiday, String season) {
		this.setTestDateKey(testDateKey);
		this.setDay(day);
		this.setMonth(month);
		this.setDayOfWeek(dayOfWeek);
		this.setWeekend(weekend);
		this.setHoliday(holiday);
		this.setSeason(season);
	}
	
	public TestDate() {
		
	}
	
	public String toString() {
		String output = "TestDate[";
		output += Integer.toString(testDateKey) + ",";
		output += Integer.toString(day) + ",";
		output += Integer.toString(month) + ",";
		output += dayOfWeek + ",";
		output += Boolean.toString(weekend) + ",";
		output += Boolean.toString(holiday) + ",";
		output += season + "]";
		return output;
	}
	
	public int getTestDateKey() {
		return testDateKey;
	}

	public void setTestDateKey(int testDateKey) {
		this.testDateKey = testDateKey;
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

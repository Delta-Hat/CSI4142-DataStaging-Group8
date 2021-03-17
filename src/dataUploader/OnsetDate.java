package dataUploader;

import java.io.Serializable;

/**
 * Onset Date data object.
 * @author sean
 *
 */
public class OnsetDate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7965822188425751087L;
	private int onsetDateKey;
	private int day;
	private int month;
	private String dayOfWeek;
	private boolean weekend;
	private boolean holiday;
	private String season;
	
	public OnsetDate(int onsetDateKey, int day, int month, String dayOfWeek, boolean weekend, boolean holiday, String season) {
		this.setOnsetDateKey(onsetDateKey);
		this.setDay(day);
		this.setMonth(month);
		this.setDayOfWeek(dayOfWeek);
		this.setWeekend(weekend);
		this.setHoliday(holiday);
		this.setSeason(season);
	}
	
	public OnsetDate() {
		
	}
	
	public String toString() {
		String output = "OnsetDate[";
		output += Integer.toString(onsetDateKey) + ",";
		output += Integer.toString(day) + ",";
		output += Integer.toString(month) + ",";
		output += dayOfWeek + ",";
		output += Boolean.toString(weekend) + ",";
		output += Boolean.toString(holiday) + ",";
		output += season + "]";
		return output;
	}
	
	public int getOnsetDateKey() {
		return onsetDateKey;
	}

	public void setOnsetDateKey(int onsetDateKey) {
		this.onsetDateKey = onsetDateKey;
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

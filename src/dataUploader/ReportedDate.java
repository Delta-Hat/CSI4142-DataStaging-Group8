package dataUploader;

import java.io.Serializable;

/**
 * Data object for reported date.
 * @author sean
 *
 */
public class ReportedDate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -467670174675823608L;
	private int reportedDateKey;
	private int day;
	private int month;
	private String dayOfWeek;
	private boolean weekend;
	private boolean holiday;
	private String season;
	
	public ReportedDate(int reportedDateKey, int day, int month, String dayOfWeek, boolean weekend, boolean holiday, String season) {
		this.setDay(day);
		this.setMonth(month);
		this.setDayOfWeek(dayOfWeek);
		this.setWeekend(weekend);
		this.setHoliday(holiday);
		this.setSeason(season);
	}
	
	public ReportedDate() {
		
	}
	
	public int getReportedDateKey() {
		return reportedDateKey;
	}

	public void setReportedDateKey(int reportedDateKey) {
		this.reportedDateKey = reportedDateKey;
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

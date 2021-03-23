package dataUploader;

import java.io.Serializable;

public class Weather implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6457792713532055407L;
	private int weatherKey;
	private double dailyHighTemperature;
	private double dailyLowTemperature;
	private boolean percipitation;
	private int dateKey;
	private int locationKey;
	
	public Weather(int weatherKey, double dailyHighTemperature, double dailyLowTemperature, boolean percipitation, int dateKey, int locationKey) {
		this.setWeatherKey(weatherKey);
		this.setDailyHighTemperature(dailyHighTemperature);
		this.setDailyLowTemperature(dailyLowTemperature);
		this.setPercipitation(percipitation);
		this.setDateKey(dateKey);
		this.setLocationKey(locationKey);
	}
	
	public Weather(int weatherKey, double dailyHighTemperature, double dailyLowTemperature, boolean percipitation) {
		this.setWeatherKey(weatherKey);
		this.setDailyHighTemperature(dailyHighTemperature);
		this.setDailyLowTemperature(dailyLowTemperature);
		this.setPercipitation(percipitation);
	}
	
	public String toString() {
		String output = "Weather[";
		output += Integer.toString(getWeatherKey()) + ",";
		output += Double.toString(getDailyHighTemperature()) + ",";
		output += Double.toString(getDailyLowTemperature()) + ",";
		output += Boolean.toString(isPercipitation()) + "]";
		return output;
	}

	public int getWeatherKey() {
		return weatherKey;
	}

	public void setWeatherKey(int weatherKey) {
		this.weatherKey = weatherKey;
	}

	public double getDailyHighTemperature() {
		return dailyHighTemperature;
	}

	public void setDailyHighTemperature(double dailyHighTemperature) {
		this.dailyHighTemperature = dailyHighTemperature;
	}

	public double getDailyLowTemperature() {
		return dailyLowTemperature;
	}

	public void setDailyLowTemperature(double dailyLowTemperature) {
		this.dailyLowTemperature = dailyLowTemperature;
	}

	public boolean isPercipitation() {
		return percipitation;
	}

	public void setPercipitation(boolean percipitation) {
		this.percipitation = percipitation;
	}

	public int getDateKey() {
		return dateKey;
	}

	public void setDateKey(int dateKey) {
		this.dateKey = dateKey;
	}

	public int getLocationKey() {
		return locationKey;
	}

	public void setLocationKey(int locationKey) {
		this.locationKey = locationKey;
	}
}

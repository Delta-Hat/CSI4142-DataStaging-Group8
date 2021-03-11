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
	
	public Weather(int weatherKey, double dailyHighTemperature, double dailyLowTemperature, boolean percipitation) {
		this.setWeatherKey(weatherKey);
		this.setDailyHighTemperature(dailyHighTemperature);
		this.setDailyLowTemperature(dailyLowTemperature);
		this.setPercipitation(percipitation);
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
}

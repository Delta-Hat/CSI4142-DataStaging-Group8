package dataUploader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class DataUploader {
	final static String COVID_PATIENT_FILE = "conposcovidloc.csv";
	final static String TORONTO_WEATHER_FILE = "en_climate_daily_ON_6158355_2020_P1D.csv";
	final static String OTTAWA_WEATHER_FILE = "en_climate_daily_ON_6105978_2020_P1D.csv";
	final static String MOBILITY_DATA_FILE = "2020_CA_Region_Mobility_Report.csv";
	final static String MOBILITY_TORONTO = "Toronto Division";
	final static String MOBILITY_OTTAWA = "Ottawa Division";
	final static String WEATHER_TORONTO = "TORONTO CITY";
	final static String WEATHER_OTTAWA = "OTTAWA CDA RCS";
	
	public static void main(String[] args) {
		
		ArrayList<String> mobilityData = getMobilityData();
		//for(String line : mobilityData) {
		//	System.out.println(line);
		//}
		
		ArrayList<String> weatherData = getWeatherData();
		
		
		weatherData = cleanseWeatherData(weatherData);
		
		for(String line : weatherData) {
			System.out.println(line);
		}
	}
	
	/**
	 * This only works if the first and last data are complete.
	 * Our current data source is as such.
	 * 
	 * 
	 * @param weatherData
	 * @return
	 */
	private static ArrayList<String> cleanseWeatherData(ArrayList<String> weatherData){
		ArrayList<String> cleansedWeatherData = new ArrayList<String>();
		for(int i = 0; i < weatherData.size(); i++) {
			String[] old = weatherData.get(i).split(",");
			String current;
			int index = 9;
			if(old[index].equals("")) {
				int j = 1;
				String[] prev = weatherData.get(i-1).split(",");
				String[] next = weatherData.get(i+j).split(",");
				while(next[index].equals("")) {
					j++;
					next = weatherData.get(i+j).split(",");
				}
				old[index] = Double.toString((Double.parseDouble(prev[index]) + Double.parseDouble(next[index]))/2);
			}
			index = 11;
			if(old[index].equals("")) {
				int j = 1;
				String[] prev = weatherData.get(i-1).split(",");
				String[] next = weatherData.get(i+j).split(",");
				while(next[index].equals("")) {
					j++;
					next = weatherData.get(i+j).split(",");
				}
				old[index] = Double.toString((Double.parseDouble(prev[index]) + Double.parseDouble(next[index]))/2);
			}
			index = 23;
			if(old[index].equals("")) {
				int j = 1;
				String[] prev = weatherData.get(i-1).split(",");
				String[] next = weatherData.get(i+j).split(",");
				while(next[index].equals("")) {
					j++;
					next = weatherData.get(i+j).split(",");
				}
				old[index] = Double.toString((Double.parseDouble(prev[index]) + Double.parseDouble(next[index]))/2);
			}
			current = old[0];
			for(int j = 1; j < old.length; j++) {
				current = current + "," + old[j];
			}
			cleansedWeatherData.add(current);
		}
		
		return cleansedWeatherData;
	}
	
	
	private static ArrayList<String> getWeatherData(){
		ArrayList<String> weatherData = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(TORONTO_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while((inputLine = inputMobile.readLine()) != null) {
			
			
					weatherData.add(inputLine);
				
			}
			inputMobile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(OTTAWA_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while((inputLine = inputMobile.readLine()) != null) {
				
					weatherData.add(inputLine);
				
			}
			inputMobile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return weatherData;
	}
	
	/**
	 * Separates the relevant mobility data from the file.
	 * @return
	 */
	private static ArrayList<String> getMobilityData(){
		ArrayList<String> mobilityData = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(MOBILITY_DATA_FILE));
			while((inputLine = inputMobile.readLine()) != null) {
				String[] data = inputLine.split(",");
				if(data[3].equals(MOBILITY_OTTAWA) || data[3].equals(MOBILITY_TORONTO)) {
					mobilityData.add(inputLine);
				}
			}
			inputMobile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mobilityData;
	}
	

}

package dataUploader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 * A reasonable person might look at this code and ask a whole bunch of questions.
 * Everything in this file is only useful for our data science project and, if were to be
 * used in any other context, would have to be heavily modified.
 * 
 * This class only serves the purpose of extracting the specific given files and uploading it to an
 * SQL database.
 * 
 * @author sean
 *
 */
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
		
		//for(String line : weatherData) {
		//	System.out.println(line);
		//}
		
		ArrayList<String> patientData = getPatientData();
		
		
		//for(String line : patientData) {
		//	System.out.println(line);
		//}
		
	}
	
	/**
	 * Extracts the patient data from the file.
	 * Thankfully, there doesn't seem to be anything that requires omission.
	 * 
	 * In a heavier duty operation, loading an entire data source into the RAM of a personal computer
	 * would be a bad idea. Thankfully, since I can open the CSV in Excel, loading the whole file into an
	 * ArrayList is *probably* not a huge problem.
	 * 
	 * Printing the whole file to the console does take time, however.
	 * @return
	 */
	private static ArrayList<String> getPatientData(){
		ArrayList<String> patientData = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputPatient = new BufferedReader(new FileReader(COVID_PATIENT_FILE));
			inputLine = inputPatient.readLine();//removes the header lines.
			while((inputLine = inputPatient.readLine()) != null) {
				patientData.add(inputLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patientData;
	}
	
	/**
	 * This only works if the first and last data are complete.
	 * Our current data source is as such.
	 * 
	 * Missing weather data cannot be omitted which is why we clean it like this.
	 * 
	 * @param weatherData
	 * @return
	 */
	private static ArrayList<String> cleanseWeatherData(ArrayList<String> weatherData){
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
			weatherData.set(i, current);
		}
		
		return weatherData;
	}
	
	/**
	 * Retrieves the weather data from given csv files.
	 * The weather data files have quotations in them for some reason so they are removed as part of the process.
	 * @return
	 */
	private static ArrayList<String> getWeatherData(){
		ArrayList<String> weatherData = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(TORONTO_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while((inputLine = inputMobile.readLine()) != null) {
				//I have no idea why but the raw csv stores the values in quotations
				//There for it is nessasarry to remove all quotations
				inputLine = removeQuotations(inputLine);
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
				inputLine = removeQuotations(inputLine);
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
	 * Removes quotations from given string
	 * @param input
	 * @return
	 */
	private static String removeQuotations(String input) {
		String[] segments = input.split("\"");
		String output = "";
		for(int i = 0; i < segments.length; i++) {
			output += segments[i];
		}
		return output;
	}
	/**
	 * Separates the relevant mobility data from the file.
	 * The file contains a bunch of mobility data we do not need.
	 * Therefore, only the mobility data in Ottawa and Toronto are extracted.
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

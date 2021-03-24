package dataUploader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A reasonable person might look at this code and ask a whole bunch of
 * questions. Everything in this file is only useful for our data science
 * project and, if were to be used in any other context, would have to be
 * heavily modified.
 * 
 * This class only serves the purpose of extracting the specific given files and
 * uploading it to an SQL database.
 * 
 * @author sean
 *
 */
public class DataUploader {
	final static String COVID_PATIENT_FILE = "conposcovidloc.csv";
	final static String TORONTO_WEATHER_FILE = "en_climate_daily_ON_6158355_2020_P1D.csv";
	final static String OTTAWA_WEATHER_FILE = "en_climate_daily_ON_6105978_2020_P1D.csv";
	final static String DURHAM_WEATHER_FILE = "en_climate_daily_ON_6155875_2020_P1D.csv";//Oshawa weather report
	final static String HALTON_WEATHER_FILE = "en_climate_daily_ON_6155750_2020_P1D.csv";//Oakvill weather report
	final static String PEEL_WEATHER_FILE = "en_climate_daily_ON_6158731_2020_P1D.csv";//Toronto Pearson airport is in mississauga.
	final static String YORK_WEATHER_FILE = "en_climate_daily_ON_6154150_2020_P1D.csv";//King city north
	final static String MOBILITY_DATA_FILE = "2020_CA_Region_Mobility_Report.csv";
	final static String MOBILITY_TORONTO = "Toronto Division";
	final static String MOBILITY_OTTAWA = "Ottawa Division";
	final static String MOBILITY_DURHAM = "Regional Municipality of Durham";
	final static String MOBILITY_HALTON = "Regional Municipality of Halton";
	final static String MOBILITY_PEEL = "Regional Municipality of Peel";
	final static String MOBILITY_YORK = "Regional Municipality of York";
	final static String WEATHER_TORONTO = "TORONTO CITY";
	final static String WEATHER_OTTAWA = "OTTAWA CDA RCS";
	final static String WEATHER_DURHAM = "OSHAWA";
	final static String WEATHER_HALTON = "OAKVILLE TWN";
	final static String WEATHER_PEEL = "TORONTO INTL A";
	final static String WEATHER_YORK = "KING CITY NORTH";
	final static String REPORTING_PHU_CITY_TORONTO = "Toronto";
	final static String REPORTING_PHU_CITY_OTTAWA = "Ottawa";
	final static String REPORTING_PHU_CITY_DURHAM = "Whitby";
	final static String REPORTING_PHU_CITY_HALTON = "Oakville";
	final static String REPORTING_PHU_CITY_PEEL = "Mississauga";
	final static String REPORTING_PHU_CITY_YORK = "Newmarket";
	//change these if you want to test this out on a new database.
	final static String HOST = "www.eecs.uottawa.ca";
	final static String PORT = "15432";
	final static String DATABASE = "group_8";
	private int reportedDateNumber = 1;
	private int onsetDateNumber = 1;
	private int testDateNumber = 1;
	private int specimenDateNumber = 1;
	private int weatherNumber = 1;
	private int mobilityNumber = 1;
	private int phuLocationKey = 1;
	private int patientKey = 1;
	private int specialMeasuresKey = 1;
	private int dateNumber = 1;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Insufficient args. Usage: DataUploader <username> <password>");
			System.exit(0);
		}
		(new DataUploader()).run(args);// I love doing this cause it's so weird.
	}

	private void run(String[] args) {
		

		ArrayList<String> mobilityData = getMobilityData();
		for(String line : mobilityData) {
		   System.out.println(line);
		}

		ArrayList<String> weatherData = getWeatherData();	

		weatherData = cleanseWeatherData(weatherData);
		
		for(String weatherLine : weatherData) {
			System.out.println(weatherLine);
		}
		
		ArrayList<String> patientData = getPatientData();
		
		for(String patientLine : patientData) {
			System.out.println(patientLine);
		}
		
		ArrayList<DateDimension> dateDimensionList = generateDateDimension();
		for(DateDimension dateDimension : dateDimensionList) {
			System.out.println(dateDimension);
		}
		
		ArrayList<PhuLocation> phuLocationList = generatePhuLocationDimension(patientData);
		for(PhuLocation phuLocation : phuLocationList) {
			System.out.println(phuLocation);
		}
		
		ArrayList<Mobility> mobilityList = generateMobilityDimension(mobilityData, dateDimensionList, phuLocationList);
		for(Mobility mobility : mobilityList) {
			System.out.println(mobility);
		}
		
		ArrayList<Weather> weatherList = generateWeatherDimension(weatherData, dateDimensionList, phuLocationList);
		for(Weather weather : weatherList) {
			System.out.println(weather);
		}
		
		
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		ArrayList<Fact> factList = generateFactDimension(patientData, dateDimensionList, mobilityList, weatherList, patientList, phuLocationList);
		System.out.println("patientList size: " + patientList.size());
		
		
		
		try (Connection connection = getConnection(args)) {
			
		
		
		for(DateDimension dateDimension : dateDimensionList) {
			try {
				uploadDateDimension(dateDimension,connection);
			}catch(SQLException e) {
				System.out.println(e);
				System.out.println("Skipping entry...");
			}
		}
		
		for(PhuLocation phuLocation : phuLocationList) {
			try {
				uploadPhuLocation(phuLocation,connection);	
			}catch(SQLException e) {
				System.out.println(e);
				System.out.println("Skipping entry...");
			}
		}
		
		for(Mobility mobility : mobilityList) {
			try {
				uploadMobility(mobility,connection);
			}catch(SQLException e) {
				System.out.println(e);
				System.out.println("Skipping entry...");
			}
		}
		
		for(Weather weather : weatherList) {
			try {
				uploadWeather(weather,connection);
			}catch(SQLException e) {
				System.out.println(e);
				System.out.println("Skipping entry...");
			}
		}
		
		for(Patient patient : patientList) {
			try {
				uploadPatient(patient,connection);
			}catch(SQLException e) {
				System.out.println(e);
				System.out.println("Skipping entry...");
			}
		}
		
		
		//I've commented this out because if this is run a second time, it will break the fact table.
		//Only run this if the fact table is empty.
		//Please.
		/*
		for(Fact fact : factList) {
			try {
				uploadFact(fact,connection);
			}catch(SQLException e) {
				System.out.println(e);
				System.out.println("Skipping entry...");
			}
		}
		*/
		
		
		connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
			
		//ArrayList<Patient> patientList = generatePatientDimension(patientData);
		//for(Patient patient : patientList) {
		//	System.out.println(patient);
		//}
		
		
		

		// for(String line : weatherData) {
		// System.out.println(line);
		// }
		/*
		
		ArrayList<Fact> factList = new ArrayList<Fact>();
		for (String line : patientData) {
			ReportedDate reportedDate = toReportedDate(line);
			System.out.println(line);
			System.out.println(reportedDate);
			OnsetDate onsetDate = toOnsetDate(line);
			System.out.println(onsetDate);
			TestDate testDate = toTestDate(line);
			System.out.println(testDate);
			SpecimenDate specimenDate = toSpecimenDate(line);
			System.out.println(specimenDate);
			Weather weather = toWeather(line, weatherData);
			System.out.println(weather);
			Mobility mobility = toMobility(line, mobilityData);
			System.out.println(mobility);
			PhuLocation phuLocation = toPhuLocation(line);
			System.out.println(phuLocation);
			Patient patient = toPatient(line);
			System.out.println(patient);
			SpecialMeasures specialMeasures = toSpecialMeasures(line);
			System.out.println(specialMeasures);
			Fact fact = toFact(onsetDate, reportedDate, testDate, specimenDate, patient, phuLocation, mobility,
					specialMeasures, weather, line);
			System.out.println(fact);
			factList.add(fact);
		}
		*/
		
		/*
		try (Connection connection = getConnection(args)) {
			int totalNulls = 0;
			for(Fact fact : factList) {
				totalNulls += upload(fact,connection);
			}
			System.out.println("Total nulls: " + totalNulls);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		*/

	}
	
	public ArrayList<Fact> generateFactDimension(ArrayList<String> patientData, ArrayList<DateDimension> dateList, ArrayList<Mobility> mobilityList, ArrayList<Weather> weatherList, ArrayList<Patient> patientList, ArrayList<PhuLocation> phuLocationList){
		ArrayList<Fact> factList = new ArrayList<Fact>();
		int missingReportedDates = 0;
		int missingOnsetDates = 0;
		int missingTestDates = 0;
		int missingSpecimenDates = 0;
		int missingPhuLocations = 0;
		int missingWeathers = 0;
		int skipedPatients = 0;
		int recordedPatients = 0;
		int missingMobility = 0;
		for(String patientLine : patientData) {
			int reportedDateKey;
			int onsetDateKey;
			int testDateKey;
			int specimenDateKey;
			int phuLocationKey;
			boolean missing = false;
			ReportedDate reportedDate;
			
			try {
			reportedDate = toReportedDate(patientLine);
			reportedDateKey = getDateIndex(reportedDate.getMonth(),reportedDate.getDay(),dateList);
			} catch (NullPointerException e) {
				reportedDateKey = 0;	
			}
			if(reportedDateKey == 0) {
				missingReportedDates++;
				missing = true;
			}
			try {
			OnsetDate onsetDate = toOnsetDate(patientLine);
			onsetDateKey = getDateIndex(onsetDate.getMonth(),onsetDate.getDay(),dateList);
			} catch (NullPointerException e) {
				onsetDateKey = 0;
			}
			if(onsetDateKey == 0) {
				missingOnsetDates++;
				missing = true;
			}
			try {
			TestDate testDate = toTestDate(patientLine);
			testDateKey = getDateIndex(testDate.getMonth(),testDate.getDay(),dateList);
			} catch (NullPointerException e) {
				testDateKey = 0;
			}
			if(testDateKey == 0) {
				missingOnsetDates++;
				missing = true;
			}
			try {
			SpecimenDate specimenDate = toSpecimenDate(patientLine);
			specimenDateKey = getDateIndex(specimenDate.getMonth(),specimenDate.getDay(),dateList);
			} catch (NullPointerException e) {
				specimenDateKey = 0;
			}
			if(specimenDateKey == 0) {
				missingSpecimenDates++;
				missing = true;
			}
			if(missing == true) {
				skipedPatients++;
				continue;
			}
			
			try {
			PhuLocation phuLocation = toPhuLocation(patientLine);
			phuLocationKey = getPhuLocationIndex(phuLocation.getCity(), phuLocationList);
			} catch (NullPointerException e) {
				phuLocationKey = 0;
				missingPhuLocations++;
			}
			int weatherKey = getWeatherIndex(reportedDateKey, phuLocationKey, weatherList);
			if(weatherKey == 0) {
				missingWeathers++;
			}
			Patient patient = toPatient(patientLine);
			patientList.add(patient);
			int patientKey = patient.getPatientKey();
			int specialMeasuresKey = 0;
			int mobilityKey = getMobilityKey(reportedDateKey, phuLocationKey, mobilityList);
			if(mobilityKey == 0) {
				missingMobility++;
			}
			String[] line = patientLine.split(",");
			String status = line[8];
			boolean resolved = "Resolved".equals(status);
			boolean unresolved = "Not Resolved".equals(status);
			boolean fatal = "Fatal".equals(status);
			Fact fact = new Fact();
			fact.resolved = resolved;
			fact.unresolved = unresolved;
			fact.fatal = fatal;
			fact.onsetDateKey = onsetDateKey;
			fact.reportedDateKey = reportedDateKey;
			fact.testDateKey = testDateKey;
			fact.specimenDateKey = specimenDateKey;
			fact.phuLocationKey = phuLocationKey;
			fact.weatherKey = weatherKey;
			fact.patientKey = patientKey;
			fact.mobilityKey = mobilityKey;
			fact.specialMeasuresKey = specialMeasuresKey;
			System.out.println("" + onsetDateKey + "," + reportedDateKey + "," + testDateKey + "," + specimenDateKey + "," + phuLocationKey + "," + weatherKey + "," + patientKey + "," + mobilityKey);
			recordedPatients++;
			factList.add(fact);
		}
		System.out.println("Missing reported dates: " + missingReportedDates);
		System.out.println("Missing onset dates: " + missingOnsetDates);
		System.out.println("Missing test dates: " + missingTestDates);
		System.out.println("Missing specimen dates: " + missingSpecimenDates);
		System.out.println("Missing phu locations: " + missingPhuLocations);
		System.out.println("Missing weathers: " + missingWeathers);
		System.out.println("Missing mobility: " + missingMobility);
		System.out.println("Skiped patients: " + skipedPatients);
		System.out.println("Recorded patients: " + recordedPatients);
		System.out.println("factList size: " + factList.size());
		return factList;
	}
	
	private int getMobilityKey(int dateKey, int locationKey, ArrayList<Mobility> mobilityList) {
		for(Mobility mobility : mobilityList) {
			if(mobility.getDateKey() == dateKey && mobility.getLocationKey() == locationKey) {
				return mobility.getMobilityKey();
			}
		}
		return 0;
	}
	
	private int getWeatherIndex(int dateKey, int locationKey, ArrayList<Weather> weatherList) {
		for(Weather weather : weatherList) {
			if(weather.getDateKey() == dateKey && weather.getLocationKey() == locationKey) {
				return weather.getWeatherKey();
			}
		}
		return 0;
	}
	
	private int getDateIndex(int month, int day, ArrayList<DateDimension> dateList) {
		for(DateDimension date : dateList) {
			if(date.getMonth() == month && date.getDay() == day) {
				return date.getDateDimensionKey();
			}
		}
		return 0;
	}
	
	private int getPhuLocationIndex(String location, ArrayList<PhuLocation> phuLocationList) {
		for(PhuLocation phuLocation : phuLocationList) {
			if(phuLocation.getCity().equals(location)) {
				return phuLocation.getPhuLocationKey();
			}
		}
		return 0;
	}
	
	public ArrayList<Patient> generatePatientDimension(ArrayList<String> patientData, ArrayList<DateDimension> dateList){
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		for(String patientLine : patientData) {
			Patient patient = toPatient(patientLine);
			patientList.add(patient);
		}
		return patientList;
	}
	
	public ArrayList<PhuLocation> generatePhuLocationDimension(ArrayList<String> patientData){
		ArrayList<PhuLocation> phuLocationList = new ArrayList<PhuLocation>();
		for(String patientLine : patientData) {
			
			if(!inPhuLocationList(patientLine, phuLocationList)) {
				phuLocationList.add(toPhuLocation(patientLine));
			}
		}
		
		return phuLocationList;
	}
	
	public ArrayList<Weather> generateWeatherDimension(ArrayList<String> weatherData, ArrayList<DateDimension> dateList, ArrayList<PhuLocation> phuLocationList){
		ArrayList<Weather> weatherList = new ArrayList<Weather>();
		for(DateDimension date : dateList) {
			String weatherLine = getWeatherLineFromDateAndLocation(2020, date.getMonth(), date.getDay(), REPORTING_PHU_CITY_TORONTO, weatherData);
			String[] weatherValues = weatherLine.split(",");
			double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
			double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
			boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
			int dateKey = getDateIndex(date.getMonth(), date.getDay(), dateList);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_TORONTO, phuLocationList);
			Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation,dateKey,locationKey);
			weatherNumber++;
			weatherList.add(weather);
		}
		for(DateDimension date : dateList) {
			String weatherLine = getWeatherLineFromDateAndLocation(2020, date.getMonth(), date.getDay(), REPORTING_PHU_CITY_OTTAWA, weatherData);
			String[] weatherValues = weatherLine.split(",");
			double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
			double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
			boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
			int dateKey = getDateIndex(date.getMonth(), date.getDay(), dateList);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_OTTAWA, phuLocationList);
			Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation,dateKey,locationKey);
			weatherNumber++;
			weatherList.add(weather);
		}
		for(DateDimension date : dateList) {
			String weatherLine = getWeatherLineFromDateAndLocation(2020, date.getMonth(), date.getDay(), REPORTING_PHU_CITY_DURHAM, weatherData);
			String[] weatherValues = weatherLine.split(",");
			double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
			double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
			boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
			int dateKey = getDateIndex(date.getMonth(), date.getDay(), dateList);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_DURHAM, phuLocationList);
			Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation,dateKey,locationKey);
			weatherNumber++;
			weatherList.add(weather);
		}
		for(DateDimension date : dateList) {
			String weatherLine = getWeatherLineFromDateAndLocation(2020, date.getMonth(), date.getDay(), REPORTING_PHU_CITY_HALTON, weatherData);
			String[] weatherValues = weatherLine.split(",");
			double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
			double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
			boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
			int dateKey = getDateIndex(date.getMonth(), date.getDay(), dateList);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_HALTON, phuLocationList);
			Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation,dateKey,locationKey);
			weatherNumber++;
			weatherList.add(weather);
		}
		for(DateDimension date : dateList) {
			String weatherLine = getWeatherLineFromDateAndLocation(2020, date.getMonth(), date.getDay(), REPORTING_PHU_CITY_PEEL, weatherData);
			String[] weatherValues = weatherLine.split(",");
			double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
			double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
			boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
			int dateKey = getDateIndex(date.getMonth(), date.getDay(), dateList);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_PEEL, phuLocationList);
			Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation,dateKey,locationKey);
			weatherNumber++;
			weatherList.add(weather);
		}
		for(DateDimension date : dateList) {
			String weatherLine = getWeatherLineFromDateAndLocation(2020, date.getMonth(), date.getDay(), REPORTING_PHU_CITY_YORK, weatherData);
			String[] weatherValues = weatherLine.split(",");
			double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
			double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
			boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
			int dateKey = getDateIndex(date.getMonth(), date.getDay(), dateList);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_YORK, phuLocationList);
			Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation,dateKey,locationKey);
			weatherNumber++;
			weatherList.add(weather);
		}
		return weatherList;
	}
	
	
	public ArrayList<Mobility> generateMobilityDimension(ArrayList<String> list , ArrayList<DateDimension> dateList, ArrayList<PhuLocation> phuLocationList){
		ArrayList<Mobility> mobilityList = new ArrayList<Mobility>();
		for (DateDimension date : dateList) {
			String mobilityLine = getMobilityLineFromDateAndLocation(2020,date.getMonth(),date.getDay(),REPORTING_PHU_CITY_TORONTO,list);
			String[] mobilityValues = mobilityLine.split(",");
			String subRegion = mobilityValues[3];
			String province = mobilityValues[2];
			int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
			int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
			int parks = Integer.parseInt(mobilityValues[10]);
			int transitStations = Integer.parseInt(mobilityValues[11]);
			int workplaces = Integer.parseInt(mobilityValues[12]);
			int residential = Integer.parseInt(mobilityValues[13]);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_TORONTO, phuLocationList);
			Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
					parks, transitStations, workplaces, residential, date.getDateDimensionKey(), locationKey);
			mobilityNumber++;
			mobilityList.add(mobility);
		}
		for (DateDimension date : dateList) {
			String mobilityLine = getMobilityLineFromDateAndLocation(2020,date.getMonth(),date.getDay(),REPORTING_PHU_CITY_OTTAWA,list);
			String[] mobilityValues = mobilityLine.split(",");
			String subRegion = mobilityValues[3];
			String province = mobilityValues[2];
			int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
			int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
			int parks = Integer.parseInt(mobilityValues[10]);
			int transitStations = Integer.parseInt(mobilityValues[11]);
			int workplaces = Integer.parseInt(mobilityValues[12]);
			int residential = Integer.parseInt(mobilityValues[13]);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_OTTAWA, phuLocationList);
			Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
					parks, transitStations, workplaces, residential, date.getDateDimensionKey(), locationKey);
			mobilityNumber++;
			mobilityList.add(mobility);
		}
		for (DateDimension date : dateList) {
			String mobilityLine = getMobilityLineFromDateAndLocation(2020,date.getMonth(),date.getDay(),REPORTING_PHU_CITY_DURHAM,list);
			String[] mobilityValues = mobilityLine.split(",");
			String subRegion = mobilityValues[3];
			String province = mobilityValues[2];
			int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
			int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
			int parks = Integer.parseInt(mobilityValues[10]);
			int transitStations = Integer.parseInt(mobilityValues[11]);
			int workplaces = Integer.parseInt(mobilityValues[12]);
			int residential = Integer.parseInt(mobilityValues[13]);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_DURHAM, phuLocationList);
			Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
					parks, transitStations, workplaces, residential, date.getDateDimensionKey(), locationKey);
			mobilityNumber++;
			mobilityList.add(mobility);
		}
		for (DateDimension date : dateList) {
			String mobilityLine = getMobilityLineFromDateAndLocation(2020,date.getMonth(),date.getDay(),REPORTING_PHU_CITY_HALTON,list);
			String[] mobilityValues = mobilityLine.split(",");
			String subRegion = mobilityValues[3];
			String province = mobilityValues[2];
			int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
			int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
			int parks = Integer.parseInt(mobilityValues[10]);
			int transitStations = Integer.parseInt(mobilityValues[11]);
			int workplaces = Integer.parseInt(mobilityValues[12]);
			int residential = Integer.parseInt(mobilityValues[13]);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_HALTON, phuLocationList);
			Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
					parks, transitStations, workplaces, residential, date.getDateDimensionKey(), locationKey);
			mobilityNumber++;
			mobilityList.add(mobility);
		}
		for (DateDimension date : dateList) {
			String mobilityLine = getMobilityLineFromDateAndLocation(2020,date.getMonth(),date.getDay(),REPORTING_PHU_CITY_PEEL,list);
			String[] mobilityValues = mobilityLine.split(",");
			String subRegion = mobilityValues[3];
			String province = mobilityValues[2];
			int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
			int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
			int parks = Integer.parseInt(mobilityValues[10]);
			int transitStations = Integer.parseInt(mobilityValues[11]);
			int workplaces = Integer.parseInt(mobilityValues[12]);
			int residential = Integer.parseInt(mobilityValues[13]);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_PEEL, phuLocationList);
			Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
					parks, transitStations, workplaces, residential, date.getDateDimensionKey(), locationKey);
			mobilityNumber++;
			mobilityList.add(mobility);
		}
		for (DateDimension date : dateList) {
			String mobilityLine = getMobilityLineFromDateAndLocation(2020,date.getMonth(),date.getDay(),REPORTING_PHU_CITY_YORK,list);
			String[] mobilityValues = mobilityLine.split(",");
			String subRegion = mobilityValues[3];
			String province = mobilityValues[2];
			int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
			int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
			int parks = Integer.parseInt(mobilityValues[10]);
			int transitStations = Integer.parseInt(mobilityValues[11]);
			int workplaces = Integer.parseInt(mobilityValues[12]);
			int residential = Integer.parseInt(mobilityValues[13]);
			int locationKey = getPhuLocationIndex(REPORTING_PHU_CITY_YORK, phuLocationList);
			Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
					parks, transitStations, workplaces, residential, date.getDateDimensionKey(), locationKey);
			mobilityNumber++;
			mobilityList.add(mobility);
		}
		return mobilityList;
	}

	public ArrayList<DateDimension> generateDateDimension(){
		ArrayList<DateDimension> dateList = new ArrayList<DateDimension>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(2020, 1, 15);//the calendar object stores February as a 2 because it's bad
		
		for(int i = 0; i < 120; i++) {
			calendar.getTimeInMillis();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;//case in point
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayOfWeek = getDayOfWeekFromEnumeration(calendar.get(Calendar.DAY_OF_WEEK));
			boolean weekend = getWeekendFromDayOfWeek(dayOfWeek);
			boolean holiday = getHolidayFromMonthAndDay(month, day);
			String season = getSeasonFromMonth(month);
			DateDimension date = new DateDimension(dateNumber, day, month, dayOfWeek, weekend, holiday,
					season);
			dateNumber++;
			dateList.add(date);
			//calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400000);//increments by one day.
			calendar.add(Calendar.DATE,1);
		}
		
		
		
		return dateList;
	}
	
	
	public void uploadFact(Fact fact, Connection connection) throws SQLException {
		String factQuery = "INSERT INTO fact (onset_date_key, reported_date_key, test_date_key, specimen_date_key, patient_key, phu_location_key, mobility_key, special_measures_key, weather_key, resolved, fatal, unresolved) ";
		factQuery += "VALUES ("
				+ Integer.toString(fact.onsetDateKey) + ", "
				+ Integer.toString(fact.reportedDateKey) + ", "
				+ Integer.toString(fact.testDateKey) + ", "
				+ Integer.toString(fact.specimenDateKey) + ", "
				+ Integer.toString(fact.patientKey) + ", "
				+ Integer.toString(fact.phuLocationKey) + ", "
				+ Integer.toString(fact.mobilityKey) + ", "
				+ Integer.toString(fact.specialMeasuresKey) + ", "
				+ Integer.toString(fact.weatherKey) + ", "
				+ Boolean.toString(fact.resolved).toUpperCase() + ", "
				+ Boolean.toString(fact.fatal).toUpperCase() + ", "
				+ Boolean.toString(fact.unresolved).toUpperCase() + ");";
		System.out.println(factQuery);
		Statement statement = connection.createStatement();
		statement.executeUpdate(factQuery);
	}
	
	public void uploadPatient(Patient patient, Connection connection) throws SQLException {
		String patientQuery = "INSERT INTO patient_dimension (patient_key, acquisition_group, age_group, gender, outbreak_related) ";
		patientQuery += "VALUES ("
				+ Integer.toString(patient.getPatientKey()) + ", "
				+ "'" + patient.getAcquisitionGroup() + "', "
				+ "'" + patient.getAgeGroup() + "', "
				+ "'" + patient.getGender() + "', "
				+ Boolean.toString(patient.isOutBreakRelated()).toUpperCase() + ");";
		System.out.println(patientQuery);
		Statement statement = connection.createStatement();
		statement.executeUpdate(patientQuery);
	}
	
	public void uploadWeather(Weather weather, Connection connection) throws SQLException {
		String weatherQuery = "INSERT INTO weather_dimension (weather_key, date_key, location_key, daily_high_temperature, daily_low_temperature, precipitation) ";
		weatherQuery += "VALUES ("
				+ Integer.toString(weather.getWeatherKey()) + ", "
				+ Integer.toString(weather.getDateKey()) + ", "
				+ Integer.toString(weather.getLocationKey()) + ", "
				+ Double.toString(weather.getDailyHighTemperature()) + ", "
				+ Double.toString(weather.getDailyLowTemperature()) + ", "
				+ Boolean.toString(weather.isPercipitation()).toUpperCase() + ");";
		System.out.println(weatherQuery);
		Statement statement = connection.createStatement();
		statement.executeUpdate(weatherQuery);
	}
	
	public void uploadMobility(Mobility mobility, Connection connection) throws SQLException {
		String mobilityQuery = "INSERT INTO mobility_dimension (mobility_key, date_key, location_key, sub_region, province, retail_and_recreation, grocery_and_pharmacy, parks, transit_stations, workplaces, residential) ";
		mobilityQuery += "VALUES ("
				+ Integer.toString(mobility.getMobilityKey()) + ", "
				+ Integer.toString(mobility.getDateKey()) + ", "
				+ Integer.toString(mobility.getLocationKey()) + ", "
				+ "'" + mobility.getSubRegion() + "', "
				+ "'" + mobility.getProvince() + "', "
				+ Integer.toString(mobility.getRetailAndRecreation()) + ", "
				+ Integer.toString(mobility.getGroceryAndPharmacy()) + ", "
				+ Integer.toString(mobility.getParks()) + ", "
				+ Integer.toString(mobility.getTransitStations()) + ", "
				+ Integer.toString(mobility.getWorkplaces()) + ", "
				+ Integer.toString(mobility.getResidential()) + ");";
		System.out.println(mobilityQuery);
		Statement statement = connection.createStatement();
		statement.executeUpdate(mobilityQuery);
	}
	
	public void uploadPhuLocation(PhuLocation phuLocation, Connection connection) throws SQLException {
		String phuLocationQuery = "INSERT INTO phu_location_dimension (phu_location_key, phu_name, address, city, postal_code, province, url, latitude, longitude) ";
		phuLocationQuery += "VALUES ("
				+ Integer.toString(phuLocation.getPhuLocationKey()) + ", "
				+ "'" + phuLocation.getPhuName() + "', "
				+ "'" + phuLocation.getAddress() + "', "
				+ "'" + phuLocation.getCity() + "', "
				+ "'" + phuLocation.getPostalCode() + "', "
				+ "'" + phuLocation.getProvince() + "', "
				+ "'" + phuLocation.getUrl() + "', "
				+ Double.toString(phuLocation.getLatitude()) + ", "
				+ Double.toString(phuLocation.getLongitude()) + ");";
		System.out.println(phuLocationQuery);
		Statement statement = connection.createStatement();
		statement.executeUpdate(phuLocationQuery);
	}
	
	public void uploadDateDimension(DateDimension date, Connection connection) throws SQLException {
		String dateDimensionQuery = "INSERT INTO date_dimension (date_key, day, month, day_of_week, weekend, holiday, season) ";
		dateDimensionQuery += "VALUES ("
				+ Integer.toString(date.getDateDimensionKey()) + ", "
				+ Integer.toString(date.getDay()) + ", "
				+ Integer.toString(date.getMonth()) + ", "
				+ "'" + date.getDayOfWeek() + "', "
				+ Boolean.toString(date.isWeekend()).toUpperCase() + ", "
				+ Boolean.toString(date.isHoliday()).toLowerCase() + ", "
				+ "'" + date.getSeason() + "');";
		System.out.println(dateDimensionQuery);
		Statement statement = connection.createStatement();
		statement.executeUpdate(dateDimensionQuery);
	}
	
	public int upload(Fact fact, Connection connection) throws SQLException {
		int totalNulls = 0;
		
		String reportedDateQuery = "INSERT INTO reported_date_dimension (reported_date_key, day, month, day_of_week, weekend, holiday, season) ";
		if(fact.reportedDate != null) {
			reportedDateQuery += "VALUES ("
					+ Integer.toString(fact.reportedDate.getReportedDateKey()) + ", "
					+ Integer.toString(fact.reportedDate.getDay()) + ", "
					+ Integer.toString(fact.reportedDate.getMonth()) + ", "
					+ "'" + fact.reportedDate.getDayOfWeek() + "', "
					+ Boolean.toString(fact.reportedDate.isWeekend()).toUpperCase() + ", "
					+ Boolean.toString(fact.reportedDate.isHoliday()).toUpperCase() + ", "
					+ "'" + fact.reportedDate.getSeason() + "');";
			
		}else {
			totalNulls++;
		}
		System.out.println(reportedDateQuery);
		
		String onsetDateQuery = "INSERT INTO onset_date_dimension (onset_date_key, day, month, day_of_week, weekend, holiday, season) ";
		if(fact.onsetDate != null) {
			onsetDateQuery += "VALUES ("
					+ Integer.toString(fact.onsetDate.getOnsetDateKey()) + ", "
					+ Integer.toString(fact.onsetDate.getDay()) + ", "
					+ Integer.toString(fact.onsetDate.getMonth()) + ", "
					+ "'" + fact.onsetDate.getDayOfWeek() + "', "
					+ Boolean.toString(fact.onsetDate.isWeekend()).toUpperCase() + ", "
					+ Boolean.toString(fact.onsetDate.isHoliday()).toUpperCase() + ", "
					+ "'" + fact.onsetDate.getSeason() + "');";
		}else {
			totalNulls++;
		}
		System.out.println(onsetDateQuery);
		
		String testDateQuery = "INSERT INTO test_date_dimension (test_date_key, day, month, day_of_week, weekend, holiday, season) ";
		if(fact.testDate != null) {
			testDateQuery += "VALUES ("
					+ Integer.toString(fact.testDate.getTestDateKey()) + ", "
					+ Integer.toString(fact.testDate.getDay()) + ", "
					+ Integer.toString(fact.testDate.getMonth()) + ", "
					+ "'" + fact.testDate.getDayOfWeek() + "', "
					+ Boolean.toString(fact.testDate.isWeekend()).toUpperCase() + ", "
					+ Boolean.toString(fact.testDate.isHoliday()).toUpperCase() + ", "
					+ "'" + fact.testDate.getSeason() + "');";
		}else {
			totalNulls++;
		}
		System.out.println(testDateQuery);
		
		String specimenDateQuery = "INSERT INTO specimen_date_dimension (specimen_date_key, day, month, day_of_week, weekend, holiday, season) ";
		if(fact.specimenDate != null) {
			specimenDateQuery += "VALUES ("
					+ Integer.toString(fact.specimenDate.getSpecimenDateKey()) + ", "
					+ Integer.toString(fact.specimenDate.getDay()) + ", "
					+ Integer.toString(fact.specimenDate.getMonth()) + ", "
					+ "'" + fact.specimenDate.getDayOfWeek() + "', "
					+ Boolean.toString(fact.specimenDate.isWeekend()).toUpperCase() + ", "
					+ Boolean.toString(fact.specimenDate.isHoliday()).toUpperCase() + ", "
					+ "'" + fact.specimenDate.getSeason() + "');";
		}else {
			totalNulls++;
		}
		System.out.println(specimenDateQuery);
		
		String weatherQuery = "INSERT INTO weather_dimension (weather_key, daily_high_temperature, daily_low_temperature, precipitation) ";
		if(fact.weather != null) {
			weatherQuery += "VALUES ("
					+ Integer.toString(fact.weather.getWeatherKey()) + ", "
					+ Double.toString(fact.weather.getDailyHighTemperature()) + ", "
					+ Double.toString(fact.weather.getDailyLowTemperature()) + ", "
					+ Boolean.toString(fact.weather.isPercipitation()).toUpperCase() + ");";
		} else {
			totalNulls++;
		}
		System.out.println(weatherQuery);
		
		String mobilityQuery = "INSERT INTO mobility_dimension (mobility_key, sub_region, province, retail_and_recreation, grocery_and_pharmacy, parks, transit_stations, workplaces, residential) ";
		if(fact.mobility != null) {
			mobilityQuery += "VALUES ("
					+ Integer.toString(fact.mobility.getMobilityKey()) + ", "
					+ "'" + fact.mobility.getSubRegion() + "', "
					+ "'" + fact.mobility.getProvince() + "', "
					+ Integer.toString(fact.mobility.getRetailAndRecreation()) + ", "
					+ Integer.toString(fact.mobility.getGroceryAndPharmacy()) + ", "
					+ Integer.toString(fact.mobility.getParks()) + ", "
					+ Integer.toString(fact.mobility.getTransitStations()) + ", "
					+ Integer.toString(fact.mobility.getWorkplaces()) + ", "
					+ Integer.toString(fact.mobility.getResidential()) + ");";
		} else {
			totalNulls++;
		}
		System.out.println(mobilityQuery);
		
		String phuLocationQuery = "INSERT INTO phu_location_dimension (phu_location_key, phu_name, address, city, postal_code, province, url, latitude, longitude) ";
		if(fact.phuLocation != null) {
			phuLocationQuery += "VALUES ("
					+ Integer.toString(fact.phuLocation.getPhuLocationKey()) + ", "
					+ "'" + fact.phuLocation.getPhuName() + "', "
					+ "'" + fact.phuLocation.getAddress() + "', "
					+ "'" + fact.phuLocation.getCity() + "', "
					+ "'" + fact.phuLocation.getPostalCode() + "', "
					+ "'" + fact.phuLocation.getProvince() + "', "
					+ "'" + fact.phuLocation.getUrl() + "', "
					+ Double.toString(fact.phuLocation.getLatitude()) + ", "
					+ Double.toString(fact.phuLocation.getLongitude()) + ");";
		} else {
			totalNulls++;
		}
		System.out.println(phuLocationQuery);
		
		String patientQuery = "INSERT INTO patient_dimension (patient_key, acquisition_group, age_group, gender, outbreak_related) ";
		if(fact.patient != null) {
			patientQuery += "VALUES ("
					+ Integer.toString(fact.patient.getPatientKey()) + ", "
					+ "'" + fact.patient.getAcquisitionGroup() + "', "
					+ "'" + fact.patient.getAgeGroup() + "', "
					+ "'" + fact.patient.getGender() + "', "
					+ Boolean.toString(fact.patient.isOutBreakRelated()).toUpperCase() + ");";
		}else {
			totalNulls++;
		}
		System.out.println(patientQuery);
		
		String specialMeasuresQuery = "INSERT INTO special_measures_dimension (special_measures_key, title, description, keyword_1, keyword_2, start_year, end_year, start_month, end_month, start_day, end_day) ";
		if(fact.specialMeasures != null) {
			specialMeasuresQuery += "VALUES ("
					+ Integer.toString(fact.specialMeasures.getSpecialMeasuresKey()) + ", "
					+ "'" + fact.specialMeasures.getTitle() + "', "
					+ "'" + fact.specialMeasures.getDescription() + "', "
					+ "'" + fact.specialMeasures.getKeyword1() + "', "
					+ "'" + fact.specialMeasures.getKeyword2() + "', "
					+ Integer.toString(fact.specialMeasures.getStartYear()) + ", "
					+ Integer.toString(fact.specialMeasures.getEndYear()) + ", "
					+ Integer.toString(fact.specialMeasures.getStartMonth()) + ", "
					+ Integer.toString(fact.specialMeasures.getEndMonth()) + ", "
					+ Integer.toString(fact.specialMeasures.getStartDay()) + ", "
					+ Integer.toString(fact.specialMeasures.getEndDay()) + ");";
		}else {
			totalNulls++;
		}
		System.out.println(specialMeasuresQuery);
		
		String factQuery = "INSERT INTO fact_table (onset_date_key, reported_date_key, test_date_key, specimen_date_key, patient_key, location_key, mobility_key, special_measures_key, weather_key, resolved, fatal, unresolved) ";
		factQuery += "VALUES ("
				+ Integer.toString(fact.onsetDate.getOnsetDateKey()) + ", "
				+ Integer.toString(fact.reportedDate.getReportedDateKey()) + ", "
				+ Integer.toString(fact.testDate.getTestDateKey()) + ", "
				+ Integer.toString(fact.specimenDate.getSpecimenDateKey()) + ", "
				+ Integer.toString(fact.patient.getPatientKey()) + ", "
				+ Integer.toString(fact.phuLocation.getPhuLocationKey()) + ", "
				+ Integer.toString(fact.mobility.getMobilityKey()) + ", "
				+ Integer.toString(fact.specialMeasures.getSpecialMeasuresKey()) + ", "
				+ Integer.toString(fact.weather.getWeatherKey()) + ", "
				+ Boolean.toString(fact.resolved).toUpperCase() + ", "
				+ Boolean.toString(fact.fatal).toUpperCase() + ", "
				+ Boolean.toString(fact.unresolved).toUpperCase() + ");";
		System.out.println(factQuery);
		
		if (totalNulls == 0) {
			Statement statement = connection.createStatement();
			try {
				statement.executeUpdate(reportedDateQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(onsetDateQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(testDateQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(specimenDateQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(weatherQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(mobilityQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(phuLocationQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(patientQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(specialMeasuresQuery);
			} catch (Exception e) {

			}
			try {
				statement.executeUpdate(factQuery);
			} catch (Exception e) {

			}
			try {
				statement.close();
			} catch (Exception e) {

			}

		}
		
		
		
		return totalNulls;
	}

	public Connection getConnection(String[] args) throws SQLException {
		String username = args[0];
		String password = args[1];
		System.out.println("Connecting to database.");
		Connection connection = null;
		String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
		connection = DriverManager.getConnection(url, username, password);
		System.out.println("Connection established.");
		return connection;
	}

	private Fact toFact(OnsetDate onsetDate, ReportedDate reportedDate, TestDate testDate, SpecimenDate specimenDate,
			Patient patient, PhuLocation phuLocation, Mobility mobility, SpecialMeasures specialMeasures,
			Weather weather, String patientDataLine) {
		Fact fact = new Fact();
		fact.onsetDate = onsetDate;
		fact.reportedDate = reportedDate;
		fact.testDate = testDate;
		fact.specimenDate = specimenDate;
		fact.patient = patient;
		fact.phuLocation = phuLocation;
		fact.mobility = mobility;
		fact.weather = weather;
		fact.specialMeasures = specialMeasures;
		String[] line = patientDataLine.split(",");
		String status = line[8];
		boolean resolved = "Resolved".equals(status);
		boolean unresolved = "Not Resolved".equals(status);
		boolean fatal = "Fatal".equals(status);
		fact.resolved = resolved;
		fact.unresolved = unresolved;
		fact.fatal = fatal;
		return fact;
	}

	/**
	 * Place holder until we have a datasource for special measures.
	 * 
	 * @param patientDataLine
	 * @return
	 */
	private SpecialMeasures toSpecialMeasures(String patientDataLine) {
		String title = "placeholder";
		String description = "placeholder event";
		String keyword1 = "placeholder";
		String keyword2 = "placeholder";
		int startYear = 2020;
		int endYear = 2020;
		int startMonth = 1;
		int endMonth = 12;
		int startDay = 1;
		int endDay = 31;
		SpecialMeasures specialMeasures = new SpecialMeasures(specialMeasuresKey, title, description, keyword1,
				keyword2, startYear, endYear, startMonth, endMonth, startDay, endDay);
		specialMeasuresKey++;
		return specialMeasures;
	}

	private Patient toPatient(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String gender = line[6];
		String ageGroup = line[5];
		String acquisitionGroup = line[7];
		boolean outBreakRelated = "Yes".equals(line[9]);
		Patient patient = new Patient(patientKey, gender, ageGroup, acquisitionGroup, outBreakRelated);
		patientKey++;
		return patient;
	}

	private PhuLocation toPhuLocation(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String phuName = line[11];
		String address = line[12];
		String city = line[13];
		String postalCode = line[14];
		String province = "Ontario";// why do even track this if all the data is in Ontario?
		String url = line[15];
		double latitude = Double.parseDouble(line[16]);
		double longitude = Double.parseDouble(line[17]);
		PhuLocation phuLocation = new PhuLocation(phuLocationKey, phuName, address, city, postalCode, province, url,
				latitude, longitude);
		phuLocationKey++;
		return phuLocation;
	}
	
	private boolean inPhuLocationList(String patientDataLine, ArrayList<PhuLocation> phuLocationList) {
		String[] line = patientDataLine.split(",");
		for(PhuLocation phuLocation : phuLocationList) {
			if(phuLocation.getAddress().equals(line[12])) {
				return true;
			}
		}
		return false;
	}

	private Mobility toMobility(String patientDataLine, ArrayList<String> mobilityData) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[2].split("-");
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		String reportingCity = line[13];
		String mobilityLine = getMobilityLineFromDateAndLocation(year, month, day, reportingCity, mobilityData);
		if (mobilityLine == null) {
			return null;
		}
		String[] mobilityValues = mobilityLine.split(",");
		String subRegion = mobilityValues[3];
		String province = mobilityValues[2];
		int retailAndRecreation = Integer.parseInt(mobilityValues[8]);
		int groceryAndPharmacy = Integer.parseInt(mobilityValues[9]);
		int parks = Integer.parseInt(mobilityValues[10]);
		int transitStations = Integer.parseInt(mobilityValues[11]);
		int workplaces = Integer.parseInt(mobilityValues[12]);
		int residential = Integer.parseInt(mobilityValues[13]);
		Mobility mobility = new Mobility(mobilityNumber, subRegion, province, retailAndRecreation, groceryAndPharmacy,
				parks, transitStations, workplaces, residential);
		mobilityNumber++;
		return mobility;
	}

	private String getMobilityLineFromDateAndLocation(int year, int month, int day, String reportingCity,
			ArrayList<String> mobilityData) {
		for (int i = 0; i < mobilityData.size(); i++) {
			String[] line = mobilityData.get(i).split(",");
			String[] date = line[7].split("-");
			int mYear = Integer.parseInt(date[0]);
			int mMonth = Integer.parseInt(date[1]);
			int mDay = Integer.parseInt(date[2]);
			if (((reportingCity.equals(REPORTING_PHU_CITY_TORONTO) && line[3].equals(MOBILITY_TORONTO))
					|| (reportingCity.equals(REPORTING_PHU_CITY_OTTAWA) && line[3].equals(MOBILITY_OTTAWA))
					|| (reportingCity.equals(REPORTING_PHU_CITY_DURHAM) && line[3].equals(MOBILITY_DURHAM))
					|| (reportingCity.equals(REPORTING_PHU_CITY_HALTON) && line[3].equals(MOBILITY_HALTON))
					|| (reportingCity.equals(REPORTING_PHU_CITY_PEEL) && line[3].equals(MOBILITY_PEEL))
					|| (reportingCity.equals(REPORTING_PHU_CITY_YORK) && line[3].equals(MOBILITY_YORK))
					) && (year == mYear && month == mMonth && day == mDay)) {
				return mobilityData.get(i);
			}
		}
		return null;
	}

	private Weather toWeather(String patientDataLine, ArrayList<String> weatherData, ArrayList<PhuLocation> phuLocationList) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[2].split("-");
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		String reportingCity = line[13];
		String weatherLine = getWeatherLineFromDateAndLocation(year, month, day, reportingCity, weatherData);
		if (weatherLine == null) {
			return null;
		}
		String[] weatherValues = weatherLine.split(",");
		double dailyHighTemperature = Double.parseDouble(weatherValues[9]);
		double dailyLowTemperature = Double.parseDouble(weatherValues[11]);
		boolean percipitation = !(0 == Double.parseDouble(weatherValues[23]));
		Weather weather = new Weather(weatherNumber, dailyHighTemperature, dailyLowTemperature, percipitation);
		weatherNumber++;
		return weather;

	}

	private String getWeatherLineFromDateAndLocation(int year, int month, int day, String reportingCity,
			ArrayList<String> weatherData) {
		for (int i = 0; i < weatherData.size(); i++) {
			String[] weatherValues = weatherData.get(i).split(",");
			int wYear = Integer.parseInt(weatherValues[5]);
			int wMonth = Integer.parseInt(weatherValues[6]);
			int wDay = Integer.parseInt(weatherValues[7]);
			if (((reportingCity.equals(REPORTING_PHU_CITY_TORONTO) && weatherValues[2].equals(WEATHER_TORONTO))
					|| (reportingCity.equals(REPORTING_PHU_CITY_OTTAWA) && weatherValues[2].equals(WEATHER_OTTAWA))
					|| (reportingCity.equals(REPORTING_PHU_CITY_DURHAM) && weatherValues[2].equals(WEATHER_DURHAM))
					|| (reportingCity.equals(REPORTING_PHU_CITY_HALTON) && weatherValues[2].equals(WEATHER_HALTON))
					|| (reportingCity.equals(REPORTING_PHU_CITY_PEEL) && weatherValues[2].equals(WEATHER_PEEL))
					|| (reportingCity.equals(REPORTING_PHU_CITY_YORK) && weatherValues[2].equals(WEATHER_YORK))
					)
					&& (year == wYear && month == wMonth && day == wDay)) {
				return weatherData.get(i);
			}

		}

		return null;
	}

	private SpecimenDate toSpecimenDate(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[4].split("-");
		if (dateValues.length != 3) {
			return null;
		}
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		boolean weekend = false;
		boolean holiday = false;
		String dayOfWeek = "";
		String season = "";
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		int dayOfWeekEnumeration = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = getDayOfWeekFromEnumeration(dayOfWeekEnumeration);
		weekend = getWeekendFromDayOfWeek(dayOfWeek);
		holiday = getHolidayFromMonthAndDay(month, day);
		season = getSeasonFromMonth(month);
		SpecimenDate specimenDate = new SpecimenDate(specimenDateNumber, day, month, dayOfWeek, weekend, holiday,
				season);
		specimenDateNumber++;
		return specimenDate;
	}

	private TestDate toTestDate(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[3].split("-");
		if (dateValues.length != 3) {
			return null;
		}
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		boolean weekend = false;
		boolean holiday = false;
		String dayOfWeek = "";
		String season = "";
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		int dayOfWeekEnumeration = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = getDayOfWeekFromEnumeration(dayOfWeekEnumeration);
		weekend = getWeekendFromDayOfWeek(dayOfWeek);
		holiday = getHolidayFromMonthAndDay(month, day);
		season = getSeasonFromMonth(month);
		TestDate testDate = new TestDate(testDateNumber, day, month, dayOfWeek, weekend, holiday, season);
		testDateNumber++;
		return testDate;
	}

	private OnsetDate toOnsetDate(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[1].split("-");
		if (dateValues.length != 3) {
			return null;
		}
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		boolean weekend = false;
		boolean holiday = false;
		String dayOfWeek = "";
		String season = "";
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		int dayOfWeekEnumeration = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = getDayOfWeekFromEnumeration(dayOfWeekEnumeration);
		weekend = getWeekendFromDayOfWeek(dayOfWeek);
		holiday = getHolidayFromMonthAndDay(month, day);
		season = getSeasonFromMonth(month);
		OnsetDate onsetDate = new OnsetDate(onsetDateNumber, day, month, dayOfWeek, weekend, holiday, season);
		onsetDateNumber++;
		return onsetDate;
	}

	/**
	 * Converts given patient data string into a ReportedDate object. There case
	 * reported date is always present without special cases. This is because the
	 * case report date is a prerequisite to showing up in the data source.
	 * 
	 * @param patientDataLine
	 * @return
	 */
	private ReportedDate toReportedDate(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[2].split("-");
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		boolean weekend = false;// these are the default values
		boolean holiday = false;
		String dayOfWeek = "";
		String season;
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		int dayOfWeekEnumeration = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = getDayOfWeekFromEnumeration(dayOfWeekEnumeration);
		weekend = getWeekendFromDayOfWeek(dayOfWeek);
		holiday = getHolidayFromMonthAndDay(month, day);
		season = getSeasonFromMonth(month);
		ReportedDate reportedDate = new ReportedDate(reportedDateNumber, day, month, dayOfWeek, weekend, holiday,
				season);
		reportedDateNumber++;
		return reportedDate;

	}

	private String getDayOfWeekFromEnumeration(int dayOfWeekEnumeration) {
		String dayOfWeek = "";
		switch (dayOfWeekEnumeration) {
		case Calendar.SUNDAY:
			dayOfWeek = "Sunday";
			break;
		case Calendar.MONDAY:
			dayOfWeek = "Monday";
			break;
		case Calendar.TUESDAY:
			dayOfWeek = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			dayOfWeek = "Wednesday";
			break;
		case Calendar.THURSDAY:
			dayOfWeek = "Thursday";
			break;
		case Calendar.FRIDAY:
			dayOfWeek = "Friday";
			break;
		case Calendar.SATURDAY:
			dayOfWeek = "Saturday";
			break;
		default:
			System.out.println("ERROR: Invalid day of week.");
		}
		return dayOfWeek;
	}

	private boolean getWeekendFromDayOfWeek(String dayOfWeek) {
		if (dayOfWeek.equals("Saturday") || dayOfWeek.equals("Sunday")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	private boolean getHolidayFromMonthAndDay(int month, int day) {
		boolean holiday = false;
		if (month == 2 && day == 17) {
			holiday = true;
		}
		if (month == 4 && day == 10) {
			holiday = true;
		}
		if (month == 4 && day == 13) {
			holiday = true;
		}
		if (month == 5 && day == 18) {
			holiday = true;
		}
		return holiday;
	}

	/**
	 * Gets season from given month.
	 * 
	 * @param month
	 * @return
	 */
	private String getSeasonFromMonth(int month) {
		String season = "";
		switch (month) {
		case 1:
			season = "Winter";
			break;
		case 2:
			season = "Winter";
			break;
		case 3:
			season = "Spring";
			break;
		case 4:
			season = "Spring";
			break;
		case 5:
			season = "Spring";
			break;
		case 6:
			season = "Summer";
			break;
		case 7:
			season = "Summer";
			break;
		case 8:
			season = "Summer";
			break;
		case 9:
			season = "Fall";
			break;
		case 10:
			season = "Fall";
			break;
		case 11:
			season = "Fall";
			break;
		case 12:
			season = "Winter";
			break;
		default:
			System.out.println("ERROR: Invalid month with no corrisponding season.");
		}
		return season;
	}

	/**
	 * Extracts the patient data from the file. Thankfully, there doesn't seem to be
	 * anything that requires omission.
	 * 
	 * In a heavier duty operation, loading an entire data source into the RAM of a
	 * personal computer would be a bad idea. Thankfully, since I can open the CSV
	 * in Excel, loading the whole file into an ArrayList is *probably* not a huge
	 * problem.
	 * 
	 * Printing the whole file to the console does take time, however.
	 * 
	 * @return
	 */
	private ArrayList<String> getPatientData() {
		ArrayList<String> patientData = new ArrayList<String>();
		ArrayList<String> cityList = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputPatient = new BufferedReader(new FileReader(COVID_PATIENT_FILE));
			inputLine = inputPatient.readLine();// removes the header lines.
			while ((inputLine = inputPatient.readLine()) != null) {
				String[] data = inputLine.split(",");
				if(!cityList.contains(data[13])) {
					cityList.add(data[13]);
				}
				if ((data[13].equals(REPORTING_PHU_CITY_OTTAWA) || data[14].equals(REPORTING_PHU_CITY_TORONTO))//the toronto phu has a comma
						&& isInRange(inputLine)) {
					if(data[14].equals(REPORTING_PHU_CITY_TORONTO)) {
						inputLine = removeCommaFromAddress(inputLine);
					}
					patientData.add(inputLine);
				}
			}
			inputPatient.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Same case here. We append to the existing order.
		//Were this a permanent operation, we would create a system for checking if data is already in the
		//staging area and append to that.
		try {
			BufferedReader inputPatient = new BufferedReader(new FileReader(COVID_PATIENT_FILE));
			inputLine = inputPatient.readLine();// removes the header lines.
			while ((inputLine = inputPatient.readLine()) != null) {
				String[] data = inputLine.split(",");
				if(!cityList.contains(data[13])) {
					cityList.add(data[13]);
				}
				if ((data[13].equals(REPORTING_PHU_CITY_DURHAM) || data[13].equals(REPORTING_PHU_CITY_HALTON) || data[13].equals(REPORTING_PHU_CITY_PEEL) || data[13].equals(REPORTING_PHU_CITY_YORK))//the toronto phu has a comma
						&& isInRange(inputLine)) {
					patientData.add(inputLine);
				}
			}
			inputPatient.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String city : cityList) {
			System.out.println(city);
		}
		return patientData;
	}
	
	private String removeCommaFromAddress(String patientLine) {
		String[] line = patientLine.split(",");
		String output = line[0];
		for(int i = 1; i < line.length; i++) {
			if(i==13) {
				output += line[i];
			}else {
				output += "," + line[i];
			}
		}
		return removeQuotations(output);
	}

	private boolean isInRange(String patientDataLine) {
		String[] line = patientDataLine.split(",");
		String[] dateValues = line[2].split("-");
		int year = Integer.parseInt(dateValues[0]);
		int month = Integer.parseInt(dateValues[1]);
		int day = Integer.parseInt(dateValues[2]);
		Calendar calendar = Calendar.getInstance();
		Calendar upperBound = Calendar.getInstance();
		Calendar lowerBound = Calendar.getInstance();
		upperBound.set(2020, 6, 15);
		lowerBound.set(2020, 2, 15);
		calendar.set(year, month, day);
		return lowerBound.getTimeInMillis() < calendar.getTimeInMillis()
				&& calendar.getTimeInMillis() < upperBound.getTimeInMillis();
	}

	/**
	 * This only works if the first and last data are complete. Our current data
	 * source is as such.
	 * 
	 * Missing weather data cannot be omitted which is why we clean it like this.
	 * 
	 * @param weatherData
	 * @return
	 */
	private ArrayList<String> cleanseWeatherData(ArrayList<String> weatherData) {
		for (int i = 0; i < weatherData.size(); i++) {
			String[] old = weatherData.get(i).split(",");
			String current;
			int index = 9;
			if (old[index].equals("")) {
				int j = 1;
				String[] prev = weatherData.get(i - 1).split(",");
				String[] next = weatherData.get(i + j).split(",");
				while (next[index].equals("")) {
					j++;
					next = weatherData.get(i + j).split(",");
				}
				old[index] = Double.toString((Double.parseDouble(prev[index]) + Double.parseDouble(next[index])) / 2);
			}
			index = 11;
			if (old[index].equals("")) {
				int j = 1;
				String[] prev = weatherData.get(i - 1).split(",");
				String[] next = weatherData.get(i + j).split(",");
				while (next[index].equals("")) {
					j++;
					next = weatherData.get(i + j).split(",");
				}
				old[index] = Double.toString((Double.parseDouble(prev[index]) + Double.parseDouble(next[index])) / 2);
			}
			index = 23;
			if (old[index].equals("")) {
				int j = 1;
				String[] prev = weatherData.get(i - 1).split(",");
				String[] next = weatherData.get(i + j).split(",");
				while (next[index].equals("")) {
					j++;
					next = weatherData.get(i + j).split(",");
				}
				old[index] = Double.toString((Double.parseDouble(prev[index]) + Double.parseDouble(next[index])) / 2);
			}
			current = old[0];
			for (int j = 1; j < old.length; j++) {
				current = current + "," + old[j];
			}
			weatherData.set(i, current);
		}

		return weatherData;
	}

	/**
	 * Retrieves the weather data from given csv files. The weather data files have
	 * quotations in them for some reason so they are removed as part of the
	 * process.
	 * 
	 * @return
	 */
	private ArrayList<String> getWeatherData() {
		ArrayList<String> weatherData = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(TORONTO_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while ((inputLine = inputMobile.readLine()) != null) {
				// I have no idea why but the raw csv stores the values in quotations
				// There for it is nessasarry to remove all quotations
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
			while ((inputLine = inputMobile.readLine()) != null) {
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
			BufferedReader inputMobile = new BufferedReader(new FileReader(DURHAM_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while ((inputLine = inputMobile.readLine()) != null) {
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
			BufferedReader inputMobile = new BufferedReader(new FileReader(HALTON_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while ((inputLine = inputMobile.readLine()) != null) {
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
			BufferedReader inputMobile = new BufferedReader(new FileReader(PEEL_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while ((inputLine = inputMobile.readLine()) != null) {
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
			BufferedReader inputMobile = new BufferedReader(new FileReader(YORK_WEATHER_FILE));
			inputLine = inputMobile.readLine();
			while ((inputLine = inputMobile.readLine()) != null) {
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
	 * 
	 * @param input
	 * @return
	 */
	private String removeQuotations(String input) {
		String[] segments = input.split("\"");
		String output = "";
		for (int i = 0; i < segments.length; i++) {
			output += segments[i];
		}
		return output;
	}

	/**
	 * Separates the relevant mobility data from the file. The file contains a bunch
	 * of mobility data we do not need. Therefore, only the mobility data in Ottawa
	 * and Toronto are extracted.
	 * 
	 * @return
	 */
	private ArrayList<String> getMobilityData() {
		ArrayList<String> mobilityData = new ArrayList<String>();
		String inputLine;
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(MOBILITY_DATA_FILE));
			while ((inputLine = inputMobile.readLine()) != null) {
				String[] data = inputLine.split(",");
				if (data[3].equals(MOBILITY_OTTAWA) || data[3].equals(MOBILITY_TORONTO)) {
					mobilityData.add(inputLine);
				}
			}
			inputMobile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//You might be asking why we do this twice. 
		//As of writing this, the data for Toronto and Ottawa have already been uploaded.
		//Creating the list like this ensures the data will be uploaded in the same order.
		try {
			BufferedReader inputMobile = new BufferedReader(new FileReader(MOBILITY_DATA_FILE));
			while ((inputLine = inputMobile.readLine()) != null) {
				String[] data = inputLine.split(",");
				if (data[3].equals(MOBILITY_DURHAM) || data[3].equals(MOBILITY_HALTON) || data[3].equals(MOBILITY_PEEL) || data[3].equals(MOBILITY_YORK)) {
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
//
	public DataUploader() {

	}
}

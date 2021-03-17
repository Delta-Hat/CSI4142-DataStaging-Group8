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
	final static String MOBILITY_DATA_FILE = "2020_CA_Region_Mobility_Report.csv";
	final static String MOBILITY_TORONTO = "Toronto Division";
	final static String MOBILITY_OTTAWA = "Ottawa Division";
	final static String WEATHER_TORONTO = "TORONTO CITY";
	final static String WEATHER_OTTAWA = "OTTAWA CDA RCS";
	final static String REPORTING_PHU_CITY_TORONTO = "Toronto";
	final static String REPORTING_PHU_CITY_OTTAWA = "Ottawa";
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

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Insufficient args. Usage: DataUploader <username> <password>");
			System.exit(0);
		}
		(new DataUploader()).run(args);// I love doing this cause it's so weird.
	}

	private void run(String[] args) {
		

		ArrayList<String> mobilityData = getMobilityData();
		// for(String line : mobilityData) {
		// System.out.println(line);
		// }

		ArrayList<String> weatherData = getWeatherData();

		weatherData = cleanseWeatherData(weatherData);

		// for(String line : weatherData) {
		// System.out.println(line);
		// }

		ArrayList<String> patientData = getPatientData();
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
		
		String specimenDateQuery = "INSERT INTO specimen_date_dimension (test_date_key, day, month, day_of_week, weekend, holiday, season) ";
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
		
		if(totalNulls == 0) {
			Statement statement = connection.createStatement();
			statement.executeQuery(reportedDateQuery);
			statement.executeQuery(onsetDateQuery);
			statement.executeQuery(testDateQuery);
			statement.executeQuery(specimenDateQuery);
			statement.executeQuery(weatherQuery);
			statement.executeQuery(mobilityQuery);
			statement.executeQuery(phuLocationQuery);
			statement.executeQuery(patientQuery);
			statement.executeQuery(specialMeasuresQuery);
			statement.executeQuery(factQuery);
			statement.close();
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
					|| (reportingCity.equals(REPORTING_PHU_CITY_OTTAWA) && line[3].equals(MOBILITY_OTTAWA)))
					&& (year == mYear && month == mMonth && day == mDay)) {
				return mobilityData.get(i);
			}
		}
		return null;
	}

	private Weather toWeather(String patientDataLine, ArrayList<String> weatherData) {
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
					|| (reportingCity.equals(REPORTING_PHU_CITY_OTTAWA) && weatherValues[2].equals(WEATHER_OTTAWA)))
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
		String inputLine;
		try {
			BufferedReader inputPatient = new BufferedReader(new FileReader(COVID_PATIENT_FILE));
			inputLine = inputPatient.readLine();// removes the header lines.
			while ((inputLine = inputPatient.readLine()) != null) {
				String[] data = inputLine.split(",");
				if ((data[13].equals(REPORTING_PHU_CITY_OTTAWA) || data[13].equals(REPORTING_PHU_CITY_TORONTO))
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
		return patientData;
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
		return mobilityData;
	}

	public DataUploader() {

	}
}

package dataUploader;

public class Fact {
	OnsetDate onsetDate;
	ReportedDate reportedDate;
	TestDate testDate;
	SpecimenDate specimenDate;
	Patient patient;
	PhuLocation phuLocation;
	Mobility mobility;
	SpecialMeasures specialMeasures;
	Weather weather;
	boolean resolved;
	boolean unresolved;
	boolean fatal;
	int onsetDateKey;
	int reportedDateKey;
	int testDateKey;
	int specimenDateKey;
	int patientKey;
	int phuLocationKey;
	int mobilityKey;
	int specialMeasuresKey;
	int weatherKey;

	public Fact() {

	}

	public String toString() {
		String output = "Fact[";
		if (onsetDate != null) {
			output += Integer.toString(onsetDate.getOnsetDateKey()) + ",";
		} else {
			output += null + ",";
		}
		if (reportedDate != null) {
			output += Integer.toString(reportedDate.getReportedDateKey()) + ",";
		} else {
			output += null + ",";
		}
		if (testDate != null) {
			output += Integer.toString(testDate.getTestDateKey()) + ",";
		} else {
			output += null + ",";
		}
		if (specimenDate != null) {
			output += Integer.toString(specimenDate.getSpecimenDateKey()) + ",";
		} else {
			output += null + ",";
		}
		output += Integer.toString(patient.getPatientKey()) + ",";
		output += Integer.toString(phuLocation.getPhuLocationKey()) + ",";
		output += Integer.toString(mobility.getMobilityKey()) + ",";
		output += Integer.toString(specialMeasures.getSpecialMeasuresKey()) + ",";
		output += Integer.toString(weather.getWeatherKey()) + ",";
		output += Boolean.toString(resolved) + ",";
		output += Boolean.toString(unresolved) + ",";
		output += Boolean.toString(fatal) + "]";
		return output;
	}
}

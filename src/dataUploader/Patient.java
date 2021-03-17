package dataUploader;

import java.io.Serializable;

public class Patient implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3689416929181305747L;
	
	private int patientKey;
	private String gender;
	private String ageGroup;
	private String acquisitionGroup;
	private boolean outBreakRelated;
	
	public Patient(int patientKey, String gender, String ageGroup, String acquisitionGroup, boolean outBreakRelated) {
		this.setPatientKey(patientKey);
		this.setGender(gender);
		this.setAgeGroup(ageGroup);
		this.setAcquisitionGroup(acquisitionGroup);
		this.setOutBreakRelated(outBreakRelated);
	}
	
	public String toString() {
		String output = "Patient[";
		output += Integer.toString(getPatientKey()) + ",";
		output += getGender() + ",";
		output += getAgeGroup() + ",";
		output += getAcquisitionGroup() + ",";
		output += Boolean.toString(isOutBreakRelated()) + "]";
		return output;
	}

	public int getPatientKey() {
		return patientKey;
	}

	public void setPatientKey(int patientKey) {
		this.patientKey = patientKey;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getAcquisitionGroup() {
		return acquisitionGroup;
	}

	public void setAcquisitionGroup(String acquisitionGroup) {
		this.acquisitionGroup = acquisitionGroup;
	}

	public boolean isOutBreakRelated() {
		return outBreakRelated;
	}

	public void setOutBreakRelated(boolean outBreakRelated) {
		this.outBreakRelated = outBreakRelated;
	}
}

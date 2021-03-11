package dataUploader;

import java.io.Serializable;

public class Mobility implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1790653335786054032L;
	private int mobilityKey;
	private String metroArea;
	private String subRegion;
	private String province;
	private int retailAndRecreation;
	private int groceryAndPharmacy;
	private int parks;
	private int transitStations;
	private int workplaces;
	private int residential;
	
	public Mobility(int mobilityKey, String metroArea, String subRegion, String province, int retailAndRecreation, int groceryAndPharmacy, int parks, int transitStations, int workplaces, int residential) {
		this.mobilityKey = mobilityKey;
		this.metroArea = metroArea;
		this.subRegion = subRegion;
		this.province = province;
		this.retailAndRecreation = retailAndRecreation;
		this.groceryAndPharmacy = groceryAndPharmacy;
		this.parks = parks;
		this.transitStations = transitStations;
		this.workplaces = workplaces;
		this.residential = residential;
	}

	public int getMobilityKey() {
		return mobilityKey;
	}

	public void setMobilityKey(int mobilityKey) {
		this.mobilityKey = mobilityKey;
	}

	public String getMetroArea() {
		return metroArea;
	}

	public void setMetroArea(String metroArea) {
		this.metroArea = metroArea;
	}

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public int getRetailAndRecreation() {
		return retailAndRecreation;
	}

	public void setRetailAndRecreation(int retailAndRecreation) {
		this.retailAndRecreation = retailAndRecreation;
	}

	public int getGroceryAndPharmacy() {
		return groceryAndPharmacy;
	}

	public void setGroceryAndPharmacy(int groceryAndPharmacy) {
		this.groceryAndPharmacy = groceryAndPharmacy;
	}

	public int getParks() {
		return parks;
	}

	public void setParks(int parks) {
		this.parks = parks;
	}

	public int getTransitStations() {
		return transitStations;
	}

	public void setTransitStations(int transitStations) {
		this.transitStations = transitStations;
	}

	public int getWorkplaces() {
		return workplaces;
	}

	public void setWorkplaces(int workplaces) {
		this.workplaces = workplaces;
	}

	public int getResidential() {
		return residential;
	}

	public void setResidential(int residential) {
		this.residential = residential;
	}
}
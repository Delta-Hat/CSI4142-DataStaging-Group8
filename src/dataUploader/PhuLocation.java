package dataUploader;

import java.io.Serializable;

public class PhuLocation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5934068774709525798L;
	private int phuLocationKey;
	private String phuName;
	private String address;
	private String city;
	private String postalCode;
	private String province;
	private String url;
	private double latitude;
	private double longitude;
	
	public PhuLocation(int phuLocationKey, String phuName, String address, String city, String postalCode, String province, String url, double latitude, double longitude) {
		this.setPhuLocationKey(phuLocationKey);
		this.setPhuName(phuName);
		this.setAddress(address);
		this.setCity(city);
		this.setPostalCode(postalCode);
		this.setProvince(province);
		this.setUrl(url);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}
	
	public PhuLocation() {
		
	}

	public int getPhuLocationKey() {
		return phuLocationKey;
	}

	public void setPhuLocationKey(int phuLocationKey) {
		this.phuLocationKey = phuLocationKey;
	}

	public String getPhuName() {
		return phuName;
	}

	public void setPhuName(String phuName) {
		this.phuName = phuName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}

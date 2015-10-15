package com.tagdish.batch.domain;

import java.io.Serializable;

public class BatchZipCode implements Serializable {

	private static final long serialVersionUID = -256534249126506109L;
	private String zipCode;
	private String latitude;
	private String longtitude;
	private String city;
	private String state;
	private String county;
	private String zipClass;
	
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getZipClass() {
		return zipClass;
	}
	public void setZipClass(String zipClass) {
		this.zipClass = zipClass;
	}
}

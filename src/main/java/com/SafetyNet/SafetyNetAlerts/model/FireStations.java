package com.SafetyNet.SafetyNetAlerts.model;

public class FireStations {

	private String address;
	private String station;

	public FireStations() {

	}

	public FireStations(String address, String station) {
		this.station = station;
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	@Override
	public String toString() {
		return "FireStations{address='" + address + "', station=" + station +"}";
	}


}

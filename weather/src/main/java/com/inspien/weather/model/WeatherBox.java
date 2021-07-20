package com.inspien.weather.model;

public class WeatherBox extends Box {

	private String district;
	private String time;
	private String sky;
	private String temperature;
	private String humidity;
	private String wind;

	public WeatherBox(String status, String district, String time, String sky, String temperature, String humidity,
			String wind) {
		super(status);
		this.district = district;
		this.time = time;
		this.sky = sky;
		this.temperature = temperature;
		this.humidity = humidity;
		this.wind = wind;
	}
	public WeatherBox setTime(String time) {
		this.time = time;
		return this;
	}
	public String getDistrict() {
		return district;
	}

	public String getTime() {
		return time;
	}

	public String getSky() {
		return sky;
	}

	public String getTemperature() {
		return temperature;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getWind() {
		return wind;
	}

}

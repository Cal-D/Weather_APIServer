package com.inspien.weather.model;

public class ErrorBox extends Box {

	private String msg;
	
	public ErrorBox(String status, String msg) {
		super(status);
		this.msg = msg;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getMsg() {
		return msg;
	}
}

package edu.uchicago.networks;

public enum HttpRequestType {
	HEAD("HEAD"),
	GET("GET");
	
	private String method;
	private HttpRequestType(String method) {
		this.method = method;
	}
}
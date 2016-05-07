//package networks;

public enum HttpRequestType {
	HEAD("HEAD"),
	GET("GET"),
	INVALID("INVALID");
	
	private String method;
	private HttpRequestType(String method) {
		this.method = method;
	}
}
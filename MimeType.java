package networks;

import java.util.*;

public enum MimeType {
	HTML("html"),
	TXT("txt"),
	PDF("pdf"),
	PNG("png"),
	JPG("jpg");

	private String type;
	private MimeType(String type){
		this.type = type;
	}

	public String toString(){
		return MimeTypeMapper.getInstance().getMappingType(this.type);
	}
}

class MimeTypeMapper {
	private final static String unknownType = "application/unknown";

	private static MimeTypeMapper instance = null;
	private Map<String, String> types = null;

	private MimeTypeMapper() {
		types = new HashMap<String, String>();
		buildMapper();
	}

	private void buildMapper() {
		types.put("html", "text/html");
		types.put("txt", "text/plain");
		types.put("pdf", "application/pdf");
		types.put("png", "image/png");
		types.put("jpg", "image/jpeg");
	}

	public String getMappingType(String type) {
		if (types.containsKey(type)) {
			return types.get(type);
		}

		return unknownType;
	}

	public static MimeTypeMapper getInstance() {
		if (instance == null) {
			synchronized(MimeTypeMapper.class) {
				if (instance == null) {
					instance = new MimeTypeMapper();
				}
			} 	
		}

		return instance;
	}

}

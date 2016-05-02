package networks;

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
		if(this.type.equals("html")){
			return "text/html";
		}
		if(this.type.equals("txt")){
			return "text/plain";
		}
		if(this.type.equals("pdf")){
			return "application/pdf";
		}
		if(this.type.equals("png")){
			return "image/png";
		}
		if(this.type.equals("jpg")){
			return "image/jpeg";
		}

		return "NoSuchType";
	}
}

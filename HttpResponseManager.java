package networks;

import java.io.*;
import java.util.*;


public class HttpResponseManager {
	private Map<String, String> header = null;
	private HttpRequestManager requestManager = null;
	private String requestedFilePath = null;

	private Map<String, String> redirectURL = null;
	private MimeType datatype = null;

	public HttpResponseManager(HttpRequestManager request) {
		header = new HashMap<String, String>();
		requestManager = request;

		requestedFilePath = "www" + requestManager.getFilePath();
		getRedirectURL();

		this.datatype = getMimeType();
	}

	private void getRedirectURL(){
		String oneLine;
		redirectURL = new HashMap<String, String>();

		try{
			FileReader redirectDef = new FileReader("www/redirect.defs");
			BufferedReader redIn = new BufferedReader(redirectDef);

			while((oneLine = redIn.readLine()) != null){
				String[] parts = oneLine.split(" ");
				redirectURL.put("www" + parts[0], parts[1]);
				System.out.println("www" + parts[0] + " : " + parts[1]);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private MimeType getMimeType(){
		String[] parts = requestedFilePath.split("\\.");
		String type = parts[parts.length - 1];

		return MimeTypeMapper.getInstance().containsType(type) ? MimeType.valueOf(type.toUpperCase()) :
												MimeType.valueOf("UNKNOWN");
	}

	private void buildHeader(StringBuffer sb, int contentLen) {
		header.put("Connection", "Closed");
		header.put("Date", new Date().toString());
		header.put("Content-Length", Integer.toString(contentLen));
		header.put("Server", "Hello I am server!");
		header.put("Content-Type", this.datatype.toString()); 

		for (String name : header.keySet()) {
			sb.append(String.format("%s: %s\r\n", name, header.get(name)));
		}
		sb.append("\r\n");
	}

	private int buildReternData(ByteArrayOutputStream output, File f) {
		int totalBytesRead = 0;

		try {
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f));
			byte[] byteArray = new byte[2000];
			int bytesRead = 0;
			while ((bytesRead = inputStream.read(byteArray)) != -1) {
				output.write(byteArray, 0, bytesRead);
				totalBytesRead += bytesRead;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return totalBytesRead;
	}

	public void buildResponse(DataOutputStream toClientStream) {
		StringBuffer sb = new StringBuffer();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		File requestedFile = new File(this.requestedFilePath);
				//TODO: handle redirect
				System.out.println("requestedFilePath: " + requestedFilePath);

		//invalid request
		if(!this.requestManager.getRequestType().equals(HttpRequestType.GET) &&
			 !this.requestManager.getRequestType().equals(HttpRequestType.HEAD)){
			sb.append(String.format("HTTP/1.1 403 Invalid Request \r\n"));
			buildHeader(sb, 0);
		}
		//redirection
		else if(redirectURL.containsKey(this.requestedFilePath)){
			sb.append(String.format("HTTP/1.1 301 Redirection \r\n"));
			String newURL = redirectURL.get(this.requestedFilePath) + "\r\n";
			int contentLength = newURL.length();
			byte[] byteArray = newURL.getBytes();
			outputStream.write(byteArray, 0, byteArray.length);
			buildHeader(sb, contentLength);
		}
		//not found
		else if (!requestedFile.exists() || this.requestedFilePath.equals("www/redirect.defs")) {
			//TODO: throw exception
			//not found -- or require redirect.defs
			sb.append(String.format("HTTP/1.1 404 Not Found \r\n"));
			buildHeader(sb, 0);
		}
		//200 ok
		else{
			//this.getMimeType();
			sb.append(String.format("HTTP/1.1 200 OK \r\n"));
			int contentLength = buildReternData(outputStream, requestedFile);
			buildHeader(sb, contentLength);
		}
		//send reply
		try {
			toClientStream.writeBytes("\r");
			toClientStream.writeBytes(sb.toString());
			if(this.requestManager.getRequestType().equals(HttpRequestType.GET) ||
					redirectURL.containsKey(this.requestedFilePath)){
				//return data when : Request = GET
				//									 Redirection
				outputStream.writeTo(toClientStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}

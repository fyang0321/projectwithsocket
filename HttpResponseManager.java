package networks;

import java.io.*;
import java.util.*;


public class HttpResponseManager {
	private Map<String, String> header = null;
	private HttpRequestManager requestManager = null;
	private String requestedFilePath = null;

	public HttpResponseManager(HttpRequestManager request) {
		header = new HashMap<String, String>();
		requestManager = request;

		requestedFilePath = "www" + requestManager.getFilePath();
	}	

	private void buildHeader(StringBuffer sb) {
		header.put("Connection", "Closed");
		header.put("Date", new Date().toString());
		header.put("Content-Length", "0"); //TODO: how to calculate length
		header.put("Server", "Hello I am server!");

		header.put("Content-Type", "application/unknown");

		for (String name : this.header.keySet()) {
			sb.append(String.format("%s: %s\r\n", name, this.header.get(name)));
		}
		sb.append("\r\n");
	}

	private void buildReternData(ByteArrayOutputStream output, File f) {
		try {
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f));
			byte[] byteArray = new byte[2000];
			int bytesRead = 0;
			while ((bytesRead = inputStream.read(byteArray)) != -1) {
				output.write(byteArray, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void buildResponse(DataOutputStream toClientStream) {
		StringBuffer sb = new StringBuffer();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				//TODO: handle redirect
		File requestedFile = new File(this.requestedFilePath);
		if (!requestedFile.exists()) {
			//TODO: throw exception
		}

		sb.append(String.format("HTTP/1.1 200 OK \r\n"));
		buildHeader(sb);
		buildReternData(outputStream, requestedFile);
		try {
			toClientStream.writeBytes("\r");
			toClientStream.writeBytes(sb.toString());
			outputStream.writeTo(toClientStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
}
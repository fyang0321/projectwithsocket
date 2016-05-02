package networks;

import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestManager {
	private HttpRequestType type;
	private String filePath = null;
	private float httpVersion;

	private Map<String, String> metaData;

	public HttpRequestManager() {
		metaData = new HashMap<String, String>();
	}

	public void handleRequest(BufferedReader clientRequest) throws IOException {
		boolean isFirstLine = true;
		String line = null;
		try {
			while ( (line = clientRequest.readLine()) != null && !line.equals("")) {
				if (isFirstLine) { //handle the first Request line
					String[] parts = line.split(" ");
					this.type = HttpRequestType.valueOf(parts[0]);
					this.filePath = parts[1];
					this.httpVersion = Float.parseFloat(parts[2].replaceAll("HTTP/", ""));
					isFirstLine = false;
				} else {
					String[] parts = line.split(":");
					if (parts.length > 1)
						metaData.put(parts[0], parts[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} 
	}

	public HttpRequestType getRequestType() {
		return type;
	}

	public String getFilePath() {
		return filePath;
	}

	public float getHttpVersion() {
		return httpVersion;
	}

	public int getLength() {
		return Integer.parseInt(metaData.get("Content-Length").replaceAll("^\"|\"$", "").trim());
	}

	//unit test
	public static void main(String[] args) {
		// Path currentRelativePath = Paths.get("");
		// String s = currentRelativePath.toAbsolutePath().toString();
		// System.out.println("Current relative path is: " + s);
		HttpRequestManager manager = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("test"));
			manager = new HttpRequestManager();
		
				manager.handleRequest(br);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
		}
		System.out.println(manager.getRequestType().toString());
		System.out.println("File path is " + manager.getFilePath());
		System.out.println(manager.getHttpVersion());	
		System.out.println(manager.getLength());
	}
}


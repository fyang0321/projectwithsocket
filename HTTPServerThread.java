package networks;

import java.net.*;
import java.io.*;

public class HTTPServerThread extends Thread {
	private Socket socket = null;
 
    public HTTPServerThread(Socket socket) {
        super("HTTPServerThread");
        this.socket = socket;
    }
     
    public void run() {
        try {
        	boolean running = true;
    	
            DataOutputStream toClientStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader fromClientStream  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            HttpRequestManager requestManager = new HttpRequestManager();
            requestManager.handleRequest(fromClientStream);

            HttpResponseManager responseManager = new HttpResponseManager(requestManager);
            responseManager.buildResponse(toClientStream);

            socket.close();
        } catch (IOException e) { 
            e.printStackTrace();
    		System.exit(-1);
    	}
    }
}

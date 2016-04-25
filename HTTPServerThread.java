package edu.uchicago.networks;

import java.net.*;
import java.io.*;

public class HTTPServerThread {
	private Socket socket = null;
 
    public HTTPServerThread(Socket socket) {
        super("HTTPServerThread");
        this.socket = socket;
    }
     
    public void run() {
    	boolean running = true;
    	DataOutputStream toClientStream = new DataOutputStream(socket.getOutputStream());
		BufferedReader fromClientStream  = new BufferedReader(new InputStreamReader(socket.getInputStream()));;

    	try {
    		while (running) {
    			HttpRequestManager requestManager = new HttpRequestManager();
    			requestManager.handleRequest(fromClientStream);

    			HttpResponseManager responseManager = HttpResponseManager(requestManager.getRequest());
    			responseManager.response(toClientStream);

    			socket.close();
    		} catch (Exception e) { //TODO: handle exceptions
    			system.exit(-1);
    		}

    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
    }
}

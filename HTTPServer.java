package networks;

import java.net.*;
import java.io.*;
import java.util.*;

public class HTTPServer {
	public static void main(String[] args) throws IOException {
		Map<String, String> flags = Utils.parseCmdlineFlags(args);
		if (!flags.containsKey("--serverPort")) {
			System.out.println("usage: Server --serverPort=12345");
			System.exit(-1);
		}

		int serverPort = -1;
		try {
			serverPort = Integer.parseInt(flags.get("--serverPort"));
		} catch (NumberFormatException e) {
			System.out.println("Invalid port number! Must be an integer.");
			System.exit(-1);
		}

        boolean listening = true;
        ServerSocket serverSocket = new ServerSocket(serverPort);
        while (listening) {    
        	try {
                new HTTPServerThread(serverSocket.accept()).start();
        	} catch (IOException e) {
            	System.err.println("Could not listen on port " + serverPort);
            	System.exit(-1);
        	}
    	}
	}
}

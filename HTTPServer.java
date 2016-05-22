//package networks;

import java.net.*;
import java.io.*;
import java.util.*;

public class HTTPServer {
	public static void main(String[] args) throws IOException {
		Map<String, String> flags = Utils.parseCmdlineFlags(args);
		if (!flags.containsKey("--serverPort") || !flags.containsKey("--sslServerPort")) {
			System.out.println("usage: Server --serverPort = 4444 --sslServerPort = 12345");
			System.exit(-1);
		}

		int serverPort = -1, sslServerPort = -1;
		try {
			serverPort = Integer.parseInt(flags.get("--serverPort"));
			sslServerPort = Integer.parseInt(flags.get("--sslServerPort"));
		} catch (NumberFormatException e) {
			System.out.println("Invalid port number! Must be an integer.");
			System.exit(-1);
		}

		if (serverPort == sslServerPort) {
			System.out.println("sslServerPort must be different from serverPort ");
			System.exit(-1);
		}

		if (serverPort < 1024 || sslServerPort < 1024) {
			System.out.println("Port number 0-1023 are reserved for operating system.");
			System.exit(-1);
		}

		boolean isSSL = true;
        try {
			new HTTPServerThread(isSSL, sslServerPort).start();
			new HTTPServerThread(!isSSL, serverPort).start();
   	    } catch (Exception e) {
            System.err.println("Could not listen on port " + serverPort);
            System.exit(-1);
        }
	}
}

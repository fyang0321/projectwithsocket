//package networks;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;


public class HTTPServerThread extends Thread {
    private ServerSocket serverSocket = null;

    public HTTPServerThread(boolean isSSL, int portNumber) {
        super("HTTPServerThread");
        serverSocket = initializeSocket(isSSL, portNumber);
    }

    public void run() {
        boolean listening = true;
        while (listening) {
            try {
                Socket socket = serverSocket.accept();

                DataOutputStream toClientStream = new DataOutputStream(socket.getOutputStream());
                BufferedReader fromClientStream  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                HttpRequestManager requestManager = new HttpRequestManager();
                while(true)
                {
                  requestManager.handleRequest(fromClientStream);
                  String cntType = requestManager.getCntType();
                  HttpResponseManager responseManager = new HttpResponseManager(requestManager);
                  responseManager.buildResponse(toClientStream);
                  if(cntType.equals(" close")){
                    break;
                  }
                }

                socket.close();
                // System.out.println("Close Connection");
            } catch (IOException e) {
              // System.out.println("Timed out, close this socket.");
              System.out.println("Remote socket may be closed");
        	  }
        }
    }

    private ServerSocket initializeSocket(boolean isSSL, int portNumber) {
        if (isSSL) {
            String password = "123456";
            SSLServerSocket sslServerSocket = null;

            try {
                KeyStore store = KeyStore.getInstance("JKS");
                store.load(new FileInputStream("server.jks"), password.toCharArray());
                KeyManagerFactory factory = KeyManagerFactory.getInstance("SunX509");
                factory.init(store, password.toCharArray());
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(factory.getKeyManagers(), null, null);
                SSLServerSocketFactory sslFactory = context.getServerSocketFactory();
                sslServerSocket = (SSLServerSocket)sslFactory.createServerSocket(portNumber);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            return sslServerSocket;
        } else {
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(portNumber);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            return serverSocket;
        }
    }

}

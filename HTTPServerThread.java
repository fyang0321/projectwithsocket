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
            } catch (KeyStoreException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (CertificateException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (KeyManagementException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (IOException e) {
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


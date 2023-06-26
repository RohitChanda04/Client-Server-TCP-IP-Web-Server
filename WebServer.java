package com.webServer.santaClaraUniversity;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	
	private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
	private static final String DOCUMENT_ROOT = "-documentRoot";
	private static final String PORT = "-port";
	
	public static void main(String[] args) {
		if(args.length !=4 || !args[0].equals(DOCUMENT_ROOT) || !args[2].equals(PORT)) {
			logger.error("\n\nERROR: Arguments missing or format wrong!\n\n" + 
				    "Please ensure that the required arguments are in below format -\n" +
				          "-documentRoot {root directory} -port {port number}");
				      System.exit(-1);
		}
	    String documentRootDirectory = args[1];
	    int port = Integer.parseInt(args[3]);
	    logger.info("\n\nWeb Server listening on port {}", port);
	    try (ServerSocket serverSideSocket = new ServerSocket(port)) {
	    	while (true) {	    		
		        Socket clientSideSocket = serverSideSocket.accept();
		        Thread thread = new Thread(new HttpRequest(clientSideSocket, documentRootDirectory));
		        thread.start();
		      }
	    }
	    catch (IOException e) {
	    	logger.error("Unsuccessful attempt: unable to listen on port {}", port);
	    }
  }
}
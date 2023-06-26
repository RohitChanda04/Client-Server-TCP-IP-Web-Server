package com.webServer.santaClaraUniversity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HttpRequest implements Runnable {
	
	  private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	  private static final String INDEX_FILE_NAME = "index.html";
	  private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";
	  
	  //Declaring the type of content 
	  private static final String CONTENT_TYPE_HTML = "text/html";
	  private static final String CONTENT_TYPE_JPG = "image/jpeg";
	  private static final String CONTENT_TYPE_PNG = "image/png";
	  private static final String CONTENT_TYPE_GIF = "image/gif";
	  private static final String CONTENT_TYPE_DEFAULT = "application/octet-stream";
	  
	  // Declaring the Status Codes
	  private static final String STATUS_200 = "HTTP/1.1 200 OK";
	  private static final String STATUS_400 = "HTTP/1.1 400 Bad Request";
	  private static final String STATUS_403 = "HTTP/1.1 403 Forbidden";
	  private static final String STATUS_404 = "HTTP/1.1 404 Not Found";
	  private static final String STATUS_405 = "HTTP/1.1 405 Method Not Allowed";
	  private static final String STATUS_500 = "HTTP/1.1 500 Internal Server Error";

	  private static final String CONTENT_400 = "<HTML> <HEAD><TITLE>Not Found</TITLE></HEAD> <BODY>404 Error: Not Found</BODY></HTML>";

	  private Socket socket;
	  private String rootDirectory;
	  
	  public HttpRequest(Socket socket, String rootDirectory) {
		  this.socket = socket;
		  this.rootDirectory = rootDirectory;
	  }
	  
	  @Override
	  public void run() {
	    logger.info("Current thread {}", Thread.currentThread().getName());

	    try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	         DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {

	    	int statusCode;
	    	String contentType;
	    	byte[] entityBody = null;
	    	
	    	String request = br.readLine();
	    	
	    	// Generating "400 - Bad Request" Response
	    	if (request.isBlank() || request == null) {
	    		statusCode = 400;
	    		createResponse(statusCode, CONTENT_TYPE_HTML, null, os);
	    		closeSocket();
	    		return;
	    	}
	    	
	    	logger.info("Request body - {}", request);
	    	String[] requestArgsFromConsole = request.split("\\s+");
	    	
	    	// Generating "405 - Method Not Allowed or Invalid Method" Response
	    	if (!requestArgsFromConsole[0].equalsIgnoreCase("GET")) {
	    		statusCode = 405;
	            createResponse(statusCode, CONTENT_TYPE_HTML, null, os);
	            closeSocket();
	            return;
	    	}

	    	String filename = requestArgsFromConsole[1];
	    	
	    	if (filename.equals("/"))
	    		filename = INDEX_FILE_NAME;
	    	
	    	Path filePath = Paths.get(rootDirectory, filename);
	    	
	    	//Construct the response message
	    	if (Files.exists(filePath)) {
	    		if (Files.isDirectory(filePath)) {
	            	logger.error("Requested resource {} is a directory", filePath.toString());
	            	statusCode = 400;
	            	contentType = CONTENT_TYPE_HTML;
	            }
	            else if (!Files.isReadable(filePath)) {
	            	logger.error("Do not have access to read the file {}", filePath.toString());
	            	statusCode = 403;
	            	contentType = CONTENT_TYPE_HTML;
	            }
	            else {
	            	logger.info("Requested file {}", filePath.toString());
	                String fileName = filePath.getFileName().toString();
	                if (fileName.endsWith(".html") || fileName.endsWith(".txt"))
	                	contentType = CONTENT_TYPE_HTML;
	                else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
	                	contentType = CONTENT_TYPE_JPG; 
	                else if (fileName.endsWith(".png"))
	                	contentType = CONTENT_TYPE_PNG; 
	                else if (fileName.endsWith(".gif"))
	                	contentType = CONTENT_TYPE_GIF;
	                else
	                	contentType = CONTENT_TYPE_DEFAULT;
	                entityBody = Files.readAllBytes(filePath);
	                statusCode = 200;
	            }
	    	}
	    	else {
	    		logger.error("Requested file {} does not exist ", filePath.toString());
	    		statusCode = 404;
	    		contentType = CONTENT_TYPE_HTML;
	      	  	entityBody = CONTENT_400.getBytes();
	    	}
	    	
	    	createResponse(statusCode, contentType, entityBody, os);
	    	closeSocket(); // closing the connection/socket successfully at the end

	    }
	    catch (IOException e) {
	      logger.error("Error in processing request", e);
	    }

	  }

	  private void closeSocket() throws IOException {
	    socket.close();
	  }

	  private void createResponse(int statusCode, String contentType, byte[] entityBody, DataOutputStream os) throws IOException {
	    //Write HTTP status line
	    os.writeBytes(createStatusLine(statusCode));

	    //Write Headers
	    String headers = createHeaders(statusCode, contentType, entityBody);
	    os.writeBytes(headers);

	    // Add a blank line to indicate end of headers
	    os.writeBytes(CARRIAGE_RETURN_LINE_FEED);

	    //Write Content
	    if (entityBody != null) {
	      os.write(entityBody);
	    }
	  }

	  // Creating the Status Line
	  private String createStatusLine(int statusCode) {
		  // Using Switch statement for faster computing
		  switch (statusCode) {
		  case 200:
	        return STATUS_200 + CARRIAGE_RETURN_LINE_FEED;
	      case 400:
	        return STATUS_400 + CARRIAGE_RETURN_LINE_FEED;
	      case 403:
	        return STATUS_403 + CARRIAGE_RETURN_LINE_FEED;
	      case 404:
	        return STATUS_404 + CARRIAGE_RETURN_LINE_FEED;
	      case 405:
	        return STATUS_405 + CARRIAGE_RETURN_LINE_FEED;
	      default:
	        return STATUS_500 + CARRIAGE_RETURN_LINE_FEED;
	    }
	  }

	  // Creating headers for the Response
	  private String createHeaders(int statusCode, String contentType, byte[] entityBody) {
	    StringBuilder header = new StringBuilder();
	    header.append("Date: ").append(getServerTime()).append(CARRIAGE_RETURN_LINE_FEED);
	    header.append("Content-Type: ").append(contentType).append(CARRIAGE_RETURN_LINE_FEED);
	    if (entityBody != null)
	    	header.append("Content-Length: ").append(entityBody.length).append(CARRIAGE_RETURN_LINE_FEED);
	    if (statusCode == 405)
	    	header.append("Allow: GET").append(CARRIAGE_RETURN_LINE_FEED);
	    return header.toString();
	  }

	  // Getting the Timestamp
	  private String getServerTime() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
	            .withZone(ZoneId.of("GMT"));
	    return LocalDateTime.now().format(formatter);
	  }

}

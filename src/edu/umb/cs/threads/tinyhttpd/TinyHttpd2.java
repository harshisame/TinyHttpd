package edu.umb.cs.threads.tinyhttpd;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
//import java.util.ArrayList;
//import java.util.Iterator;
import java.util.StringTokenizer;

//import javax.imageio.ImageIO;
//import javax.imageio.stream.ImageInputStream;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
//import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.File;

public class TinyHttpd2{
	private static final int PORT = 8888;
	private ServerSocket serverSocket;

	public void init(){
		try{
			try{
				serverSocket = new ServerSocket(PORT);
				System.out.println("Socket created.");
			
				while(true){	
					System.out.println( "Listening to a connection on the local port " +
										serverSocket.getLocalPort() + "..." );
					Socket client = serverSocket.accept();
					System.out.println( "\nA connection established with the remote port " + 
										client.getPort() + " at " +
										client.getInetAddress().toString() );
						
					new Thread(new TinyHttpd3(client)).start();
				}
			}
			finally{
				serverSocket.close();
			}
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}

	public void executeCommand( Socket client ){
		try {
			
			client.setSoTimeout(30000);
			BufferedReader in = new BufferedReader( new InputStreamReader( client.getInputStream() ) );  
			PrintStream out = new PrintStream( client.getOutputStream() );  
			String requestFile = "index.html";
			
			try {	
				System.out.println( "I/O setup done" );
				
				String line = in.readLine();
				
				StringTokenizer token = new StringTokenizer(line);
				String command = token.nextToken();
				
				if(command.equals("GET") || command.equals("HEAD")){
					requestFile = token.nextToken();
					
					if(requestFile.startsWith("/")){
						requestFile = "." + requestFile;
						File file = new File(requestFile);
						
						if(file.exists()){
							out.println("HTTP/1.0 200 OK");
						}
						else{
							out.println("HTTP/1.0 404 Not Found");
						}
						
						System.out.println(file.getName() + " requested.");
						sendFile(out, file);
						
					}
				}
				else if(command.equals("POST")){
					
					StringBuilder html = new StringBuilder();
					
					int bodyLength = 0;
					
					while(!(line = in.readLine()).equals("")){
						
						final String header = "Content-Length: ";
						if(line.startsWith(header)){
							bodyLength = Integer.parseInt(line.substring(header.length()));
						}
					}
					int content = 0;
					int i=0;
					while(i<bodyLength){
						content = in.read();
						html.append((char)content);
						i++;
					}
					System.out.println(html);
					out.println("Input data from user: " + html);
				}
				else{
					out.println("HTTP/1.0 501 Not Implemented");
				}
				
				while( line != null ) {
					System.out.println(line);
					if(line.equals("")){
						break;
					}
					line = in.readLine();
				}
				System.out.println(line);

				out.flush();
				
				
			}
			finally{
				in.close();
				out.close();
				client.close();
				System.out.println( "A connection is closed." );				
			}
		}
		catch(Exception exception) {
			exception.printStackTrace();
		}
	} 
	
	private void sendFile(PrintStream out, File file){
		try{
			DataInputStream fin = new DataInputStream(new FileInputStream(file));
			
			try{
				
				int len = (int) file.length();
				out.println("Content-Length: " + len);
				out.println("Date: "+new Date());
				out.println("");  

				byte buf[] = new byte[len];
				fin.readFully(buf);
				
				if(len != 0){
					out.write(buf, 0, len);
				}
				
				out.write(buf, 0, len);
				out.flush();
			}
			finally{
				fin.close();
			}
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TinyHttpd2 server = new TinyHttpd2();
		server.init();
	}

}

package MultiTask_Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Comm {
	
	static String status = "dandy";
	static String received = "";
			
	static String IP = "127.0.0.1";
		
	static ServerSocket serverSocket;
	
	public static int inwards = 1234; //this must match with client port of database
	public static int outwards = 4321;
	
	static Comm peer;
		
    public static void create()
    {       	
    	try {
    		serverSocket = new ServerSocket(inwards);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public static void sendMessage(String text)
    {      
		Socket client = null;
		try {
			client = new Socket(IP, outwards);
		} catch ( IOException e) {}
		
		PrintStream myPS = null;
		try {
			myPS = new PrintStream(client.getOutputStream());
			myPS.println(text);
			status = "dandy";
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
			status = "could not connect";
		}
		
		
		BufferedReader myBR = null;
		try {
			myBR = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
		
		String temp = null;
		try {
			temp = myBR.readLine();
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}

    }
    
    public static String getStatus() {
    	return status;
    }
    
    public static String getReceived() {
    	return received;
    }
    
    public static void receive() throws Exception {
		Socket SS_accept = serverSocket.accept();
		
		BufferedReader SS_BF= new BufferedReader(new InputStreamReader(SS_accept.getInputStream()));
	
		String clientMessage = SS_BF.readLine();
	
		if (clientMessage!=null) {
			PrintStream SSPS = new PrintStream(SS_accept.getOutputStream());
			received = clientMessage;
		}
	}


}

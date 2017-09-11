package jrdp;

import java.net.*;

public class Client {
	private String address,password;
	private int port;
	private double compression;
	
	private Socket socket;
	
	public Client(String theAddress,int thePort,String thePassword,double theCompression) {
		address = theAddress;
		port = thePort;
		password = thePassword;
		compression = theCompression;
	}
	
	public void connect() {
		
	}
	
}

package jrdp;

import java.net.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;

public class Client extends JFrame {
	private String address,password;
	private int port;
	private double compression;
	
	private Socket socket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	public JLabel label;
	
	public Client(String theAddress,int thePort,String thePassword,double theCompression) {
		address = theAddress;
		port = thePort;
		password = thePassword;
		compression = theCompression;
		
		label = new JLabel(new ImageIcon());
		add(label);
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
	}
	
	public void connect() {
		try {
			socket = new Socket(address, port);
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sOutput.writeObject(password);
			sOutput.writeDouble(compression);
			sOutput.flush();
			System.out.println("Info: Client connected to server");
		}catch(Exception ex) {ex.printStackTrace();}
		new ListenFromServer(socket,sInput,sOutput).start();
	}
	
	public void revailidateFrame() {
		revalidate();
	}
	
	class ListenFromServer extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		ListenFromServer(Socket thesocket,ObjectInputStream thesInput,ObjectOutputStream thesOutput){
			socket = thesocket;sInput = thesInput;sOutput = thesOutput;
		}
		
		public void run() {
			while(true) {
				try {
					BufferedImage screenshot = (BufferedImage) sInput.readObject();
					label = new JLabel(new ImageIcon(screenshot));
					revailidateFrame();
					System.out.println("Info: Updated Image Label");
				}catch(Exception ex) {ex.printStackTrace();}
			}
		}
	}
}
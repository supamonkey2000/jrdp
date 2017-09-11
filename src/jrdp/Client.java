package jrdp;

import java.net.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;

public class Client extends JFrame {
	/**
	 * @author Joshua C Moore
	 */private static final long serialVersionUID = 1L;
	
	private String address,password;
	private int port;
	private double compression;
	public int height,width;
	
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
		height = getHeight();
		width = getWidth();
	}
	
	public void connect() {
		try {
			socket = new Socket(address, port);
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sOutput.writeObject(password);
			sOutput.writeDouble(compression);
			sOutput.flush();
			System.out.println("Info: Client connected to server at "+socket.getRemoteSocketAddress()+":"+socket.getPort());
		}catch(Exception ex) {ex.printStackTrace();}
		System.out.println("Info: Starting Listener thread");
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
					byte[] toConvertBytes = (byte[])sInput.readObject();
					BufferedImage screenshot = new NetworkHandler().bytesToImage(toConvertBytes);
					Image newImage = new ImageIcon(screenshot).getImage().getScaledInstance(width, height, java.awt.Image.SCALE_FAST);
					label.setIcon(new ImageIcon(newImage));
					revailidateFrame();
				}catch(Exception ex) {ex.printStackTrace();}
			}
		}
	}
}
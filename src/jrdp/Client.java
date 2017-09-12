package jrdp;

import java.net.*;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
	public int height,width,serverHeight,serverWidth;
	public double scaleRatio;
	
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
			String serverXY = (String)sInput.readObject();
			String[] serverXYarray = serverXY.split("x");
			serverWidth=Integer.parseInt(serverXYarray[0]);
			serverHeight=Integer.parseInt(serverXYarray[1]);
			scaleRatio=((serverWidth / width) + (serverHeight / height) / 2);
			
			System.out.println("Info: Client connected to server at "+socket.getRemoteSocketAddress());
		}catch(Exception ex) {ex.printStackTrace();}
		System.out.println("Info: Starting Listener thread");
		new ListenFromServer(socket,sInput,sOutput).start();
		new SendInput(socket, sInput, sOutput).start();
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
	
	class SendInput extends Thread {
		Robot robot;
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		SendInput(Socket thesocket,ObjectInputStream thesInput,ObjectOutputStream thesOutput){
			socket = thesocket;sInput = thesInput;sOutput = thesOutput;try{robot = new Robot();}catch(Exception e){}
		}
		
		public void run() {
			//data format (separated by ":") (keystrokes are separated by "~")
			//{winStartX, winStartY, winEndX, winEndY, key, mouseXincrement, mouseYincrement, scrollIncrement}
			addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e){}@Override public void keyReleased(KeyEvent e){}
				@Override
				public void keyPressed(KeyEvent e) { /////// PRESSED KEY
					try {
						String send = "-1:-1:-1:-1:"+e.getKeyCode()+":0:0:0:-1";
						sOutput.writeObject(send);
						sOutput.flush();
						System.out.println("Info: Sent a key!");
					}catch(Exception ex) {}
				}
			});
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						@SuppressWarnings("static-access")
						int button = e.getMaskForButton(e.getButton());
						String send = "-1:-1:-1:-1:-1:0:0:0:"+button;
						sOutput.writeObject(send);
						sOutput.flush();
						System.out.println("Info: Sent a mouse click!");
					}catch(Exception ex) {}
				}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}
			});
			
			addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseMoved(MouseEvent e) {
					int localX = e.getX();
					int localY = e.getY();
					int sendX = (int)(localX * scaleRatio);
					int sendY = (int)(localY * scaleRatio);
					String send = "-1:-1:-1:-1:-1:"+sendX+":"+sendY+":0:-1";
					try {
						sOutput.writeObject(send);
						sOutput.flush();
					}catch(Exception ex) {}
				}
				@Override
				public void mouseDragged(MouseEvent e) {
					int localX = e.getX();
					int localY = e.getY();
					int sendX = (int)(localX * scaleRatio);
					int sendY = (int)(localY * scaleRatio);
					String send = "-1:-1:-1:-1:-1:"+sendX+":"+sendY+":0:"+e.getButton();
					try {
						sOutput.writeObject(send);
						sOutput.flush();
					}catch(Exception ex) {}
				}
			});
		}
	}
}
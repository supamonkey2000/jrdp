package jrdp;

import java.net.*;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import javax.swing.*;

class Client extends JFrame {
	
	private String password;
	private int port;
	private double compression;
	private double height,width,serverHeight,serverWidth;
	private double scaleRatio;
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] buf = new byte[1000];
	private JLabel label;
	private InetAddress address;
	
	Client(String address,int thePort,String thePassword,double theCompression) {
		try {
			this.address = InetAddress.getByName(address);
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
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	void connect() {
		try {
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf, buf.length);
			System.out.println("Created");
			socket.connect(address, port);
			System.out.println("Connected");
			buf = password.getBytes();
			socket.send(new DatagramPacket(buf, buf.length));
			buf = Double.toString(compression).getBytes();
			socket.send(new DatagramPacket(buf, buf.length));
			System.out.println("Sent password and compression");
			socket.receive(packet);
			String serverXY = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Received size");
			String[] serverXYarray = serverXY.split("x");
			serverWidth=Integer.parseInt(serverXYarray[0]);
			serverHeight=Integer.parseInt(serverXYarray[1]);
			scaleRatio=(((serverWidth / width) + (serverHeight / height)) / 2);
			
			System.out.println("Info: Client connected to server at "+socket.getRemoteSocketAddress()+" with screen size "+serverWidth+"x"+serverHeight);
		}catch(Exception ex) {ex.printStackTrace();}
		System.out.println("Info: Starting Listener thread");
		new ListenFromServer().start();
		//new SendInput(socket, sInput, sOutput).start();
	}
	
	class ListenFromServer extends Thread {
		
		public void run() {
			while(true) {
				try {
					//byte[] toConvertBytes = (byte[])sInput.readObject();
					//BufferedImage screenshot = new NetworkHandler().bytesToImage(toConvertBytes);
					//Image newImage = new ImageIcon(new NetworkHandler().bytesToImage((byte[])sInput.readObject())).getImage().getScaledInstance((int)width, (int)height, java.awt.Image.SCALE_SMOOTH);
					socket.receive(packet);
					label.setIcon(new ImageIcon(new ImageIcon(new NetworkHandler().bytesToImage(packet.getData())).getImage().getScaledInstance((int)width, (int)height, java.awt.Image.SCALE_SMOOTH)));
				}catch(Exception ex) {System.out.println("WARN: Socket closed by Server!");break;}
			}
		}
	}
	
	class SendInput extends Thread {
		Robot robot;
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		SendInput(Socket thesocket,ObjectInputStream thesInput,ObjectOutputStream thesOutput){
			socket = thesocket;sInput = thesInput;sOutput = thesOutput;
			try {
				robot = new Robot();
			} catch(Exception e) {
				e.printStackTrace();
			}
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
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						int button=e.getButton();
						String send = "-1:-1:-1:-1:-1:0:0:0:"+button;
						sOutput.writeObject(send);
						sOutput.flush();
						System.out.println("Info: Sent a mouse click!");
					} catch(Exception ex) {
						ex.printStackTrace();
					}
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
					} catch(Exception ex) {
						ex.printStackTrace();
					}
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
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
	}
}
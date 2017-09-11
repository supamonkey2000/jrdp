package jrdp;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class Server {
	private int port;
	private String password;
	
	private ServerSocket serverSocket = null;
	
	public Server(int thePort,String thePassword) {
		port = thePort;
		password = thePassword;
	}
	
	public void start() {
		try {
			System.out.println("Info: Starting server");
			serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();
			ClientThread cl = new ClientThread(socket);
			System.out.println("Info: Running ClientThread.start()");
			cl.start();
			System.out.println("Info: Started without errors");
		}catch(IOException ex) {ex.printStackTrace();}
	}
	
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		double compression = 0.50;
		
		int windowStartX=0;
		int windowStartY=0;
		int windowEndX=0;
		int windowEndY=0;
		
		ClientThread(Socket theSocket){
			socket = theSocket;
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				String testPassword = (String) sInput.readObject();
				if(!testPassword.equals(password)) {
					socket.close();
				}
				System.out.println("Info: Connection accepted at "+socket.getRemoteSocketAddress());
				double tmpCompression = (double) sInput.readDouble();
				if(tmpCompression > 0.00 && tmpCompression <=1.00) {
					compression = tmpCompression;
				}
			}catch(Exception e) {e.printStackTrace();}
		}
		
		public void run() {
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			int serverWidth = gd.getDisplayMode().getWidth();
			int serverHeight = gd.getDisplayMode().getHeight();
			try {
				sOutput.writeObject(serverWidth+"x"+serverHeight);
				sOutput.flush();
			}catch(Exception ex){}
			System.out.println("Info: Running imageThread");
			Thread imageThread = new Thread() {
				public void run() {
					ImageHandler ih = new ImageHandler(compression,socket);
					while(true) {
						BufferedImage screenshot = ih.getScreenshot(-1,-1,-1,-1);
						//eventually the client will decide what the parameters are for zooming and stuff. for now just use -1-1-1-1
						try{
							sOutput.writeObject(new NetworkHandler().imageToBytes(screenshot)); //originally writeObject(screenshot);
							sOutput.flush();
						}catch(Exception ex) {}
						
					}
				}
			};
			imageThread.start();
			System.out.println("Info: ImageThread started");
			InputHandler inh = new InputHandler();
			System.out.println("Info: Starting Input loop");
			while(true) {
				try {
					String input = sInput.readUTF();
					//data format (separated by ":") (keystrokes are separated by "~")
					//{winStartX, winStartY, winEndX, winEndY, key, mouseXincrement, mouseYincrement, scrollIncrement}
					String[] data = input.split(":");
					windowStartX = Integer.parseInt(data[0]);
					windowStartY = Integer.parseInt(data[1]);
					windowEndX = Integer.parseInt(data[2]);
					windowEndY = Integer.parseInt(data[3]);
					String keyStrokes = data[4];//String[] and .split("~");
					int mXi = Integer.parseInt(data[5]);
					int mYi = Integer.parseInt(data[6]);
					int mSi = Integer.parseInt(data[7]);
					int mClick = Integer.parseInt(data[8]);
					inh.handle(keyStrokes,mXi,mYi,mSi,mClick);
				}catch(Exception ex) {}
			}
		}
	}
	
}

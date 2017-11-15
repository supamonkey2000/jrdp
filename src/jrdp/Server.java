package jrdp;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

class Server {
	private int port;
	private String password;

	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private byte[] buf = new byte[1000];
	//private InetAddress address;
	
	Server(int thePort,String thePassword) {
		port = thePort;
		password = thePassword;
	}
	
	void start() {
		try {
			System.out.println("Info: Starting server");
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String password = new String(packet.getData(), 0, packet.getLength());
			if(password.equals(this.password)) {
				ClientThread cl = new ClientThread(socket.getInetAddress());
				System.out.println("Info: Running ClientThread.start()");
				cl.start();
				System.out.println("Info: Started without errors");
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	class ClientThread extends Thread {
		double compression = 0.50;
		InetAddress address;
		
		ClientThread(InetAddress address) {
			this.address = address;
			try {
				System.out.println("Info: Connection accepted at " + address.getHostAddress());
				socket.receive(packet);
				String compString = new String(packet.getData(), 0, packet.getLength());
				double tmpCompression = Double.parseDouble(compString);
				System.out.println("Info: Compression from client = " + tmpCompression);
				if(tmpCompression > 0.00 && tmpCompression <=1.00) {
					compression = tmpCompression;
				}
				System.out.println("Info: Using " + compression + " for compression");
			}catch(Exception e) {e.printStackTrace();}
		}
		
		public void run() {
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			int serverWidth = gd.getDisplayMode().getWidth();
			int serverHeight = gd.getDisplayMode().getHeight();
			try {
				buf = (serverWidth + "x" + serverHeight).getBytes();
				socket.send(new DatagramPacket(buf, buf.length, address, port));
			}catch(Exception ex){/**/}
			System.out.println("Info: Running imageThread");
			Thread imageThread = new Thread() {
				public void run() {
					//ImageHandler ih = new ImageHandler(compression,socket);
					while(true) {
						//BufferedImage screenshot = ih.getScreenshot(-1,-1,-1,-1);
						//eventually the client will decide what the parameters are for zooming and stuff. for now just use -1-1-1-1
						try{
							long time1 = new Date().getTime();
							//gOutput.write(new NetworkHandler().imageToBytes(new ImageHandler(compression,socket).getScreenshot(-1,-1,-1,-1))); //originally writeObject(screenshot);
							//sOutput.writeObject(new NetworkHandler().imageToBytes(new ImageHandler(compression,socket).getScreenshot(-1,-1,-1,-1))); //originally writeObject(screenshot);
							long time2 = new Date().getTime();
							long dif = time2 - time1;
							System.out.println("Time 1: " + time1 + "\nTime 2: "+time2+"\nDiff: "+dif);
							buf = new NetworkHandler().imageToBytes(new ImageHandler(compression,socket).getScreenshot(-1,-1,-1,-1));
							socket.send(new DatagramPacket(buf, buf.length, address, port));
						} catch(Exception ex) {
							System.out.println("WARN: Socket has been closed by Client!");
							break;
						}
					}
				}
			};
			imageThread.start();
			System.out.println("Info: ImageThread started");
			InputHandler inh = new InputHandler();
			System.out.println("Info: Starting Input loop");
			/*while(true) {
				try {
					String input = (String)sInput.readObject();
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
			}*/
		}
	}	
}
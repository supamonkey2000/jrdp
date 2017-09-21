package jrdp;
import javax.swing.JFrame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.*;

public class JRDP extends JFrame {
	/**
	 * @author Joshua C Moore
	 */private static final long serialVersionUID = 1L;
	
	private JTextField hostPasswordTF,hostPortTF,clientAddressTF,clientPortTF,clientPasswordTF,clientCompressionTF;
	private JLabel hostPasswordLBL,hostPortLBL,clientAddressLBL,clientPortLBL,clientPasswordLBL,clientCompressionLBL;
	private JButton serverB,connectB;
	
	private JRDP() {
		super("Java Remote Desktop Protocol Viewer");
		setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		//gbc.weightx=0.1;
		//gbc.weighty=0.1;
		
		int tmpPassword = ThreadLocalRandom.current().nextInt(10000000,99999999);
		
		JLabel hostLabel = new JLabel("Host Setup\n");
		gbc.gridx=0;gbc.gridy=0;add(hostLabel,gbc);
		
		hostPasswordLBL = new JLabel("Set a password: ");
		hostPasswordTF = new JTextField(Integer.toString(tmpPassword));
		gbc.gridx = 0;gbc.gridy = 1;add(hostPasswordLBL,gbc);gbc.gridx = 1;add(hostPasswordTF,gbc);
		
		hostPortLBL = new JLabel("Set a port: ");
		hostPortTF = new JTextField("65432");
		gbc.gridx=0;gbc.gridy=2;add(hostPortLBL,gbc);gbc.gridx=1;add(hostPortTF,gbc);
		
		serverB = new JButton("Start Server");
		gbc.gridx=0;gbc.gridy=3;add(serverB,gbc);
		
		//address port password compression
		clientAddressLBL = new JLabel("Address of Server: ");
		clientPortLBL = new JLabel("Port of Server: ");
		clientPasswordLBL = new JLabel("Server Password: ");
		clientCompressionLBL = new JLabel("Compression level: ");
		clientAddressTF = new JTextField("255.255.255.255");
		clientPortTF = new JTextField("65432");
		clientPasswordTF = new JTextField("password");
		clientCompressionTF = new JTextField("0.50");
		connectB = new JButton("Connect");
		
		JLabel clientLabel = new JLabel("Client");
		JLabel emptyLabel1 = new JLabel("  ");
		gbc.gridy=4;add(emptyLabel1,gbc);
		gbc.gridy=5;add(clientLabel,gbc);
		gbc.gridy=6;add(clientAddressLBL,gbc);gbc.gridx=1;add(clientAddressTF,gbc);
		gbc.gridx=0;gbc.gridy=7;add(clientPortLBL,gbc);gbc.gridx=1;add(clientPortTF,gbc);
		gbc.gridx=0;gbc.gridy=8;add(clientPasswordLBL,gbc);gbc.gridx=1;add(clientPasswordTF,gbc);
		gbc.gridx=0;gbc.gridy=9;add(clientCompressionLBL,gbc);gbc.gridx=1;add(clientCompressionTF,gbc);
		gbc.gridx=0;gbc.gridy=10;add(connectB,gbc);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		setVisible(true);
		
		serverB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Server server = new Server(Integer.parseInt(hostPortTF.getText()),hostPasswordTF.getText());
				server.start();
			}
		});
		
		connectB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Client client = new Client(clientAddressTF.getText(),Integer.parseInt(clientPortTF.getText()),clientPasswordTF.getText(),Double.parseDouble(clientCompressionTF.getText()));
				client.connect();
			}
		});
	}
	
	public static void main(String[] args) {
		new JRDP();
	}
}
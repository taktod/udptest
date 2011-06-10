package com.ttProject.udptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class EchoClient extends Thread {
	private DatagramSocket socket;
	private String server = "xxx.xxx.xxx.xxx"; // change ipaddress.
	private int port = 12345;
	
	private Map<String, InetSocketAddress> socketMap = new HashMap<String, InetSocketAddress>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EchoClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public EchoClient() throws Exception {
		socket = new DatagramSocket();
		System.out.println("start client.");
		DatagramPacket sendPacket;
		sendPacket = new DatagramPacket("test".getBytes(), 0, "test".getBytes().length, new InetSocketAddress(server, port));
		socket.send(sendPacket);
		this.start();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		String line;
		// 送信動作
		while((line = br.readLine()) != null) {
			System.out.println("line data:" + line);
			for(String key : socketMap.keySet()) {
				sendPacket = new DatagramPacket(line.getBytes(), 0, line.getBytes().length, socketMap.get(key));
				socket.send(sendPacket);
			}
		}
	}
	@Override
	public void run() {
		DatagramPacket recvPacket;
		DatagramPacket sendPacket;
		while(true) {
			try {
				recvPacket = new DatagramPacket(new byte[1024], 1024);
				socket.receive(recvPacket);
				String data = new String(recvPacket.getData(), 0, recvPacket.getLength()).trim();
				System.out.println("address:" + recvPacket.getAddress() + " port:" + recvPacket.getPort());
				System.out.println("data:" + data);
				if(data.startsWith("/") && data.contains(":")) {
					System.out.println("this data for socket Connection");
					String key = data;
					String[] serverData = key.substring(1).split(":");
					String server = serverData[0];
					int port = Integer.parseInt(serverData[1]);
					InetSocketAddress address = new InetSocketAddress(server, port);
					sendPacket = new DatagramPacket("init".getBytes(), 0, "init".getBytes().length, address);
					socket.send(sendPacket);
					socketMap.put(key, address);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

package com.ttProject.udptest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class EchoServer extends Thread {
	private DatagramSocket socket;
	private Map<String, InetSocketAddress> addressMap = new HashMap<String, InetSocketAddress>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EchoServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public EchoServer() throws Exception {
		socket = new DatagramSocket(12345);
		System.out.println("start server.");
		this.start();
	}
	@Override
	public void run() {
		DatagramPacket recvPacket;
		DatagramPacket sendPacket;
		while(true) {
			try {
				recvPacket = new DatagramPacket(new byte[1024], 1024);
				socket.receive(recvPacket);
				System.out.println("address:" + recvPacket.getAddress() + " port:" + recvPacket.getPort());
				System.out.println("data:" + new String(recvPacket.getData(), 0, recvPacket.getLength()).trim());
				String recvkey = recvPacket.getAddress() + ":" + recvPacket.getPort();
				InetSocketAddress address = new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
				if(!addressMap.containsKey(recvkey)) {
					System.out.println("new server key:" + recvkey);
					// 前クライアントに新しい人が追加されたことを通知しておく。
					for(String key : addressMap.keySet()) {
						System.out.println("known key:" + key);
						sendPacket = new DatagramPacket(recvkey.getBytes(), 0, recvkey.getBytes().length, addressMap.get(key));
						socket.send(sendPacket);
						sendPacket = new DatagramPacket(key.getBytes(), 0, key.getBytes().length, address);
						socket.send(sendPacket);
					}
					// あたらしいクライアントが追加された。
					addressMap.put(recvkey, address);
				}
				sendPacket = new DatagramPacket(recvPacket.getData(), 0, recvPacket.getLength(), address);
				socket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

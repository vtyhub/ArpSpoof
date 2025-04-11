package test;

import java.net.ServerSocket;
import java.net.Socket;

public class TestSocket {
	public static final int startport = 0;
	public static final int endport = 65536;
	public static final int portnumber = endport - startport;

	public static void main(String[] args) {
		ServerSocket[] server = new ServerSocket[portnumber];
		Socket[] client = new Socket[portnumber];
		for (int i = startport; i < endport; i++) {
			int temp = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						server[temp - startport] = new ServerSocket(temp);
						client[temp - startport] = server[temp - startport].accept();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println(temp + "port has been used");
					}
				}
			}).start();
		}
	}
}

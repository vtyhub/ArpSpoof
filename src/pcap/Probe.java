package pcap;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Probe {

	

	@SuppressWarnings("unused")
	private static void search() {
		for (int num = 0; num <= 255; num++) {
			final String host = "172.16.221." + num;
			new Thread() {
				public void run() {
					try {
						InetAddress hostAddress = InetAddress.getByName(host);

						if (!hostAddress.getHostName().equalsIgnoreCase(hostAddress.getHostAddress())) {
							System.out.println(hostAddress.getHostName() + ":" + host);
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public static void main(String[] args) {

	}
}

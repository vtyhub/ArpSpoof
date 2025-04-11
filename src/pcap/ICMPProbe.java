package pcap;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import method.IPv4Convert;

public class ICMPProbe {

	public static void probeICMP(byte[] localIPBytes, byte[] maskBytes, int ms) throws IOException {
		byte[] networkBytes = IPv4Convert.networkAddressBytes(localIPBytes, maskBytes);
		int prefix = IPv4Convert.getPrefix(maskBytes);
		InetAddress byAddress = Inet4Address.getByAddress(localIPBytes);
		if (byAddress.isReachable(ms)) {

		}

	}

	public static void main(String[] args) {
		final String ips = "192.168.5.";
		for (int i = 1; i < 255; i++) {
			new Thread(new Probe(ips + i)).start();
		}
	}

	private static class Probe implements Runnable {
		private String ip;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				InetAddress address = Inet4Address.getByName(ip);
				if (address.isReachable(1000)) {
					System.out.println(ip + "主机在线");
				} else {
					System.out.println(ip + "离线");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Probe(String ip) {
			// TODO Auto-generated constructor stub
			this.ip = ip;
		}
	}
}

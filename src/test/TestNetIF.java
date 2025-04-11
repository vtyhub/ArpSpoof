package test;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class TestNetIF {
	public static void main(String[] args) throws SocketException, UnknownHostException {
		Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
		Vector<NetworkInterface> list = new Vector<>();
		while (nifs.hasMoreElements()) {
			list.add((NetworkInterface) nifs.nextElement());
		}

		List<InterfaceAddress> list2 = list.get(10).getInterfaceAddresses();
		for (int i = 0; i < list2.size(); i++) {
			System.out.println(list2.get(i));
		}

		Enumeration<InetAddress> inet = list.get(10).getInetAddresses();
		while (inet.hasMoreElements()) {
			InetAddress inetAddress = (InetAddress) inet.nextElement();
			System.out.println("inet  " + inetAddress);
			System.out.println("inet  " + inetAddress.getHostName());
			System.out.println("inet  " + inetAddress.getHostAddress());
		}

		InterfaceAddress interfaceAddress = list2.get(0);
		InetAddress address = interfaceAddress.getAddress();
		InetAddress broadcast = interfaceAddress.getBroadcast();
		short length = interfaceAddress.getNetworkPrefixLength();

		System.out.println(address.getHostAddress());
		System.out.println(broadcast.getHostAddress());
		System.out.println(length);

		InetAddress localHost = InetAddress.getLocalHost();
		System.out.println(localHost.getHostName());
		System.out.println(localHost.getHostAddress());
		System.out.println(Integer.parseInt("065"));
		// localHost.
	}
}

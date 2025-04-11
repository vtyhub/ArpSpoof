package test;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TestNet {
	public static void main(String[] args) throws SocketException {
		Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
		System.out.println(nifs);
		int[] a = new int[0];
		System.out.println(a);
	}
}

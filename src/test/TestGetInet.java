package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestGetInet {
public static void main(String[] args) throws UnknownHostException {
	byte[] mac={(byte) 0xfa,0x45,0x58,(byte) 0xac,(byte) 0xfe,(byte) 0xc3};
	System.out.println(MACConvert(mac));
	byte[] bs = InetAddress.getByName("17.17.1.1").getAddress();
	for (int i = 0; i < bs.length; i++) {
		System.out.println(bs[i]);
	}
}

public static String MACConvert(byte[] bytes) {
	
    StringBuilder buf = new StringBuilder(bytes.length * 2);
    for (int i = 0; i < bytes.length-1; i++) {
    	// 使用String的format方法进行转换
    	buf.append(String.format("%02x", new Integer(bytes[i] & 0xff)));
    	buf.append("-");
	}
    buf.append(String.format("%02x", new Integer(bytes[5] & 0xff)));
    return buf.toString();
}

}

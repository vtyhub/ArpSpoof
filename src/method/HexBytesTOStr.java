package method;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class HexBytesTOStr {

	/** 
	 * 方法一： byte[] to hex string
	 * 
	 * @param bytes
	 * @return
	 */ 
	public static String hexBytesTOS1(byte[] bytes) {
		// 一个byte为8位，可用两个十六进制位标识
		final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] buf = new char[bytes.length * 2];
		int a = 0;
		int index = 0;
		for (byte b : bytes) { // 使用除与取余进行转换
			if (b < 0) {
				a = 256 + b;
			} else {
				a = b;
			}

			buf[index++] = HEX_CHAR[a / 16];
			buf[index++] = HEX_CHAR[a % 16];
		}

		return new String(buf);
	}

	/**
	 * 方法二： byte[] to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String hexBytesTOS2(byte[] bytes) {
		final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] buf = new char[bytes.length * 2];
		int index = 0;
		for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
			buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
			buf[index++] = HEX_CHAR[b & 0xf];
		}

		return new String(buf);
	}

	/**
	 * 方法三： byte[] to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String hexBytesTOS3(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) { // 使用String的format方法进行转换
			buf.append(String.format("%02x", new Integer(b & 0xff)));
		}

		return buf.toString();
	}



	public static void main(String[] args) {
		NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
		String str1 = MACConvert.macToS(deviceList[2].mac_address,2);
		System.out.println(str1);
		System.out.println(deviceList[2].datalink_description);
		System.out.println(deviceList[2].description);
		System.out.println(deviceList[2].name);
	}
}

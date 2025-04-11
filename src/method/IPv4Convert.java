package method;

public class IPv4Convert {
	
	public static final String ARPLayerBroadcastIP = "0.0.0.0";
	public static final byte[] ARPLayerBroadcastIPBytes = strToBytes(ARPLayerBroadcastIP);
 
	/** 
	 * 通过前缀获取byte[],String子网掩码的两种算法,String的归约到byte[]
	 * 
	 * @param prefix
	 * @return
	 */
	public static byte[] getIPv4ByteMask1(int prefix) {
		final int byteofbit = 8, number = 4, minprefix = 0, maxprefix = byteofbit * number;
		if (prefix < minprefix) {
			prefix = minprefix;
		}
		if (prefix > maxprefix) {
			prefix = maxprefix;
		}
		byte[] mask = new byte[number];
		int group = prefix / byteofbit, mod = prefix % byteofbit;
		for (int i = 0; i < group; i++) {
			mask[i] = (byte) 0xff;
		}
		if (mod != 0) {
			for (int i = 1; i <= mod; i++) {
				mask[group] += 1L << (byteofbit - i);// 为了以防万一，还是采用1L
			}
			for (int i = 0; i < number - group - 1; i++) {
				mask[group + 1 + i] = 0x00;
			}
		} else {
			for (int i = 0; i < number - group; i++) {
				mask[group + i] = 0x00;
			}
		}
		return mask;
	}

	public static byte[] getIPv4ByteMask2(int prefix) {
		final int byteofbit = 8, number = 4, minprefix = 0, maxprefix = byteofbit * number;
		if (prefix < minprefix) {
			prefix = minprefix;
		}
		if (prefix > maxprefix) {
			prefix = maxprefix;
		}
		int tempmask = 0;
		byte[] mask = new byte[number];
		for (int i = 1; i <= prefix; i++) {
			tempmask += 1 << (maxprefix - i);
		}
		for (int i = number - 1; i >= 0; i--) {
			mask[i] |= tempmask >>> (maxprefix - (i + 1) * byteofbit);// 让字节数组获取，因此应该是或运算
		}
		return mask;
	}

	public static String getIPv4StrMask1(int prefix) {
		return bytesToStr(getIPv4ByteMask1(prefix));
	}

	public static String getIPv4StrMask2(int prefix) {
		return bytesToStr(getIPv4ByteMask2(prefix));
	}

	/**
	 * 通过byte[],String子网掩码获取前缀长度，后者归约到前者
	 * 
	 * @param bytemask
	 * @return
	 */
	public static int getPrefix(byte[] bytemask) {
		int tempmask = 0, byteofbit = 8;
		for (int i = 0; i < bytemask.length; i++) {
			int temp = bytemask[i] << (byteofbit * (bytemask.length - i - 1));
			tempmask |= temp;
		}
		return Integer.toBinaryString(tempmask).lastIndexOf("1") + 1;// 返回的是索引，索引从0开始因此用索引计算个数要加1
	}

	public static int getPrefix(String strmask) {
		return getPrefix(strToBytes(strmask));
	}

	/**
	 * 通过前缀长度，byte[]形式子网掩码，String子网掩码获取可用主机数，后两个归约到前缀长度
	 * 
	 * @param prefix
	 * @return
	 */
	public static long getMaxHostNum(int prefix) {
		// 下面一开始使用1默认为int，左移达到31位就会有异常，这里必须使用1L才能代表一个long
		return (1L << (32 - prefix)) - 2;
	}

	public static long getMaxHostNum(byte[] bytemask) {
		return getMaxHostNum(getPrefix(bytemask));
	}

	public static long getMaxHostNum(String strmask) {
		return getMaxHostNum(getPrefix(strmask));
	}

	/**
	 * byte[]形式和String形式子网掩码的互相转换
	 * 
	 * @param ipv4bytes
	 * @return
	 */
	public static String bytesToStr(byte[] ipv4bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ipv4bytes.length; i++) {
			int temp = ipv4bytes[i] & 0xff;// 使用范围更大的int来避免符号位为1，Java中没有无符号变量
			sb.append(temp);// 这里之所以不采用范围刚合适的short是因为采用的话，Java编译器要求参与&0xff这个运算的表达式的结果强制转化为short
			sb.append('.');// 也许因为Java位运算默认返回int值，而short占用的内存字节数比int小的缘故？temp使用long也不会要求转换int，使用占用空间更大的不会出现符号位异常
							// 看来向上转换是在编译器允许的安全范围之内
		}
		return sb.substring(0, sb.length() - 1);// -1是为了将最后一个.去掉，四位之间只有三个.
	}

	// 将172.16.1.242这样的十进制字符串转化为byte数组
	public static byte[] strToBytes(String ipv4str) {
		String[] ipv4strsp = ipv4str.trim().split("\\.");
		byte[] ipv4bytes = new byte[ipv4strsp.length];
		for (int i = 0; i < ipv4bytes.length; i++) {

			// 使用Byte.parseByte(ipv4strsp[i], 10)会转换异常，因为Java中没有无符号类型
			// byte类型的取值在-128到127之间，所以一旦ipv4strsp[i]的十进制表示超出了这个范围，就会转换异常，而这是很常见的
			// 比如192.168.170.155这四个地址位每一位就都超过了这个范围，只能通过使用内存中存储空间更大的类型避免符号为负
			// 本着能用尽可能小的空间，能跟目标尽可能匹配就匹配的原则没有使用大众普遍使用的Integer，使用了Short

			ipv4bytes[i] = (byte) Short.parseShort(ipv4strsp[i], 10);
		}
		return ipv4bytes;
	}

	/**
	 * 通过主机地址和子网掩码返回网络地址
	 * 
	 * @param localIPBytes
	 * @param maskBytes
	 * @return
	 */
	public static byte[] networkAddressBytes(byte[] localIPBytes, byte[] maskBytes) {
		byte[] networkaddress = new byte[maskBytes.length];
		for (int i = 0; i < maskBytes.length; i++) {
			networkaddress[i] = (byte) (localIPBytes[i] & maskBytes[i]);
		}
		return networkaddress;
	}

	public static byte[] networkAddressBytes(byte[] localIPBytes, int prefix) {
		return networkAddressBytes(localIPBytes, getIPv4ByteMask2(prefix));
	}

	public static byte[] networkAddressBytes(String localIP, String mask) {
		byte[] localIPBytes = IPv4Convert.strToBytes(localIP);
		byte[] maskBytes = IPv4Convert.strToBytes(mask);
		return networkAddressBytes(localIPBytes, maskBytes);
	}

	public static byte[] networkAddressBytes(String localIP, int prefix) {
		return networkAddressBytes(localIP, getIPv4StrMask2(prefix));
	}

	/**
	 * 返回String类型的网络地址
	 * 
	 * @param localIPBytes
	 * @param maskBytes
	 * @return
	 */
	public static String networkAddressStr(byte[] localIPBytes, byte[] maskBytes) {
		return bytesToStr(networkAddressBytes(localIPBytes, maskBytes));
	}

	public static String networkAddressStr(byte[] localIPBytes, int prefix) {
		return networkAddressStr(localIPBytes, getIPv4ByteMask1(prefix));
	}

	public static String networkAddressStr(String localIP, String mask) {
		return bytesToStr(networkAddressBytes(localIP, mask));
	}

	public static String networkAddressStr(String localIP, int prefix) {
		return networkAddressStr(localIP, getIPv4StrMask1(prefix));
	}
}

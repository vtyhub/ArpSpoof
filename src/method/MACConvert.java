package method;

public class MACConvert {

	public static final String LinkLayerBroadcastMAC = "ff-ff-ff-ff-ff-ff";
	public static final byte[] LinkLayerBroadcastMACBytes = sToMAC(LinkLayerBroadcastMAC);

	public static final String ARPLayerBroadcastMAC = "00-00-00-00-00-00";
	public static final byte[] ARPLayerBroadcastMACBytes = sToMAC(ARPLayerBroadcastMAC);

	public static byte[] sToMAC(String s) {
		byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		String[] s1 = s.trim().split("-");
		for (int x = 0; x < s1.length; x++) {
			mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
		}
		return mac;
	}

	public static String macToS(byte[] bytes, int type) {
		StringBuilder sb;
		switch (type) {
		case 1:
			sb = new StringBuilder(HexBytesTOStr.hexBytesTOS1(bytes));
			break;
		case 2:
			sb = new StringBuilder(HexBytesTOStr.hexBytesTOS2(bytes));
			break;
		case 3:
			sb = new StringBuilder(HexBytesTOStr.hexBytesTOS3(bytes));
			break;
		default:
			return "";
		}

		for (int i = sb.length() - 2; i > 0; i -= 2) {
			sb.insert(i, '-');
		}
		return sb.toString();
	}

	public static String macToS(byte[] bytes) {
		return macToS(bytes, 1);
	}

	public static void main(String[] args) {
		System.out.println(macToS(sToMAC("00-00-00-04-00-f0"), 3));
		byte[] sToMAC = sToMAC("00-00-00-00-00-00");
		for (byte b : sToMAC) {
			System.out.println(b);
		}
	}
}

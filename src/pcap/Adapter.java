package pcap;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import method.IPv4Convert;
import method.MACConvert;
import method.Remove;

public class Adapter {
	// һ�������������������һ������������������Ҫ��ѡ�е������������������ϵjpcap��netinf��һ��һ���������������ɣ�����ʵ��������Ӧ�ó�ʼ��

	static {
		setAdapterArray();
		setNetInfList();
		setIntegerMap();
		setAdapterMap();
	} 
 
	private static NetworkInterface[] AdapterArray;
	private static ArrayList<java.net.NetworkInterface> NetIfList;
	private static HashMap<Integer, Integer> IntegerMap;
	private static HashMap<NetworkInterface, java.net.NetworkInterface> AdapterMap;

	private static byte[][] MACByte;
	private static String[] MACString;

	private NetworkInterface selectedadapter;
	private java.net.NetworkInterface selectednetinf;

	public Adapter(Integer adapt) {
		this(AdapterArray[adapt]);
	}

	public Adapter(NetworkInterface selectedadapter) {
		this.selectedadapter = selectedadapter;
		this.selectednetinf = AdapterMap.get(selectedadapter);

		Enumeration<InetAddress> enuminet = this.selectednetinf.getInetAddresses();
		while (enuminet.hasMoreElements()) {
			InetAddress address = enuminet.nextElement();
			if (address.getAddress().length == 4) {
				this.localIPByte = address.getAddress();
				this.localIP = address.getHostAddress();
				break;
			}
		}

		this.localMACByte = selectedadapter.mac_address;
		this.localMAC = MACConvert.macToS(this.localMACByte, 1);

		List<InterfaceAddress> infl = selectednetinf.getInterfaceAddresses();
		for (int i = 0; i < infl.size(); i++) {
			short prefixLength = infl.get(i).getNetworkPrefixLength();
			if (prefixLength > 0 && prefixLength < 33) {
				this.ipv4prefix = prefixLength;// ͵����1
				this.localMask = IPv4Convert.getIPv4StrMask2(this.ipv4prefix);
				this.localMaskByte = IPv4Convert.getIPv4ByteMask2(ipv4prefix);
			} else {
				this.ipv6prefix = prefixLength;
			}
		}

		this.adaptername = selectednetinf.getName();
		this.localdesc = selectednetinf.getDisplayName();
	}

	public static void setAdapter(Adapter adapter, NetworkInterface jpcapadapter) {
		adapter = new Adapter(jpcapadapter);
	}

	private String adaptername;
	private String localdesc;
	private String localdatalinkdesc;

	private InetAddress localInet;

	private String localHostName;

	private String localIP;
	private byte[] localIPByte;

	private int ipv4prefix;
	private int ipv6prefix;

	private String localMask;
	@SuppressWarnings("unused")
	private byte[] localMaskByte;

	@SuppressWarnings("unused")
	private String localGateway;
	@SuppressWarnings("unused")
	private byte[] localGatewayByte;

	@SuppressWarnings("unused")
	private String localDNS;
	@SuppressWarnings("unused")
	private byte[] localDNSByte;

	private String localMAC;
	private byte[] localMACByte;

	public NetworkInterface getSelectedadapter() {
		return selectedadapter;
	}

	public java.net.NetworkInterface getSelectednetinf() {
		return selectednetinf;
	}

	public String getAdaptername() {
		return adaptername;
	}

	public String getLocaldesc() {
		return localdesc;
	}

	public int getIPv4Prefix() {
		return this.ipv4prefix;
	}

	public int getIPv6Prefix() {
		return this.ipv6prefix;
	}

	public static jpcap.NetworkInterface[] getAdapterArray() {
		// ûʱ��ȥΪÿһ��Ԫ�����
		return AdapterArray.clone();
	}

	public static void setAdapterArray() {
		AdapterArray = removeUselessAdapter(JpcapCaptor.getDeviceList());
	}

	public static ArrayList<java.net.NetworkInterface> getNetInfList() {
		// ûʱ�����
		return NetIfList;
	}

	public static void setNetInfList() {
		// ����ʵ����setʱ���ָ��
		ArrayList<java.net.NetworkInterface> NetInfList = new ArrayList<java.net.NetworkInterface>();
		try {
			Enumeration<java.net.NetworkInterface> nifs = java.net.NetworkInterface.getNetworkInterfaces();
			while (nifs.hasMoreElements()) {
				NetInfList.add(nifs.nextElement());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Adapter.NetIfList = removeUselessNetInf(NetInfList);
	}

	public static void setIntegerMap() {
		IntegerMap = pcapToNet(AdapterArray, NetIfList);
	}

	public static void setAdapterMap() {
		AdapterMap = pcapToNet(IntegerMap, AdapterArray, NetIfList);
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	public String getLocalMask() {
		return localMask;
	}

	public void setLocalMask(String localMask) {
		this.localMask = localMask;
	}

	public String getLocalMAC() {
		return localMAC;
	}

	public void setLocalMAC(String localMAC) {
		this.localMAC = localMAC;
	}

	// jpcap.NetworkInterface�е�mac_addressʵ��������MAC��ַ
	// java.net.NetworkInterface.getHardwareAddress()��ȡMAC��ַ
	// ͨ��MAC��ַ��ͬ��������ƥ��
	// type ��mac to String�����ַ�ʽ֮һ
	// �������������ԭ�����շ����ݰ���Ҫjpcap����jpcap������������������̫���ˣ�������jdk�бȽ϶࣬������Ҫһ��networkinterface��interface��ӳ��
	// ԭ�����Ϊ�������Ǿ��������ӿڣ�����ԭ������Ѿ����Ͻ�set�����У����������������Ӧ���Ǿ��������ӿ�
	public static HashMap<Integer, Integer> pcapToNet(NetworkInterface[] AdapterArray,
			ArrayList<java.net.NetworkInterface> NetInfList) {
		String[] pcapmac = new String[AdapterArray.length];
		String[] netmac = new String[NetInfList.size()];

		for (int i = 0; i < pcapmac.length; i++) {
			byte[] mac_address = AdapterArray[i].mac_address;
			if (mac_address != null) {
				pcapmac[i] = MACConvert.macToS(mac_address);
			}
		}
		pcapmac = Remove.removeUseless(pcapmac);

		for (int i = 0; i < netmac.length; i++) {
			byte[] hardwareAddress = null;
			try {
				hardwareAddress = NetInfList.get(i).getHardwareAddress();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			if (hardwareAddress != null) {
				netmac[i] = MACConvert.macToS(hardwareAddress);
			}
		}
		netmac = Remove.removeUseless(netmac);

		return pcapToNet(pcapmac, netmac);
	}

	// JPcap��ȡ���������б��п������ظ�MAC��ַ��������Ч�ģ�Ӧ��ȥ���ظ���
	public static HashMap<Integer, Integer> pcapToNet(String[] pcapmac, String[] netmac) {
		HashMap<Integer, Integer> hashMap = new HashMap<>();
		for (int i = 0; i < pcapmac.length; i++) {
			for (int j = 0; j < netmac.length; j++) {
				if (pcapmac[i].equals(netmac[j])) {
					hashMap.put(i, j);
				}
			}
		}
		return hashMap;
	}

	// �������Ϊ����������
	public static HashMap<NetworkInterface, java.net.NetworkInterface> pcapToNet(HashMap<Integer, Integer> IntegerMap,
			NetworkInterface[] AdapterArray, List<java.net.NetworkInterface> NetInfList) {
		HashMap<NetworkInterface, java.net.NetworkInterface> AdapterMap = new HashMap<NetworkInterface, java.net.NetworkInterface>(
				IntegerMap.size());
		for (int i = 0; i < IntegerMap.size(); i++) {
			AdapterMap.put(AdapterArray[i], NetInfList.get(IntegerMap.get(i)));
		}
		return AdapterMap;
	}

	public static NetworkInterface[] removeUselessAdapter(NetworkInterface[] AdapterArray) {
		String[] pcapmac = new String[AdapterArray.length];

		for (int i = 0; i < pcapmac.length; i++) {
			byte[] mac_address = AdapterArray[i].mac_address;
			if (mac_address != null) {
				pcapmac[i] = MACConvert.macToS(mac_address);
			}
		}

		ArrayList<String> list = new ArrayList<String>();
		ArrayList<NetworkInterface> AdapterList = new ArrayList<NetworkInterface>();

		for (int i = 0; i < pcapmac.length; i++) {
			if (pcapmac[i] != null && !list.contains(pcapmac[i])) {
				list.add(pcapmac[i]);// ��Ϊif���ڶ��������Բ���ȥ����һ��
				AdapterList.add(AdapterArray[i]);
			}
		}
		return AdapterList.toArray(new NetworkInterface[0]);
	}

	public static ArrayList<java.net.NetworkInterface> removeUselessNetInf(List<java.net.NetworkInterface> NetInfList) {

		String[] netmac = new String[NetInfList.size()];

		for (int i = 0; i < netmac.length; i++) {
			byte[] hardwareAddress = null;
			try {
				hardwareAddress = NetInfList.get(i).getHardwareAddress();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			if (hardwareAddress != null) {
				netmac[i] = MACConvert.macToS(hardwareAddress);
			}
		}
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<java.net.NetworkInterface> NewNetInfList = new ArrayList<java.net.NetworkInterface>();

		for (int i = 0; i < netmac.length; i++) {
			if (netmac[i] != null && !list.contains(netmac[i])) {
				list.add(netmac[i]);
				NewNetInfList.add(NetInfList.get(i));
			}
		}
		return NewNetInfList;
	}

	public static HashMap<Integer, Integer> getIntegerMap() {
		return IntegerMap;
	}

	public static HashMap<NetworkInterface, java.net.NetworkInterface> getAdapterMap() {
		return AdapterMap;
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {

		}
	}

}

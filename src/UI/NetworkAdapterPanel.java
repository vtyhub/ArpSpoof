package UI;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import jpcap.NetworkInterface;
import method.IPv4Convert;
import method.MACConvert;
import pcap.Adapter;
import pcap.AdapterInfo;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class NetworkAdapterPanel extends JPanel {

	private MainUI mainui;

	private JScrollPane AdapterScrollPane;
	private static String[] columnname = new String[] { "Number", "Name", "Description", "Status", "MAC Address",
			"DHCPv4 Status", "IPv4 Address", "Default Gateway", "Preferred DNS", "Alternate DNS", "IPv6 Auto",
			"IPv6 Address" };
	public static final int COLUMNCOUNT = columnname.length;
	private DefaultTableModel AdapterTableModel;
	private JTable adapterTable;
	private JPopupMenu AdapterTablePopup;
	private JButton btnReset;
	private JButton btnClear;
 
	private TableColumnModel adapterTableColumnModel;

	/** 
	 * Create the panel.
	 */
	public NetworkAdapterPanel(MainUI mainui) {
		this.mainui = mainui;

		// tablemodel
		this.AdapterTableModel = new DefaultTableModel(columnname, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// table
		this.adapterTable = new JTable(AdapterTableModel);
		adapterTable.getTableHeader().setReorderingAllowed(false);// 列位置不可移动
		adapterTable.getTableHeader().setResizingAllowed(true);//列宽不可改变？
		adapterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 只允许同时最多选中一行

		// --------------------------------------------------------------------------------
		// adapterTable ColumnModel
		adapterTableColumnModel = adapterTable.getColumnModel();

		// 0 Number
		adapterTableColumnModel.getColumn(0).setMinWidth(55);
		adapterTableColumnModel.getColumn(0).setMaxWidth(55);

		// 1 Name
		adapterTableColumnModel.getColumn(1).setMinWidth(50);
		adapterTableColumnModel.getColumn(1).setMaxWidth(50);

		// 2 Description
		adapterTableColumnModel.getColumn(2).setMinWidth(240);// 曾经250
		adapterTableColumnModel.getColumn(2).setMaxWidth(240);

		// 3 Media Status
		adapterTableColumnModel.getColumn(3).setMinWidth(80);
		adapterTableColumnModel.getColumn(3).setMaxWidth(80);

		// 4 MAC
		adapterTableColumnModel.getColumn(4).setMaxWidth(MainUI.MACWIDTH);
		adapterTableColumnModel.getColumn(4).setMinWidth(MainUI.MACWIDTH);

		// 5 DHCPv4 Status
		adapterTableColumnModel.getColumn(5).setMaxWidth(93);
		adapterTableColumnModel.getColumn(5).setMinWidth(93);

		// 6 IPv4
		adapterTableColumnModel.getColumn(6).setMinWidth(MainUI.IPWIDTH + 15);
		adapterTableColumnModel.getColumn(6).setMaxWidth(MainUI.IPWIDTH + 15);

		// 7 gateway
		adapterTableColumnModel.getColumn(7).setMaxWidth(MainUI.IPWIDTH);
		adapterTableColumnModel.getColumn(7).setMinWidth(MainUI.IPWIDTH);

		// 8 Preferred DNS
		adapterTableColumnModel.getColumn(8).setMaxWidth(MainUI.IPWIDTH);
		adapterTableColumnModel.getColumn(8).setMinWidth(MainUI.IPWIDTH);

		// 9 Alternate DNS
		adapterTableColumnModel.getColumn(9).setMaxWidth(MainUI.IPWIDTH);
		adapterTableColumnModel.getColumn(9).setMinWidth(MainUI.IPWIDTH);

		// 10 IPv6 Auto
		adapterTableColumnModel.getColumn(10).setMinWidth(60);
		adapterTableColumnModel.getColumn(10).setMaxWidth(60);

		// 11 IPv6
		adapterTableColumnModel.getColumn(11).setMinWidth(225);
		adapterTableColumnModel.getColumn(11).setMaxWidth(225);
		// 1359
		// ----------------------------------------------------------------------------------

		// select item
		JMenuItem selectitem = new JMenuItem("Select Adapter");
		selectitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 视图上是从1开始计算，而Map中是从0开始的，所以从视图上获取的索引应该-1再用在Map中
				int selectedRow = adapterTable.getSelectedRow();
				Integer key = Integer.valueOf(adapterTable.getValueAt(selectedRow, 0).toString()) - 1;// 其实值和selestedRow相同
				mainui.setAdapter(key);
				ARPAttackPanel attack = mainui.getARPTabbed().getAttackPanel();
				attack.setAdapterDescriptionTF(adapterTable.getValueAt(selectedRow, 2).toString());
				attack.setAdapterNameTF(adapterTable.getValueAt(selectedRow, 1).toString());
				attack.setAlternateDNSTF(adapterTable.getValueAt(selectedRow, 9).toString());
				attack.setGatewayTF(adapterTable.getValueAt(selectedRow, 7).toString());
				if (adapterTable.getValueAt(selectedRow, 6) != null) {
					String IPv4andMask = adapterTable.getValueAt(selectedRow, 6).toString();
					String prefix = IPv4andMask.substring(IPv4andMask.lastIndexOf("/") + 1, IPv4andMask.length());
					String IPv4 = IPv4andMask.substring(0, IPv4andMask.lastIndexOf("/"));
					try {
						attack.setHostnameTF(InetAddress.getByName(IPv4).getHostName());
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					attack.setIpTF(IPv4);
					attack.setMaskTF(IPv4Convert.getIPv4StrMask1(Integer.valueOf(prefix)));
				} else {
					attack.setIpTF("");
					attack.setMaskTF("");
				}

				attack.setMacTF(adapterTable.getValueAt(selectedRow, 4).toString());
				attack.setPreferredDNSTF(adapterTable.getValueAt(selectedRow, 8).toString());// dns不会出现null

			}
		});

		// popupmenu
		this.AdapterTablePopup = new JPopupMenu();
		this.AdapterTablePopup.add(selectitem);
		MainUI.addPopup(adapterTable, AdapterTablePopup);

		// button reset
		this.btnReset = new JButton("Reset");
		btnReset.addActionListener((e) -> {
			clearTable(AdapterTableModel);
			setAdapterPanel();
		});

		// button clear
		this.btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearTable(AdapterTableModel);
			}
		});

		// scroll
		this.AdapterScrollPane = new JScrollPane(this.adapterTable);

		// layout
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(AdapterScrollPane, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup().addComponent(btnClear)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnReset)))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(AdapterScrollPane, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(btnReset).addComponent(btnClear))
						.addContainerGap(40, Short.MAX_VALUE)));
		setLayout(groupLayout);

		// setPanel
		setAdapterPanel();
	}

	public static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public void setAdapterPanel() {

		HashMap<Integer, Integer> integerMap = Adapter.getIntegerMap();
		ArrayList<java.net.NetworkInterface> iflist = Adapter.getNetInfList();

		for (int i = 0; i < integerMap.size(); i++) {
			Adapter adapter = new Adapter(i);
			String MAC = adapter.getLocalMAC();

			String[] add = new String[COLUMNCOUNT];

			add[0] = String.valueOf(i + 1);// Number

			add[1] = iflist.get(integerMap.get(i)).getName();// Name java.net.NetworkIf类有详细描述

			add[2] = iflist.get(integerMap.get(i)).getDisplayName();// Description java.net.NetworkIf

			add[3] = AdapterInfo.getMediaStatus(MAC);// media status

			add[4] = MAC;// MAC Address
			// add[4] =
			// MACConvert.macToS(Adapter.getNetInfList().get(integerMap.get(i)).getHardwareAddress(),
			// 1);

			add[5] = AdapterInfo.getDHCPv4Status(MAC);

			Enumeration<InetAddress> inet = iflist.get(integerMap.get(i)).getInetAddresses();
			while (inet.hasMoreElements()) {
				InetAddress address = inet.nextElement();
				String hostAddress = address.getHostAddress() == null ? "" : address.getHostAddress();
				int ipv4prefix = adapter.getIPv4Prefix();
				if("".equals(hostAddress)) {
					add[6]="";
				}else if (hostAddress.length() <= 15 && !hostAddress.contains("%")) {
					add[6] = hostAddress + "/" + ipv4prefix;// IPv4 偷懒 没有
				} else {
					add[11] = hostAddress.substring(0, hostAddress.indexOf("%")) + "%" + adapter.getIPv6Prefix();// IPv6
				}
			}

			add[7] = AdapterInfo.getIPv4Gateway(MAC);

			String[] iPv4DNS = AdapterInfo.getIPv4DNS(MAC);
			for (int j = 0; j < iPv4DNS.length; j++) {// 8 9 首选备用DNS
				if (iPv4DNS[j] == null)
					add[8 + j] = "";
				else
					add[8 + j] = iPv4DNS[j];
			}

			add[10] = AdapterInfo.getIPv6AutoAddressStatus(MAC);

			AdapterTableModel.addRow(add);
		}
	}

	public void clearTable(DefaultTableModel model) {
		while (model.getRowCount() > 0)
			model.removeRow(0);
	}

	public static void main(String[] args) {
		MainUI test = new MainUI();
		test.getContentPane().add(new NetworkAdapterPanel(test));
		test.setVisible(true);
	}
}

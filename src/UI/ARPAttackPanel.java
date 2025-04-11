package UI;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ARPAttackPanel extends JPanel {
 
	private MainUI mainui;

	private static String[] columnname = new String[] { "Hostname", "IP Address", "MAC Address" };

	private DefaultTableModel scantablemodel;
	private JTable scantable;
	private JScrollPane scantablescroll;

	private JPopupMenu attackpopup;

	private JMenuItem customARPitem;

	private JLabel lblLocalHostname;
	private JTextField HostnameTF;

	private JLabel lblLocalIp;
	private JTextField ipTF;
	private JLabel lblSubnetMask;
	private JTextField maskTF;
	private JLabel lblLocalMac;
	private JTextField macTF;
	private JLabel lblDefaultGateway;
	private JTextField gatewayTF;
	private JLabel lblPreferredDns;
	private JTextField preferredDNSTF;
	private JLabel lblAlternateDns;
	private JTextField alternateDNSTF;
	private JLabel lblAdapterName;
	private JTextField adapterNameTF;
	private JLabel lblAdapterDescription;
	private JTextField adapterDescriptionTF;

	private JButton btnCustomArp;

	/**
	 * Create the panel.
	 */

	public ARPAttackPanel(MainUI mainui) {
		this.mainui = mainui;

		// JPanel
		this.setBounds(0, 0, 811, 402);

		// customARP menuitem
		customARPitem = new JMenuItem("Custom ARP");
		customARPitem.addActionListener((e) -> {
			new CustomARPDialog(mainui).setVisible(true);
		});

		// attack popup
		attackpopup = new JPopupMenu();
		attackpopup.add(customARPitem);

		// scantable model
		scantablemodel = new DefaultTableModel(columnname, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		// scantable
		scantable = new JTable(scantablemodel);
		scantable.getTableHeader().setReorderingAllowed(false);//列位置不可移动
		scantable.getTableHeader().setResizingAllowed(false);//列宽不可改变,和通过限定列的最大最小宽度一样的方法的区别是，连列都无法选定
		
		//		scantable.setAutoResizeMode(mode);
		MainUI.addPopup(scantable, attackpopup);
		
		//scantable columnmodel
		TableColumnModel scantablecolumnmodel = scantable.getColumnModel();
		
		// scantable scroll
		scantablescroll = new JScrollPane();
		scantablescroll.setBounds(10, 85, 751, 179);
		scantablescroll.setViewportView(scantable);

		// hostname label
		lblLocalHostname = new JLabel("Local Hostname");
		lblLocalHostname.setBounds(444, 10, 99, 15);

		// hostname tf
		HostnameTF = new JTextField();
		HostnameTF.setBounds(553, 10, 208, 15);
		HostnameTF.setEditable(false);

		// local mac label
		lblLocalMac = new JLabel("Local MAC");
		lblLocalMac.setBounds(105, 35, 111, 15);

		// mac tf
		macTF = new JTextField();
		macTF.setBounds(105, 60, 111, 15);
		macTF.setEditable(false);

		// localip label
		lblLocalIp = new JLabel("Local IP");
		lblLocalIp.setBounds(226, 35, 99, 15);

		// IPTF
		ipTF = new JTextField();
		ipTF.setBounds(226, 60, 99, 15);
		ipTF.setEditable(false);

		// subnetmask label
		lblSubnetMask = new JLabel("Subnet Mask");
		lblSubnetMask.setBounds(335, 35, 99, 15);

		// mask tf
		maskTF = new JTextField();
		maskTF.setBounds(335, 60, 99, 15);
		maskTF.setEditable(false);

		// gateway label
		lblDefaultGateway = new JLabel("Default Gateway");
		lblDefaultGateway.setBounds(444, 35, 99, 15);

		// gateway TF
		gatewayTF = new JTextField();
		gatewayTF.setBounds(444, 60, 99, 15);
		gatewayTF.setEditable(false);

		// preferreddns label
		lblPreferredDns = new JLabel("Preferred DNS");
		lblPreferredDns.setBounds(553, 35, 99, 15);

		// preferredDNS TF
		preferredDNSTF = new JTextField();
		preferredDNSTF.setBounds(553, 60, 99, 15);
		preferredDNSTF.setEditable(false);

		// alternatedns label
		lblAlternateDns = new JLabel("Alternate DNS");
		lblAlternateDns.setBounds(662, 35, 99, 15);

		// alternateDNS TF
		alternateDNSTF = new JTextField();
		alternateDNSTF.setBounds(662, 60, 99, 15);
		alternateDNSTF.setEditable(false);

		// adapterName label
		lblAdapterName = new JLabel("Adapter Name");
		lblAdapterName.setBounds(10, 35, 85, 15);

		// adapterName TF
		adapterNameTF = new JTextField();
		adapterNameTF.setBounds(10, 60, 85, 15);
		adapterNameTF.setEditable(false);

		// customARP Jbutton
		btnCustomArp = new JButton("Custom ARP");
		btnCustomArp.addActionListener((e) -> {
			if(mainui.getAdapter()==null) {
				JOptionPane.showMessageDialog(this, "Adapter not set!");
				return;
			}
			new CustomARPDialog(mainui, "", macTF.getText(), macTF.getText(), "", "").setVisible(true);
		});
		btnCustomArp.setBounds(638, 274, 123, 23);

		// AdapterDescription JLabel
		lblAdapterDescription = new JLabel("Adapter Description");
		lblAdapterDescription.setBounds(10, 10, 115, 15);

		// adapterdescription TF
		adapterDescriptionTF = new JTextField();
		adapterDescriptionTF.setEditable(false);
		adapterDescriptionTF.setBounds(135, 10, 299, 15);
		adapterDescriptionTF.setColumns(10);

		// Panel
		setLayout(null);
		add(lblLocalHostname);
		add(HostnameTF);
		add(lblAdapterName);
		add(lblLocalMac);
		add(lblLocalIp);
		add(lblSubnetMask);
		add(lblDefaultGateway);
		add(lblPreferredDns);
		add(lblAlternateDns);
		add(adapterNameTF);
		add(macTF);
		add(ipTF);
		add(maskTF);
		add(gatewayTF);
		add(preferredDNSTF);
		add(alternateDNSTF);
		add(scantablescroll);
		add(lblAdapterDescription);
		add(adapterDescriptionTF);

		add(btnCustomArp);

	}

	// --------------GET------------------------------------------------------------
	public String getAdapterName() {
		return adapterNameTF.getText();
	}

	// -----------------------GET--------------------------------------------------------
	// -----------------------------SET---------------------------------------------
	public void setHostnameTF(String s) {
		HostnameTF.setText("s");
		;
	}

	public void setIpTF(String s) {
		this.ipTF.setText(s);
	}

	public void setMaskTF(String s) {
		this.maskTF.setText(s);
	}

	public void setMacTF(String s) {
		this.macTF.setText(s);
	}

	public void setGatewayTF(String s) {
		this.gatewayTF.setText(s);
	}

	public void setPreferredDNSTF(String s) {
		this.preferredDNSTF.setText(s);
	}

	public void setAlternateDNSTF(String s) {
		this.alternateDNSTF.setText(s);
	}

	public void setAdapterNameTF(String s) {
		this.adapterNameTF.setText(s);
	}

	public void setAdapterDescriptionTF(String s) {
		this.adapterDescriptionTF.setText(s);
	}

	// ------------------SET-----------------------------------------------------------
	public static void main(String[] args) {
		MainUI test = new MainUI();
		// 676是panel的尺寸，691才能够两边对称为何？
		test.getContentPane().add(new ARPAttackPanel(test));
		test.setVisible(true);
	}

}

package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jpcap.NetworkInterface;
import pcap.Adapter;

import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MainUI extends JFrame {

	public static final int MACWIDTH = 110;// 在CustomARP里还全都是111
	public static final int IPWIDTH = 99;

	public static final int MAINTABBEDWIDTH = ARPTabbed.TABBEDWIDTH;
	public static final int MAINUIWIDTH = MAINTABBEDWIDTH + 16;// 多次测试的结果,16完全贴合

	private Adapter adapter;

	private JPanel contentPanel;
  
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmAbout;

	private ARPTabbed arpTabbed;
	private NetworkAdapterPanel networkAdapterPanel;
	private JTabbedPane mainTabbed;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	// ------------------------------get------------------------------

	public Adapter getAdapter() {
		return adapter;
	}

	public ARPTabbed getARPTabbed() {
		return arpTabbed;
	}
	
	
	// ------------------------------get------------------------------
	
	// ----------------------set--------------------------------
	public void setAdapter(Integer key) {
		this.adapter = new Adapter(key);
	}

	public void setAdapter(NetworkInterface adapter) {
		this.adapter = new Adapter(adapter);
	}
	
	

	// ------------------------set----------------------------------
	/**
	 * Create the frame.
	 */
	public MainUI() {

		// JFream
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, MAINUIWIDTH, 600);// x，y代表窗口左上角在屏幕上弹出来的坐标

		// mntmAbout
		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new About().setVisible(true);
			}
		});

		// mnFile
		mnFile = new JMenu("File");
		mnFile.add(mntmAbout);

		// menuBar
		menuBar = new JMenuBar();
		menuBar.add(mnFile);
		setJMenuBar(menuBar);

		// contentPanel
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		// this.getContentPane().add(contentPane);
		setContentPane(contentPanel);// 两种方式？
		contentPanel.setLayout(null);

		// ARPTabbed
		arpTabbed = new ARPTabbed(this);

		// network adapter
		networkAdapterPanel = new NetworkAdapterPanel(this);

		// MainTabbed
		mainTabbed = new JTabbedPane(JTabbedPane.TOP);
		mainTabbed.setBounds(0, 0, MAINTABBEDWIDTH, 550);
		// mainTabbed.setSize(arpTabbed.getSize());//不知道为什么在ARPTabbed里可以的在这里就是不行
		// mainTabbed.setBounds(this.ARPTabbed.getBounds());
		mainTabbed.addTab("ARP", null, arpTabbed, null);
		mainTabbed.addTab("Network Adapter", null, networkAdapterPanel, null);
		contentPanel.add(mainTabbed);

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
}

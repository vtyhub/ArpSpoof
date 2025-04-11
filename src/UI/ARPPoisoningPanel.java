package UI;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import UI.CustomARPDialog.PoisoningThread;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class ARPPoisoningPanel extends JPanel {
	private MainUI mainui;

	private DefaultTableModel poiTableModel;
	private JTable poiTable;
	private JScrollPane poiScrollPane;
	private JPopupMenu poiTablePopup;
	private JMenuItem recoverItem;
	private JMenuItem ceaseItem;
	private TableColumnModel poiTableColumnModel;

	private static final String[] poiTableModelArray = new String[] { "Number", "Sequence", "Adapter Name", "Hostname",
			"Link Dst MAC", "Link Src MAC", "ARP Src MAC", "Src IP", "ARP Dst MAC", "Dst IP", "Start Time",
			"Attack Mode", "Send Interval", "Max Packets", "Sent Packets", "Pause" };
	public static final int poiTableModelArrayLen = poiTableModelArray.length;
	public static final int SentPackagesColumn = poiTableModelArrayLen - 2;

	private ArrayList<CustomARPDialog.PoisoningThread> poisoningList;
	private int sequence = 0;

	public static final int SCROLLPANEWIDTH = 1324;// 1324���
	public static final int PANELWIDTH = SCROLLPANEWIDTH + 20;

	public ArrayList<CustomARPDialog.PoisoningThread> getThreadlist() {
		return poisoningList;
	}

	public DefaultTableModel getPoiTableModel() {
		return poiTableModel;
	}

	public synchronized int addSequence() {
		return ++this.sequence;
	}//��ֹ����߳�ͬ���޸�ʱ����쳣

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public ARPPoisoningPanel(MainUI mainui) {
		this.mainui = mainui;
		this.poisoningList = new ArrayList<CustomARPDialog.PoisoningThread>();

		// PoiTable Model
		poiTableModel = new DefaultTableModel(poiTableModelArray, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		// poiTabel
		poiTable = new JTable(poiTableModel);
		poiTable.getTableHeader().setReorderingAllowed(false);// ��λ�ò����ƶ�
		poiTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// ���õ��п���ʱ������������϶�
		// poiTable.getTableHeader().setResizingAllowed(false);// ���ɸı��п�

		// -------------------------------------------------------------------------
		// poi TableColumnModel
		poiTableColumnModel = poiTable.getColumnModel();
		// Number
		poiTableColumnModel.getColumn(0).setMaxWidth(75);
		poiTableColumnModel.getColumn(0).setMinWidth(75);
		// Sequence
		poiTableColumnModel.getColumn(1).setMaxWidth(75);
		poiTableColumnModel.getColumn(1).setMinWidth(75);
		// Adapter Name
		poiTableColumnModel.getColumn(2).setMaxWidth(85);
		poiTableColumnModel.getColumn(2).setMinWidth(85);
		// hostname
		poiTableColumnModel.getColumn(3).setMaxWidth(75);
		poiTableColumnModel.getColumn(3).setMinWidth(75);

		for (int i = 4; i <= 6; i++) {
			// ǰ���� :"Link Dst MAC", "Link Src MAC","ARP Src MAC"
			poiTableColumnModel.getColumn(i).setMaxWidth(MainUI.MACWIDTH);
			poiTableColumnModel.getColumn(i).setMinWidth(MainUI.MACWIDTH);
		}
		// SrcIP
		poiTableColumnModel.getColumn(7).setMaxWidth(MainUI.IPWIDTH);
		poiTableColumnModel.getColumn(7).setMinWidth(MainUI.IPWIDTH);
		// ARP Dst MAC
		poiTableColumnModel.getColumn(8).setMaxWidth(MainUI.MACWIDTH);
		poiTableColumnModel.getColumn(8).setMinWidth(MainUI.MACWIDTH);
		// Dst IP
		poiTableColumnModel.getColumn(9).setMaxWidth(MainUI.IPWIDTH);
		poiTableColumnModel.getColumn(9).setMinWidth(MainUI.IPWIDTH);
		// Start Time
		poiTableColumnModel.getColumn(10).setMaxWidth(124);
		poiTableColumnModel.getColumn(10).setMinWidth(124);
		// Attack Mode
		poiTableColumnModel.getColumn(11).setMaxWidth(110);
		poiTableColumnModel.getColumn(11).setMinWidth(110);
		// Send Interval
		poiTableColumnModel.getColumn(12).setMaxWidth(80);
		poiTableColumnModel.getColumn(12).setMinWidth(80);
		// Max Packets
		poiTableColumnModel.getColumn(13).setMaxWidth(75);
		poiTableColumnModel.getColumn(13).setMinWidth(75);
		// sent Packets
		poiTableColumnModel.getColumn(14).setMaxWidth(80);
		poiTableColumnModel.getColumn(14).setMinWidth(80);

		// Sleep Time
		poiTableColumnModel.getColumn(15).setMaxWidth(57);
		poiTableColumnModel.getColumn(15).setMinWidth(57);
		// ----------------------------------------------------------------------------

		// scroll pane
		poiScrollPane = new JScrollPane(poiTable);
		poiScrollPane.setBounds(10, 10, SCROLLPANEWIDTH, 303);

		// recover item
		recoverItem = new JMenuItem("Recover");

		// cease item
		ceaseItem = new JMenuItem("Cease");
		ceaseItem.addActionListener((e) -> {
			int[] selectedRows = poiTable.getSelectedRows();
			if (selectedRows.length == 0) {
				return;
			}
			for (int i = 0; i < selectedRows.length; i++) {
				System.out.println(selectedRows[i]);
			}

			for (int i = 0; i < selectedRows.length; i++) {
				final int selectedRow = selectedRows[i] - i;// ��ȥѭ��������ÿ��ȥһ�к����е����������Զ�-1�����Լ�ȥ������(i)�������Ҫ-��
				final int lastIndexOfRow = this.poiTable.getRowCount() - 1;
				if (selectedRow < lastIndexOfRow) {
					// ������е�����С�����һ�е�����
					// ��Ϊ������0��ʼ ����������������-1 ���ѭ��ʹ��<=�ж�����
					// ��Ҫɾ������֮���һ�п�ʼ�������һ�н���
					PoisoningThread[] needchange = new PoisoningThread[lastIndexOfRow - selectedRow];// ��Ϊ������<=������ұգ���������ҿ����������-1
					int k = 0;
					for (int j = selectedRow + 1; j <= lastIndexOfRow; j++) {
						needchange[k] = this.poisoningList.get(j);
						needchange[k].setStop();
						needchange[k].setRow(j - 1);
						k++;
					}
					this.poisoningList.get(selectedRow).setStop();
					this.poisoningList.remove(selectedRow);
					this.poiTableModel.removeRow(selectedRow);
					for (int j = 0; j < needchange.length; j++) {
						needchange[j].setStart();
					}
				} else {
					this.poisoningList.get(selectedRow).setStop();
					this.poisoningList.remove(selectedRow);
					this.poiTableModel.removeRow(selectedRow);
				}
			}

		});

		// popup menu
		poiTablePopup = new JPopupMenu();
		poiTablePopup.add(recoverItem);
		poiTablePopup.add(ceaseItem);
		MainUI.addPopup(poiTable, poiTablePopup);

		// recoverALL Button
		JButton btnRecoverAll = new JButton("Recover All");
		btnRecoverAll.setBounds(1222, 323, 112, 23);
		btnRecoverAll.addActionListener((e) -> {
			String[] str = new String[] { "Alice", "192.168.1.1", "11-11-11-11-11-11", "192.168.1.5",
					"22-22-22-22-22-22", "Broadcast", LocalDateTime.now().toString(), "1000", "0" };
			poiTableModel.addRow(str);
			poiTableModel.addRow(str);
			// poiTableModel.cell

			new Thread(() -> {
				for (int i = 0; i < 10000000; i++) {
					poiTableModel.setValueAt(i, 0, str.length - 1);
				}
			}).start();

		});

		// ceaseAll button
		JButton btnCeaseAll = new JButton("Cease All");
		btnCeaseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int size = poisoningList.size();
				for (int i = 0; i < size; i++) {
					poisoningList.get(0).setStop();
					poisoningList.remove(0);
					poiTableModel.removeRow(0);
				}
				// int rowCount = poiTableModel.getRowCount();
				// for(int i=0;i<rowCount;i++) {
				// }
			}
		});
		btnCeaseAll.setBounds(1100, 323, 112, 23);

		// btnoutputlist button
		JButton btnOutputList = new JButton("Output List");
		btnOutputList.setBounds(978, 323, 112, 23);

		// ARPPoisoning Panel
		this.setBounds(0, 0, 1400, 365);
		setLayout(null);
		add(poiScrollPane);
		add(btnOutputList);
		add(btnCeaseAll);
		add(btnRecoverAll);
	}

	public static void main(String[] args) {
	}
}

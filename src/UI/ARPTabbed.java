package UI;

import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class ARPTabbed extends JTabbedPane {
	private MainUI mainui;

	private ARPPoisoningPanel poisoningPanel;
	private ARPAttackPanel attackPanel;

	public static final int TABBEDWIDTH = ARPPoisoningPanel.PANELWIDTH + 10;
	
	public ARPPoisoningPanel getPoisoningPanel() {
		return poisoningPanel;
	}

	public ARPAttackPanel getAttackPanel() {
		return attackPanel;
	}

	/**
	 * Create the panel.
	 */
	public ARPTabbed(MainUI mainui) {

		super(JTabbedPane.TOP);
		// ARPAttackPanel
		attackPanel = new ARPAttackPanel(mainui);
		this.addTab("Attack", null, attackPanel, null);

		// PoisoningPanel
		poisoningPanel = new ARPPoisoningPanel(mainui);
		this.addTab("Poisoning", null, poisoningPanel, null);

		// ARPTabbed Pane
		// this.setSize(1307, 400);
		this.setMinimumSize(poisoningPanel.getSize());// poisoning最宽因此从这里获取
		// attackPanel.GETsz
	}

}

package test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TestSize extends JPanel {
	private JFrame jfream;
	private JTextField txtNumber;
	
	/**
	 * Create the panel.
	 */
	public TestSize() {
		setLayout(null);
		
		txtNumber = new JTextField();
		txtNumber.setBounds(0, 0, 240, 21);
		add(txtNumber);
		txtNumber.setColumns(10);
		
	}
	
	public static void main(String[] args) {
		
	}
}

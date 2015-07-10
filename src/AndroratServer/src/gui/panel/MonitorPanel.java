package gui.panel;

import gui.UserGUI;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashSet;
import javax.swing.JSplitPane;

/**
 * Diese Klasse ist für die Tabs zum Monitoren der einkommenden Anrufe oder SMS zuständig.
 */
public class MonitorPanel extends JPanel {
	
	private UserGUI gui;
	private boolean monitoring = false;
	private boolean callMonitor;
	
	private JLabel lblReceived;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	
	private JTextArea phoneNumbersTextArea;
	private JButton btnStartMonitoring;
	private ColorPane colorPane;
	private JSplitPane splitPane;

	/**
	 * Diese Methode erstellt das entspechende Panel
	 * @param gui	Die Guo
	 * @param callMonitor	Ob es sich um Call Monitor oder SMS Monitor handelt
	 */
	public MonitorPanel(UserGUI gui, boolean callMonitor) {
		this.gui = gui;
		this.callMonitor = callMonitor;
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
					.addGap(9))
		);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Optional filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		btnStartMonitoring = new JButton("Start monitoring");
		btnStartMonitoring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonMonitoring();
			}
		});
		
		JLabel lblTypeOfCall = new JLabel("Phone numbers :");
		
		phoneNumbersTextArea = new JTextArea();
		
		lblReceived = new JLabel("Incomming call");
		lblReceived.setForeground(Color.DARK_GRAY);
		
		lblNewLabel = new JLabel("Missed call");
		lblNewLabel.setForeground(Color.ORANGE);
		
		lblNewLabel_1 = new JLabel("Accepted call");
		lblNewLabel_1.setForeground(Color.GREEN);
		
		lblNewLabel_2 = new JLabel("Sent call");
		lblNewLabel_2.setForeground(Color.BLUE);
		
		lblNewLabel_3 = new JLabel("Hanged up call");
		lblNewLabel_3.setForeground(Color.RED);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(phoneNumbersTextArea, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
						.addComponent(btnStartMonitoring, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
						.addComponent(lblTypeOfCall)
						.addComponent(lblReceived)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel_2)
						.addComponent(lblNewLabel_3))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(23)
					.addComponent(lblTypeOfCall)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(phoneNumbersTextArea, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblReceived)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_3)
					.addGap(12)
					.addComponent(btnStartMonitoring)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		colorPane = new ColorPane();
		scrollPane.setViewportView(colorPane);
		setLayout(groupLayout);
		
		if(!callMonitor) this.hideCallLabels();
	}
	
	private void hideCallLabels() {
		lblNewLabel.setVisible(false);
		lblNewLabel_1.setVisible(false);
		lblNewLabel_2.setVisible(false);
		lblNewLabel_3.setVisible(false);
		lblReceived.setVisible(false);
	}

	/**
	 * Diese Methode wir aufgerufen wenn der Button Start monitoring gedrückt wird.
	 * Da der Button zum Starten und Stoppen des Monitoning zuständig ist, wird zu erst überprüft welcher Zustand zutrifft.
	 * jenachdem wird der Text des Buttons verändert.
	 * Sollte es schon aktiv sein so wird zusätzlich die Variable callMonitor überprüft. Sollte diese true sein wird die Funktion fireStopCallMonitoring aufgerufen.
	 * Sonste wird die Methode fireStopSMSMonitoring der Klasse GUI aufgerufen.
	 * Sollte das ganze noch nicht aktiv sein so wird der Filter auf den Inhalt überprüft. Im Anschluss wird dann jenachdem welchen Wert callMonitor besitzt die Methode
	 * fireStartCallMonitoring oder die Methode fireStartSMSMonitoring aufgerufen.
	 */
	private void fireButtonMonitoring() {
		if(monitoring) {
			btnStartMonitoring.setText("Start monitoring");
			monitoring = false;
			if(callMonitor) gui.fireStopCallMonitoring();
			else gui.fireStopSMSMonitoring();
		} else {
			colorPane.setText("");
			btnStartMonitoring.setText("Stop monitoring");
			monitoring = true;
			
			HashSet<String> phoneNumbers;
			if(phoneNumbersTextArea.getText().equals("")) {
				phoneNumbers = null;
			}
			else {
				phoneNumbers = new HashSet<String>();
				String[] phoneNbr = phoneNumbersTextArea.getText().split("\n");
				for(String phone : phoneNbr) {
					System.out.println("Phone nbr ="+phone+"!");
					phoneNumbers.add(phone);
				}
			}
			
			if(callMonitor)
				gui.fireStartCallMonitoring(phoneNumbers);
			else
				gui.fireStartSMSMonitoring(phoneNumbers);
		}
	}

	/**
	 * Diese Methode ist zum bestimmen des Type des eingehenden Anrufes da.
	 * Zusätzlich werden die Informationen dann in unterschiedlichen Farben dargestellt.
	 * @param type	Der Type des Anrufs
	 * @param phoneNumber	Die Telefonnummer
	 */
	public void addMonitoredCall(int type, String phoneNumber) {
		Color color = Color.darkGray;
		String message = "";
		if(phoneNumber == null)
			phoneNumber = "";
		if (type ==1) {
			message = "Incoming call: ";
		}
		else if(type == 2) {
			color = Color.orange;
			message = "Missed call: ";
		}
		else if(type == 3) {
			color = Color.green;
			message = "Handled call: ";
		}
		else if(type == 4) {
			color = Color.blue;
			message = "Outgoing call: ";
		}
		else if(type == 5) {
			color = Color.red;
			message = "Hang up Call";
		}
		
		colorPane.append(color,message+ phoneNumber+" at "+ (new Date(System.currentTimeMillis())).toString() +"\n");
	}

	/**
	 * Diese Methode ist zum hinzufügen einer neuen SMS für das Monitorfenster zuständig.
	 * @param addr	Die Telefonnummer
	 * @param date	Der Zeitstempel
	 * @param body	Der Text der SMS
	 */
	public void addMonitoredSMS(String addr, long date, String body) {
		colorPane.append(Color.black, "Number: "+addr +"\nBody:\n"+body+"\nSMS at "+(new Date(date)).toString()+"\n\n");
	}

	/**
	 * Diese Methode ist zum Überprüfen ob das Monitoring gerade aktiv ist oder nicht.
	 * @return	True wenn aktiv, false sonst.
	 */
	public boolean getMonitoring() {
		return monitoring;
	}

	/**
	 * Diese Methode gibt zurück ob es sich um ein CallMonitor oder ein SMSMonitor handelt.
	 * @return	True wenn CallMoniotr, false sonst.
	 */
	public boolean getCallMonitor() {
		return callMonitor;
	}
}

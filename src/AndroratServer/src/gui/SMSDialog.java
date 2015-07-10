package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Diese Klasse stellt die GUI zur Verfügung die benötigt wird um eine SMS zu versenden.
 */
public class SMSDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextPane textPane;
	private String[] result;

	/**
	 * Erstellt den Dialog für die SMS.
	 * @param owner	Der Besitzer
	 */
	public SMSDialog(Frame owner) {
		super(owner, "Sending SMS", true);
		setBounds(100, 100, 340, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblTargetCellNumber = new JLabel("Target cell number :");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JLabel lblSmsText = new JLabel("SMS Text :");
		
		textPane = new JTextPane();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(textPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
						.addComponent(textField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addComponent(lblTargetCellNumber, Alignment.LEADING)
						.addComponent(lblSmsText, Alignment.LEADING))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTargetCellNumber)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(37)
					.addComponent(lblSmsText)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fireButtonOk();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fireButtonCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Die Methode wird aufgerufen wenn der OK Button gedrückt wird.
	 * Die Inhalte des Textfeldes werden in den String result geschrieben.
	 * Danach wird das Fenster geschlossen.
	 */
	private void fireButtonOk() {
		result = new String[2];
		result[0] = textField.getText();
		result[1] = textPane.getText();
		setVisible(false);
		dispose();
	}

	/**
	 * Diese Methode wird aufgerufen wenn der Button Cancel gedruckt wird.
	 * Danach wird das Fenster verschwinden.
	 */
	private void fireButtonCancel() {
		setVisible(false);
		dispose();
	}

	/**
	 * Diese Methode gibt die Klassenvariable result zurück, in der die Nummer und der Text der SMS gespeichert wurde.
	 * @return	Ein String-Array mit der Telefonnummer und dem Text.
	 */
	public String[] showDialog() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		return result;
	}
}

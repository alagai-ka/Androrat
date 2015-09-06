package gui.panel;


import gui.UserGUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;

public class AlarmPanel extends JPanel {
    UserGUI gui;
    private JTextField hourField;
    private JTextField minuteField;


    public AlarmPanel(UserGUI gui){
        this.gui = gui;



        JLabel lblHour = new JLabel("Hour :");
        JLabel lblMinute = new JLabel("Minute :");

        minuteField = new JTextField();
        minuteField.setHorizontalAlignment(SwingConstants.LEFT);
        minuteField.setText("31");
        minuteField.setColumns(10);

        hourField = new JTextField();
        hourField.setHorizontalAlignment(SwingConstants.LEFT);
        hourField.setText("23");
        hourField.setColumns(10);

        JButton btnSetClock = new JButton("SetClock");
        btnSetClock.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fireButtonSetClock();
            }
        });
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "SetClock", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblHour, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                                        .addComponent(hourField, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
                                .addGap(18)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblMinute, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                                        .addComponent(minuteField, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
                                .addGap(18)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnSetClock)
                                        .addGap(18)))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)

                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(lblHour, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(hourField, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18)
                                                .addGroup(groupLayout.createSequentialGroup()
                                                        .addComponent(lblMinute, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(minuteField, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18)
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addComponent(btnSetClock)
                                                                .addGap(18))))))
        );


    }
    public void fireButtonSetClock(){
        byte[] args = new byte [2];
        String hour = hourField.getText();
        Byte b1 = new Byte(hour);
        String minute =minuteField.getText();
        args[0] = b1;
        Byte b2 = new Byte(minute);
        args[1] = b2;
        gui.fireSetAlarm(args);

    }
}

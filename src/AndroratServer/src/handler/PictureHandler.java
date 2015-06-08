package handler;

import server.Server;
import Packet.Packet;
import Packet.RawPacket;
import gui.GUI;

/**
 * Diese Klasse ist zum Verarbeiten von Bilder, die von der Kamera aufgenommen wurden, zust�ndig.
 */
public class PictureHandler implements PacketHandler {
	/**
	 * gui Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI des Ger�ts welches verbunden ist.
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse wei�t den Klassenvariablen die �bergebenen Daten zu.
	 * @param chan	Der Kanal
	 * @param imei	Die IMEI
	 * @param gui	Die Gui
	 */
	public PictureHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Diese Methode erstellt eine Lognachricht f�r die GUI, entfernt dann den Listener f�r den Datenkanal und ruft dann die updateUserPicture-Methode auf um das Bild auf der GUI anzuzeigen.
	 * @param p	Das Paket
	 * @param imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String imei, Server c) 
	{
		gui.logTxt("Image data has been received");
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		RawPacket packet = (RawPacket) p;
		gui.updateUserPicture(imei, packet.getData());
	}
}

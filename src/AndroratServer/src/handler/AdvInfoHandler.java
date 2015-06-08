package handler;

import server.Server;
import Packet.AdvancedInformationPacket;
import Packet.Packet;
import gui.GUI;

/**
 * Diese Klasse verarbeitet die AdvancedPacktes die an dem Server ankommen und sorgt dafür das die Daten angezeigt werden.
 */
public class AdvInfoHandler implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfläche
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des Geräts
	 */
	private String imei;

	/**
	 * Der Konstruktor dieser Klasse befüllt alle Klassenvariablen mit ihren Daten.
	 * @param chan	Kanal
	 * @param imei	IMEI
	 * @param gui	Die graphische Oberfläche
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
	 * Diese Methode entfernt die Listener und sorgt dafür, dass die GUI upgedatet wird.
	 * @param p	Das Paket
	 * @param temp_imei	Die Imei des Geräts
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
		 * Die Gui für das Gerät mit der IMEI updaten, sodass die Daten des packet angezeigt werden.
		 */
		gui.updateAdvInformations(imei, packet);
	}
}

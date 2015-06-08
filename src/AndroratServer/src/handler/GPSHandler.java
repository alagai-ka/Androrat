package handler;

import gui.GUI;
import server.Server;

import Packet.GPSPacket;
import Packet.Packet;

/**
 * Diese Klasse verarbeitet die GPSPakete
 */
public class GPSHandler implements PacketHandler
{
	/**
	 * gui Die Benutzeroberfläche
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */

	private int channel;
	/**
	 * imei Die IMEI des verbunden Gerätes
	 */
	private String imei;

	/**
	 * Der Konstruktor befüllt die Klassenvariablen mit den Daten.
	 * @param chan	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	Die Gui
	 */
	public GPSHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	/**
	 * Diese Methode erstellt eine Lognachricht, die dann auf der GUI ausgegeben wird.
	 * Zusätlich wird der Speicher des Datenkanals geleert. Im Anschluss wird dann die updateUserMap Methode aufgerufen um den Ort auf der Karten anzuzeigen.
	 * @param p	Das Paket
	 * @param imei	Die IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p,String imei,Server c)
	{
		gui.logTxt("GPS data has been received");
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset(); // Voir si faut le mettre ailleurs !

		GPSPacket gps = (GPSPacket)p;
		gui.updateUserMap(imei, gps.getLongitude(), gps.getLatitude(), gps.getAltitude(), gps.getSpeed(), gps.getAccuracy());

	}


	@Override
	public void receive(Packet p, String temp_imei) {
		// TODO Auto-generated method stub

	}










}

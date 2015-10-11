package handler;

import gui.GUI;
import server.Server;
import Packet.Packet;
import Packet.RawPacket;

/**
 * Diese Klasse ist zur Verarbeitung von Videodatein vorhanden.
 * Allerdings gibt es noch kein Gegenstück auf dem Client.
 * Daher ist diese Klasse ohne Funktion.
 */
public class VideoHandler implements PacketHandler {

	private GUI gui;
	private int channel;
	private String imei;
	
	public VideoHandler(int channel, String imei, GUI gui) {
		this.gui = gui;
		this.channel = channel;
		this.imei = imei;
	}
	
	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Erhält die Daten des Serves und ruft die addVideoByte Methode auf um die Videodaten in die Datei zu schreiben.
	 * @param p	Das Paket mit den Videodaten
	 * @param temp_imei	Die IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("Video data has been received");
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset();
		RawPacket packet = (RawPacket) p;
		gui.addVideoBytes(imei, packet.getData());
	}

}

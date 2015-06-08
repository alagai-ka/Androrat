package handler;

import server.Server;
import Packet.LogPacket;
import Packet.Packet;
import gui.GUI;

/**
 * Diese Klasse verarbeitet die ClientLog-Pakete
 */
public class ClientLogHandler implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfläche
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des verbundenen Gerätes
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse weißt die übergebenden Daten den Klassenvaribalen zu.
	 * @param channel	Der Datenkanal
	 * @param imei	Die IMEI des verbundenen Gerätes
	 * @param gui	Die Benutzeroberfläche
	 */
	public ClientLogHandler(int channel, String imei, GUI gui) {
		this.gui = gui;
		this.channel = channel;
		this.imei = imei;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Diese Methode besorgt sich das LogPacket des Servers und überprüft dann ob es sich um eine Lognachrichte handelt oder um eine Fehlernachricht.
	 * Jenachdem wird die clientLogTxt oder die clientErrLogTxt Methode aufgerufen um diese Daten auf der Benutzeroberfläche anzuzeigen.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset();
		LogPacket packet = (LogPacket) p;
		if(packet.getType() == 0) gui.clientLogTxt(imei, packet.getDate(), packet.getMessage());
		else gui.clientErrLogTxt(imei, packet.getDate(), packet.getMessage());
	}

}

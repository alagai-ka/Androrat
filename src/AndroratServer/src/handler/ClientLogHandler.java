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
	 * gui	Die Benutzeroberfl�che
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des verbundenen Ger�tes
	 */
	private String imei;

	/**
	 * Der Konstruktor der Klasse wei�t die �bergebenden Daten den Klassenvaribalen zu.
	 * @param channel	Der Datenkanal
	 * @param imei	Die IMEI des verbundenen Ger�tes
	 * @param gui	Die Benutzeroberfl�che
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
	 * Diese Methode besorgt sich das LogPacket des Servers und �berpr�ft dann ob es sich um eine Lognachrichte handelt oder um eine Fehlernachricht.
	 * Jenachdem wird die clientLogTxt oder die clientErrLogTxt Methode aufgerufen um diese Daten auf der Benutzeroberfl�che anzuzeigen.
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

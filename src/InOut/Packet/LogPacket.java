package Packet;

import java.nio.ByteBuffer;

/**
 * Diese Klasse wird zum übermitteln von Loginformationen benutzt
 */
public class LogPacket implements Packet {
	/**
	 * date	Das Datum des Eintags
	 */
	long date;
	/**
	 * type	Ob es sich um einen Fehlereintag handelt oder nicht
	 */
	byte type; //0 ok / 1 Error
	/**
	 * message	die Lognachricht
	 */
	String message;
	
	public LogPacket() {
		
	}

	/**
	 * Der Konstruktor befüllt die Klassenvariablen mit den übergebenen Daten
	 * @param date	Das Datum des Eintrages
	 * @param type	Der Logtyp
	 * @param message	Die Lognachricht
	 */
	public LogPacket(long date, byte type, String message) {
		this.date = date;
		this.type = type;
		this.message = message;
	}

	/**
	 * Diese Methode erstellt aus den Daten des Objekts ein byte-Array
	 * @return	byte-Array mit den Daten des Objekts
	 */
	public byte[] build() {
		ByteBuffer b = ByteBuffer.allocate(9+message.length());
		b.putLong(date);
		b.put(type);
		b.put(message.getBytes());
		return b.array();
	}

	/**
	 * Diese Methode erhält ein byte-Array mit den Daten eines LogPacket und extrahiert diese aus dem byte-Array.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);
		date = b.getLong();
		type = b.get();
		byte[] tmp = new byte[b.remaining()];
		b.get(tmp);
		message = new String(tmp);
	}

	/**
	 * Lifert das Datum
	 * @return	Datum
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Lifert den Logtyp
	 * @return	Logtyp
	 */
	public byte getType() {
		return type;
	}

	/**
	 * Liefert die Lognachticht
	 * @return die Lognachricht
	 */
	public String getMessage() {
		return message;
	}

}

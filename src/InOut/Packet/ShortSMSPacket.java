package Packet;

import java.nio.ByteBuffer;

/**
 * Hiebei handelt es sich um ein kleine Smspaket um Sms zuversenden.
 */
public class ShortSMSPacket implements Packet{
	/**
	 * address_size	Größe der Telefonnummer
	 */
	private int address_size;
	/**
	 * address	Telefonnummer
	 */
	private String address;
	/**
	 * date	Datum der SMS
	 */
	private long date;
	/**
	 * body_size	Größe der Smsnachricht
	 */
	private int body_size;
	/**
	 * body	Smsnachricht
	 */
	private String body;

	/**
	 * Leerer Konstruktor
	 */
	public ShortSMSPacket() {
		
	}

	/**
	 * Dieser Konstruktor setzt die übergebenen Daten in die Klassenvariablen.
	 * @param ad	Telefonnummer
	 * @param dat	Datum
	 * @param body	Nachricht
	 */
	public ShortSMSPacket(String ad, long dat, String body) {
		this.address = ad;
		this.address_size = ad.length();
		this.date = dat;
		this.body = body;
		this.body_size = this.body.length();
	}

	/**
	 * Erstellt aus den Daten diese Objektes ein byte-Array und gibt dies zurück
	 * @return byte-Array mit den Daten des Objekts
	 */
	public byte[] build() {
		ByteBuffer b = ByteBuffer.allocate(4+4+address.length()+4+4+8+4+body.length()+4);
		b.putInt(address_size);
		b.put(address.getBytes());
		b.putLong(date);
		b.putInt(body_size);
		b.put(body.getBytes());
		return b.array();
	}

	/**
	 * Erhält ein byte-Arry mit ShortSMSPacket Daten und extrahiert diese.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);
		this.address_size = b.getInt();
		byte[] tmp = new byte[address_size];
		b.get(tmp);
		this.address = new String(tmp);
		this.date = b.getLong();
		this.body_size = b.getInt();
		tmp = new byte[body_size];
		b.get(tmp);
		this.body = new String(tmp);
	}

	/**
	 * Liefert die Telefonnummer
	 * @return	Telefonnummer
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Liefert das Datum
	 * @return	Datum
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Liefert die Nachricht
	 * @return	Nachricht
	 */
	public String getBody() {
		return body;
	}
}
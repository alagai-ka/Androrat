package Packet;

import java.nio.ByteBuffer;

/**
 * Wird benutzt um die Ordner des Gräts herunterzuladen
 */
public class FilePacket implements Packet {
	/**
	 * Daten des Ordners
	 */
	byte[] data;
	/**
	 * Der Ordner
	 */
	byte mf;
	/**
	 * Sequenznummer des Pakets
	 */
	short numSeq;
	
	public FilePacket() {
		
	}

	/**
	 * Kontruktor der Klasse. Setzt die Klassenvariablen
	 * @param num	Sequenznummer
	 * @param mf	Letzes Paket
	 * @param data	Die Daten des Ordners
	 */
	public FilePacket(short num, byte mf, byte[] data) {
		this.data = data;
		this.numSeq = num;
		this.mf = mf;
	}

	/**
	 * Erstellt aus dem Paket ein byte-Array. Dem Array wird zuerst die Sequenznummer hinzugefügt, ob es das letzte Paket ist und schlieslich die Daten des Ordners.
	 * @return	byte-Array mit den Daten des Pakets
	 */
	public byte[] build() {
		ByteBuffer b = ByteBuffer.allocate(data.length+3);
		b.putShort(numSeq);
		b.put(mf);
		b.put(data);
		return b.array();
	}

	/**
	 * Liest die Daten aus dem byte-Array aus und setzt diese in die Klassen Variablen
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);
		
		numSeq = b.getShort();
		mf = b.get();
		this.data = new byte[b.remaining()];
		b.get(data, 0, b.remaining());
	}

	/**
	 * Liefert die Daten
	 * @return	die Daten die heruntergeladen wurden
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Liefer ob es sich um das letzte Packet handelt
	 * @return	Letzes Packet
	 */
	public byte getMf() {
		return mf;
	}

	/**
	 * Liefert die Sequenznummer des Pakets
	 * @return	Sequenznummer
	 */
	public short getNumSeq() {
		return numSeq;
	}

}

package Packet;

/**
 * Eine Paket Implementierung die nur ein byte-Array speichert. Diese wird f�r die �bermittlung von Bilder und Audiodaten verwendet.
 */
public class RawPacket implements Packet {
	/**
	 * Das Byte-Array mit den Daten
	 */
	private byte[] data;

	/**
	 * Leerer Konstruktor
	 */
	public RawPacket() {
		// Nothing
	}

	/**
	 * Der Konstruktor erh�lt ein byte-Array und wei�t diese der Klassenvariablen zu.
	 * @param data
	 */
	public RawPacket(byte[] data) {
		this.data = data;
	}

	/**
	 * Gibt das byte-Array mit den Daten des Objekts zur�ck
	 * @return	byte-Array
	 */
	public byte[] build() {
		return data;
	}

	/**
	 * Wei�t das byte-Array der Klassenvariablen zu
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		data = packet;
	}

	/**
	 * Liefert die Klassenvariable zur�ck
	 * @return byte-Array
	 */
	public byte[] getData() {
		return data;
	}
}

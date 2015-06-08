package Packet;

/**
 * Eine Paket Implementierung die nur ein byte-Array speichert. Diese wird für die Übermittlung von Bilder und Audiodaten verwendet.
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
	 * Der Konstruktor erhält ein byte-Array und weißt diese der Klassenvariablen zu.
	 * @param data
	 */
	public RawPacket(byte[] data) {
		this.data = data;
	}

	/**
	 * Gibt das byte-Array mit den Daten des Objekts zurück
	 * @return	byte-Array
	 */
	public byte[] build() {
		return data;
	}

	/**
	 * Weißt das byte-Array der Klassenvariablen zu
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		data = packet;
	}

	/**
	 * Liefert die Klassenvariable zurück
	 * @return byte-Array
	 */
	public byte[] getData() {
		return data;
	}
}

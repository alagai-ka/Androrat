package Packet;

/**
 * Dieses Interface ist dazu da die unterschiedlichen Pakete für den Datentransport zu erstellen.
 */
public interface Packet
{
	/**
	 * Mit Hilfe dieser Funktion werden die Pakete in ein byte-Array umgewandelt.
	 * Die wird im Anschluss an den Server gesendet.
	 * @return	Das Paket als byte-Array
	 */
	public byte[] build();

	/**
	 * Die Methode kreiert aus dem Byte-Array wieder ein entsprechendes Paket.
	 * So können dann im Anschluss die Daten ausgelesen und ausgewertet werden.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet);
}
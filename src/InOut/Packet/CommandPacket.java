package Packet;

import java.nio.ByteBuffer;

/**
 * Diese Paket ist dazu da die Befehle an den Client zu senden.
 */
public class CommandPacket implements Packet {
	/**
	 * commande	Der Befehl
	 */
	private short commande;
	/**
	 * targetChannel	Der Kanal
	 */
	private int targetChannel;
	/**
	 * argument	Mögliche zusätzliche Informationen wie Filter
	 */
	private byte[] argument;

	public CommandPacket() {

	}

	/**
	 * Konstruktor der Klasse. Weißt die übergebenden Inforamtionen den Klassenvariablen zu
	 * @param cmd	Der Befehl
	 * @param targetChannel	Der Kanal
	 * @param arg	Zusätzliche Informationen
	 */
	public CommandPacket(short cmd, int targetChannel, byte[] arg) {
		this.commande = cmd;
		this.argument = arg;
		this.targetChannel = targetChannel;
	}

	/**
	 * Erhält ein byte-Array mit den Daten und extrahiert diese aus dem Array um sie in den Klassenvariablen zu speichern.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);
		this.commande = b.getShort();
		this.targetChannel = b.getInt();
		this.argument = new byte[b.remaining()];
		b.get(argument, 0, b.remaining());
	}

	/**
	 * Die gleiche Funktopn wie die ander parse Methode bekommt nur einen ByteBuffer übergeben
	 * @param b	ByteBuffer
	 */
	public void parse(ByteBuffer b) {
		this.commande = b.getShort();
		this.targetChannel = b.getInt();
		this.argument = new byte[b.remaining()];
		b.get(argument, 0, b.remaining());
	}

	/**
	 * Erstellt aus den Daten des Objekts ein byte-Array.
	 * @return ein byte-Array mit den Befehlsdaten
	 */
	public byte[] build() {
		byte[] byteCmd = ByteBuffer.allocate(2).putShort(commande).array();
		byte[] byteTargChan = ByteBuffer.allocate(4).putInt(targetChannel).array();
		byte[] cmdToSend = new byte[byteCmd.length + byteTargChan.length + argument.length];

		System.arraycopy(byteCmd, 0, cmdToSend, 0, byteCmd.length);
		System.arraycopy(byteTargChan, 0, cmdToSend, byteCmd.length, byteTargChan.length);
		System.arraycopy(argument, 0, cmdToSend, byteCmd.length + byteTargChan.length, argument.length);

		return cmdToSend;
	}

	/**
	 * Liefert den gesendenten Befehl
	 * @return	der Befehl
	 */
	public short getCommand() {
		return commande;
	}

	/**
	 * Liefert die Zusatzinformationen
	 * @return	Zusatzinformationen
	 */
	public byte[] getArguments() {
		return argument;
	}

	/**
	 * Liefert den Kanal
	 * @return	 der Kanal
	 */
	public int getTargetChannel() {
		return targetChannel;
	}

}

package Packet;

import java.nio.ByteBuffer;

import inout.Protocol;

/**
 * Zum Erstellen und Verwalten eines Tranport Paketes
 */
public class TransportPacket implements Packet {
	/**
	 * totalLength	Die Gesamtlänge
	 */
	private int totalLength;
	/**
	 * awaitedLength	Die erwartete Paketlänge
	 */
	private int awaitedLength;
	/**
	 * localLength	Die lokale Größe
	 */
	private int localLength;
	/**
	 * last	Letzes Paket
	 */
	private boolean last;
	/**
	 * NumSeq	Die Seqeunznummer
	 */
	private short NumSeq;
	/**
	 * channel Der Datenkanal
	 */
	private int channel;
	/**
	 * data	Die Daten.
	 */
	private byte data[];

	private int fillingPosition;

	/**
	 * Ein Konstruktor erstellt ein Objekt und stetzt die awaitedLength auf 0 und setzt fillingPosition auf 0.
	 */
	public TransportPacket() {
		awaitedLength = 0;
		fillingPosition = 0;
		
	}

	/**
	 * Ein Konstruktor der allen Klassenvariablen die übergebenen Daten zu weißt.
	 * @param tdl	Die totale Größe
	 * @param ll	Die lokale Größe
	 * @param channel	Der Datenkanal
	 * @param last	Letztes Paket
	 * @param nums	Die Sequenznummer
	 * @param data	Die Daten
	 */
	public TransportPacket(int tdl, int ll, int channel, boolean last,
			short nums, byte[] data) {
		this.totalLength = tdl;
		this.channel = channel;
		this.last = last;
		this.data = data;
		this.localLength = ll;
		this.NumSeq = nums;
	}

	/**
	 * Erhält ein Paket und extrahiert die Daten und speichert diese in den Klassenvariablen.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);

		this.totalLength = b.getInt();
		this.localLength = b.getInt();

		byte checkLast = b.get();
		if (checkLast == (byte) 1)
			this.last = true;
		else
			this.last = false;

		this.NumSeq = b.getShort();
		this.channel = b.getInt();
		this.data = new byte[b.remaining()];
		b.get(data, 0, b.remaining());
	}

	/**
	 * Erhält einen ByteBuffer und extrahiert die Daten aus dem Buffer.
	 * @param buffer	Der Buffer mit den Daten
	 * @return
	 * @throws Exception
	 */
	public boolean parse(ByteBuffer buffer) throws Exception{
		

		totalLength = buffer.getInt();
		localLength = buffer.getInt();

		byte lst = buffer.get();
		if (lst == 1)
			last = true;
		else
			last = false;

		NumSeq = buffer.getShort();
		channel = buffer.getInt();
		/*
		System.out.println("Taille totale de la donn�e : " + totalLength);
		System.out.println("Taille des donn�es du paquet : " + localLength);
		System.out.println("Dernier paquet : " + last);
		System.out.println("Position du paquet : " + NumSeq);
		System.out.println("Canal:" + channel);
		System.out.println("Recuperation de la donnee");
		*/
		// si la place restante dans le buffer est insuffisante
		/**
		 * Wenn der restliche Platz des Buffers unzureichend ist, wird die Methode dataFilling aufgerufen.
		 * Jedoch wird hier nich das gesamte Paket gespeichert sondern nur die übrigen Daten, die noch nicht ausgelesen wurden.
		 */
		if ((buffer.limit() - buffer.position()) < localLength) {
			
			dataFilling(buffer, buffer.limit() - buffer.position());
			//System.out.println("une partie du packet a ete sauvegarde");
			return true;
			
		} 
		else 
		{
			/**
			 * Wenn es genug Platz gibt wird das ganze Paket gespeichert.
			 * Dazu wird ein Byte-Array mit der lokalen Größe erstellt und die Daten des Buffers in data kopiert.
			 */
			// s'il y a assez de place, on sauvegarde tout le paquet
				data = new byte[localLength];
				buffer.get(data, 0, data.length);
				return false;
			
		}

	}

	/**
	 * Schreibt die Daten des Buffers in die Variable Date
	 * @param buffer	Der Buffer
	 * @return	Ture wenn nur die Daten geschrieben werden, Fals wenn das gesamte Packet in data gepsichert wird.
	 * @throws Exception
	 */
	public boolean parseCompleter(ByteBuffer buffer) throws Exception{
		//System.out.println("les donnees attendues sont de taille = " + awaitedLength);
		// si la taille des donnees attendues depasse celle du buffer
		if (buffer.limit() - buffer.position() < awaitedLength) {
			
			// on en recupere autant que l'on peut (taille du buffer)
			dataFilling(buffer, buffer.limit() - buffer.position());
			return true;
		} 
		else {
			
			// sinon on recupere la totalite
			dataFilling(buffer, awaitedLength);
			return false;
		}

	}

	/**
	 * Schreibt die Daten des buffers in die Varibale data. Zusätzlich wird die FillingPosition und die awaitedLength aktualisiert.
	 * @param buffer	Der Buffer
	 * @param length	Die Länge der Daten die gespeichert werden sollen.
	 */
	public void dataFilling(ByteBuffer buffer, int length) {
		/*
		System.out.println("Taille buffer.remaining : "+buffer.remaining());
		System.out.println("Taille buffer.limit : "+buffer.limit());
		System.out.println("Taille buffer.pos : "+buffer.position());
		System.out.println("Taille fillig : "+fillingPosition);
		System.out.println("Taille partialData : "+partialData.length);
		System.out.println("Taille length : "+length);
		*/
		if( data == null) data = new byte[localLength];
		
		buffer.get(data, fillingPosition, length);
		fillingPosition += length;
		awaitedLength = localLength - fillingPosition;
		
	}

	public byte[] build() {
		byte[] cmdToSend = new byte[Protocol.HEADER_LENGTH_DATA + data.length];
		byte[] header = Protocol.dataHeaderGenerator(this.totalLength,
				this.localLength, this.last, this.NumSeq, this.channel);
		System.arraycopy(header, 0, cmdToSend, 0, header.length);
		System.arraycopy(data, 0, cmdToSend, header.length, data.length);

		return cmdToSend;
	}

	/**
	 * Gibt die Gesamtgröße der Daten zurück
	 * @return	Die Gesamtgröße
	 */
	public int getTotalLength() {
		return totalLength;
	}

	/**
	 * Die lokale Größe der Daten.
	 * @return	Die lokale Größe der Daten.
	 */
	public int getLocalLength() {
		return localLength;
	}

	/**
	 * Gibt zurück ob es sich um das letzte Paket  handelt.
	 * @return	Ist es das letze Paket
	 */
	public boolean isLast() {
		return last;
	}

	/**
	 * Gibt die Sequenznummer des Pakets zurück
	 * @return	Die Sequenznummer
	 */
	public short getNumSeq() {
		return NumSeq;
	}

	/**
	 * Gibt den Datenkanal zurück.
	 * @return	Der Datenkanal
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * Gibt die Daten zurück.
	 * @return	Die Daten.
	 */
	public byte[] getData() {
		return data;
	}

}

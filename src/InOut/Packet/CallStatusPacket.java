package Packet;

import java.nio.ByteBuffer;

/**
 * Dieses Paket liefert den Status des Anrufes an den Server
 */
public class CallStatusPacket implements Packet{
	/**
	 * Der Typ der Anrufs
	 * 1 -> eingehender Anruf
	 * 2 -> verpasster Anruf
	 * 3 -> angenommener Anruf
	 * 4 -> ausgehender Anruf
	 * 5 -> aufgelegter Anruf
	 */
	int type;
	/*
	 * 1 -> Incoming call
	 * 2 -> Missed call
	 * 3 -> Call accepted
	 * 4 -> Call send
	 * 5 -> Hang Up
	 * 
	 */
	/**
	 * Telefonnummer des Anrufs
	 */
	String phonenumber;
	
	public CallStatusPacket() {
		
	}

	/**
	 * Kontruktor besetzt die Klassenvariablen
	 * @param type	Der Anruftype
	 * @param phone	Telefonnummer
	 */
	public CallStatusPacket(int type, String phone) {
		this.type = type;
		this.phonenumber = phone;
	}

	/**
	 * Erstellt aus den Daten des Objekts ein byte-Array.
	 * Hierbei ist zu beachten, dass wenn keine Telefonnummer angezeigt werden sollte, das Paket kleiner ist also mit Telefonnummer.
	 * @return byte-Array mit dem Status des Anrufes und evtl sofern angezeigt auch die Telefonnummer
	 */
	public byte[] build() {
		ByteBuffer b;
		if(phonenumber == null) {
			b = ByteBuffer.allocate(4);
			b.putInt(type);
		}
		else {
			b = ByteBuffer.allocate(4+phonenumber.length());
			b.putInt(type);
			b.put(phonenumber.getBytes());
		}
		return b.array();
	}

	/**
	 * Erhält ein byte-Array mit den Daten und liest diese aus dem Array aus.
	 * Auch hier wird zwischen Paketen mit und ohne Telefonnumer unterschieden.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b= ByteBuffer.wrap(packet);
		this.type = b.getInt();
		if(b.hasRemaining()) {
			byte[] tmp = new byte[b.remaining()];
			b.get(tmp);
			this.phonenumber = new String(tmp);
		}
		else
			this.phonenumber = null;
	}

	/**
	 * Liefert den Anruftyp
	 * @return	Anruftyp
	 */
	public int getType() {
		return type;
	}

	/**
	 * Liedert die Telefonnummer
	 * @return	Telefonnummer
	 */
	public String getPhonenumber() {
		return phonenumber;
	}
}

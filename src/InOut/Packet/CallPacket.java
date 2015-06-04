package Packet;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Diese Paket speichert die Daten der einzelnen Anrufe die in der Anrufliste stehen.
 * Es wird nicht so wie es ist versendet, sondern einem CallLogPacket hinzugefügt.
 */
public class CallPacket implements Packet, Serializable{

	private static final long serialVersionUID = 3972539952673409279L;
	/**
	 * id	Anruf id
	 */
	private int id;
	/**
	 * type Anruftyp
	 */
	private int type;
	/**
	 * date	Anrufdatum
	 */
	private long date;
	/**
	 * duration	Dauer des Anrufs
	 */
	private long duration;
	/**
	 * contact_id	Id des Kontaktes
	 */
	private int contact_id;
	/**
	 * phoneNumberSize	Größe der Telefonnummer
	 */
	private int phoneNumberSize;
	/**
	 * phoneNumber	Die Telefonnummer
	 */
	private String phoneNumber;
	/**
	 * nameSize	Größe des Namens
	 */
	private int nameSize;
	/**
	 * name	Name des Kontakts
	 */
	private String name;
	
	public CallPacket() {
	}

	/**
	 * Konsturktor der Klasse. Er befüllt die Klassenvaribalen mit den übergebenden Daten.
	 * @param id	Anrufid
	 * @param type	Anruftyp
	 * @param date	Datum des Anrufs
	 * @param duration	Dauer des Anrufs
	 * @param contact_id	Kontaktid
	 * @param number	Telefonnummer
	 * @param name	Name des Kontakts
	 */
	public CallPacket(int id, int type, long date, long duration, int contact_id, String number, String name) {
		this.id = id;
		this.type = type;
		this.date = date;
		this.duration = duration;
		this.contact_id = contact_id;
		this.phoneNumber = number;
		if(phoneNumber != null)
			this.phoneNumberSize = number.length();
		else
			this.phoneNumberSize = 0;
		this.name = name;
		if(name != null)
			this.nameSize = name.length();
		else
			this.nameSize = 0;
	}

	/**
	 * Erstellt ein byte-Array welche die Informationen des Objekts beinhält
	 * @return	byte-Array mit den Anrufinformationen
	 */
	public byte[] build() {
		ByteBuffer b = ByteBuffer.allocate(4*5+8*2+phoneNumberSize+nameSize);
		b.putInt(id);
		b.putInt(type);
		b.putLong(date);
		b.putLong(duration);
		b.putInt(contact_id);
		b.putInt(phoneNumberSize);
		b.put(phoneNumber.getBytes());
		b.putInt(nameSize);
		b.put(name.getBytes());
		return b.array();
	}

	/**
	 * Bekommt ein byte-Array mit Anrufinformationen und befüllt mit diese Daten die Klassenvariablen.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);
		this.id = b.getInt();
		this.type = b.getInt();
		this.date = b.getLong();
		this.duration = b.getLong();
		this.contact_id = b.getInt();
		this.phoneNumberSize = b.getInt();
		byte[] tmp = new byte[phoneNumberSize];
		b.get(tmp);
		this.phoneNumber = new String(tmp);
		this.nameSize = b.getInt();
		tmp = new byte[nameSize];
		b.get(tmp);
		this.name = new String(tmp);
	}

	/**
	 * Lieder die Anrufid
	 * @return	Anrufid
	 */
	public int getId() {
		return id;
	}

	/**
	 * Liefert den Anruftyp
	 * @return	Anruftyp
	 */
	public int getType() {
		return type;
	}

	/**
	 * Liefert das Anrufdatum
	 * @return	Anrufdatum
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Liedert die Anrufdauer
	 * @return	Anrufdauer
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Liefert die ID des Kontakts
	 * @return	ID des Kontakts
	 */
	public int getContact_id() {
		return contact_id;
	}

	/**
	 * Liefert die Größe der Telefonnummer
	 * @return	Größe der Telefonnummer
	 */
	public int getPhoneNumberSize() {
		return phoneNumberSize;
	}

	/**
	 * Liefert die Telefonnummer
	 * @return	Telefonnummer
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Liefert die Größe des Namens
	 * @return	Die größe des Namens
	 */
	public int getNameSize() {
		return nameSize;
	}

	/**
	 * Liefert den Namen
	 * @return	Name
	 */
	public String getName() {
		return name;
	}
}

package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Diese Klasse wird zum verpacken von Sms verwendet. Diese Klasse wird nicht versandt sonder dem SMSTreePacket hinzugefügt.
 */
public class SMSPacket implements Packet, Serializable{


	private static final long serialVersionUID = 6169284240601506961L;
	/**
	 * id	Die Id der Sms
	 */
	private int id;
	/**
	 * thread_id 	Die Id des Threads
	 */
	private int thread_id;
	//private int address_size;
	/**
	 * address	Die Telefonnummer
	 */
	private String address;
	/**
	 * person	Der Kontakt
	 */
	private int person;
	/**
	 * date	Das Datum
	 */
	private long date;
	/**
	 * read	wurde die Sms schon gelesen
	 */
	private int read;
	/**
	 * type	Typ der Sms ausgehen oder ankommend
	 */
	private int type;
	//private int body_size;
	/**
	 * body	Die Smsnachricht
	 */
	private String body;
	
	public SMSPacket() {
		
	}

	/**
	 * Kontruktor der Klasse weißt die übergebenden Daten den Klassenvariablen zu
	 * @param id	ID
	 * @param thid	Threadid
	 * @param ad	Telefonnummer
	 * @param pers	Kontakt
	 * @param dat	Datum
	 * @param read	Gelesen oder nicht
	 * @param body	Nachricht
	 * @param type	eingehend oder ausgehend
	 */
	public SMSPacket(int id, int thid, String ad, int pers, long dat, int read, String body, int type) {
		this.id = id;
		this.thread_id = thid;
		this.address = ad;
		//this.address_size = ad.length();
		this.person = pers;
		this.date = dat;
		this.read = read;
		this.body = body;
		//this.body_size = this.body.length();
		this.type = type;
	}
	
	/*
	public byte[] build() {
		
		ByteBuffer b = ByteBuffer.allocate(4*6+8+address_size+body_size);
		b.putInt(id);
		b.putInt(thread_id);
		b.putInt(address_size);
		b.put(address.getBytes());
		b.putInt(person);
		b.putLong(date);
		b.putInt(read);
		b.putInt(type);
		b.putInt(body_size);
		b.put(body.getBytes());
		return b.array();
		
	}

	public void parse(byte[] packet) {
		ByteBuffer b = ByteBuffer.wrap(packet);
		this.id = b.getInt();
		this.thread_id = b.getInt();
		this.address_size = b.getInt();
		byte[] tmp = new byte[address_size];
		b.get(tmp);
		this.address = new String(tmp);
		this.person = b.getInt();
		this.date = b.getLong();
		this.read = b.getInt();
		this.type = b.getInt();
		this.body_size = b.getInt();
		tmp = new byte[body_size];
		b.get(tmp);
		this.body = new String(tmp);
	}
	*/

	/**
	 * Erstellt aus den Daten des Paktes ein byte-Array
	 * @return	byte-Array mit den Daten des Pakets
	 */
	public byte[] build() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(this);
			return bos.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Erhält ein Paket mit den Daten eines SMSPacket und extrahiert die Daten.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(bis);
			SMSPacket p = (SMSPacket) in.readObject();
			this.id = p.id;
			this.thread_id = p.thread_id;
			this.address = p.address;
			this.body = p.body;
			this.date = p.date;
			this.person = p.person;
			this.read = p.read;
			this.type = p.type;

		} catch (Exception e) {
		}
	}

	/**
	 * Liefert den Typ
	 * @return	Typ
	 */
	public int getType() {
		return type;
	}

	/**
	 * Setzt den Typ
	 * @param t Typ
	 */
	public void setType(int t) {
		this.type =t;
	}

	/**
	 * Liefert die Id
	 * @return	Id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt die ThreadId
	 * @return	ThreadId
	 */
	public int getThread_id() {
		return thread_id;
	}

	/**
	 * Liefert die Telefonnummern
	 * @return	Telefonnummern
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Setzt den Kontakt
	 * @return Kontakt
	 */
	public int getPerson() {
		return person;
	}

	/**
	 * Lifert das Datum
 	 * @return	Datum
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Liefert ob die Sms gelesen wurde
	 * @return Sms gelesen?
	 */
	public int getRead() {
		return read;
	}

	/**
	 * Liefert die Nachricht der Sms
	 * @return Nachricht der Sms
	 */
	public String getBody() {
		return body;
	}
}

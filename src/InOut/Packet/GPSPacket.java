package Packet;

import java.nio.ByteBuffer;

/**
 * Diese Klasse ist zum Speichern und Versenden von GPS Daten vorhanden.
 */
public class GPSPacket implements Packet
{
	/**
	 * geographische Länge
	 */
	private double longitude;
	/**
	 * geographische Breite
	 */
	private double latitude;
	/**
	 * grographische Höhe
	 */
	private double altitude;
	/**
	 * Geschwindigkeit
	 */
	private float speed;
	/**
	 * Genauigkeit
	 */
	private float accuracy;

	public GPSPacket()
	{
		
	}

	/**
	 * Konstruktor der Klasse. Er setzt die Klassenvariablen
	 * @param lat	georgrphische Breite
	 * @param lon	georgrphische Länge
	 * @param alt	georgrphische Höhe
	 * @param speed	Geschwindigkeit
	 * @param acc	Genauigkeit
	 */
	public GPSPacket(double lat, double lon, double alt, float speed, float acc)
	{
		this.latitude = lat ;
		this.longitude = lon ;
		this.altitude = alt;
		this.speed = speed;
		this.accuracy = acc;
	}

	/**
	 * Erstellt ein byte-Array mit den Daten des Pakets.
	 * @return	byte-Array mit den GPS Daten
	 */
	public byte[] build()
	{
		ByteBuffer b = ByteBuffer.allocate(32);
		System.out.println("Longitude : "+longitude);
		b.putDouble(this.longitude);
		b.putDouble(this.latitude);
		b.putDouble(this.altitude);
		b.putFloat(this.speed);
		b.putFloat(this.accuracy);
		return b.array();
	}

	/**
	 * Liest aus einem übergebenen byte-Array die GPS-Daten aus.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet)
	{
		ByteBuffer b = ByteBuffer.wrap(packet);
		this.longitude = b.getDouble();
		this.latitude = b.getDouble();
		this.altitude = b.getDouble();
		this.speed = b.getFloat();
		this.accuracy = b.getFloat();
	}


	/**
	 * Liefert die geographische Länge
	 * @return	geographische Länge
	 */
	public double getLongitude()
	{
		return longitude;
	}

	/**
	 * Liefert die geographische Breite
	 * @return	geographische Breite
	 */
	public double getLatitude()
	{
		return latitude;
	}

	/**
	 * Liefert die georgraphische Höhe
	 * @return	georgraphische Höhe
	 */
	public double getAltitude()
	{
		return altitude;
	}

	/**
	 * Liefert die Geschwindigkeit
	 * @return	Geschwindigkeit
	 */
	public float getSpeed()
	{
		return speed;
	}

	/**
	 * Liefert die Genauigkeit
	 * @return	Genauigkeit
	 */
	public float getAccuracy()
	{
		return accuracy;
	}

}

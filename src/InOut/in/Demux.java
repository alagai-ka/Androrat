package in;

import Packet.TransportPacket;
import inout.Controler;
import inout.Protocol;
import java.nio.ByteBuffer;

/**
 * Klasse zum Demultiplexen
 */
public class Demux {
	// acces au controler
	/**
	 * controler	Der Zugang zum Controler
	 */
	private Controler controler;

	// un packet
	/**
	 * p	Ein Paket
	 */
	private TransportPacket p;


	// l'identifiant du client
	/**
	 * imei	Die Imei des Clients
	 */
	private String imei;

	// le buffer de lecture
	/**
	 * buffer	Ein Zwischenspeichern
	 */
	private ByteBuffer buffer;

	// variables de controle
	private boolean partialDataExpected, reading;

	/**
	 * Der Konstruktor weist den Klassenvariablen die übergebenen Daten zu.
	 * @param s	Der Controler
	 * @param i	Die IMEI
	 */
	public Demux(Controler s, String i) {
		imei = i;
		controler = s;
    	reading = true;
		partialDataExpected = false;

	}

	public boolean receive(ByteBuffer buffer) throws Exception
	{

		while (reading) {

				/**
			 	* Überprüfen ob die gesamten Daten angekommen sind.
			 	*/
				if(!partialDataExpected)
					//si on n'attend pas de donn�es partielles(dans le cas d'un paquet pas re�ue enti�rement)
				{	
						// si la taille du buffer est insuffisante
						/**
					 	* Überprüfen ob die Buffer groß genug ist für die Daten.
					 	*/
						if ((buffer.limit() - buffer.position()) < Protocol.HEADER_LENGTH_DATA) 
						{
							/**
							 * True zurückgeben
							 */
							return true;
						}
				}
	
				// dans le cas d'un paquet partiellement recue
				/**
			 	* Wenn die Daten partiell ankommen, wird die parseCompleter Methode aufgerufen um die Daten in der Klassenvariablen des Pakets zu speichern.
			 	*/
				if (partialDataExpected)
					partialDataExpected = p.parseCompleter(buffer);
				else
				/**
				 * Ansonsten wird die parse-Methode  auf dem Paket p aufgerufen. Um herauszufinden, ob immernoch nicht alle Daten angekommen sind.
				 */
				{
					p = new TransportPacket();
					partialDataExpected = p.parse(buffer);
				}

				/**
			 	* Wenn nach der Überprüfung des Pakets immer noch Daten erwartet werden, wird true zurückgeliefert.
			 	*/
				if (partialDataExpected)
					return true;
				else
				/**
				 * Ansonsten wird die Storage Methode aufgerufen um die empfangenen Daten abzurufen und zu speichern.
				 */
					controler.Storage(p, imei);
			
		}


		reading = true;
		return true;
	}

	/**
	 * Diese Methode setzt die IMEI
	 * @param i	Die IMEI
	 */
	public void setImei(String i) {
		imei = i;
	}

}

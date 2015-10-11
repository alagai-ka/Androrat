package handler;

import java.io.IOException;
import java.util.ArrayList;

import Packet.TransportPacket;

import inout.Protocol;


/*
 * Class servant d'espace de stockage temporaire 
 * aux donn�es re�ues
 * 
 * 
 * M�thode addByteData():
 * permet de compl�ter les donn�es en cours de r�ception
 * en les rajoutant dans un tableaux d'octets
 * 
 * M�thode setLastPacketInfo():
 * permet d'inscrire les informations sur le dernier paquet re�u
 */

/**
 * Diese Klasse wird zum Zwischenspeichern von Paketen verwendet.
 */
public class TemporaryStorage
{
	/**
	 * data_temp	Zum Zwischenspeichern der Pakete
	 */
	private ArrayList<byte[]> data_temp;
	/**
	 * final_data	Das Zusammengesetzte Datenpaket am Stück
	 */
	private byte[] final_data;
	
	//les informations sur le dernier paquet re�u
	/**
	 * total_length	Die Gesamtgröße der Daten
	 */
	private int total_length;
	/**
	 * last_packet_position	Die NumSeq des letzten verarbeiteten Pakets
	 */
	private int last_packet_position;
	/**
	 * size_counter	 Die bisherige Größe des Pakets.
	 */
	private int size_counter;
	/**
	 * end	Variable für den Schleifenabbruch
	 */
	private boolean end;
	
	private short data_type;
	
	
	//constructeur: en cours de reception(commande)

	/**
	 * Der Konstruktor initialisiert die Klassenvariablen.
	 */
	public TemporaryStorage()
	{
		//initialisation de l'espace de stockage
		data_temp = new ArrayList<byte[]>();
		last_packet_position = -1;
		size_counter = 0;
		
	}

	/**
	 * Diese Methode setzt die Werte der Klassenvariablen wieder auf die Werte mit denen sie inizialisiert wurden.
	 */
	public void reset() {
		data_temp = new ArrayList<byte[]>();
		last_packet_position = -1;
		size_counter = 0;
		end = false;
	}

	/**
	 * Diese Methode wird zum Hinzufügen eines Pakets verwendet.
	 * @param packet	Das Paket
	 * @return Den entsprechenden Wert des Protocols.
	 */
	public short addPacket(TransportPacket packet)
	{

		/**
		 * Überprüfen ob das übergebene Paket auch das nächste ist.
		 * Wenn nicht wird PACKET_LOST zurückgegeben.
		 */
		if(packet.getNumSeq() != (last_packet_position+1))
			return Protocol.PACKET_LOST;
		
		
		//si la suite des donn�es est attendue
		/**
		 * Überprüfen ob das Ende erreicht ist.
		 */
        if(!end)
		{	
        	//System.out.println("on rajoute des donn�es attendues");
			/**
			 * Ist das Ende nicht erreicht, so wird die total_length mit den Daten des Pakets gesetzt.
			 * Zusätzlich wird die Variable end auf packet.isLast() gesetzt. Danach wird der size_counter um die Paketlänge erhöht und die Daten des Pakets werden in data_temp gespeichert.
			 */
        	total_length = packet.getTotalLength();
			end = packet.isLast();
			size_counter+= packet.getLocalLength();
			data_temp.add(packet.getData());
			
			//si on attend encore des donn�es
			/**
			 * Überprüfen ob es dies das letzte Paket war.
			 */
			if(!end)
			{
				/**
				 * Wenn nicht so wird die last_packet_position um eins erhöht und PACKET_DONE zurückgegeben.
 				 */
				System.out.println("Paquet "+packet.getNumSeq());
				last_packet_position ++;
			  return Protocol.PACKET_DONE;
			}
			else//sinon (si c'est la fin)
			{
				/**
				 * Wenn es sich um das letzte Paket handelt so wird überprüft, ob der size_counter und die total_length übereinstimmen.
				 * Ist dies nicht der Fall so wird SIZE_ERROR zurückgegeben, da die Längen nicht übereinstimmen.
				 */
				if(size_counter != total_length)
					return Protocol.SIZE_ERROR;
				/**
				 * Hier wird nun das final_data-Array erstellt mit der Länge total_length und die entsprechenden Daten des data_tmp werden in das final_data Array kopiert.
				 */
				int i = 0;
				final_data = new byte[total_length];
				for(int n = 0;n<data_temp.size();n++)
					for(int p = 0;p<data_temp.get(n).length;p++,i++)
						final_data[i] = data_temp.get(n)[p];
				/**
				 * Ist dies der Fall so wird ALL_DONE zurückgegeben.
 				 */
				return Protocol.ALL_DONE;
			}
			
		}
		else
		/**
		 * Sollte das Ende schon erreicht sein, wird NO_MORE zurückgegeben.
		 */
			//sinon erreur
		   return Protocol.NO_MORE;
		
	}

	/**
	 * Gibt das data_temp-Array zurück.
	 * @return	Die Daten der bisher empfangenen Pakete
	 */
	public ArrayList<byte[]> getByteData()
	{
		return data_temp;
	}

	/**
	 * Gibt das final_Data-Array zurück.
	 * @return	die finale zusammengesetzte Datei.
	 */
	public byte[] getFinalData()
	{
		return final_data;
	}

	/**
	 * Gibt die Sequenznummer des letzten empfangenen Pakets zurück.
	 * @return	Letze Sequenznummer
	 */
	public int getLastPacketPositionReceived()
	{
		return last_packet_position;
	}

	/**
	 * Gibt die Gesamtgröße der Datei zurück
	 * @return	Die Gesamtgröße der Datei
	 */
	public int getTotalSize()
	{
		return total_length;
	}
}

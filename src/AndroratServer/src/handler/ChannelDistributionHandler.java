package handler;



import java.util.HashMap;
import java.util.Map;
import Packet.CommandPacket;
import Packet.Packet;

/**
 * Diese Klasse ist zur Verteilung der Datenkanäle verantwortlich.
 */
public class ChannelDistributionHandler
{

	/**
	 * paketMap	Hier werden die gesendeten CommandPackets gespeichert.
	 */
	//map des interface de gestions des donn�es
	private HashMap<Integer,Packet> packetMap;
	/**
	 * tempDataMap	In dieser Map werden Objekte der Klasse TemporaryStorage gespeichert.
	 */
	//map des espaces de stockage
	private Map<Integer,TemporaryStorage> tempDataMap;
	/**
	 * packetHandlerMap	In dieser Map werden die Handler zu den jeweiligen Commandpackets gespeichert.
	 */
	//map des gestionnaires des packets
	private Map<Integer,PacketHandler> packetHandlerMap;

	/**
	 * Der Konstruktor erstellt die unterschiedlichen Maps und weist diese den Klassenvariablen zu.
	 */
	public ChannelDistributionHandler()
	{
		//cr�ation des maps de donn�es
		packetMap = new HashMap<Integer, Packet>() ;
		tempDataMap = new HashMap<Integer,TemporaryStorage>();
		packetHandlerMap = new HashMap<Integer,PacketHandler>();
		
		//inscription du cannal 0 pour les commandes
		registerListener(0,new CommandPacket());
		tempDataMap.put(0, new TemporaryStorage());
		packetHandlerMap.put(0, new CommandHandler());
		
	}


	/**
	 * Mit dieser Methode wird überprüft, ob das übergebene Paket schon in der Paketmap vorhanden ist.
	 * Wenn nicht so wird das Paket mit dem Kanal in die Map eingepflegt.
	 * Zusätzlich wird in der tempDataMap ein neues TemporaryStorage-Objekt für dem Kanal erstellt.
	 * @param chan	Der Datenkanal
	 * @param packet	Das Paket
	 * @return	true wenn der Listener registriert wurde, sonst false.
	 */
	public boolean registerListener(int chan, Packet packet)
	{
		if(!(packetMap.containsKey(chan)))
		{
			packetMap.put(chan, packet);
			tempDataMap.put(chan, new TemporaryStorage());
			return true;
		}
		else
			return false;
	}

	/**
	 * Diese Methode registriert einen neuen Handler. Hierzu wird überprüft, ob schon ein Handler mit dem Key chan vorhanden ist.
	 * Wenn nicht wird ein neuer Eintrag in der HashMap erstellt. Hierzu wird der übergebene Handler und der Kanal, als Key, der Map hinzugefügt.
	 * @param chan	Der Kanal
	 * @param han	Der Handler
	 * @return	True wenn der Handler registriert werden konnte, sonst falls.
	 */
	public boolean registerHandler(int chan, PacketHandler han) {
		if(!(packetHandlerMap.containsKey(chan)))
		{
			packetHandlerMap.put(chan, han);
			return true;
		}
		else return false;
	}

	/**
	 * Diese Methode ist zum Entfernen von Listener.
	 * Hier wird überprüft, ob es ein Paket gibt, welches als Key den übergeben Kanal besitzt.
	 * Sollte dies der Fall sein, werden in der packetMap, der tempDataMap und der packetHandlerMap die Daten mit dem Kanal als Key entfernt.
	 * @param chan	Der Kanal
	 * @return	True wenn die Daten gelöscht wurden, sonst false.
	 */
	public boolean removeListener(int chan)
	{
		try
		{
			if((packetMap.containsKey(chan)))
			{
				packetMap.remove(chan);
				tempDataMap.remove(chan);
				packetHandlerMap.remove(chan);
				return true;
			}
		}catch(NullPointerException e)
		{
			return false;
		}
		return false;
		
	}

	/**
	 * Diese Methode gibt einen freien Kanal zurück.
	 * @return	Die Nummer des freien Kanals
	 */
	public int getFreeChannel()
	{
		/**
		 * Zufäilge Nummer ziehen.
		 */
		int i = (int) (Math.random() * 1000);
		/**
		 * Überprüfen ob der Kanal schon in der packetMap als Key auftaucht.
		 */
		while(packetMap.containsKey(i))
		{
			/**
			 * Wenn ja solange zufällig ziehen bis ein freier Kanal gefunden wurde.
			 */
			i = (int) (Math.random() * 1000);
		}
		return i ;
	}

	/**
	 * Liefet das Paket des Kanals.
	 * @param chan	Der Kanal
	 * @return	Das Paket aus der packetMap mit dem Key chan.
	 */
	public Packet getPacketMap(int chan)
	{
		return packetMap.get(chan);
	}

	/**
	 * Liefert den Handler mit dem Key chan zurück.
	 * @param chan	Der Kanal
	 * @return Das Paket aus der packetHandlerMap mit dem Key chan.
	 */
	public PacketHandler getPacketHandlerMap(int chan)
	{
		return packetHandlerMap.get(chan);
	}

	/**
	 * Liefert die TemporyStorage mit dem Key chan zurück.
	 * @param chan	Der Kanal
	 * @return Das Paket aus der tempDataMap mit dem Key chan.
	 */
	public TemporaryStorage getStorage(int chan)
	{
		return tempDataMap.get(chan);
	}
	
	
}

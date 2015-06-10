package handler;



import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Hashtable;

import server.ClientHandler;
import server.Server;

import inout.Protocol;
import Packet.CommandPacket;
import Packet.LogPacket;
import Packet.Packet;
import handler.ChannelDistributionHandler;

/**
 * Diese Klasse ist zur Verarbeitung von CommandPackets gedacht.
 */
public class CommandHandler implements PacketHandler
{
	/**
	 * command	Der Befehl des Pakets
	 */
	private short command;
	/**
	 * arg	Die zusätzlichen Argumente des Pakets
	 */
	private byte[] arg;

	/**
	 * Dies ist der Konstruktor der Klasse. Er ist leer.
	 */
	public CommandHandler()
	{
		
	}

	/**
	 * Erhält ein CommandPaket und verarbeitet dies sofern es sich um den CONNECT Befehl handelt.
	 * @param p	Das CommandPaket
	 * @param temp_imei	Die IMEI des derzeitig noch verbundenen Geräts
	 * @param c	Der Server
	 */
	@Override
	public void handlePacket(Packet p,String temp_imei,Server c) 
	{
			/**
		 	* Der Befehl des Pakets wird in der Klassenvariabelen gespeichert.
			 * Dasselbe passiert mit den Argumenten.
		 	*/
			command = ((CommandPacket) p).getCommand();
			arg = ((CommandPacket) p).getArguments();
		/**
		 * Überprüfen ob es sich um den CONNECT Befehl handelt
		 */
			switch (command) 
			{
				case Protocol.CONNECT:
					
					// Reconstruction des infos
					/**
					 * Hier werden nun die Argument Daten auf einen Stream Geschrieben und die daraus resultierende Daten werden in eine Hashtabelle gespeichert.
					 */
					ByteArrayInputStream bis = new ByteArrayInputStream(arg);
					ObjectInputStream in;
					Hashtable<String,String> h = null;
					try {
						in = new ObjectInputStream(bis);
						h = (Hashtable<String, String>) in.readObject();
					} catch (Exception e) {
						e.printStackTrace();
					}
					/**
					 * Hier wird die IMEI, die in dem Paket gespeichert ist in der Variabeln new_imei gespeichert.
					 */
					String new_imei = h.get("IMEI");
					/**
					 * Lognachricht erstellen, dass das Gerät mit der new_imei versucht eine Verbindung aufzubauen.
					 */
					c.getGui().logTxt("CONNECT command received from "+new_imei);
					//dans le cas d'un tout nouveau imei
					/**
					 * Überprüfen ob die IMEI schon vorhanden war, also sich das Gerät nur erenut verbindet.
					 */
					if(!c.getClientMap().containsKey(new_imei))
					{
						/**
						 * Wenn es sich um eine neue IMEI handelt so wird der ClientHandler ders Servers mit der temp_imei
						 * und der ChannelDistributionHandler mit der temp_imei in den Variablen ch und cdh gespeichert.
						 */
						//on r�cup�re son gestionnaire
						ClientHandler ch = c.getClientMap().get(temp_imei);
						ChannelDistributionHandler cdh = c.getChannelHandlerMap().get(temp_imei);
						/**
						 * Hier wird die IMEI des ClientHandler ch auf new_imei also die IMEI des Pakets gesetzt-
						 */
						ch.updateIMEI(new_imei); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						
						//on efface ses donn�es tomporaires attribu�es � la connexion
						/**
						 * Im Anschluss wird der Client mit der temp_imei und der entsprechende Handler gelöscht.
						 */
						c.getClientMap().remove(temp_imei);
						c.getChannelHandlerMap().remove(temp_imei);
						
						
						//et on l'inscrit
						/**
						 * Danach wird der Client mit der neuen IMEI und der ChannelHandler mit der neue IMEI in dern Variablen des Serves gespeichert.
						 */
						c.getClientMap().put(new_imei, ch);
						c.getChannelHandlerMap().put(new_imei,cdh);
						
						//On ajoute le handler pour les logs
						/**
						 * Zum Schluss werden nun noch die Handler für die LogDateien erstellt.
						 */
						c.getChannelHandlerMap().get(new_imei).registerListener(1, new LogPacket());
						c.getChannelHandlerMap().get(new_imei).registerHandler(1, new ClientLogHandler(1, new_imei, c.getGui()));
					}
					//si le client s'est reconnect� (imei d�ja inscrit)
					else
					{
						/**
						 * Sollte die IMEI im Server schon enthalten sein so verbindent sich das Gerät erneut zu dem Server.
						 * Auch hier weren wieder der ClientHandler und der ChannelDistributionHandler aus der Variabeln des Servers geholt und in der Variablen ch1 und cdh1 gespeichert.
						 * Danach werden die Daten mit der temp_imei aus den Server Variablen entfernt und die die Handelr mit der neuen IMEI als Key in die Hashtabellen des Servers geschrieben.
						 */
						//on r�cup�re son gestionnaire
						ClientHandler ch1 = c.getClientMap().get(temp_imei);
						//et son ANCIEN ChannelDistributionHandler!
						ChannelDistributionHandler cdh1 = c.getChannelHandlerMap().get(new_imei);
						
						//on efface ses donn�es tomporaires attribu�es � la connexion 
						c.getClientMap().remove(temp_imei);
						c.getChannelHandlerMap().remove(temp_imei);
						//et lors de la connexion pr�c�dente!
						c.getChannelHandlerMap().remove(new_imei);
						
						//et on l'inscrit avec son ancien ChannelDistributoinHandler
						c.getClientMap().put(new_imei, ch1);
						c.getChannelHandlerMap().put(new_imei,cdh1);

						
					}
					/**
					 * Zum Schluss werden noch die verschiedenen Daten auf der GUI angezeigt.
					 */
					c.getGui().addUser(new_imei, h.get("Country"), h.get("PhoneNumber"), h.get("Operator"), h.get("SimCountry"), h.get("SimOperator"), h.get("SimSerial"));

					break;

			}
		
		
	}

	@Override
	public void receive(Packet p, String imei) {
		// TODO Auto-generated method stub
		
	}


}

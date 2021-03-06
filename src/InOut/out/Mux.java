package out;

import inout.Protocol;

import java.io.DataOutputStream;

import Packet.TransportPacket;

/**
 * Diese Klasse ist zum Aufteilen von Dateien, um diese dann in kleinen Paketet über das Internet zu versenden.
 */
public class Mux 
{

	Sender sender ;

	/**
	 * Erstellt einen neuen Sender.
	 * @param out	Der OutputStream
	 */
	public Mux(DataOutputStream out)
	{
		sender = new Sender(out);
	}

	/**
	 * Diese Methode teilt die Daten auf und sendet sie an den Server. Außerdem werden sie mit Sequenznummern versehen und ob es sich um das letzte Paket handelt.
	 * @param chan	Der Datenkanal
	 * @param data	Die Daten
	 */
	public void send(int chan,byte[] data)
	{
		try
		{
			//System.out.println("data " + new String(data));
			TransportPacket tp;
			boolean last = false;
			boolean envoieTotal = false;
			int pointeurData = 0;
			short numSeq = 0;
			int actualLenght;

			while (!envoieTotal) 
			{
				byte[] dataToSend;

				
				if (last || ((Protocol.HEADER_LENGTH_DATA + data.length) < Protocol.MAX_PACKET_SIZE))
				{
					dataToSend = new byte[Protocol.HEADER_LENGTH_DATA + (data.length - pointeurData)];
					last = true ;
					envoieTotal = true ;
				}
				else
					dataToSend = new byte[Protocol.MAX_PACKET_SIZE];
				
				//System.out.println("datatosend 1 : " +dataToSend.length);
				
				actualLenght = dataToSend.length - Protocol.HEADER_LENGTH_DATA;


				byte[] fragData = new byte[dataToSend.length-Protocol.HEADER_LENGTH_DATA];
				System.arraycopy(data, pointeurData, fragData, 0, fragData.length);
				tp = new TransportPacket(data.length, actualLenght, chan, last, numSeq, fragData);
				dataToSend = tp.build();
				//System.out.println("datatosend 2 : " +dataToSend.length);
				/*	
				System.out.println("header " + header.length);
				System.out.println("data " + dataToSend.length);
				System.arraycopy(header, 0, dataToSend, 0, header.length);
				System.arraycopy(data, pointeurData, dataToSend, header.length, Math.min((dataToSend.length - header.length), (data.length - pointeurData)));

				
				 * System.out.println(" actual lenght ? " + actualLenght);
				 * System.out.println("data ? "+ new String(data) + " LA taille : " + data.length); 
				 * System.out.println("nouveau pointeur ? "+ pointeurData); 
				 * System.out.println("toSend ? "+ new String(dataToSend)); 
				 * System.out.println("taille ? "+ dataToSend.length);
				 * System.out.println("tailleheader ? "+ header.length); 
				 // */
				//System.out.println("Paquet " + numSeq);
				pointeurData = pointeurData + actualLenght;
				numSeq++;
				if ((data.length - pointeurData) <= (Protocol.MAX_PACKET_SIZE - Protocol.HEADER_LENGTH_DATA))
				{
					last = true;
				}
				
				sender.send(dataToSend);

			}
		}
		catch(NullPointerException e)
		{
			System.out.println("Ce channel n'est pas index�");
			e.printStackTrace();
		}
	}
}

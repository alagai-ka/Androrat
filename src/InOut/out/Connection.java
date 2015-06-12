//package InOut;

package out;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;



import out.Mux;

import in.Demux;
import in.Receiver;
import inout.Controler;

/**
 * Diese Klasse verwaltet die Verbindung.
 */
public class Connection
{
	/**
	 * s	Der Socket
	 */
	Socket s;
	/**
	 * ip	Die ip
	 */
	String ip = "localhost";
	/**
	 * port	Der Port
	 */
	int port = 5555;
	/**
	 * out der DataOutputStream
	 */
	DataOutputStream out;
	/**
	 * in	der DataInputStream
	 */
	DataInputStream in;
	
	boolean stop = false;
	/**
	 * readInstruction	Zum Auslesen der Befehle
	 */
	ByteBuffer readInstruction;
	/**
	 * m	Ein Mux
	 */
	Mux m;
	/**
	 * dem 	Ein Dmux
	 */
	Demux dem ;
	/**
	 * controler	Der Kontoller
	 */
	Controler controler;
	/**
	 * receive	Der Receiver
	 */
	Receiver receive ;

	/**
	 * Der Konstruktor setzt die übergebenen Daten auf die Klassenvariablen.
	 * @param ip	Die IP
	 * @param port	Der Port
	 */
	public Connection(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	/**
	 * Der Konstruktor setzt die übergebenen Daten auf die Klassenvariablen.
	 * @param ip	Die IP
	 * @param port	Der Port
	 * @param ctrl	Der Kontroler
	 */
	public Connection(String ip, int port, Controler ctrl)
	{
		this.ip = ip;
		this.port = port;
		this.controler = ctrl;
	}

	/**
	 * Diese Methode erstellt mit den Datne des Konstrukors einen neue Verbindung.
	 * Hierzu wird zu erst ein neuer Socket mit der ip und dem Port erstellt.
	 * Danach werden die beiden Streams für diesen Socket erstllt.
	 * Im Anschluss wird ein neuer MuX und ein neuer DMUX ersellt und ein neuer Receiver.
	 * Schliesßlich wird true zurückgegeben sollte dies erfolgreich gewesen sein und false falls es nicht geklappt hat.
	 * @return	true wenn die Verbundung erstellt wurde, false sonst.
	 */
	public boolean connect()
	{
		try
		{
			s = new Socket(ip, port);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			m = new Mux(out);
			dem = new Demux(controler, "moi");
			receive = new Receiver(s);
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Zum ernuet verbinden wird hier die Methode connect aufgerufen.
	 * @return	True wenn Verbindung steht, false sonst.
	 */
	public boolean reconnect()
	{
		return connect();
	}

	/**
	 * Diese Methode lauft auf eingehende Verbindungen zum Socket und nimmt diese an.
	 * Zusätzlich werden dann die verschiedenen Stream für diese Verbindung erstellt.
	 * Auch ein neuer Mux wird erstellt. Sollte dies erfolgreich gewesen sein so wird true zurückgegeben, ansonsten false.
	 * @param ss	Der Serversocket.
	 * @return	true wenn die Verbindung aktzeptiert wurde und die Variablen entsprechend erstellt, false sonst.
	 */
	public boolean accept(ServerSocket ss)
	{
		try
		{
			s = ss.accept();

			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			m = new Mux(out);
			return true;

		} catch (IOException e)
		{

			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Diese Methode empfängt die Daten des Receivers und extrahiert daraus die Instruktionen und gibt diese zurück.
	 * @return	Die Instruktionen
	 * @throws Exception
	 */
	public ByteBuffer getInstruction() throws Exception
	{
		readInstruction = receive.read();
		
		if(dem.receive(readInstruction))
			readInstruction.compact();
		else
			readInstruction.clear();
		
		return readInstruction;
	}

	/**
	 * Übergibt den Kanal und das Paket an den Mux um die Daten zu senden.
	 * @param chan	Der Kanal
	 * @param packet	Das Paket
	 */
	public void sendData(int chan, byte[] packet)
	{
		m.send(chan, packet);
	}

	/**
	 * Diese Methode beendet die Verbindung in dem der Socket geschlossen wird.
	 */
	public void stop() {
		try {
			s.close();
		} catch (IOException e) {
			
		}
	}
}
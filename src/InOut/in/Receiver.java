package in;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import inout.Protocol;

/**
 * Diese Klasse ist zum Empfangen von Daten gedacht.
 */
public class Receiver {
	/**
	 * socket	Der Softwaresocket
	 */
	private Socket socket;
	/**
	 * received_data	Die empfangenen Daten
	 */
	private byte[] received_data;
	/**
	 * buffer	Ein Zwischenspeicher
	 */
	private ByteBuffer buffer;
	/**
	 * is	Ein Stream
	 */
	private InputStream is;

	/**
	 * Dies ist der Konstruktor. Er wei�t den �bergebenen Socket der Klassenvariablen zu.
	 * Danach wird f�r diesen Socket ein neuer InputStream erstellt und in der Klassenvariablen gespeichert.
	 * Danach werden die beiden Buffer mit der MAX_PACKET_SIZE erstellt.
	 * @param s	Der Socket
	 * @throws IOException
	 */
	public Receiver(Socket s) throws IOException {
		socket = s;
		is = socket.getInputStream();

		received_data = new byte[Protocol.MAX_PACKET_SIZE];
		buffer = ByteBuffer.allocate(Protocol.MAX_PACKET_SIZE);
	}

	/**
	 * Liest die Bytes der Klassenvariablen received_data und speichert die Anzahl der gelesenen Bytes in n.
	 * Danach wird der buffer geleert. Im Anschluss werden die n Bytes von received_data in dem buffer gespeichert.
	 * Danach wird dieser Buffer zur�ckgegeben.
	 * @return	Der Buffer der die reveiced_data enth�lt.
	 * @throws IOException
	 * @throws SocketException
	 */
	public ByteBuffer read() throws IOException, SocketException { // A supprimer !
		int n = 0;

		n = is.read(received_data);

		buffer.clear();		
		buffer = ByteBuffer.wrap(received_data, 0, n);
		//System.out.println("data has been read:" + buffer.limit());

		return buffer;
	}

	/**
	 * Erstellt received_data aus dem ByteBuffer.
	 * @param b	Der ByteBuffer
	 * @return	Der Buffer mit den received Daten.
	 * @throws IOException
	 * @throws SocketException
	 */
	public ByteBuffer read(ByteBuffer b) throws IOException, SocketException {
		int n = 0;
		
		byte[] theRest = null;
		/**
		 * �berpr�fen ob die Position des ByteBuffers gr��er als 0 und kleiner als HEADER_LENGTH_DATA ist.
		 */
		if(b.position()>0 && b.position()<Protocol.HEADER_LENGTH_DATA)
		{
			/**
			 * Erstellt ein Byte-Array das so gro� ist wie die Position des Buffers ist.
			 */
			theRest = new byte[b.position()];
			b.flip();
			/**
			 * Hier werden die Daten von b bis zur BufferPosition in theRest gespeichert.
			 */
			b.get(theRest, 0, b.limit());
			/**
			 * F�gt die Daten aus theRest dem Buffer receivedData hinzu.
			 */
			System.arraycopy(theRest, 0, received_data, 0, theRest.length);
			//for(int i = 0; i<theRest.length;i++)
			//	received_data[i] = theRest[i];
			
			//System.out.println("theRest len = "+theRest.length);
			/**
			 * Liest die Daten von received_data, ab der L�nge von theRest bis zum Max-Size-die L�nge von the Rest.
			 */
			n = is.read(received_data,theRest.length,Protocol.MAX_PACKET_SIZE-theRest.length);
			/**
			 * Danach wird n um die L�nge von the Rest erh�ht.
			 */
			n+=theRest.length;
		  }
		else
		/**
		 * Liest die Daten von received_data und speichert die Zahl der gelesenen Bytes in n
		 */
			n = is.read(received_data);
		
		
		//buffer.clear();
		/**
		 * Speichert received_data in dem buffer
		 */
		buffer = ByteBuffer.wrap(received_data, 0, n);
		//System.out.println("data has been read:" + n);
		/**
		 * Gibt den Buffer zur�ck.
		 */
		return buffer;
	}

}

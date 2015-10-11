package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import gui.GUI;
import inout.Protocol;
import Packet.CommandPacket;
import Packet.Packet;

import in.Demux;
import in.Receiver;
import out.Mux;

/**
 * Diese Klasse ist für das Empfangen der Daten auf dem Server zuständig.
 */
public class ClientHandler extends Thread {
	/**
	 * imei	Die IMEI des Gerätes
	 */
	private String imei;
	/**
	 * clientSocket Der Socket des Gerätes mit der IMEI
	 */
	private Socket clientSocket;
	/**
	 * receiver	Der Empfänger der Daten
	 */
	private Receiver receiver;
	/**
	 * server	Der Server
	 */
	private Server server;
	/**
	 * demux	Der Demultiplexer
	 */
	private Demux demux;
	/**
	 * mux	Der Multiplexer
	 */
	private Mux mux;
	/**
	 * buffer	In diesem Buffer werden die Daten gespeichert.
	 */
	private ByteBuffer buffer;
	/**
	 * connected	Hier wird festgehalten ob eine Verbindung zum Client besteht.
	 */
	private boolean connected;
	/**
	 * mainGUI	Die Gui
	 */
	private GUI mainGUI;

	/**
	 * Der Konstruktor weist den Klassenvariablen die übergebenen Daten zu.
	 * Zusätzlich werden mit den übergebenen Daten auch die benötigten Objekte erstellt.
	 * @param your_socket	Der Socket
	 * @param id	Die id
	 * @param s	Der Server
	 * @param mainGUI	Die HUI
	 * @throws IOException
	 */
	public ClientHandler(Socket your_socket, String id, Server s, GUI mainGUI)
			throws IOException {
		this.mainGUI = mainGUI;
		server = s;
		imei = id;
		clientSocket = your_socket;
		receiver = new Receiver(clientSocket);
		demux = new Demux(server, imei);
		mux = new Mux(new DataOutputStream(clientSocket.getOutputStream()));
		connected = true;
		buffer = ByteBuffer.allocate(Protocol.MAX_PACKET_SIZE);
		buffer.clear();

	}

	// attend des donn�es du Receiver et les transmet au Demultiplexeur

	/**
	 * Diese Methode empfängt die Daten und übergibt diese dem Demultiplexer. Zusätzlich werden hier verschiedene Fehler abgefangen,
	 * die dazu führen, dass die Verbindung getrennt wird.
	 */
	public void run() {
		/**
		 * Die Schleife ist solange aktiv wie es eine bestehende Verbindung gibt.
		 */
		while (connected) {

			try {
				
			     //System.out.println("");
				
				//buffer = receiver.read();
				/**
				 * Speichert die Daten des Receivers in dem Buffer
				 */
			     buffer = receiver.read(buffer);
			     
				try {
					if (demux.receive(buffer)) {
						//System.out.println("Restant: "+buffer.remaining()+" Position: "+buffer.position()+" Limit: "+buffer.limit());
						buffer.compact();
					}
				} catch (Exception e) {
					/**
					 * Es gab ein Fehler bei dem Demultiplexen. Daher wird eine Lognachricht erstellt und die Schleife beendet.
					 */
					connected = false;
					/*
					connected = false;
					try {
						clientSocket.close();
						mainGUI.deleteUser(imei);
						
					} catch (IOException e1) {
					}*/
					server.getGui().logErrTxt("ERROR: while deconding received stream (Demux) : "+e.getCause());
				}

			}
			catch (IOException e) {
				/**
				 * Beenden der Schleife
				 */
				connected = false;
				try {
					/**
					 * Der Socket wird geschlossen und der User mit der IMEI von der GUI gelöscht.
					 */
					clientSocket.close();
					mainGUI.deleteUser(imei);
				} catch (IOException e1) {
					server.getGui().logErrTxt("ERROR: while reading from a socket(Receiver)");
				}
			}
			catch(IndexOutOfBoundsException e) {
				/**
				 * Lognachricht erstellen und die Schleife beenden
				 */
				server.getGui().logErrTxt("Client ended gently !");
				connected = false;
				try {
					/**
					 * Socket wird geschlossen und das Gerät von der Gui entfernt.
					 */
					clientSocket.close();
					mainGUI.deleteUser(imei);
				} catch (IOException e1) {
					server.getGui().logErrTxt("Cannot close socket when socket client closed it before");
				}
			}
		}
		/**
		 * Der Handler wird gelöscht.
		 */
		server.DeleteClientHandler(imei);
	}

	// transmet les donn�es � envoyer au Multiplexeur

	/**
	 * Diese Methode erstellt ein neues CommandPacket und gibt dies der Klasse mux zum Versenden.
	 * @param command	Der Befehl
	 * @param channel	Der Kanal
	 * @param args	Die Argumente
	 */
	public void toMux(short command, int channel, byte[] args) {
		Packet packet = new CommandPacket(command, channel, args);
		mux.send(0, packet.build());

		//server.getGui().logTxt("Request sent :" + command + ",on the channel "+ channel);


	}

	/**
	 * Eine neue IMEI setzen.
	 * @param i	Die neue IMEI
	 */
	public void updateIMEI(String i) {
		imei = i;
		demux.setImei(imei);
	}

}

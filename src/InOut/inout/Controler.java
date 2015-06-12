package inout;

import Packet.TransportPacket;

/**
 * Ein Interface, welches von der Klasse Server benutzt wird.
 */
public interface Controler {

	public void Storage(TransportPacket tp, String i);
}

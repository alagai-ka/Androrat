package handler;


import server.Server;
import inout.Controler;
import Packet.Packet;

/**
 * Diese ist das Interface für alle Handler des Servers
 */
public interface PacketHandler 
{
   public void receive(Packet p,String imei);

   public void handlePacket(Packet p, String temp_imei, Server c);

}

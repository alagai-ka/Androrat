package my.app.client;

import android.os.AsyncTask;
import inout.Controler;
import out.Connection;

import java.nio.ByteBuffer;

/**
 * Diese Klasse wird benötigt um den Verbindungsaufbau in einem eigenen Task zu realisieren.
 */
public class AsyncConnection extends AsyncTask<Void,Void,Void>{
    Connection conn;
    String ip;
    int port;
    Controler ctrl;
    boolean done = false;
    boolean connected = false;

    /**
     * Der Konstruktor erhält die Daten, welche für den Verbindungaufbau nötig sind und speichert sie in den Klassenvariablen.
     * @param ip    Die IP
     * @param port  Der Port
     * @param ctrl  Der Controller
     */
    public AsyncConnection(String ip, int port, Controler ctrl){
        this.ip = ip;
        this.port=port;
        this.ctrl=ctrl;
    }

    /**
     * Diese Methode erstellt eine Objekt der Klasse Connection und baut dann eine Verbindung auf. Dies geschieht allerdings in einem eigenen Task.
     * @param voids Keine Parameter
     * @return  Es wird nichts zurückgegeben
     */
    @Override
    protected Void doInBackground(Void... voids) {

        conn = new Connection(ip,port,ctrl);
        connected = conn.connect();
        done = true;
        return null;
    }


    /**
     * Diese Methode ruft die getInstruction Methode der Klasse Connection auf.
     * @return  Die Instruktionen
     * @throws Exception
     */
    public ByteBuffer getInstruction() throws Exception{
        ByteBuffer read;
        read = conn.getInstruction();
        return read;
    }

    /**
     * Diese Methode ruft die sendData Methode der Klasse Connection auf.
     * @param chan  Der Datenkanal
     * @param packet Das zu sendende Paket
     */
    public void sendData(int chan, byte[] packet){
        conn.sendData(chan,packet);
    }

    /**
     * Diese Methode ruft die stop-Methode der Klasse Connection auf.
     */
    public void stop() {
        conn.stop();
    }

    /**
     * Diese Methode gibt, wenn der Task fertig ausgeführt wurde, zurück ob der Verbindungsaufbau erfolgreich war oder nicht.
     * @return  true wenn der Verbindungsaufbau erfolgreich war, false sonst.
     */
    public boolean returnResult(){
        while(!done){}
        return connected;
    }
}

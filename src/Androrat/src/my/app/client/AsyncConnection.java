package my.app.client;

import android.os.AsyncTask;
import inout.Controler;
import out.Connection;

import java.nio.ByteBuffer;

public class AsyncConnection extends AsyncTask<Void,Void,Void>{
    Connection conn;
    String ip;
    int port;
    Controler ctrl;
    boolean done = false;
    boolean connected = false;

    public AsyncConnection(String ip, int port, Controler ctrl){
        this.ip = ip;
        this.port=port;
        this.ctrl=ctrl;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        conn = new Connection(ip,port,ctrl);
        connected = conn.connect();
        done = true;
        return null;
    }



    public ByteBuffer getInstruction() throws Exception{
        ByteBuffer read;
        read = conn.getInstruction();
        return read;
    }

    public void sendData(int chan, byte[] packet){
        conn.sendData(chan,packet);
    }
    public void stop() {
        conn.stop();
    }
    public boolean returnResult(){
        while(!done){}
        return connected;
    }
}

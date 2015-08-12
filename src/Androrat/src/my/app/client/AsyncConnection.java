package my.app.client;

import android.os.AsyncTask;
import inout.Controler;
import out.Connection;

import java.nio.ByteBuffer;

public class AsyncConnection extends AsyncTask<Void,Void,Boolean>{
    Connection conn;
    String ip;
    int port;
    Controler ctrl;

    public AsyncConnection(String ip, int port, Controler ctrl){
        this.ip = ip;
        this.port=port;
        this.ctrl=ctrl;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean connected;
        conn = new Connection(ip,port,ctrl);
        connected = conn.connect();
        return connected;
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
}

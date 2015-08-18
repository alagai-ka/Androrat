package my.app.Library;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import my.app.client.ClientListener;


public class Torch {
    private Camera camera;
    boolean flashExists;
    boolean on;
    Parameters params;
    ClientListener ctx;

    public Torch(ClientListener c){
        ctx = c;
    }
    public boolean turnOnFlash(){
        flashExists = ctx.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!flashExists) {
            return false;
        } else {
            camera = Camera.open();
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            on = true;
        }
        return true;
    }
    public void turnOffFlash(){
        if(on) {
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            camera.release();
        }
    }

}

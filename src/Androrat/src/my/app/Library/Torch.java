package my.app.Library;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import my.app.client.ClientListener;

/**
 * Diese Klasse ist zum Erhalten der Kontrolle über das Blitzlicht vorhanden.
 */
public class Torch {
    private Camera camera;
    boolean flashExists;
    boolean on;
    Parameters params;
    ClientListener ctx;

    /**
     * Der Konstruktor erhält den Client und speichert ihn in der Klassenvariable.
     * @param c
     */
    public Torch(ClientListener c){
        ctx = c;
    }

    /**
     * Mit dieser Methode wird das Blitzlicht aktiviert.
     * @return  true wenn ein Blitzlicht existiert, false sonst.
     */
    public boolean turnOnFlash(){
        /**
         * Überprüfen ob ein Blitzlicht existert.
         */
        flashExists = ctx.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!flashExists) {
            return false;
        } else {
            /**
             * Ist dies der Fall so wird ein Kameraobjekt erstellt und diesem der Parameter FLASH_MODE_TORSCH übergeben und das Blitzlicht mit der Methode startPreview gestartet.
             */
            camera = Camera.open();
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            on = true;
        }
        return true;
    }

    /**
     * Diese Methode ist zum Ausschalte des Blitzlichts vorhanden.
     */
    public void turnOffFlash(){
        if(on) {
            /**
             * Nimmt die vorhin erstellte Kamera und übergibt ihr den Parameter FLASH_MODE_OFF . Im Anschluss wird die Kamera beedent und gelöscht.
             */
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            camera.release();
        }
    }

}

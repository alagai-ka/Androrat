package my.app.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 *Diese Klasse empf�ngt Intents die den String alarm_message enthalten und verarbeitet diese.
 * Es handelt sich um einen BroadcastReceiver
 */
public class AlarmListener extends BroadcastReceiver {
	/**
	 * Der String TAG ist ein einfacher Identifizierer der Klasse
	 */
	public final String TAG = AlarmListener.class.getSimpleName();
	
    @Override
	/**
	 * Wird aufgerufen wenn ein Intent ermpfangen wird.
	 * @param context Kontext
	 * @param intent Der empfangene Intent
	 */
    public void onReceive(Context context, Intent intent) {
		/**
		 * Dem Log den Tag und die Nachricht Alarm received! hinzuf�gen zu Testzwecken.
		 */
    	Log.d(TAG, "Alarm received !");
		/**
		 * Versuch den Intent zu verarbeiten sollte dies nicht Funktioniern wird die Exception abgefangen und dem Log eine Nachricht hinzugef�gt.
		 */
	   try {
		   /**
			* Die Extras aus dem Intent abrufen und in der Variablen bundle speichern.
			* Den String des Felds "alarm_message" aus dem bundle extrahieren und diesen in dem String message speichern
			*/
			Bundle bundle = intent.getExtras();
			String message = bundle.getString("alarm_message");
		   /**
			* �berpr�fen ob der String leer ist, sollte diese der Fall sein so handelt es sich nicht um einen Alarm
			*/
			if(message != null) {
				/**
				 * Dem Log zu Testzwecken den Tag und die Nachtricht hinzuf�gen
				 */
				Log.i(TAG, "Message received: "+message);
				/**
				 * Neuer Intent f�r die Klasse Client erstellen und die Action auf den Identifikator der Klasse setzen.
				 * Im Anschluss wird er Service Client gestartet.
				 */
				Intent serviceIntent = new Intent(context, Client.class);
				serviceIntent.setAction(AlarmListener.class.getSimpleName());//By this way the Client will know that it was AlarmListener that launched it
				context.startService(serviceIntent);
				
			}
			//Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(TAG, "Error in Alarm received !"+ e.getMessage());
	   }
    }
}
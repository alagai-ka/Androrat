package my.app.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Diese Klasse startet den Client Service wenn das Gerät gebootet wurde. Es ist als dafür da, die Anwendung auch nach dem Booten des Gerätes automatisch zu starten.
 * Zustäzlich handelt es sich bei dieser Klasse um einen BroadcastReceiver. Diese Komponenten empfängt Broadcast und verarbeitet diese.
 */

public class BootReceiver extends BroadcastReceiver {
	/**
	 * Der String TAG enhält den simpleName der Klasse BootReceiver. Diese identifiziert die Klasse.
	 */
	public final String TAG = BootReceiver.class.getSimpleName();

	/**
	 * Diese Funktion wird aufgerufen wenn der BroadcastReceiver ein Intend empfängt.
	 * @param context Kontext der Anwendung
	 * @param intent Der empfangene Intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		/**
		 * Für Testzwecke wird dem Log ein String übergeben um anzuzeigen, dass der Boot Intent empfangen wurde.
		 */
		Log.i(TAG,"BOOT Complete received by Client !");
		/**
		 * Extrahiert aus dem Intent dessen Action und speichert diese in dem String action.
		 */
		String action = intent.getAction();
		/**
		 * Überprüft ob der String action der Selbe ist wie der String "Intent.ACTION_BOOT_COMPLETED". Dieser Intent wird gesendet sobald das Gerät gebootet wurde.
		 * Sollte das Gerät gebootet sein, so wird ein neuer Intent erstellt, der an die Klasse Client gesendet wird und diese somit startet.
		 * Zusätzlich wird dem Intent als Action der Name des BroadcastReceiver übergeben. Somit ist es möglich festzustellen von welcher Klasse der Intent gesendet wurde.
		 */
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)) { //android.intent.action.BOOT_COMPLETED
			Intent serviceIntent = new Intent(context, Client.class);
			serviceIntent.setAction(BootReceiver.class.getSimpleName());
			//context.startService(serviceIntent);
			context.startActivity(serviceIntent);
		}
	}

}
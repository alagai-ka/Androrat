package my.app.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Diese Klasse startet den Client Service wenn das Ger�t gebootet wurde. Es ist als daf�r da, die Anwendung auch nach dem Booten des Ger�tes automatisch zu starten.
 * Zust�zlich handelt es sich bei dieser Klasse um einen BroadcastReceiver. Diese Komponenten empf�ngt Broadcast und verarbeitet diese.
 */

public class BootReceiver extends BroadcastReceiver {
	/**
	 * Der String TAG enh�lt den simpleName der Klasse BootReceiver. Diese identifiziert die Klasse.
	 */
	public final String TAG = BootReceiver.class.getSimpleName();

	/**
	 * Diese Funktion wird aufgerufen wenn der BroadcastReceiver ein Intend empf�ngt.
	 * @param context Kontext der Anwendung
	 * @param intent Der empfangene Intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		/**
		 * F�r Testzwecke wird dem Log ein String �bergeben um anzuzeigen, dass der Boot Intent empfangen wurde.
		 */
		Log.i(TAG,"BOOT Complete received by Client !");
		/**
		 * Extrahiert aus dem Intent dessen Action und speichert diese in dem String action.
		 */
		String action = intent.getAction();
		/**
		 * �berpr�ft ob der String action der Selbe ist wie der String "Intent.ACTION_BOOT_COMPLETED". Dieser Intent wird gesendet sobald das Ger�t gebootet wurde.
		 * Sollte das Ger�t gebootet sein, so wird ein neuer Intent erstellt, der an die Klasse Client gesendet wird und diese somit startet.
		 * Zus�tzlich wird dem Intent als Action der Name des BroadcastReceiver �bergeben. Somit ist es m�glich festzustellen von welcher Klasse der Intent gesendet wurde.
		 */
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)) { //android.intent.action.BOOT_COMPLETED
			Intent serviceIntent = new Intent(context, Client.class);
			serviceIntent.setAction(BootReceiver.class.getSimpleName());
			//context.startService(serviceIntent);
			context.startActivity(serviceIntent);
		}
	}

}
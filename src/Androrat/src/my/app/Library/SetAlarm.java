package my.app.Library;


import android.content.Intent;
import android.provider.AlarmClock;
import my.app.client.ClientListener;

/**
 * Klasse zum Erstellen eines Alarms.
 */
public class SetAlarm {
    int hour;
    int minute;
    ClientListener c;

    /**
     * Der Konstruktor
     * @param cl Der Client
     */
    public SetAlarm(ClientListener cl){
        c = cl;
    }

    /**
     * Diese Methode erstellt ein Intent um den Alarm zu erstellen.
     * @param args  Die Uhrzeit des Alarms
     */
    public void SetAlarm(byte[] args){
        /**
         * Die Uhrzeit des Alarms
         */
        hour = args[0];
        minute = args[1];
        /**
         * AlarmClock Intent erstellen
         */
        Intent setAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        /**
         * Die Uhrzeit übergeben.
         */
        setAlarm.putExtra(AlarmClock.EXTRA_HOUR,hour);
        setAlarm.putExtra(AlarmClock.EXTRA_MINUTES,minute);
        setAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /**
         * Den Intent versenden.
         */
        c.startActivity(setAlarm);
    }
}

package my.app.Library;


import android.content.Intent;
import android.provider.AlarmClock;
import my.app.client.ClientListener;

public class SetAlarm {
    int hour;
    int minute;
    ClientListener c;

    public SetAlarm(ClientListener cl){
        c = cl;
    }

    public void SetAlarm(byte[] args){
        hour = args[0];
        minute = args[1];
        Intent setAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        setAlarm.putExtra(AlarmClock.EXTRA_HOUR,hour);
        setAlarm.putExtra(AlarmClock.EXTRA_MINUTES,minute);
        setAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(setAlarm);
    }
}

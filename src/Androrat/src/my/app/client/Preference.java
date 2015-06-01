package my.app.client;

import android.os.Bundle;

import android.preference.PreferenceActivity;


/**
 * Der Sinn dieser Klasse ist nicht eindeutig, da bisher noch kein Intent gefunden wurde, der diese Activity startet.
 * Vom Namen her und dem zugrunde liegenden Layout vermute ich, dass es sich hierbei ursprünglich um ein Einstellungsmenü handeln sollte
 */
public class Preference extends PreferenceActivity {

 


 
    @Override
    /**
     * Wird aufgerufen wenn die Activity gestartet wird.
     * @param savedInstanceState Gespeicherter Zustand
     */
    public void onCreate(Bundle savedInstanceState) {
        /**
         * Stellt den gespeicherten Zustand wieder her und lädt die in xml.preferences fesgelegten Elemente.
         */
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);

    }

}

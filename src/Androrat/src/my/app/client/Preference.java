package my.app.client;

import android.os.Bundle;

import android.preference.PreferenceActivity;


/**
 * Der Sinn dieser Klasse ist nicht eindeutig, da bisher noch kein Intent gefunden wurde, der diese Activity startet.
 * Es werden jedoch die Daten die in der xml.preferences Datei gespeichert wurden wieder hergestellt.
 * Dabei handelt es sich um die IP, den Port, die Schlüsselwörter, die Nummern für SMS und Telefonie und den waitTrigger.
 */
public class Preference extends PreferenceActivity {

 


 
    @Override
    /**
     * Wird aufgerufen wenn die Activity gestartet wird.
     * @param savedInstanceState Gespeicherter Zustand
     */
    public void onCreate(Bundle savedInstanceState) {
        /**
         * Stellt den gespeicherten Zustand wieder her und lädt die in xml.preferences gespeicherten Elemente
         */
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);

    }

}

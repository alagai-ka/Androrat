package my.app.client;

import android.os.Bundle;

import android.preference.PreferenceActivity;


/**
 * Der Sinn dieser Klasse ist nicht eindeutig, da der einzige Intent der diese Klasse aktivieren könnte, in der Klasse Client auskommentiert ist.
 * Es werden jedoch die Daten die in der xml.preferences Datei gespeichert wurden wieder hergestellt.
 * Dabei handelt es sich um die IP, den Port, die Schlüsselwörter, die Nummern für SMS und Telefonie und den waitTrigger.
 */
public class Preference extends PreferenceActivity {




    /**
     * Wird aufgerufen wenn die Activity gestartet wird.
     * @param savedInstanceState Gespeicherter Zustand
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /**
         * Stellt den gespeicherten Zustand wieder her und lädt die in xml.preferences gespeicherten Elemente
         */
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);

    }

}

package my.app.client;

import my.app.client.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * Der Sinn dieser Klasse ist nicht offensichtlich. Es scheint als sei dies urspürlich als MainKlasse angedacht worden, wurde jedoch im nachhinein durch die LauncherActivity ersetzt.
 * Diese Klasse wird nie aufgerufen und hat effektiv keine Funktion. Daher denke ich es wurde lediglich vergessen diese Datei zu entfernen.
 *
 */
public class AndroratActivity extends Activity {
    /** Diese Funktion wir aufgerufen wenn die Activtiy erstellt wird. Hier soll dann das Layout auf das R.layout.main gesetzt werden und zustätzlich, falls der Zustand gespeichtert wurde dieser wiederhergestellt.
     * Allerdings werden hier die Funtkionen für die im layout.main festgelegten Buttons und TextEditfelder nicht deklariert. Daher war der Schluss naheliegend, dass diese Activtiy keine Funktion hat.*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }
}
package my.app.client;

import my.app.client.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Diese ist die main Klasse und die Klasse, welche aufgerufen wird sollte die Anwendung gestartet werden.
 * Dies wird im AndroidManifest.xml festgellegt.
 */
public class LauncherActivity extends Activity {
    /**
     * Client Der Intent ,welcher später an die Klasse Client gesendet wird und diese startet
     * btnStart Start-Button
     * btnStop Stopp-Button
     * ipfield Textfeld für die IP
     * portfield Textfeld für den Port
      */
	Intent Client, ClientAlt;
	Button btnStart, btnStop;
	EditText ipfield, portfield;
    @Override
    /**
     * Diese Funktion wir beim Starten der Activity aufgerufen.
     * @param savedInstanceState Sollte die Activity pausiert werden, wird in diesem Bundle der Zustand gespeichert.
     */
    public void onCreate(Bundle savedInstanceState) {
        /**
         * Sollte es einen gespeicherten Zustand geben wird dieser wirderhergestellt.
         * Im Anschluss wird das Layout auf das unter R.layout.main definierte Layout gesetzt.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /**
         * Erstellen eines Intents für die Client-Klasse. Zudem wird zur Identifizierung des Senders, die Action des Intents auf dem Klassennamen gesetzt.
         */
        Client = new Intent(this, Client.class);
        Client.setAction(LauncherActivity.class.getName());
        /**
         * Hier werden die Buttons erstellt gemäß den in layout.main definierten Buttons.
         * Außerdem werden hier ebenfalls die EditText Felder erstellt.
         */
        btnStart = (Button) findViewById(R.id.buttonstart);
        btnStop = (Button) findViewById(R.id.buttonstop);
        ipfield = (EditText) findViewById(R.id.ipfield);
        portfield = (EditText) findViewById(R.id.portfield);
        /**
         * Diese Funktion wir aufgerufen wenn auf den btnStart geklickt wird
         */
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /**
                 * Sollte der Button gedrückt werden, so wird dem Intent der Inhalt des IP-Felds und des Port-Felds übergeben.
                 * Danach wird der Intent gesendet und somit der Service Client gestartet.
                 * Zustäzlich wird der btnStart deaktiviert und der btnStop aktiviert.
                 */
            	Client.putExtra("IP", ipfield.getText().toString());
            	Client.putExtra("PORT", new Integer(portfield.getText().toString()));
                startService(Client);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                //finish();                
            }
        });
        /**
         * Diese Funktion wird aufgerufen wenn auf den btnStop geklicjt wird
         */
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /**
                 * Sollte dies geschehen so wird der Service Client gestoppt.
                 * Außerdem wird der btnStart aktiviert und der btnStop deaktiviert
                 */
                stopService(Client);  
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                //finish(); 
            }
        });
    }
}
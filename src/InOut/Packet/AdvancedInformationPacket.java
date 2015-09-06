package Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Dieses Paket ist dazu da die Daten der Klasse AdvancedSystemInfo zu speichern und zu übertragen.
 */
public class AdvancedInformationPacket implements Packet, Serializable{

	private static final long serialVersionUID = 44346671562310318L;
	/**
	 * phoneNumber	Telefonnummer
	 */
	String phoneNumber;
	/**
	 * IMEI	Die IMEI ist die eindeutige Gerätenummer zur Identifizierung von GMS- und UTMS-Endgeräten
	 */
	String IMEI;
	/**
	 * softwareVersion	Die Version der auf dem Gerät befindlichen Software
	 */
	String softwareVersion;
	/**
	 * countyCode	Länderkennung
	 */
	String countryCode;
	/**
	 * operatorCode	Anbieterkennung
	 */
	String operatorCode;
	/**
	 * operatorName	Anbieterbezeichnung
	 */
	String operatorName;
	/**
	 * simOperatorCode	Kennung des Simkarten Anbieters
	 */
	String simOperatorCode;
	/**
	 * simOperatorName	Name des Simkarten Anbiertes
	 */
	String simOperatorName;
	/**
	 * simCountryCode	Länderkennung der Simkarte
	 */
	String simCountryCode;
	/**
	 * simSerial	Seriennummer des Simkarte
	 */
	String simSerial;

	/**
	 * wifiAviable	Gibt es ein Wifinetz
	 */
	boolean wifiAvailable;
	/**
	 * wifiConnectedOrConnecting	Ist das Gerät zu diesem WIFI-Netz verbunden oder verbindet es sich gerade dazu
	 */
	boolean wifiConnectedOrConnecting;
	/**
	 * wifiExtraInfos	zusätzliche Informationen wie die SSID
	 */
	String wifiExtraInfos;
	/**
	 * wifiReason	Der Grund warum ein Verbindungsversuch fehlgeschlagen ist
	 */
	String wifiReason;

	/**
	 * mobileNetworkName	Der Name des mobile Netzwerks
	 */
	String mobileNetworkName;
	/**
	 * mobileNetworkAviable	Ist diese Netzwerk verfügbar
	 */
	boolean mobileNetworkAvailable;
	/**
	 * mobileNetworkConnectedOrConnecting	Ist das Grät mit dem Netzwerk verbunden oder verbindet es sich gerade
	 */
	boolean mobileNetworkConnectedOrConnecting;
	/**
	 * mobileNwtworkExtraInfos	Zusätzliche Infomrationen über den Netzwerkstatus
	 */
	String mobileNetworkExtraInfos;
	/**
	 * mobileNetworkReasons	Gründe warum ein Verbindungsversuch fehlgeschlagen ist
	 */
	String mobileNetworkReason;

	/**
	 * androidVersion	Die Version des auf dem Gerät installierten Android
	 */
	String androidVersion;
	/**
	 * androidSdk	Das SDK-Level welches auf dem Gerät vorhanden ist
	 */
	int androidSdk;

	/**
	 * sensors	Liste alles verbauten Sensoren
	 */
	ArrayList<String> sensors;

	/**
	 * batteryHealth	Der Zustand der Batterie
	 */
	int batteryHealth;
	/**
	 * batteryLevel	Das Ladungslevel der Batterie
	 */
	int batteryLevel;
	/**
	 * batteryPlugged	Indikator ob die Batterie geladen wird oder nicht
	 */
	int batteryPlugged;
	/**
	 * batteryPresent	Indikator ob die Batterie vorhanden ist
	 */
	boolean batteryPresent;
	/**
	 * batteryScale	Zeigt das maximale Level der Batterieladung
	 */
	int batteryScale;
	/**
	 * batteryStatus	Status der Batterie
	 */
	int batteryStatus;
	/**
	 * batteryTechnology Beschreibt die Technologie der verbauten Batterie
	 */
	String batteryTechnology;
	/**
	 * batteryTemperature	Die Temperatur der Batterie
	 */
	int batteryTemperature;
	/**
	 * batteryVoltage	Das Voltlevelt der Batterie
	 */
	int batteryVoltage;
	boolean rooted;

	/**
	 * Erstellt aus den Daten dieses Pakets ein byte_Array
	 * @return	byte_Array das die Daten des Paketes enhält
	 */
	public byte[] build() {
		try {
			/**
			 * Neuer ByteArrayOutputStream bos
			 */
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			/**
			 * Ein ObjektOutputStream out
			 */
			ObjectOutputStream out = new ObjectOutputStream(bos);
			/**
			 * Hier werden die Daten des Pakets auf den Stream geschrieben
			 */
			out.writeObject(this);
			/**
			 * Hier werden die Daten des Streams in ein byte-Array umgewandelt und zurückgegeben
			 */
			return bos.toByteArray();
		} catch (IOException e) {
			/**
			 * Sollte eine Ausnahme geworfen werden so wird null zurückgegeben.
			 */
			return null;
		}
	}

	/**
	 * Mit dieser Methode werden dei Klassenvariablen mit den Daten aus dem byte-Array befüllt.
	 * @param packet Ein Paket mit den Daten des byte-Arrays
	 */
	public void parse(byte[] packet) {
		/**
		 * Neuer InputStream des Packets
		 */
		ByteArrayInputStream bis = new ByteArrayInputStream(packet);
		ObjectInputStream in;
		try {
			/**
			 * Hier wird ein neuer OBjectInputStream erzeugt.
			 */
			in = new ObjectInputStream(bis);
			/**
			 * Dieser wird benutzt um ein AdvancedInfroamtionPacket zu erzeugen
			 */
			AdvancedInformationPacket adv = (AdvancedInformationPacket) in.readObject();
			/**
			 * Im Anschluss werden die Klassenvariablen mit den entsprechenden Daten befüllt.
			 */
			setPhoneNumber(adv.getPhoneNumber());
			setIMEI(adv.getIMEI());
			setSoftwareVersion(adv.getSoftwareVersion());
			setCountryCode(adv.getCountryCode());
			setOperatorCode(adv.getOperatorCode());
			setOperatorName(adv.getOperatorName());
			setSimOperatorCode(adv.getSimOperatorCode());
			setSimOperatorName(adv.getSimOperatorName());
			setSimCountryCode(adv.getSimCountryCode());
			setSimSerial(adv.getSimSerial());
			setWifiAvailable(adv.isWifiAvailable());
			setWifiConnectedOrConnecting(adv.isWifiConnectedOrConnecting());
			setWifiExtraInfos(adv.getWifiExtraInfos());
			setWifiReason(adv.getWifiReason());
			setMobileNetworkName(adv.getMobileNetworkName());
			setMobileNetworkAvailable(adv.isMobileNetworkAvailable());
			setMobileNetworkConnectedOrConnecting(adv.isMobileNetworkConnectedOrConnecting());
			setMobileNetworkExtraInfos(adv.getMobileNetworkExtraInfos());
			setMobileNetworkReason(adv.getMobileNetworkReason());
			setAndroidVersion(adv.getAndroidVersion());
			setAndroidSdk(adv.getAndroidSdk());
			setSensors(adv.getSensors());
			setBatteryHealth(adv.getBatteryHealth());
			setBatteryLevel(adv.getBatteryLevel());
			setBatteryPlugged(adv.getBatteryPlugged());
			setBatteryPresent(adv.isBatteryPresent());
			setBatteryScale(adv.getBatteryScale());
			setBatteryStatus(adv.getBatteryStatus());
			setBatteryTechnology(adv.getBatteryTechnology());
			setBatteryTemperature(adv.getBatteryTemperature());
			setBatteryVoltage(adv.getBatteryVoltage());
			setRooted(adv.getRoot());
		} catch (Exception e) {
		}
	}

	/**
	 * Liefert die Telefonnummer
	 * @return	Telefonnummer
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Liedert die Länderkennung der Simkarte
	 * @return	Länderkennung der Simkarte
	 */
	public String getSimCountryCode() {
		return simCountryCode;
	}

	/**
	 * Setzt die Länderkennung der Simkarte
	 * @param code	Länderkennung
	 */
	public void setSimCountryCode(String code) {
		this.simCountryCode = code;
	}

	/**
	 * Setzt die Telefonnummer
	 * @param phoneNumber	Telefonnummer
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Liefert die IMEI des Geräts
	 * @return	IMEI
	 */
	public String getIMEI() {
		return IMEI;
	}

	/**
	 * Setzt die IMEI
	 * @param iMEI	IMEI
	 */
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	/**
	 * Liefert die Softwareversion
	 * @return	Softwareversoin
	 */
	public String getSoftwareVersion() {
		return softwareVersion;
	}

	/**
	 * Setzt die Softwareversion
	 * @param softwareVersion	Softwareversion
	 */
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	/**
	 * Liefert die Länderkennung
	 * @return	Länderkennung
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Setzt die Länderkennung
	 * @param countryCode	Länderkennung
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Liefer die Anbierterkennung
	 * @return	Anbierterkennung
	 */
	public String getOperatorCode() {
		return operatorCode;
	}

	/**
	 * Setzt die Anbiertekennung
	 * @param operatorCode	Anbiertekennung
	 */
	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	/**
	 * Liefert den Anbietername
	 * @return	Anbietername
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * Setzt den Anbietename
	 * @param operatorName	Anbietername
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * Liefert die Anbieterkennung der Simkarte
	 * @return Anbieterkennung der Simkarte
	 */
	public String getSimOperatorCode() {
		return simOperatorCode;
	}

	/**
	 * Setzt die Anbieterkennung der Simkarte
	 * @param simOperatorCode	Anbieterkennung der Simkarte
	 */
	public void setSimOperatorCode(String simOperatorCode) {
		this.simOperatorCode = simOperatorCode;
	}

	/**
	 * Liefert den Namen des Simkartenanbieters
	 * @return	Name des Simkartenanbieters
	 */
	public String getSimOperatorName() {
		return simOperatorName;
	}

	/**
	 * Setzt den Namen des Simkartenanbieters
	 * @param simOperatorName	Name des Simkartenanbieters
	 */
	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}

	/**
	 * Liefert die Seriennummer der Simkarte
	 * @return	Seriennummer der Simkarte
	 */
	public String getSimSerial() {
		return simSerial;
	}

	/**
	 * Setzt die Seriennummer der Simkarte
	 * @param simSerial	Seriennummer der Simkarte
	 */
	public void setSimSerial(String simSerial) {
		this.simSerial = simSerial;
	}

	/**
	 * Liefert ob ein Wifinetz vorhanden ist
	 * @return	Wifibetz vorhanden
	 */
	public boolean isWifiAvailable() {
		return wifiAvailable;
	}

	/**
	 * Setzt ob ein Wifinetz vorhanden ist
	 * @param wifiAvailable	Wifinetz vorhanden
	 */
	public void setWifiAvailable(boolean wifiAvailable) {
		this.wifiAvailable = wifiAvailable;
	}

	/**
	 * Liefert ob eine Wifinetzverbundung vorhanden ist
	 * @return	Wifinetzverbundung vorhanden
	 */
	public boolean isWifiConnectedOrConnecting() {
		return wifiConnectedOrConnecting;
	}

	/**
	 * Setzt ob eine Wifinetverbindung vorhanden ist
	 * @param wifiConnectedOrConnecting	Wifinetzverbindung vorhanden
	 */
	public void setWifiConnectedOrConnecting(boolean wifiConnectedOrConnecting) {
		this.wifiConnectedOrConnecting = wifiConnectedOrConnecting;
	}

	/**
	 * Liefert zusätzliche Informationen über das Wifi
	 * @return	zusätzliche Informationen über das Wifi
	 */
	public String getWifiExtraInfos() {
		return wifiExtraInfos;
	}

	/**
	 * Setzt zusätzliche Informationen über das Wifi
	 * @param wifiExtraInfos	zusätzliche Informationen über das Wifi
	 */
	public void setWifiExtraInfos(String wifiExtraInfos) {
		this.wifiExtraInfos = wifiExtraInfos;
	}

	/**
	 * Liefert den Grund warum keine Verbindung aufgebaut werden konnte
	 * @return	Grund warum keine Verbindung aufgebaut werden konnte
	 */
	public String getWifiReason() {
		return wifiReason;
	}

	/**
	 * Setzt den Grund warum keine Verbindung aufgebaut werden konnte
	 * @param wifiReason	Grund warum keine Verbindung aufgebaut werden konnte
	 */
	public void setWifiReason(String wifiReason) {
		this.wifiReason = wifiReason;
	}

	/**
	 * Liefert den Namen des mobilen Netzwerkes
	 * @return	Name des mobilen Netzwerkes
	 */
	public String getMobileNetworkName() {
		return mobileNetworkName;
	}

	/**
	 * Setzt den Namen des mobilen Netzwerkes
	 * @param mobileNetworkName	Namen des mobilen Netzwerkes
	 */
	public void setMobileNetworkName(String mobileNetworkName) {
		this.mobileNetworkName = mobileNetworkName;
	}

	/**
	 * Liefert ob ein mobiles Netzwerk vorhanden ist
	 * @return	mobiles Netzwerk vorhanden
	 */
	public boolean isMobileNetworkAvailable() {
		return mobileNetworkAvailable;
	}

	/**
	 * Setzt ob ein mobiles Netzwerk vorhanden ist
	 * @param mobileNetworkAvailable	mobiles Netzwerk vorhanden
	 */
	public void setMobileNetworkAvailable(boolean mobileNetworkAvailable) {
		this.mobileNetworkAvailable = mobileNetworkAvailable;
	}

	/**
	 * Liefert ob eine Verbindung zum Netzwerk besteht
	 * @return	Verbindung zum Netzwerk besteht
	 */
	public boolean isMobileNetworkConnectedOrConnecting() {
		return mobileNetworkConnectedOrConnecting;
	}

	/**
	 * Setzt ob eine Verbindung zum Netzwerk besteht
	 * @param mobileNetworkConnectedOrConnecting	Verbindung zum Netzwerk besteht
	 */
	public void setMobileNetworkConnectedOrConnecting(
			boolean mobileNetworkConnectedOrConnecting) {
		this.mobileNetworkConnectedOrConnecting = mobileNetworkConnectedOrConnecting;
	}

	/**
	 * Liefert zusätzliche Informationen über das Netzwerk
	 * @return	zusätzliche Informationen über das Netzwerk
	 */
	public String getMobileNetworkExtraInfos() {
		return mobileNetworkExtraInfos;
	}

	/**
	 * Setzt zusätzliche Informationen über das Netzwerk
	 * @param mobileNetworkExtraInfos	zusätzliche Informationen über das Netzwerk
	 */
	public void setMobileNetworkExtraInfos(String mobileNetworkExtraInfos) {
		this.mobileNetworkExtraInfos = mobileNetworkExtraInfos;
	}

	/**
	 * Liefert den Grund warum ein Verbindungversuch fehlgeschalgen ist
	 * @return	Grund warum ein Verbindungversuch fehlgeschalgen ist
	 */
	public String getMobileNetworkReason() {
		return mobileNetworkReason;
	}

	/**
	 * Setzt den Grund warum ein Verbindungversuch fehlgeschalgen ist
	 * @param mobileNetworkReason	Grund warum ein Verbindungversuch fehlgeschalgen ist
	 */
	public void setMobileNetworkReason(String mobileNetworkReason) {
		this.mobileNetworkReason = mobileNetworkReason;
	}

	/**
	 * Liefert die Androidversion
	 * @return	Androidversion
	 */
	public String getAndroidVersion() {
		return androidVersion;
	}

	/**
	 * Setzt die Androidversion
	 * @param androidVersion	Androidversion
	 */
	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	/**
	 * Liefert das SDK-Level
	 * @return	SDK-Level
	 */
	public int getAndroidSdk() {
		return androidSdk;
	}

	/**
	 * Setzt das SDK-Level
	 * @param androidSdk	SDK-Level
	 */
	public void setAndroidSdk(int androidSdk) {
		this.androidSdk = androidSdk;
	}

	/**
	 * Liefert die Sensorenliste
	 * @return	Sensorenliste
	 */
	public ArrayList<String> getSensors() {
		return sensors;
	}

	/**
	 * Setzt die Sensorenliste
	 * @param sensors	Sensorenliste
	 */
	public void setSensors(ArrayList<String> sensors) {
		this.sensors = sensors;
	}

	/**
	 * Liefert den Zustand der Batterie
	 * @return	Zustand der Batterie
	 */
	public int getBatteryHealth() {
		return batteryHealth;
	}

	/**
	 * Setzt den Zustand der Batterie
	 * @param batteryHealth	Zustand der Batterie
	 */
	public void setBatteryHealth(int batteryHealth) {
		this.batteryHealth = batteryHealth;
	}

	/**
	 * Liefert das Ladelevel der Batterie
	 * @return	Ladelevel der Batterie
	 */
	public int getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Setzt das Ladelevel der Batterie
	 * @param batteryLevel	Ladelevel der Batterie
	 */
	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	/**
	 * Liefert ob die Batterie am Kabel hängt
	 * @return	Batterie am Kabel
	 */
	public int getBatteryPlugged() {
		return batteryPlugged;
	}

	/**
	 * Setzt ob die Batterie am Kabel hängt
	 * @param batteryPlugged	Batterie am Kabel
	 */
	public void setBatteryPlugged(int batteryPlugged) {
		this.batteryPlugged = batteryPlugged;
	}

	/**
	 * Liefert ob eine Batterie vorhanden ist
	 * @return	Batterie vorhanden
	 */
	public boolean isBatteryPresent() {
		return batteryPresent;
	}

	/**
	 * Setzt ob eine Batterie vorhanden ist
	 * @param batteryPresent	Batterie vorhanden
	 */
	public void setBatteryPresent(boolean batteryPresent) {
		this.batteryPresent = batteryPresent;
	}

	/**
	 * Liefert das maimale Ladelevel der Battierie
	 * @return	maimale Ladelevel der Battierie
	 */
	public int getBatteryScale() {
		return batteryScale;
	}

	/**
	 * Setzt das maimale Ladelevel der Battierie
	 * @param batteryScale	maimale Ladelevel der Battierie
	 */
	public void setBatteryScale(int batteryScale) {
		this.batteryScale = batteryScale;
	}

	/**
	 * Liefert den Batteriestatus
	 * @return	Batteriestatus
	 */
	public int getBatteryStatus() {
		return batteryStatus;
	}

	/**
	 * Setzt den Batteriestatus
	 * @param batteryStatus	Batteriestatus
	 */
	public void setBatteryStatus(int batteryStatus) {
		this.batteryStatus = batteryStatus;
	}

	/**
	 * Liefert die Batterietechnologie
	 * @return Batterietechnologie
	 */
	public String getBatteryTechnology() {
		return batteryTechnology;
	}

	/**
	 * Setzt die Batterietechnologie
	 * @param batteryTechnology	Batterietechnologie
	 */
	public void setBatteryTechnology(String batteryTechnology) {
		this.batteryTechnology = batteryTechnology;
	}

	/**
	 * Liefert die Temperatur der Batterie
	 * @return	Temperatur der Batterie
	 */
	public int getBatteryTemperature() {
		return batteryTemperature;
	}

	/**
	 * Setzt die Temperatur der Batterie
	 * @param batteryTemperature	Temperatur der Batterie
	 */
	public void setBatteryTemperature(int batteryTemperature) {
		this.batteryTemperature = batteryTemperature;
	}

	/**
	 * Liefert das derzeitige Voltlevel der Batterie
	 * @return	Voltlevel der Batterie
	 */
	public int getBatteryVoltage() {
		return batteryVoltage;
	}

	/**
	 * Setzt das derzeitige Voltlevel der Batterie
	 * @param batteryVoltage	Voltlevel der Batterie
	 */
	public void setBatteryVoltage(int batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public void setRooted(boolean root){
		rooted = root;
	}
	public boolean getRoot(){
		return rooted;
	}

}

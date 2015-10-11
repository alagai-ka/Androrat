package gui;

import javax.swing.ImageIcon;

/*
public class User {
    
    private String imei;
    private String countryCode;
    private String telNumber;
    private String operator;
    private String simCountryCode;
    private String simOperator;
    private String simSerial;
    
    public User(String i, String cc, String tn, String op, String simcc, String simop, String simserial) {
        imei = i;
        countryCode = cc;
        telNumber = tn;
        operator = op;
        simCountryCode = simcc;
        simOperator = simop;
        simSerial = simserial;
    }
*/

/**
 * Diese Klasse ist zum Erstellen und Speichern der Daten eines Clients der sich zum Server verbunden hat.
 */
public class User {
    
    private String imei;
    private String countryCode;
    private String telNumber;
    private String operator;
    private String simCountryCode;
    private String simOperator;
    private String simSerial;
    private String image;

    /**
     * Dies ist der Konstruktor der Klasse, der alle �bergebenen Daten den Klassenvariablen zuweist.
     * @param img   Name des Bilddatei f�r das Land
     * @param i Die IMEI des Ger�tes
     * @param cc    Die L�nderkennung
     * @param tn    Die Telefonnummer
     * @param op    Der Anbieter
     * @param simcc Die L�nderkennung der Simkarte
     * @param simop Der Anbieter der Simkarte
     * @param simserial Die Seriennummer des Simkarte
     */
    public User(String img, String i, String cc, String tn, String op, String simcc, String simop, String simserial) {
    	image = img ;
        imei = i;
        countryCode = cc;
        telNumber = tn;
        operator = op;
        simCountryCode = simcc;
        simOperator = simop;
        simSerial = simserial;
    }

    /**
     * Gibt den String des Bildes zur�ck.
     * @return  Der Sting mit dem Namen des Bildes.
     */
	public String getImage()
	{
		return image;
	}

    /**
     * Bekommt ein String des Bildes und bef�llt damit die Klassenvariable.
     * @param image Name des Bildes
     */
	public void setImage(String image)
	{
		this.image = image;
	}

    /**
     * Bekommt die L�nderkennung und bef�llt mit den Daten die Klassenvariable.
     * @param countryCode   Die L�nderkennung
     */
	public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Bekommt die imei und speichert diese in der Klassenvariable.
     * @param imei  Die imei
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Setzt den Anbieter.
     * @param operator  Der Anbieter.
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Setzt die L�nderkennung der Simkarte.
     * @param simCountryCode    Die L�nderkennung der Simkarte
     */
    public void setSimCountryCode(String simCountryCode) {
        this.simCountryCode = simCountryCode;
    }

    /**
     * Setzt den Anbieter der Simkarte.
     * @param simOperator   Der Anbieter der Simkarte
     */
    public void setSimOperator(String simOperator) {
        this.simOperator = simOperator;
    }

    /**
     * Setzt die Seriennummer der Simkarte.
     * @param simSerial Die Seriennummer der Simkarte
     */
    public void setSimSerial(String simSerial) {
        this.simSerial = simSerial;
    }

    /**
     * Setzt die Telefonnummer.
     * @param telNumber Die Telefonnumer
     */
    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    /**
     * Liefert die L�nderkennung.
     * @return  Die L�nderkennung
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Liefert die IMEI.
     * @return  String der die IMEI enth�lt.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Liefert den Anbieter.
     * @return  Der Anbieter
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Liefert die L�nderkennung der Simkarte.
     * @return Die L�nderkennung der Simkarte
     */
    public String getSimCountryCode() {
        return simCountryCode;
    }

    /**
     * Liefert den Anbieter der Simkarte.
     * @return  Der Anbieter der Simkarte
     */
    public String getSimOperator() {
        return simOperator;
    }

    /**
     * Liefert die Seriennummer der Simkarte.
     * @return  Die Seriennummer der Simkarte
     */
    public String getSimSerial() {
        return simSerial;
    }

    /**
     * Liefert die Telefonnummer.
     * @return  Die Telefonnummer
     */
    public String getTelNumber() {
        return telNumber;
    }
    
}

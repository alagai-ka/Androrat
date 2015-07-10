package gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Diese Klasse ist zum Speichern und verwalten der unterschiedlichen User die sich zum Server verbunden haben da.
 */
public class UserModel extends AbstractTableModel {
    
	/*
    private final List<User> users = new ArrayList<User>();
    private final String[] headers = {"IMEI", "Localisation", "Num�ro tel", "Op�rateur", "Pays SIM", "Op�rateur SIM", "Serial SIM"};

    public UserModel() {
        super();
    }
    
    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return headers[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return users.get(rowIndex).getImei();
            case 1:
                return users.get(rowIndex).getCountryCode();
            case 2:
                return users.get(rowIndex).getTelNumber();
            case 3:
                return users.get(rowIndex).getOperator();
            case 4:
                return users.get(rowIndex).getSimCountryCode();
            case 5:
                return users.get(rowIndex).getSimOperator();
            case 6:
                return users.get(rowIndex).getSimSerial();
            default:
                return null;
        }
    }
    
    public void addUser(User user) {
        users.add(user);
        fireTableRowsInserted(users.size()-1, users.size()-1);
    }
    
    public void removeUser(String imei) {
        for(User user : users) {
            if(user.getImei().equals(imei)) {
                users.remove(user);
                return ;
            }
        }
    }
    */
    private final List<User> users = new ArrayList<User>();
    private final String[] headers = {"Flag","IMEI", "Location", "Phone Number", "Operator", "Country SIM", "Operator SIM", "Serial SIM"};

    /**
     * Der Konsturktor
     */
    public UserModel() {
        super();
    }

    /**
     * Liefert die Anzahl der User.
     * @return  Anzahl der User
     */
    @Override
    public int getRowCount() {
        return users.size();
    }

    /**
     * Liefert die Länge des Sting-Arrays headers.
     * @return  Die Länge des headers
     */
    @Override
    public int getColumnCount() {
        return headers.length;
    }

    /**
     * Liefert das Element des Array headers an der Stelle columnIndex.
     * @param columnIndex   Die Stelle
     * @return  Der Inhalt an der Stelle columnIndex
     */
    @Override
    public String getColumnName(int columnIndex) {
        return headers[columnIndex];
    }
/*
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
            	return users.get(rowIndex).getImei();
            case 1:
            	  return users.get(rowIndex).getCountryCode();
            case 2:
            	 return users.get(rowIndex).getTelNumber();
            case 3:
            	  return users.get(rowIndex).getOperator();
            case 4:
            	 return users.get(rowIndex).getSimCountryCode();
            case 5:
            	 return users.get(rowIndex).getSimOperator();
            case 6 :
            	return users.get(rowIndex).getSimSerial();
            default:
                return null;
        }
    }
    */
  //*

    /**
     * Liefet das Element an der Stelle rowIndex columnIndex des Arrays users.
     * @param rowIndex  Der Index der Zeile
     * @param columnIndex   Der Index der Spalte
     * @return  Den entsprechende Wert (Bild, Imei, Länderkennung, Telefonnummer, Anbieter, Länderkennung der Simkarte, Anbieter der Simkarte, Seriennummer der Simkarte)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    	if(rowIndex < users.size()) {
	        switch(columnIndex){
	            case 0:
	            	return users.get(rowIndex).getImage();
	            case 1:
	            	return users.get(rowIndex).getImei();
	            case 2:
	            	  return users.get(rowIndex).getCountryCode();
	            case 3:
	            	 return users.get(rowIndex).getTelNumber();
	            case 4:
	            	  return users.get(rowIndex).getOperator();
	            case 5:
	            	 return users.get(rowIndex).getSimCountryCode();
	            case 6:
	            	 return users.get(rowIndex).getSimOperator();
	            case 7 :
	            	return users.get(rowIndex).getSimSerial();
	            default:
	                return null;
	        }
    	} else return null;
    }

    /**
     * Fügt einen neuen User dem Array users hinzu.
     * @param user  Der neue User
     */
    public void addUser(User user) {
        users.add(user);
        fireTableRowsInserted(users.size()-1, users.size()-1);
    }

    /**
     * Entfernt den User mit der übergebenen IMEI aus dem users Array.
     * @param imei  Die IMEI des Benutzers der entfernt werden soll.
     */
    public void removeUser(String imei) {
        for(User user : users) {
            if(user.getImei().equals(imei)) {
                users.remove(user);
                return ;
            }
        }
    }
}

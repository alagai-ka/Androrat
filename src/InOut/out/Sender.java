package out;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Diese Methode ist zum Schreiben von Daten auf den DataOutputStream zuständig.
 */
public class Sender {
	/**
	 * out	Der DataOutputStream
	 */
	DataOutputStream out;

	/**
	 * Weist der Klassenvariable den übergebenen Stream zu.
	 * @param out	Der DataoutputStream
	 */
	public Sender(DataOutputStream out)
	{
		this.out = out ;
	}

	/**
	 * Versucht die übergebenen Daten auf den Stream zu schreiben.
	 * @param data	Die Daten
	 */
	public void send(byte[] data)
	{
		try
		{
			out.write(data);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

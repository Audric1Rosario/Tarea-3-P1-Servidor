package logical;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server extends Thread {
	public static Vector usuarios = new Vector();
	@SuppressWarnings("resource")
	@Override
	public void run() {
		ServerSocket sfd = null;
		try {
			sfd = new ServerSocket(7000);
		}
		catch (IOException ioe)
		{
			System.out.println("Comunicación rechazada."+ioe);
			System.exit(1);
		}

		while (true)
		{
			try
			{
				Socket nsfd = sfd.accept();
				System.out.println("Conexion aceptada de: "+nsfd.getInetAddress());
				Flow flujo = new Flow(nsfd);
				Thread t = new Thread(flujo);
				t.start();
			}
			catch(IOException ioe)
			{
				System.out.println("Error: "+ioe);
			}
		}
	}
	/*public static void main (String args[])
	{
		
	}*/
}

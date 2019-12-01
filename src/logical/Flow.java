package logical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;

public class Flow extends Thread {
	Socket nsfd;
	DataInputStream FlujoLectura;
	DataOutputStream FlujoEscritura;

	public Flow (Socket sfd)
	{
		nsfd = sfd;
		try
		{
			FlujoLectura = new DataInputStream(new BufferedInputStream(sfd.getInputStream()));
			FlujoEscritura = new DataOutputStream(new BufferedOutputStream(sfd.getOutputStream()));
		}
		catch(IOException ioe)
		{
			System.out.println("IOException(Flujo): "+ioe);
		}
	}

	public void run()
	{
		broadcast(nsfd.getInetAddress()+"> se ha conectado");
		Server.usuarios.add ((Object) this);
		while(true)
		{
			try
			{
				String linea = FlujoLectura.readUTF();
				if (!linea.equals(""))
				{
					linea = nsfd.getInetAddress() +"> "+ linea + " > El número " + linea + (esPrimo(Integer.valueOf(linea)) ? " es primo" : " no es primo, es un compuesto.");
					broadcast(linea);
				}
			}
			catch(IOException ioe)
			{
				Server.usuarios.removeElement(this);
				broadcast(nsfd.getInetAddress()+"> se ha desconectado");
				break;
			}
		}
	}

	private boolean esPrimo(int num) {
		boolean loEs = true;
		
		float raiz = (float)Math.sqrt((double)num);
		for (int i = 2; i <= raiz; i++) {
			if (num % i == 0) {
				loEs = false;
			}
		}
		
		return loEs;
	}
	
	public void broadcast(String mensaje)
	{
		synchronized (Server.usuarios)
		{
			Enumeration e = Server.usuarios.elements();
			while (e.hasMoreElements())
			{
				Flow f = (Flow) e.nextElement();
				try
				{
					synchronized(f.FlujoEscritura)
					{
						f.FlujoEscritura.writeUTF(mensaje);
						f.FlujoEscritura.flush();
					}
				}
				catch(IOException ioe)
				{
					System.out.println("Error: "+ioe);
				}
			}
		}
	}
}

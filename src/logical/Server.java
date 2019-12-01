package logical;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import visual.Servidor;

public class Server extends Thread {
	public static Vector usuarios = new Vector();
	public Server() {
		super();
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		ServerSocket sfd = null;
		try {
			sfd = new ServerSocket(15000);
		}
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Comunicación rechazada: "+ioe, "Error", JOptionPane.ERROR_MESSAGE);;
			System.exit(1);
		}

		// Esperar a ver si cierran el servidor.
		while (!Thread.currentThread().isInterrupted())
		{
			try
			{
				if (sfd != null) {
					Socket nsfd = sfd.accept();
					//Servidor.getTxtConexion().append("Conexion aceptada de: "+nsfd.getInetAddress());
					Flow flujo = new Flow(nsfd);
					Thread t = new Thread(flujo);
					t.start(); 
				}
			}
			catch(IOException ioe)
			{
				JOptionPane.showMessageDialog(null, "Error: "+ioe, "Error", JOptionPane.ERROR_MESSAGE);;
			}
		}

		// Cerrar socket
		try {
			sfd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Este broadcast solo se utilizara al cerrar. 
	// Así se notifica a las aplicaciones clientes que deben cerrar tambien.
	public void broadcast() {
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
						f.FlujoEscritura.writeUTF("Proceso terminado");
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

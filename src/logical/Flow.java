package logical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import visual.Servidor;

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
		catch(IOException ioe) {
			JOptionPane.showMessageDialog(null, "IOException(Flujo): "+ioe, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void run()
	{
		Servidor.getTxtConexion().append(nsfd.getInetAddress()+"> se ha conectado\n");
		Server.usuarios.add ((Object) this);
		while(true)
		{
			try
			{
				String linea = FlujoLectura.readUTF();
				if (!linea.equals(""))
				{
					//linea = nsfd.getInetAddress() +"> "+ linea + " > El número " + linea + (esPrimo(Integer.valueOf(linea)) ? " es primo" : " no es primo, es un compuesto.");
					int index = linea.indexOf('-');
					if (index != -1) {
						String nfactura = linea.substring(index - 1, linea.indexOf('\n', index - 1));
						Servidor.getTxtConexion().append(nsfd.getInetAddress() + "> Ha enviado: " + nfactura);
						// Crear directorio para las facturas.
						String path = System.getProperty("user.dir");
						path += "/data/facturas/" + nsfd.getInetAddress();
						File directorio = new File(path);

						if (!directorio.exists()) {
							if (!directorio.mkdirs())  {
								JOptionPane.showMessageDialog(null, "Error al cargar datos.", "Data.", JOptionPane.ERROR_MESSAGE);
							}
						}

						// Crear el fichero para guardar el .txt
						File archivo = new File(path + "/" + nfactura + ".txt");
						FileWriter escritor;
						try {
							escritor = new FileWriter(archivo);
							// Escribe el archivo con la informacion
							for (int i=0; i< linea.length(); i++)
								escritor.write(linea.charAt(i));
							escritor.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						Servidor.actualizarTabla(nfactura, nsfd.getInetAddress().toString());
					}

				}
			}
			catch(IOException ioe)
			{
				Server.usuarios.removeElement(this);
				Servidor.getTxtConexion().append(nsfd.getInetAddress()+"> se ha desconectado\n");
				break;
			}
		}
	}

}

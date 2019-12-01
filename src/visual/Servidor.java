package visual;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import logical.Server;

import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JTextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Servidor extends JFrame {

	private JPanel contentPane;
	// Table
	private static JTable tableUser;
	private static Object row[];
	private static DefaultTableModel model;
	// Variables lógicas.
	private static JTextArea txtConexion;
	private Server background;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servidor frame = new Servidor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Servidor() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				// Cerrar el socket
				background.broadcast();
				background.interrupt();			
			}
		});
		background = new Server();
		background.start();
		setResizable(false);
		setTitle("Servidor");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Servidor.class.getResource("/images/cheese.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 497, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Archivos recientes.", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 461, 239);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		panel.add(scrollPane, BorderLayout.CENTER);

		model = new DefaultTableModel();
		String headers[] = { "Hora", "Fecha", "Nombre", "Usuario"};
		model.setColumnIdentifiers(headers);
		tableUser = new JTable();
		tableUser.setModel(model);
		tableUser.getTableHeader().setResizingAllowed(false);
		tableUser.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(tableUser);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Registro de conexiones recientes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(10, 261, 461, 136);
		contentPane.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_1.add(scrollPane_1, BorderLayout.CENTER);

		txtConexion = new JTextArea();
		txtConexion.setEditable(false);
		scrollPane_1.setViewportView(txtConexion);
		txtConexion.setLineWrap(true);
		txtConexion.setWrapStyleWord(true);

		JButton btnAbrirCarpeta = new JButton("Abrir carpeta");
		btnAbrirCarpeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Crear directorio para las facturas. (Si no existe)
				String path = System.getProperty("user.dir");				
				path += "/data/facturas";				
				File directorio = new File(path);
				if (!directorio.exists()) {
					if (!directorio.mkdirs())  {
						JOptionPane.showMessageDialog(null, "Error al cargar datos.", "Data.", JOptionPane.ERROR_MESSAGE);
					}
				}
				Path spath = Paths.get(path);
				try {
		            Desktop.getDesktop().open(spath.toFile());
		        } catch (IOException ex) {
		            JOptionPane.showMessageDialog(null, "Error: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
		        }
				
			}
		});
		btnAbrirCarpeta.setToolTipText("Abre la carpeta donde estan los archivos");
		btnAbrirCarpeta.setBounds(351, 407, 120, 23);
		contentPane.add(btnAbrirCarpeta);
	}

	//String headers[] = { "Hora", "Fecha", "Nombre", "Usuario"};
	public static void actualizarTabla(String nfactura, String nombreUsuario) {
		row = new Object[model.getColumnCount()];
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss z");
		row[0] = formater.format(new Date());
		formater = new SimpleDateFormat("dd-MM-yyyy");
		row[1] = formater.format(new Date());
		row[2] = nombreUsuario;
		row[3] = nfactura;
		model.addRow(row);
	}
	
	public static JTextArea getTxtConexion() {
		return txtConexion;
	}
}

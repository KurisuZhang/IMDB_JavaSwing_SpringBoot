import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class GUIFrame extends JFrame {

	private JPanel contentPane;
	private JTextField jtextField;
	private JTable jtable;
	private ArrayList<Movie> movies;

	Object[] column = {"Poster", "Name", "Year", "Director","Stars","Genres","Rating" };
	Object[][] data = {};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIFrame frame = new GUIFrame();
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
	public GUIFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel2 = new JPanel();
		panel.add(panel2, BorderLayout.CENTER);

		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(new ImageIcon("src/logo.png"));
		panel2.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(" Movie Name: ");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		panel2.add(lblNewLabel_1);
		
		jtextField = new JTextField();
		jtextField.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		jtextField.setPreferredSize(new Dimension(500, 50));
		panel2.add(jtextField);
		
		JButton jButton = new JButton("Search");
		jButton.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		panel2.add(jButton);

		JPanel panel3 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel3.getLayout();
		flowLayout.setVgap(20);
		panel.add(panel3, BorderLayout.SOUTH);

		JLabel lblNewLabel_2 = new JLabel("Note: typically it will take around 4s to retrieve data and download images");
		lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		panel3.add(lblNewLabel_2);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		jtable = new JTable(){
			public Class getColumnClass(int column) {
				return (column == 0) ? Icon.class : Object.class;
			}
		};
		scrollPane.setViewportView(jtable);
		((DefaultTableCellRenderer)jtable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		jtable.getTableHeader().setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		jtable.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		jtable.setRowHeight(296);
		jtable.setModel(new DefaultTableModel(data,column));

		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = jtextField.getText();
				if (name.equals("")){
					JOptionPane.showMessageDialog(contentPane,"Movie name can't be NULL", "Warning", JOptionPane.WARNING_MESSAGE);
				}else {
					try {
						IMDB imdb = new IMDB(name,contentPane);
			            Thread findThread = new Thread(new Runnable() {
			                @Override
			                public void run() {
			                    JOptionPane.showMessageDialog(contentPane, "Done", "Searching", JOptionPane.PLAIN_MESSAGE);
			                }
			            });
			            findThread.start();
						movies = imdb.doTheSearch();
						createTable(movies);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	public void createTable(ArrayList<Movie> ms) throws IOException {
		data = new Object[ms.size()][7];
		System.out.println(ms.size());
		Set<Thread> threadSet = new HashSet<Thread>();
		Set<URLThread> urlThreadSet = new HashSet<URLThread>();
		for (Movie m : ms) {
			URLThread urlThread = new URLThread(m);
			Thread thread = new Thread(urlThread);
			threadSet.add(thread);
			urlThreadSet.add(urlThread);
			thread.start();
		}


		for (int i = 0; i < ms.size(); i++) {
			data[i][1] = ms.get(i).getTitle();
			data[i][2] = ms.get(i).getYear();
			data[i][3] = ms.get(i).getDirectors();
			data[i][4] = ms.get(i).getStars();
			data[i][5] = ms.get(i).getGenres();
			data[i][6] = ms.get(i).getImDbRating();
		}

		// wait for all thread end
		for (Thread t : threadSet) {
			while (t.isAlive()) {
				continue;
			}
		}

		int i = 0;
		for (URLThread urlThread : urlThreadSet){
			Image image = urlThread.getImage();
			data[i][0] = new ImageIcon(image);
			i++;
		}

		jtable.setModel(new DefaultTableModel(data,column));
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		jtable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		jtable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		jtable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		jtable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		jtable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		jtable.getColumnModel().getColumn(2).setMaxWidth(100);
		jtable.getColumnModel().getColumn(6).setMaxWidth(100);

	}
}

package uni.booksolibrary;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class BookPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int book_id = -1;

	JPanel upBookPanel = new JPanel();
	JPanel middleBookPanel = new JPanel();
	JPanel downBookPanel = new JPanel();

	JTextField firstNameTF = new JTextField();
	JTextField lastNameTF = new JTextField();
	JTextField titleTF = new JTextField();
	JTextField publicationYearTF = new JTextField();
	JTextField publisherTF = new JTextField();
	JTextField languageTF = new JTextField();

	JLabel firstNameL = new JLabel("Author's First Name:");
	JLabel lastNameL = new JLabel("Author's Last Name:");
	JLabel titleL = new JLabel("Title");
	JLabel genreL = new JLabel("Genre:");
	JLabel publicationYearL = new JLabel("Publication Year:");
	JLabel publisherL = new JLabel("Publisher:");
	JLabel languageL = new JLabel("Language:");

	String[] genres = { "Self-Help", "Romance", "Comedy", "Thriller", "Science", "History", "Mistery", "Literature" };

	String[] languages = { "English", "Macedonian", "Spanish", "Bulgarian", "German", "Serbian" };

	JComboBox<String> genreCombo = new JComboBox<String>(genres);
	JComboBox<String> languageCombo = new JComboBox<String>(languages);
	JComboBox<String> authorsCombo = new JComboBox<String>();

	JButton addButton = new JButton("Add");
	JButton deleteButton = new JButton("Delete");
	JButton editButton = new JButton("Edit");
	JButton searchButton = new JButton("Search"); // by title
	JButton refreshButton = new JButton("Refresh");
	JButton clearButton = new JButton("Clear");

	JTable table = new JTable();
	JScrollPane scroll = new JScrollPane();

	private BorrowingPanel bw;
	private ReferencePanel rf;

	public BookPanel(BorrowingPanel bw, ReferencePanel rf) {

		this.bw = bw;
		this.rf = rf;
		this.setSize(1000, 600);
		this.setLayout(new GridLayout(3, 1));

		this.add(upBookPanel);

		// Up-panel
		upBookPanel.setLayout(new GridLayout(7, 2));

		upBookPanel.add(firstNameL);
		upBookPanel.add(firstNameTF);
		upBookPanel.add(lastNameL);
		upBookPanel.add(lastNameTF);
		upBookPanel.add(titleL);
		upBookPanel.add(titleTF);
		upBookPanel.add(genreL);
		upBookPanel.add(genreCombo);
		upBookPanel.add(publicationYearL);
		upBookPanel.add(publicationYearTF);
		upBookPanel.add(publisherL);
		upBookPanel.add(publisherTF);
		upBookPanel.add(languageL);
		upBookPanel.add(languageCombo);

		this.add(middleBookPanel);

		// Middle panel

		middleBookPanel.add(addButton);
		middleBookPanel.add(deleteButton);
		middleBookPanel.add(editButton);
		middleBookPanel.add(searchButton);
		middleBookPanel.add(refreshButton);
		middleBookPanel.add(clearButton);
		middleBookPanel.add(authorsCombo);

		addButton.addActionListener(new AddAction());
		deleteButton.addActionListener(new DeleteAction());
		editButton.addActionListener(new EditAction());
		searchButton.addActionListener(new SearchAction());
		refreshButton.addActionListener(new RefreshAction());
		clearButton.addActionListener(new ClearAction());

		scroll.setPreferredSize(new Dimension(700, 150));
		scroll.setViewportView(table);
		downBookPanel.add(scroll);
		this.add(downBookPanel);
		refreshTable();
		refreshCombo();
		table.addMouseListener(new MouseAction());

		this.setVisible(true);

	}

	public void refreshTable() {

		conn = DBConnection.getConnection();
		try {
			state = conn.prepareStatement("select * from books");
			result = state.executeQuery();
			table.setModel(new TableModel(result));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clearForm() {

		firstNameTF.setText("");
		lastNameTF.setText("");
		titleTF.setText("");
		genreCombo.setSelectedIndex(0);
		publicationYearTF.setText("");
		publisherTF.setText("");
		languageCombo.setSelectedIndex(0);

	}

	public void refreshCombo() {

		authorsCombo.removeAllItems();

		String sql = "select title from books";
		conn = DBConnection.getConnection();
		String item = "";

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			while (result.next()) {
				item = result.getObject(1).toString();

				authorsCombo.addItem(item);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "insert into books(authorFirstName,authorlastName,title,genre,"
					+ "publicationYear,publisher,language) values(?,?,?,?,?,?,?)";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, firstNameTF.getText());
				state.setString(2, lastNameTF.getText());
				state.setString(3, titleTF.getText());
				state.setString(4, genreCombo.getSelectedItem().toString());
				state.setInt(5, Integer.parseInt(publicationYearTF.getText()));
				state.setString(6, publisherTF.getText());
				state.setString(7, languageCombo.getSelectedItem().toString());
				state.execute();
				refreshTable();
				refreshCombo();
				bw.populateBooksCombo();
				rf.populateBookCombo();
				;

				clearForm();
				book_id = -1;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = table.getSelectedRow();
			book_id = Integer.parseInt(table.getValueAt(row, 0).toString());
			firstNameTF.setText(table.getValueAt(row, 1).toString());
			lastNameTF.setText(table.getValueAt(row, 2).toString());
			titleTF.setText(table.getValueAt(row, 3).toString());
			publicationYearTF.setText(table.getValueAt(row, 5).toString());
			publisherTF.setText(table.getValueAt(row, 6).toString());
			for (int i = 0; i < genreCombo.getItemCount(); i++) {
				if (genreCombo.getItemAt(i).equals(table.getValueAt(row, 4).toString())) {
					genreCombo.setSelectedIndex(i);
					break;
				}
			}
			for (int i = 0; i < languageCombo.getItemCount(); i++) {
				if (languageCombo.getItemAt(i).equals(table.getValueAt(row, 7).toString())) {
					languageCombo.setSelectedIndex(i);
					break;
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "delete from books where book_id = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, book_id);
				state.execute();
				refreshTable();
				refreshCombo();
				bw.populateBooksCombo();
				rf.populateBookCombo();
				clearForm();
				book_id = -1;

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class EditAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "update books set authorfirstName = ?, authorlastName = ?, title = ?, "
					+ "genre = ?, publicationyear = ? ,publisher = ? ,language = ?" + "where book_id = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, firstNameTF.getText());
				state.setString(2, lastNameTF.getText());
				state.setString(3, titleTF.getText());
				state.setString(4, genreCombo.getSelectedItem().toString());
				state.setInt(5, Integer.parseInt(publicationYearTF.getText()));
				state.setString(6, publisherTF.getText());
				state.setString(7, languageCombo.getSelectedItem().toString());
				state.setInt(8, book_id);
				state.execute();
				refreshTable();
				refreshCombo();
				bw.populateBooksCombo();
				rf.populateBookCombo();
				clearForm();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	class RefreshAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			refreshTable();
			clearForm();

		}

	}

	class ClearAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			clearForm();

		}

	}

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "select * from books where title= ?";
			int rowCount = 0;

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, titleTF.getText());
				result = state.executeQuery();

				try {

					table.setModel(new TableModel(result));

					rowCount = table.getRowCount();

					if (rowCount == 0) {

						String sqlQ = "select * from books where title like '%" + titleTF.getText() + "%'";
						state = conn.prepareStatement(sqlQ);
						result = state.executeQuery();
						table.setModel(new TableModel(result));
						rowCount = table.getRowCount();

					}
				} catch (Exception e2) {
					// TODO: handle exception
				}

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

}

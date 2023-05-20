package uni.booksolibrary;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReferencePanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;

	JPanel upRefPanel = new JPanel();
	JPanel midRefPanel = new JPanel();
	JPanel downRefPanel = new JPanel();

	JLabel memberNameL = new JLabel("Member's Name: ");
	JLabel bookTitleL = new JLabel("Book: ");

	JComboBox<String> membersCombo = new JComboBox<String>();
	JComboBox<String> booksCombo = new JComboBox<String>();
	ArrayList<String> comboItems = new ArrayList<String>();
	ArrayList<String> bookItems = new ArrayList<String>();

	JTable table = new JTable();
	JScrollPane scroll = new JScrollPane(table);

	JButton searchButton = new JButton("Search");
	JButton clearButton = new JButton("Clear");

	public ReferencePanel() {

		this.setSize(1500, 600);

		this.setLayout(new GridLayout(3, 1));

		upRefPanel.setLayout(new GridLayout(2, 1));
		upRefPanel.add(memberNameL);
		memberNameL.setHorizontalAlignment(JLabel.CENTER);
		memberNameL.setFont(new Font("Century Gothic", Font.BOLD, 20));
		upRefPanel.add(membersCombo);
		upRefPanel.add(bookTitleL);
		bookTitleL.setFont(new Font("Century Gothic", Font.BOLD, 22));
		bookTitleL.setHorizontalAlignment(JLabel.CENTER);
		upRefPanel.add(booksCombo);
		this.add(upRefPanel);

		midRefPanel.add(searchButton);
		midRefPanel.add(clearButton);
		searchButton.addActionListener(new SearchAction());
		clearButton.addActionListener(new ClearAction());
		this.add(midRefPanel);

		scroll.setPreferredSize(new Dimension(700, 150));
		downRefPanel.add(scroll);
		refreshTable();
		populateCombo();
		populateBookCombo();
		this.add(downRefPanel);
		this.setVisible(true);

	}

	// --------------------- METHODS --------------------------------
	public void refreshTable() {

		conn = DBConnection.getConnection();
		String sql = "SELECT CONCAT(m.firstName, ' ', m.lastName) AS Member," + "b.title AS Book, "
				+ "br.borrowingDate AS Borrowing," + "br.returnDate AS Returning " + "FROM borrowing br "
				+ "JOIN Members m ON br.member_id = m.member_id " + "JOIN Books b ON br.book_id = b.book_id";

//		SELECT CONCAT(m.firstName, ' ', m.lastName) AS Member, b.title AS Book, br.borrowingDate AS Borrowing,
		// br.returnDate AS Returning FROM borrowing br JOIN Members m ON br.member_id =
		// m.member_id JOIN
		// Books b ON br.book_id = b.book_id

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
			String[] columnNames = new String[columnCount];

			for (int i = 0; i < columnCount; i++) {
				columnNames[i] = metaData.getColumnLabel(i + 1);
			}

			columnNames[0] = "MEMBER";
			columnNames[1] = "BOOK";
			columnNames[2] = "BORROWING";
			columnNames[3] = "RETURNING";

			DefaultTableModel model = new DefaultTableModel(columnNames, 0);
			while (result.next()) {
				Object[] rowData = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					rowData[i] = result.getObject(i + 1);
				}
				model.addRow(rowData);
			}
			table.setModel(model);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void populateCombo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT firstName, lastName FROM Members");
			result = state.executeQuery();
			TableModel tb = new TableModel(result);

			int rowCount = tb.getRowCount();
			String item;
			comboItems.clear();
			comboItems.add("");

			for (int i = 0; i < rowCount; i++) {

				item = tb.getValueAt(i, 0).toString() + " " + tb.getValueAt(i, 1);
				comboItems.add(item);
			}
			membersCombo.removeAllItems();
			comboItems.forEach(comboItems -> membersCombo.addItem(comboItems));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void populateBookCombo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("Select title from books");
			result = state.executeQuery();
			TableModel tb = new TableModel(result);
			int rowCount = tb.getRowCount();
			String item;
			bookItems.clear();
			bookItems.add("");

			for (int i = 0; i < rowCount; i++) {

				item = tb.getValueAt(i, 0).toString();
				bookItems.add(item);
			}
			booksCombo.removeAllItems();
			bookItems.forEach(bookItems -> booksCombo.addItem(bookItems));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "SELECT CONCAT(m.firstName, ' ', m.lastName) AS Member," + "b.title AS Book, "
					+ "br.borrowingDate AS Borrowing," + "br.returnDate AS Returning " + "FROM borrowing br "
					+ "JOIN Members m ON br.member_id = m.member_id " + "JOIN Books b ON br.book_id = b.book_id"
					+ " WHERE CONCAT(m.firstName, ' ', m.lastName) = ? AND B.TITLE = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, membersCombo.getSelectedItem().toString());
				state.setString(2, booksCombo.getSelectedItem().toString());
				result = state.executeQuery();

				try {
					table.setModel(new TableModel(result));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	class ClearAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshTable();

		}

	}

}
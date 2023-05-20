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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

public class BorrowingPanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int borrower_id = -1;
	int book_id = -1;
	int member_id = -1;

	ArrayList<String> membersComboItems = new ArrayList<String>();
	ArrayList<String> booksComboItems = new ArrayList<String>();

	SpinnerDateModel borrowingDate = new SpinnerDateModel();
	SpinnerDateModel returnDate = new SpinnerDateModel();

	JSpinner borrowingDateSpinner = new JSpinner(borrowingDate);
	JSpinner returnDateSpinner = new JSpinner(returnDate);

	JPanel upBorrowingPanel = new JPanel();
	JPanel middleBorrowingPanel = new JPanel();
	JPanel downBorrowingPanel = new JPanel();

	JLabel memberNameL = new JLabel("Member");
	JLabel bookL = new JLabel("Book");
	JLabel borrowingDateL = new JLabel("Borrowing Date");
	JLabel returnDateL = new JLabel("Return Date");

	JComboBox<String> membersCombo = new JComboBox<String>();
	JComboBox<String> booksCombo = new JComboBox<String>();
	JTextField memberTF = new JTextField();
	JTextField booksTF = new JTextField();

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	JButton addButton = new JButton("Add");
	JButton deleteButton = new JButton("Delete");
	JButton editButton = new JButton("Edit");
	JButton searchButton = new JButton("Search"); // by last name
	JButton refreshButton = new JButton("Refresh");
	JButton clearButton = new JButton("Clear");

	public BorrowingPanel() {

		this.setSize(1500, 600);

		this.setLayout(new GridLayout(3, 1));

		this.add(upBorrowingPanel);

		// Up-panel

		upBorrowingPanel.setLayout(new GridLayout(4, 2));
		upBorrowingPanel.add(memberNameL);
		upBorrowingPanel.add(membersCombo);
		upBorrowingPanel.add(bookL);
		upBorrowingPanel.add(booksCombo);
		upBorrowingPanel.add(borrowingDateL);
		upBorrowingPanel.add(borrowingDateSpinner);
		upBorrowingPanel.add(returnDateL);
		upBorrowingPanel.add(returnDateSpinner);

		borrowingDateSpinner.setEditor(new JSpinner.DateEditor(borrowingDateSpinner, "yyyy-MM-dd"));
		returnDateSpinner.setEditor(new JSpinner.DateEditor(returnDateSpinner, "yyyy-MM-dd"));

		this.add(middleBorrowingPanel);

		// Middle panel

		middleBorrowingPanel.add(addButton);
		middleBorrowingPanel.add(deleteButton);
		middleBorrowingPanel.add(editButton);
		middleBorrowingPanel.add(refreshButton);
		middleBorrowingPanel.add(clearButton);

		addButton.addActionListener(new AddAction());
		deleteButton.addActionListener(new DeleteAction());
		refreshButton.addActionListener(new RefreshAction());
		clearButton.addActionListener(new ClearAction());

		// Down panel

		myScroll.setPreferredSize(new Dimension(700, 150));
		downBorrowingPanel.add(myScroll);
		table.addMouseListener(new MouseAction());
		this.add(downBorrowingPanel);

		populateMembersCombo();
		populateBooksCombo();
		refreshTable();

		this.setVisible(true);

	}

	// ----------------------- METHODS ----------------------

	public void populateMembersCombo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT firstName, lastName FROM Members");
			result = state.executeQuery();
			TableModel tb = new TableModel(result);

			int rowCount = tb.getRowCount();
			String item;
			membersComboItems.clear();
			membersComboItems.add("");

			for (int i = 0; i < rowCount; i++) {

				item = tb.getValueAt(i, 0).toString() + " " + tb.getValueAt(i, 1).toString();
				membersComboItems.add(item);
			}

			membersCombo.removeAllItems();
			membersComboItems.forEach(comboItems -> membersCombo.addItem(comboItems));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void populateBooksCombo() {

		conn = DBConnection.getConnection();
		try {
			state = conn.prepareStatement("select title FROM books");
			result = state.executeQuery();
			TableModel tb = new TableModel(result);

			int rowCount = tb.getRowCount();
			String item;
			booksComboItems.clear();
			booksComboItems.add("");

			for (int i = 0; i < rowCount; i++) {

				item = tb.getValueAt(i, 0).toString();
				booksComboItems.add(item);
			}

			booksCombo.removeAllItems();
			booksComboItems.forEach(comboItems -> booksCombo.addItem(comboItems));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clearForm() {

		membersCombo.setSelectedItem("");
		booksCombo.setSelectedItem("");
		borrowingDateSpinner.setValue(new Date());
		returnDateSpinner.setValue(new Date());

	}

	public void refreshTable() {

		conn = DBConnection.getConnection();
		try {
			state = conn.prepareStatement("select * from borrowing");
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

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			// this is the prepared statement
			String sql = "INSERT INTO borrowing (member_id, borrower_name, book_id, book_title, "
					+ "borrowingdate, returndate) VALUES ((SELECT member_id FROM Members"
					+ " WHERE CONCAT(firstname, ' ', lastname) LIKE ?), ?,"
					+ " (SELECT book_id FROM Books WHERE title = ?),?,?,?)";

			try {
				state = conn.prepareStatement(sql);

				state.setString(1, membersCombo.getSelectedItem().toString());
				state.setString(2, membersCombo.getSelectedItem().toString());
				state.setString(3, booksCombo.getSelectedItem().toString());
				state.setString(4, booksCombo.getSelectedItem().toString());
				state.setObject(5, borrowingDateSpinner.getValue());
				state.setObject(6, returnDateSpinner.getValue());
				state.execute();
				refreshTable();
				clearForm();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "delete from borrowing where borrowing_id=?";

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, borrower_id);
				state.execute();
				refreshTable();
				clearForm();
				borrower_id = -1;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class ClearAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			clearForm();

		}

	}

	class RefreshAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			refreshTable();

		}

	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = table.getSelectedRow();
			borrower_id = Integer.parseInt(table.getValueAt(row, 0).toString());
			member_id = Integer.parseInt(table.getValueAt(row, 1).toString());
			membersCombo.setSelectedItem(table.getValueAt(row, 2).toString());
			book_id = Integer.parseInt(table.getValueAt(row, 3).toString());
			booksCombo.setSelectedItem(table.getValueAt(row, 4).toString());

			// Parse the date strings into Date objects
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date borrowingDate = dateFormat.parse(table.getValueAt(row, 5).toString());
				Date returnDate = dateFormat.parse(table.getValueAt(row, 6).toString());
				borrowingDateSpinner.setValue(borrowingDate);
				returnDateSpinner.setValue(returnDate);
			} catch (ParseException ex) {
				ex.printStackTrace();
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

	class EditAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String updateSql = "UPDATE borrowing" + " SET(member_id, borrower_name, book_id, book_title,"
					+ " borrowingdate, returndate values((SELECT member_id FROM Members WHERE "
					+ "CONCAT(firstname, ' ', lastname ) LIKE ?), ?, (SELECT book_id FROM Books WHERE title = ?),"
					+ " ?, ?, ? ) where member_id = ?";

			try {
				PreparedStatement state = conn.prepareStatement(updateSql);

				state.setString(1, membersCombo.getSelectedItem().toString());
				state.setString(2, membersCombo.getSelectedItem().toString());
				state.setString(3, booksCombo.getSelectedItem().toString());
				state.setString(4, booksCombo.getSelectedItem().toString());
				state.setObject(5, borrowingDateSpinner.getValue().toString());
				state.setObject(6, returnDateSpinner.getValue().toString());
				state.setInt(7, borrower_id);
				state.executeUpdate(updateSql);
				refreshTable();
				clearForm();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}
}

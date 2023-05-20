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

public class MemberPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int member_id = -1;

	JPanel upMemberPanel = new JPanel();
	JPanel midMemberPanel = new JPanel();
	JPanel downMemberPanel = new JPanel();

	JLabel firstNameL = new JLabel("First Name:");
	JLabel lastNameL = new JLabel("Last Name:");
	JLabel sexL = new JLabel("Sex:");
	JLabel ageL = new JLabel("Age:");
	JLabel phoneNumberL = new JLabel("Phone Number:");
	JLabel emailL = new JLabel("Email:");

	JTextField firstNameTF = new JTextField();
	JTextField lastNameTF = new JTextField();
	JTextField ageTF = new JTextField();
	JTextField phoneNumberTF = new JTextField();
	JTextField emailTF = new JTextField();

	String[] item = { "Male", "Female" };
	JComboBox<String> sexCombo = new JComboBox<String>(item);
	JComboBox<String> memberCombo = new JComboBox<String>();

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	JButton addButton = new JButton("Add");
	JButton deleteButton = new JButton("Delete");
	JButton editButton = new JButton("Edit");
	JButton searchButton = new JButton("Search"); // search by last name
	JButton refreshButton = new JButton("Refresh");
	JButton clearButton = new JButton("Clear");

	private BorrowingPanel bw;
	private ReferencePanel rp;

	public MemberPanel(BorrowingPanel bw, ReferencePanel rp) {

		this.setSize(1500, 600);
		this.bw = bw;
		this.rp = rp;
		this.setLayout(new GridLayout(3, 1));

		// Up-panel
		this.add(upMemberPanel);

		upMemberPanel.setLayout(new GridLayout(7, 2));
		upMemberPanel.add(firstNameL);
		upMemberPanel.add(firstNameTF);
		upMemberPanel.add(lastNameL);
		upMemberPanel.add(lastNameTF);
		upMemberPanel.add(ageL);
		upMemberPanel.add(ageTF);
		upMemberPanel.add(sexL);
		upMemberPanel.add(sexCombo);
		upMemberPanel.add(phoneNumberL);
		upMemberPanel.add(phoneNumberTF);
		upMemberPanel.add(emailL);
		upMemberPanel.add(emailTF);

		// Middle panel
		this.add(midMemberPanel);

		midMemberPanel.add(addButton);
		midMemberPanel.add(editButton);
		midMemberPanel.add(deleteButton);
		midMemberPanel.add(searchButton);
		midMemberPanel.add(refreshButton);
		midMemberPanel.add(clearButton);
		midMemberPanel.add(memberCombo);

		addButton.addActionListener(new AddAction());
		deleteButton.addActionListener(new DeleteAction());
		editButton.addActionListener(new EditAction());
		searchButton.addActionListener(new SearchAction());
		refreshButton.addActionListener(new RefreshAction());
		clearButton.addActionListener(new ClearAction());
		// Down-panel

		myScroll.setPreferredSize(new Dimension(700, 150));
		downMemberPanel.add(myScroll);
		table.addMouseListener(new MouseAction());
		this.add(downMemberPanel);
		refreshTable();
		refreshCombo();

		this.setVisible(true);
	}

	// ------------------- METHODS ---------------------
	public void refreshTable() {

		conn = DBConnection.getConnection();
		try {
			state = conn.prepareStatement("select * from members");
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

	public void refreshCombo() {

		memberCombo.removeAllItems();

		String sql = "select member_id, firstName, lastName from members";
		conn = DBConnection.getConnection();
		String item = "";

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			while (result.next()) {
				item = result.getObject(1).toString() + "." + result.getObject(2).toString() + " "
						+ result.getObject(3).toString();
				memberCombo.addItem(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clearForm() {
		firstNameTF.setText("");
		lastNameTF.setText("");
		ageTF.setText("");
		sexCombo.setSelectedItem(0);
		phoneNumberTF.setText("");
		emailTF.setText("");
	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "insert into members(firstName, lastName, sex, age, phoneNumber, email)"
					+ "values(?,?,?,?,?,?)";
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, firstNameTF.getText());
				state.setString(2, lastNameTF.getText());
				state.setString(3, sexCombo.getSelectedItem().toString());
				state.setInt(4, Integer.parseInt(ageTF.getText()));
				state.setString(5, phoneNumberTF.getText());
				state.setString(6, emailTF.getText());
				state.execute();
				refreshTable();
				refreshCombo();
				bw.populateMembersCombo();
				rp.populateCombo();
				clearForm();

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
			member_id = Integer.parseInt(table.getValueAt(row, 0).toString());
			firstNameTF.setText(table.getValueAt(row, 1).toString());
			lastNameTF.setText(table.getValueAt(row, 2).toString());
			ageTF.setText(table.getValueAt(row, 4).toString());
			phoneNumberTF.setText(table.getValueAt(row, 5).toString());
			emailTF.setText(table.getValueAt(row, 6).toString());
			if (table.getValueAt(row, 3).toString().equals("Male")) {
				sexCombo.setSelectedIndex(0);
			} else {
				sexCombo.setSelectedIndex(1);
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
			String sql = "delete from members where member_id=?";

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, member_id);
				state.execute();
				refreshTable();
				refreshCombo();
				bw.populateMembersCombo();
				rp.populateCombo();
				clearForm();
				member_id = -1;

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

			String sql = "update members set firstName = ?, lastName = ?, sex = ?, age = ?,"
					+ " phoneNumber = ?, email = ? where member_id = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, firstNameTF.getText());
				state.setString(2, lastNameTF.getText());
				state.setString(3, sexCombo.getSelectedItem().toString());
				state.setInt(4, Integer.parseInt(ageTF.getText()));
				state.setString(5, phoneNumberTF.getText());
				state.setString(6, emailTF.getText());
				state.setInt(7, member_id);
				state.execute();
				refreshTable();
				refreshCombo();
				bw.populateMembersCombo();
				rp.populateCombo();
				clearForm();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "select * from members where lastname = ? ";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, lastNameTF.getText());
				result = state.executeQuery();
				table.setModel(new TableModel(result));
			} catch (Exception e1) {
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

}

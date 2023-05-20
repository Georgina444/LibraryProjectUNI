package uni.booksolibrary;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MyFrame extends JFrame {

	private static final long serialVersionUID = 7842766040905693337L;

	ReferencePanel referencePanel = new ReferencePanel();
	BorrowingPanel borrowingPanel = new BorrowingPanel();

	BookPanel bookPanel = new BookPanel(borrowingPanel, referencePanel);

	MemberPanel memberPanel = new MemberPanel(borrowingPanel, referencePanel);

	JPanel panelReader = new JPanel();
	JPanel panelBook = new JPanel();
	JPanel panelBorrow = new JPanel();
	JPanel panelRef = new JPanel();

	JTabbedPane tab = new JTabbedPane();

	public MyFrame() {

		this.setSize(1000, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.add(panelReader);

		tab.add(panelReader, "Members");
		tab.add(panelBook, "Books");
		tab.add(panelBorrow, "Borrowing");
		tab.add(panelRef, "Reference");

		panelReader.add(memberPanel);
		panelBook.add(bookPanel);
		panelBorrow.add(borrowingPanel);
		panelRef.add(referencePanel);

		this.add(tab);

		this.setVisible(true);
	}

}

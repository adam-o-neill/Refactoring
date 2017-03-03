import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {

	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");

	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");

	private long currentByteStart = 0;
	private final RandomFile application = new RandomFile();

	private final FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");

	private File file;

	private boolean change = false;

	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
			saveChange, cancelChange;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private static final EmployeeDetails frame = new EmployeeDetails();

	final Font font1 = new Font("SansSerif", Font.BOLD, 16);

	private String generatedFileName;

	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;

	final String[] gender = { "", "M", "F" };

	final String[] department = { "", "Administration", "Production", "Transport", "Management" };

	final String[] fullTime = { "", "Yes", "No" };

	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		fileMenu.add(open = new JMenuItem("Open")).addActionListener(this);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(save = new JMenuItem("Save")).addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveAs = new JMenuItem("Save As")).addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(this);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(this);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem("First"));
		firstItem.addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem("Previous"));
		prevItem.addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem("Next"));
		nextItem.addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem("Last"));
		lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);

		closeMenu.add(closeApp = new JMenuItem("Close")).addActionListener(this);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}

	private JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		addSearch(searchPanel, searchByIdField, searchId, "ID");
		addSearch(searchPanel, searchBySurnameField, searchSurname, "Surname");

		return searchPanel;
	}

	private void addSearch(JPanel searchPanel, JTextField jTextField, JButton jButton, String name) {
		searchPanel.add(new JLabel("Search by " + name + ":"), "growx, pushx");
		searchPanel.add(jTextField = new JTextField(20), "width 200:200:200, growx, pushx");
		jTextField.addActionListener(this);
		jTextField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(jButton = new JButton(new ImageIcon(
				new ImageIcon("imgres.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		jButton.addActionListener(this);
		jButton.setToolTipText("Search Employee By " + name);
	}

	private JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));

		addNavigButtons(navigPanel, first, "first");
		addNavigButtons(navigPanel, previous, "previous");
		addNavigButtons(navigPanel, next, "next");
		addNavigButtons(navigPanel, last, "last");

		return navigPanel;
	}

	private void addNavigButtons(JPanel navigPanel, JButton button, String name){
		navigPanel.add(button = new JButton(new ImageIcon(
				new ImageIcon(name + ".png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(this);
		first.setToolTipText("Display " + name + " Record");
	}

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), "growx, pushx");
		add.addActionListener(this);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), "growx, pushx");
		edit.addActionListener(this);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), "growx, pushx");
		displayAll.addActionListener(this);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}

	private JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), "growx, pushx");
		empDetails.add(idField = new JTextField(20), "growx, pushx, wrap");
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), "growx, pushx");
		empDetails.add(ppsField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Surname:"), "growx, pushx");
		empDetails.add(surnameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("First Name:"), "growx, pushx");
		empDetails.add(firstNameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Gender:"), "growx, pushx");
		empDetails.add(genderCombo = new JComboBox<>(gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(departmentCombo = new JComboBox<>(department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(fullTimeCombo = new JComboBox<>(fullTime), "growx, pushx, wrap");

		buttonPanel.add(saveChange = new JButton("Save"));
		saveChange.addActionListener(this);
		saveChange.setVisible(false);
		saveChange.setToolTipText("Save changes");
		buttonPanel.add(cancelChange = new JButton("Cancel"));
		cancelChange.addActionListener(this);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		forEachEmpDetails(empDetails);
		return empDetails;
	}

	private void forEachEmpDetails(JPanel empDetails) {
		JTextField field;
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
				field.getDocument().addDocumentListener(this);
			}
			else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {

					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}
				});
			}
		}
	}

	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");

		if(thisEmployee != null && thisEmployee.getEmployeeId() != 0) {

			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			}
			found = false;

			while (!found && countDep < department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			}
			idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			ppsField.setText(thisEmployee.getPps().trim());
			surnameField.setText(thisEmployee.getSurname().trim());
			firstNameField.setText(thisEmployee.getFirstName());
			genderCombo.setSelectedIndex(countGender);
			departmentCombo.setSelectedIndex(countDep);
			salaryField.setText(format.format(thisEmployee.getSalary()));

			if (thisEmployee.getFullTime())
				fullTimeCombo.setSelectedIndex(1);
			else
				fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}

	private void displayEmployeeSummaryDialog() {

		if (isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmployees());
	}

	private void displaySearchByIdDialog() {
		if (isSomeoneToDisplay())
			new SearchByIdDialog(EmployeeDetails.this);
	}

	private void displaySearchBySurnameDialog() {
		if (isSomeoneToDisplay())
			new SearchBySurnameDialog(EmployeeDetails.this);
	}

	private void firstRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getFirst();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();

			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();
		}
	}

	private void previousRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getPrevious(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getPrevious(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	private void nextRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getNext(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getNext(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	private void lastRecord() {

		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getLast();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();

			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();
		}
	}

	public void searchEmployeeById() {
		boolean found = false;

		try {
			if (isSomeoneToDisplay()) {
				firstRecord();
				int firstId = currentEmployee.getEmployeeId();

				if (searchByIdField.getText().trim().equals(idField.getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				}
				else {
					nextRecord();

					while (firstId != currentEmployee.getEmployeeId()) {

						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							displayRecords(currentEmployee);
							break;
						} else
							nextRecord();
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			}
		}
		catch (NumberFormatException e) {
			searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		}
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}

	public void searchEmployeeBySurname() {
		boolean found = false;

		if (isSomeoneToDisplay()) {
			firstRecord();
			String firstSurname = currentEmployee.getSurname().trim();

			if (searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(currentEmployee);
			}
			else {
				nextRecord();

				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {

					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(currentEmployee);
						break;
					}
					else
						nextRecord();
				}
			}

			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		}
		searchBySurnameField.setText("");
	}

	public int getNextFreeId() {
		int nextFreeId = 0;

		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();

			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}

	public void addRecord(Employee newEmployee) {
		application.openWriteFile(file.getAbsolutePath());

		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	private void deleteRecord() {
		if (isSomeoneToDisplay()) {

			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if (returnVal == JOptionPane.YES_OPTION) {

				application.openWriteFile(file.getAbsolutePath());

				application.deleteRecords(currentByteStart);
				application.closeWriteFile();

				if (isSomeoneToDisplay()) {
					nextRecord();
					displayRecords(currentEmployee);
				}
			}
		}
	}

	private Vector<Object> getAllEmployees() {
		Vector<Object> allEmployee = new Vector<>();
		Vector<Object> empDetails;
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();
		firstId = currentEmployee.getEmployeeId();

		addEmployees(allEmployee, firstId);
		currentByteStart = byteStart;

		return allEmployee;
	}

	private void addEmployees(Vector<Object> allEmployee, int firstId) {
		Vector<Object> empDetails;
		do {
			empDetails = new Vector<>();
			empDetails.addElement(currentEmployee.getEmployeeId());
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(currentEmployee.getGender());
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(currentEmployee.getSalary());
			empDetails.addElement(currentEmployee.getFullTime());

			allEmployee.addElement(empDetails);
			nextRecord();
		} while (firstId != currentEmployee.getEmployeeId());
	}

	private void editDetails() {
		if (isSomeoneToDisplay()) {

			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);
		}
	}

	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}

	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay;

		application.openReadFile(file.getAbsolutePath());

		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();

		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}

	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = true;

		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1))
					&& Character.isDigit(pps.charAt(2)) && Character.isDigit(pps.charAt(3))
					&& Character.isDigit(pps.charAt(4)) && Character.isDigit(pps.charAt(5))
					&& Character.isDigit(pps.charAt(6)) && Character.isLetter(pps.charAt(7))
					&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {

				application.openReadFile(file.getAbsolutePath());

				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();
			}
		}

		return ppsExist;
	}

	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();


		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}

	private boolean checkForChanges() {
		boolean anyChanges = false;

		if (change) {
			saveChanges();
			anyChanges = true;
		}


		else {
			setEnabled(false);
			displayRecords(currentEmployee);
		}

		return anyChanges;
	}

	private boolean checkInput() {
		boolean valid = true;


		if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentByteStart)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
			surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
			firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
			genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
			departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		try {
			Double.parseDouble(salaryField.getText());

			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			}
		}
		catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			}
		}
		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}

		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");

		if (ppsField.isEditable())
			setToWhite();

		return valid;
	}

	private void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}

	public void setEnabled(boolean booleanValue) {
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(!booleanValue);
		searchBySurnameField.setEnabled(!booleanValue);
		searchId.setEnabled(!booleanValue);
		searchSurname.setEnabled(!booleanValue);
	}

	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");

		fc.setFileFilter(datfilter);
		File newFile;

		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();
			}
		}

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();

			if (file.getName().equals(generatedFileName))
				file.delete();
			file = newFile;

			application.openReadFile(file.getAbsolutePath());
			firstRecord();
			displayRecords(currentEmployee);
			application.closeReadFile();
		}
	}

	private void saveFile() {

		if (file.getName().equals(generatedFileName))
			saveFileAs();
		else {
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

				if (returnVal == JOptionPane.YES_OPTION) {

					if (!idField.getText().equals("")) {

						application.openWriteFile(file.getAbsolutePath());

						currentEmployee = getChangedDetails();


						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();
					}
				}
			}

			displayRecords(currentEmployee);
			setEnabled(false);
		}
	}

	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if (returnVal == JOptionPane.YES_OPTION) {
			application.openWriteFile(file.getAbsolutePath());

			currentEmployee = getChangedDetails();

			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();
			changesMade = false;
		}
		displayRecords(currentEmployee);
		setEnabled(false);
	}

	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");

		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			if (!checkFileName(newFile)) {
				newFile = new File(newFile.getAbsolutePath() + ".dat");

				application.createFile(newFile.getAbsolutePath());
			}
			else
				application.createFile(newFile.getAbsolutePath());

			try {
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				if (file.getName().equals(generatedFileName))
					file.delete();
				file = newFile;
			}
			catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
		changesMade = false;
	}

	private void exitApp() {
		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();
					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}
				else if (returnVal == JOptionPane.NO_OPTION) {
					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}
			}
			else {
				if (file.getName().equals(generatedFileName))
					file.delete();
				System.exit(0);
			}
		} else {
			if (file.getName().equals(generatedFileName))
				file.delete();
			System.exit(0);
		}
	}

	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		return fileName.toString();
	}

	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		file = new File(generatedFileName);
		application.createFile(file.getName());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeApp) {
			if (checkInput() && !checkForChanges())
				exitApp();
		} else if (e.getSource() == open) {
			if (checkInput() && !checkForChanges())
				openFile();
		} else if (e.getSource() == save) {
			if (checkInput() && !checkForChanges())
				saveFile();
			change = false;
		} else if (e.getSource() == saveAs) {
			if (checkInput() && !checkForChanges())
				saveFileAs();
			change = false;
		} else if (e.getSource() == searchById) {
			if (checkInput() && !checkForChanges())
				displaySearchByIdDialog();
		} else if (e.getSource() == searchBySurname) {
			if (checkInput() && !checkForChanges())
				displaySearchBySurnameDialog();
		} else if (e.getSource() == searchId || e.getSource() == searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField)
			searchEmployeeBySurname();
		else if (e.getSource() == saveChange) {
			if (checkInput() && !checkForChanges())
				;
		} else if (e.getSource() == cancelChange)
			cancelChange();
		else if (e.getSource() == firstItem || e.getSource() == first) {
			if (checkInput() && !checkForChanges()) {
				firstRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == prevItem || e.getSource() == previous) {
			if (checkInput() && !checkForChanges()) {
				previousRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == nextItem || e.getSource() == next) {
			if (checkInput() && !checkForChanges()) {
				nextRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == lastItem || e.getSource() == last) {
			if (checkInput() && !checkForChanges()) {
				lastRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == listAll || e.getSource() == displayAll) {
			if (checkInput() && !checkForChanges())
				if (isSomeoneToDisplay())
					displayEmployeeSummaryDialog();
		} else if (e.getSource() == create || e.getSource() == add) {
			if (checkInput() && !checkForChanges())
				new AddRecordDialog(EmployeeDetails.this);
		} else if (e.getSource() == modify || e.getSource() == edit) {
			if (checkInput() && !checkForChanges())
				editDetails();
		} else if (e.getSource() == delete || e.getSource() == deleteButton) {
			if (checkInput() && !checkForChanges())
				deleteRecord();
		} else if (e.getSource() == searchBySurname) {
			if (checkInput() && !checkForChanges())
				new SearchBySurnameDialog(EmployeeDetails.this);
		}
	}

	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());

		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");

		dialog.add(navigPanel(), "width 150:150:150, wrap");

		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");

		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	private static void createAndShowGUI() {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(EmployeeDetails::createAndShowGUI);
	}

	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	public void windowClosing(WindowEvent e) {
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
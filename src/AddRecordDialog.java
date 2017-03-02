import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

class AddRecordDialog extends JDialog implements ActionListener {
	private JTextField idField;
	private JTextField ppsField;
	private JTextField surnameField;
	private JTextField firstNameField;
	private JTextField salaryField;
	private JComboBox<String> genderCombo;
	private JComboBox<String> departmentCombo;
	private JComboBox<String> fullTimeCombo;
	private JButton save;
	private JButton cancel;
	private final EmployeeDetails parent;

	public AddRecordDialog(EmployeeDetails parent) {
		setTitle("Add Record");
		setModal(true);
		this.parent = parent;
		this.parent.setEnabled(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane(dialogPane());
		setContentPane(scrollPane);
		
		getRootPane().setDefaultButton(save);
		
		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);
	}

	private Container dialogPane() {
		JPanel empDetails, buttonPanel;
		empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();
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
		empDetails.add(genderCombo = new JComboBox<>(this.parent.gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(departmentCombo = new JComboBox<>(this.parent.department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(fullTimeCombo = new JComboBox<>(this.parent.fullTime), "growx, pushx, wrap");

		buttonPanel.add(save = new JButton("Save"));
		save.addActionListener(this);
		save.requestFocus();
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(this.parent.font1);
			if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
			}
			else if(empDetails.getComponent(i) instanceof JTextField){
				field = (JTextField) empDetails.getComponent(i);
				if(field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
				field.setDocument(new JTextFieldLimit(20));
			}
		}
		idField.setText(Integer.toString(this.parent.getNextFreeId()));
		return empDetails;
	}

	private void addRecord() {
		boolean fullTime = false;
		Employee theEmployee;

		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(), surnameField.getText().toUpperCase(),
				firstNameField.getText().toUpperCase(), genderCombo.getSelectedItem().toString().charAt(0),
				departmentCombo.getSelectedItem().toString(), Double.parseDouble(salaryField.getText()), fullTime);
		this.parent.currentEmployee = theEmployee;
		this.parent.addRecord(theEmployee);
		this.parent.displayRecords(theEmployee);
	}

	private boolean checkInput() {
		boolean valid = true;

		if (ppsField.getText().equals("")) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (this.parent.correctPps(this.ppsField.getText().trim(), -1)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (surnameField.getText().isEmpty()) {
			surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (firstNameField.getText().isEmpty()) {
			firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (genderCombo.getSelectedIndex() == 0) {
			genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (departmentCombo.getSelectedIndex() == 0) {
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
			salaryField.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		if (fullTimeCombo.getSelectedIndex() == 0) {
			fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		}
		return valid;
	}

	private void setToWhite() {
		ppsField.setBackground(Color.WHITE);
		surnameField.setBackground(Color.WHITE);
		firstNameField.setBackground(Color.WHITE);
		salaryField.setBackground(Color.WHITE);
		genderCombo.setBackground(Color.WHITE);
		departmentCombo.setBackground(Color.WHITE);
		fullTimeCombo.setBackground(Color.WHITE);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save) {

			if (checkInput()) {
				addRecord();
				dispose();
				this.parent.changesMade = true;
			} else {
				JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
				setToWhite();
			}
		}
		else if (e.getSource() == cancel)
			dispose();
	}
}
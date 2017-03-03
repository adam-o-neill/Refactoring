import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	public void createFile(String fileName) {
		RandomAccessFile file = null;
        try
		{
			file = new RandomAccessFile(fileName, "rw");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		}

		finally {
			closeFile(file);
		}
	}

	private void closeFile(RandomAccessFile file){
		try {
			if (file != null)
				file.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}

	public void openWriteFile(String fileName) {
		try
		{
			output = new RandomAccessFile(fileName, "rw");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		}
	}

	public void closeWriteFile() {
		closeFile(output);
	}

	public long addRecords(Employee employeeToAdd) {
		long currentRecordStart = 0;

		RandomAccessEmployeeRecord record;
		try
		{
			record = getRandomAccessEmployeeRecord(employeeToAdd);

			output.seek(output.length());
			record.write(output);
			currentRecordStart = output.length();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;
	}

	private RandomAccessEmployeeRecord getRandomAccessEmployeeRecord(Employee employeeToAdd) {
		RandomAccessEmployeeRecord record;
		record = new RandomAccessEmployeeRecord(employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
                employeeToAdd.getSurname(), employeeToAdd.getFirstName(), employeeToAdd.getGender(),
                employeeToAdd.getDepartment(), employeeToAdd.getSalary(), employeeToAdd.getFullTime());
		return record;
	}


	public void changeRecords(Employee newDetails, long byteToStart) {
		RandomAccessEmployeeRecord record;
		try
		{
			record = getRandomAccessEmployeeRecord(newDetails);

			output.seek(byteToStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	public void deleteRecords(long byteToStart) {
		RandomAccessEmployeeRecord record;

		try
		{
			record = new RandomAccessEmployeeRecord();
			output.seek(byteToStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	public void openReadFile(String fileName) {
		try
		{
			input = new RandomAccessFile(fileName, "r");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not supported!");
		}
	}

	public void closeReadFile() {
		closeFile(input);
	}

	public long getFirst() {
		long byteToStart = 0;

		try {
			input.length();
		}
		catch (IOException e) {
			System.out.print(e.getMessage());
		}
		
		return byteToStart;
	}

	public long getLast() {
		long byteToStart = 0;
		try {
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}
		catch (IOException e) {
			System.out.print(e.getMessage());
		}

		return byteToStart;
	}

	public long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);

			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException | IOException e) {
			System.out.print(e.getMessage());
		}

		return byteToStart;
	}

	public long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);

			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException | IOException e) {
			System.out.print(e.getMessage());
		}

		return byteToStart;
	}

	public Employee readRecords(long byteToStart) {
		Employee thisEmp;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			input.seek(byteToStart);
			record.read(input);
		}
		catch (IOException e) {
			System.out.print(e.getMessage());
		}
		
		thisEmp = record;

		return thisEmp;
	}

	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long currentByte = 0;

		try {

			while (currentByte != input.length() && !ppsExist) {

				if (currentByte != currentByteStart) {
					input.seek(currentByte);
					record.read(input);

					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}
				}
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return ppsExist;
	}

	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		try {

			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);
				record.read(input);

				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return someoneToDisplay;
	}
}
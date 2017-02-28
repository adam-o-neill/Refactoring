/*
 * 
 * This is a Random Access Employee record definition
 * 
 * */

import java.io.RandomAccessFile;
import java.io.IOException;

public class RandomAccessEmployeeRecord extends Employee
{  
    public static final int SIZE = 175; // Size of each RandomAccessEmployeeRecord object

   // Create empty record
   public RandomAccessEmployeeRecord()
   {
      this(0, "","","",'\0', "", 0.0, false);
   } // end RandomAccessEmployeeRecord

   // Initialize record with details
   public RandomAccessEmployeeRecord( int employeeId, String pps, String surname, String firstName, char gender, 
		   String department, double salary, boolean fullTime)
   {
      super(employeeId, pps, surname, firstName, gender, department, salary, fullTime);
   } // end RandomAccessEmployeeRecord

   // Read a record from specified RandomAccessFile
   public void read( RandomAccessFile file ) throws IOException
   {
	   	setEmployeeId(file.readInt());
		setPps(readName(file));
		setSurname(readName(file));
		setFirstName(readName(file));
		setGender(file.readChar());
		setDepartment(readName(file));
		setSalary(file.readDouble());
		setFullTime(file.readBoolean());
   } // end read

   // Ensure that string is correct length
   private String readName( RandomAccessFile file ) throws IOException
   {
      char name[] = new char[ 20 ], temp;

      for ( int count = 0; count < name.length; count++ )
      {
         temp = file.readChar();
         name[ count ] = temp;
      } // end for     
      
      return new String( name ).replace( '\0', ' ' );
   } // end readName

   // Write a record to specified RandomAccessFile
   public void write( RandomAccessFile file ) throws IOException
   {
      file.writeInt( getEmployeeId() );
      writeName(file, getPps().toUpperCase());
      writeName( file, getSurname().toUpperCase() );
      writeName( file, getFirstName().toUpperCase() );
      file.writeChar(getGender());
      writeName(file,getDepartment());
      file.writeDouble( getSalary() );
      file.writeBoolean(getFullTime());
   } // end write

   // Ensure that string is correct length
   private void writeName( RandomAccessFile file, String name )
      throws IOException
   {
      StringBuffer buffer = null;

      if ( name != null ) 
         buffer = new StringBuffer( name );
      else 
         buffer = new StringBuffer( 20 );

      buffer.setLength( 20 );
      file.writeChars( buffer.toString() );
   } // end writeName
} // end class RandomAccessEmployeeRecord
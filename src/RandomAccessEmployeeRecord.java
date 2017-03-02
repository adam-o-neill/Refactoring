import java.io.RandomAccessFile;
import java.io.IOException;

class RandomAccessEmployeeRecord extends Employee
{  
   public static final int SIZE = 175;

   public RandomAccessEmployeeRecord()
   {
      this(0, "","","",'\0', "", 0.0, false);
   }

   public RandomAccessEmployeeRecord( int employeeId, String pps, String surname, String firstName, char gender, 
		   String department, double salary, boolean fullTime)
   {
      super(employeeId, pps, surname, firstName, gender, department, salary, fullTime);
   }

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
   }

   private String readName( RandomAccessFile file ) throws IOException
   {
      char name[] = new char[ 20 ], temp;

      for ( int count = 0; count < name.length; count++ )
      {
         temp = file.readChar();
         name[ count ] = temp;
      }
      
      return new String( name ).replace( '\0', ' ' );
   }

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
   }

   private void writeName( RandomAccessFile file, String name )
      throws IOException
   {
      StringBuffer buffer;

      if ( name != null ) 
         buffer = new StringBuffer( name );
      else 
         buffer = new StringBuffer( 20 );

      buffer.setLength( 20 );
      file.writeChars( buffer.toString() );
   }
}
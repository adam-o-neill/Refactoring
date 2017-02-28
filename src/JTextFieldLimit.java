/*
 * 
 * This is a class for limiting input in text fields
 * 
 * */

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
// set text field input limits
class JTextFieldLimit extends PlainDocument {
  private int limit;
  JTextFieldLimit(int limit) {
    super();
    this.limit = limit;
  }// end JTextFieldLimit

  JTextFieldLimit(int limit, boolean upper) {
    super();
    this.limit = limit;
  }// end JTextFieldLimit

  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
    if (str == null)
      return;

    if ((getLength() + str.length()) <= limit) 
      super.insertString(offset, str, attr);
    else
    	JOptionPane.showMessageDialog(null, "For input " + limit + " characters maximum!");
  }// end insertString
}// end class JTextFieldLimits
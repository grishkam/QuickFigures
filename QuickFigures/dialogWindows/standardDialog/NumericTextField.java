package standardDialog;

import javax.swing.JTextField;


/**Just a text field that keeps track of a number input*/
public class NumericTextField extends JTextField  {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double originalnum=0;
	private int decimalplaces=3;
	

	public NumericTextField(double number, int deci) {
		super(number+"");
		setDecimalPlaces(deci);
	
		setNumber(number);
	
	}
	
	public NumericTextField(double number) {
		this(number, 0);
	}
	
	public void setNumber(double d) {
		originalnum=d;
		this.setText(numberToString(d));
		
	}
	//public void getNumber() {}
	
	public String numberToString(double d) {
		double factor = Math.pow(10, getDecimalPlaces());
		int d2=(int)(d*factor);
		return ""+ (d2/factor);
		
	}
	
	public double getNumberFromField() {
		String st=this.getText();
		try {
			
			double out = Double.parseDouble(st);
			if (Double.isNaN(out)) return originalnum;
			originalnum=out;
			return  originalnum;
		} catch (Throwable t) {
			return originalnum;
		}
	}
	
	public boolean isBlank() {
		return this.getText().trim().equals("");
	}

	public int getDecimalPlaces() {
		return decimalplaces;
	}

	public void setDecimalPlaces(int decimalplaces) {
		this.decimalplaces = decimalplaces;
		this.setColumns(getDecimalPlaces()+4);
	}
	
	
	
}

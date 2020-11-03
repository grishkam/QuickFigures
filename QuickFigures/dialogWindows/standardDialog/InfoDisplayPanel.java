package standardDialog;

import java.awt.Rectangle;

import javax.swing.JLabel;

public class InfoDisplayPanel extends  StringInputPanel{

	private JLabel content=new JLabel();
	
	
	
	public InfoDisplayPanel(String labeln, String contend) {
		super(labeln, contend);
		setContentText(contend);
		// TODO Auto-generated constructor stub
	}
	
	
	public InfoDisplayPanel(String labeln, Rectangle contend) {
		super(labeln, contend.toString());
		setToDimension(contend);
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void setContentText(String contend) {
		//field.setText(contend);
		getTextField().setText(contend);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	protected JLabel getTextField() {
		if (content==null) {
			content=new JLabel();
		}
		return content;
	}


}

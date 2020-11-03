package standardDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringInputPanel extends JPanel implements OnGridLayout, KeyListener{

	JLabel label=new JLabel();
	JTextField field=new JTextField(15); {field.addKeyListener(this);}
	ArrayList<StringInputListener> lis=new ArrayList<StringInputListener>();
	String lasts="";
	private String key;
	private String originalStatus;
	
	
	public StringInputPanel(String labeln, String contend) {
		//field.setColumns(contend.length()+10);
		label.setText(labeln);
		setContentText(contend);
		lasts=contend;
		this.originalStatus=contend;
	}
	
	public StringInputPanel(String labeln, String contend, int fieldLength) {
		//field.setColumns(contend.length()+10);
		label.setText(labeln);
		field.setText(contend);
		lasts=contend;
		field.setColumns(fieldLength);
		this.originalStatus=contend;
	}
	
	void revert() {
		setContentText(originalStatus);
	}
	
	public void setContentText(String contend) {
		field.setText(contend);
	}
	
	

	
	
	public String getTextFromField() {
		return field.getText();
	}
	
	public void addStringInputListener(StringInputListener l) {
		lis.add(l);
	}
	public void removeStringInputListener(StringInputListener l) {
		lis.remove(l);
	}
	public void notifyLiseners(StringInputEvent e) {
		for(StringInputListener l:lis) {l.StringInput(e);}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.insets=firstInsets;
		gc.gridx=x0;
		gc.gridy=y0;
		gc.anchor = GridBagConstraints.EAST;
		jp.add(label, gc);
		gc.gridx++;
		gc.insets=lastInsets;
		gc.anchor = GridBagConstraints.WEST;
		jp.add(getTextField(), gc);
		
		
	}
	
	protected Component getTextField() {
		return field;
	}

	
	@Override
	public int gridHeight() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int gridWidth() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (this.getTextFromField().equals(lasts)||arg0.getSource()!=field) return;
		
		StringInputEvent e = new StringInputEvent(this, this.field, this.getTextFromField());
		e.setKey(key);
		this.notifyLiseners(e);
		lasts=this.getTextFromField();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	public void setKey(String key) {
	this.key=key;
		
	}
	
	public void setToDimension(Rectangle contend) {
		String st= contend.width+" X "+contend.height;
		 setContentText(st);
	}


	
	
}

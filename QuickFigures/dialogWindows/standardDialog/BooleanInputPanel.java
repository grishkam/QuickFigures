package standardDialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BooleanInputPanel extends JPanel implements OnGridLayout, ItemListener{

	JLabel label=new JLabel();
	JCheckBox field=new JCheckBox("", false); {field.addItemListener(this);}
	boolean originAlStatus=false;
	
	ArrayList<BooleanInputListener> lis =new 	ArrayList<BooleanInputListener>();
	private String key;
	public void addBooleanInputListener(BooleanInputListener l) {
		lis.add(l);
	}
	public void removeBooleanInputListener(BooleanInputListener l) {
		lis.remove(l);
	}
	
	
	public BooleanInputPanel(String labeln, boolean b) {
		label.setText(labeln);
		field.setSelected(b);
		 originAlStatus=b;
	}
	
	public BooleanInputPanel(String labeln, boolean b, JCheckBox field2) {
		field=field2;
		field.addItemListener(this);
		label.setText(labeln);
		field.setSelected(b);
		 originAlStatus=b;
	}
	
	public String getTextFromField() {
		return field.getText();
	}
	
	public boolean isChecked() {
		return field.isSelected();
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
		jp.add(field, gc);
		
		
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
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource()==field) {
			BooleanInputEvent bi = new BooleanInputEvent(this, field, field.isSelected());
			bi.setKey(key);
			this.notifyListeners(bi);
		}
		
	}

	
	public void notifyListeners(BooleanInputEvent bi) {
		for(BooleanInputListener l:lis) {
			l.booleanInput(bi);
		}
	}
	public void setKey(String key) {
		this.key=key;
		
	}
	
	/**Changes the status of the item to its original*/
	void revert() {
		field.setSelected(originAlStatus);
	}

	
	
}
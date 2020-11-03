package standardDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ItemSelectblePanel extends JPanel implements OnGridLayout, ItemListener{

	JLabel label=new JLabel();
	UserSelectable box=null;
	
	public UserSelectable getBox() {return box;}
	ArrayList<ChoiceInputListener> listeners=new ArrayList<ChoiceInputListener>();
	private String key;
	private int originalStatus;
	
	
	public ItemSelectblePanel(String labeln, UserSelectable s) {
		label.setText(labeln);
		box=s;
		s.addItemListener(this);
		this.originalStatus=s.getSelectionNumber();
	}
	
	public int getSelectedItemNumber() {
		return getBox().getSelectionNumber();
	}
	
	void revert() {
		getBox().setSelectionNumber(originalStatus);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.EAST;
		gc.insets=firstInsets;
		gc.gridx=x0;
		gc.gridy=y0;
		jp.add(label, gc);
		
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx++;
		gc.insets=lastInsets;
		if (box instanceof Component)jp.add((Component) box, gc);
		
		
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

	




	
	
	 	
	
	public void notifyListeners(ChoiceInputEvent ni) {
		//NumberInputEvent ni = new NumberInputEvent(this, getNumberFromField() );
		for(ChoiceInputListener l :listeners) {
			if(l==null) continue;
			l.numberChanged(ni);
		}
	}
	
	public void addChoiceInputListener(ChoiceInputListener ni) {
		listeners.add(ni);
	}
	public void removeChoiceInputListener(ChoiceInputListener ni) {
		listeners.remove(ni);
	}
	public ArrayList<ChoiceInputListener> getChoiceInputListeners() {
		return listeners;
	}



	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource()==box) {
			ChoiceInputEvent ni = new ChoiceInputEvent(this,(Component) box, box.getSelectionNumber(), box.getSelectedItem()) ;;
			ni.setKey(key);
			this.notifyListeners(ni);
		}
		
	}

	public void setKey(String key) {
		this.key=key;
	}
	
	
}

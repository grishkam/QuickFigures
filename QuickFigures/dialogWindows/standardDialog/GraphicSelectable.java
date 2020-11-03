package standardDialog;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public abstract class GraphicSelectable extends GraphicComponent implements MouseListener, MouseMotionListener, UserSelectable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<ItemListener> listeners=new ArrayList<ItemListener>();
	
	
	@Override
	public void addItemListener(ItemListener l) {
		listeners.add(l);
		
	}
	
	@Override
	public void removeItemListener(ItemListener l) {
		listeners.remove(l);
	}
	
	public void notifyListeners(ItemEvent iv) {
		for(ItemListener l: listeners) {
			l.itemStateChanged(iv);
		}
	}



}

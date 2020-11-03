package standardDialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorInputPanel extends ObjectInputPanel implements OnGridLayout, ColorListChoice, ChangeListener {

	/**
	 * 
	 */
	JLabel label=new JLabel("Text");
	private static final long serialVersionUID = 1L;
	ColorChoicePopup c;
	private String key;
	private ArrayList<ColorInputListener> listeners=new ArrayList<ColorInputListener>();
	private Color originalStatus;
	
	public ColorInputPanel(String st, Color i) {
		label.setText(st);
		c=new ColorChoicePopup(i);
		c.addChangeListener(this);
		this.originalStatus=i;
	}

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=x0;
		gc.gridy=y0;
		gc.insets=firstInsets;
		gc.anchor = GridBagConstraints.EAST;
		jp.add(label, gc);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx++;
	
		gc.insets=lastInsets;
		 jp.add(c, gc);
		
	}

	@Override
	public int gridHeight() {
		return 1;
	}

	@Override
	public int gridWidth() {
		return 3;
	}

	@Override
	public List<Color> getColors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getSelectedColor() {
		// TODO Auto-generated method stub
		return c.getSelectedColor();
	}

	public void setKey(String key) {
		this.key=key;
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		ColorInputEvent oee = new ColorInputEvent(this, c, getSelectedColor());
		
		oee.setKey(this.key);
		this.notifyListeners(oee);
		
	}
	
	public void notifyListeners(ColorInputEvent ni) {
		for(ColorInputListener l :listeners) {
			if(l==null) continue;
			l.ColorChanged(ni);
		}
	}
	
	public void addColorInputListener(ColorInputListener ni) {
		listeners.add(ni);
	}
	public void removeColorInputListener(ColorInputListener ni) {
		listeners.remove(ni);
	}
	public ArrayList<ColorInputListener> getColorInputListeners() {
		return listeners;
	}
	
	void revert() {
		c.setSelectedColor(originalStatus);
	}

	@Override
	public int getRainbow() {
		return 100000;
	}

}

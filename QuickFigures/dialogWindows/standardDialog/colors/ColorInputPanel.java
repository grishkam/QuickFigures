/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2022.2
 */
package standardDialog.colors;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import standardDialog.ObjectInputPanel;
import standardDialog.OnGridLayout;

/**A panel for selecting a color*/
public class ColorInputPanel extends ObjectInputPanel implements OnGridLayout, ColorListChoice, ChangeListener {

	/**
	 * 
	 */
	private static final int RAINBOW_CODE = 100000;
	/**
	 * 
	 */
	JLabel label=new JLabel("Text");
	private static final long serialVersionUID = 1L;
	ColorChoiceSquare c;
	private String key;
	private ArrayList<ColorInputListener> listeners=new ArrayList<ColorInputListener>();
	private Color originalStatus;
	
	public ColorInputPanel(String st, Color i) {
		label.setText(st);
		c=new ColorChoiceSquare(i);
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
		return null;
	}

	@Override
	public Color getSelectedColor() {
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
	
	public void revert() {
		c.setSelectedColor(originalStatus);
	}
	
	public void setSimulateSelectColor(Color color)  {
		notifyListeners(new ColorInputEvent(this, c, color));
	}

	@Override
	public int getRainbow() {
		return RAINBOW_CODE;
	}

}

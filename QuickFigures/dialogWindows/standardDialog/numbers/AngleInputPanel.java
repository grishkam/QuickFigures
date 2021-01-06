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
 * Version: 2021.1
 */
package standardDialog.numbers;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**A subclass of number input panel that allows the user to input the nuber for an angle
 * by dragging an angle box
 * @see AngleBox
 * @see NumberInputPanel
 * */
public class AngleInputPanel extends NumberInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   AngleBox angle=new AngleBox(); {angle.addMouseMotionListener(this);field.setDecimalPlaces(3);field.setColumns(7);}
   
   {includeSlider=false; }
   
   public AngleInputPanel(String name, double angle, boolean includeField) {
	   this.label.setText(name);
	   this.setNumber(angle);
	   this.includeField=includeField;
		this.originalStatus=angle;//the angle should be in radians
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
		
		
		
			gc.insets=middleInsets;
			jp.add(getPanelForContents(angle, field), gc);
			gc.gridx++;
		
		//gc.insets=lastInsets;
		//if (this.includeField)  jp.add(field, gc);
		
	}
   
  
   
   @Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getSource()==field) {
			number=field.getNumberFromField()/(180/Math.PI);
			angle.setAngle(number);
			angle.repaint();
			notifyListeners(new NumberInputEvent(this, field, number) );
			this.repaint();
		}
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getSource()==angle) {
			if (field!=null)
			field.setNumber(angle.getAngle()*180/Math.PI);
			if (angle!=null)
			this.number=angle.getAngle();
			notifyListeners(new NumberInputEvent(this, slider, number) );
			this.repaint();
		}
		
	}

   
   
	public void setNumber(double d) {
		
		number=d;
		if (field!=null)field.setNumber(d*180/Math.PI);
		if (angle!=null)angle.setAngle(d);
	}
	public double getNumber() {
		return number;
	}
	
}

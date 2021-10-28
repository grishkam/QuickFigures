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
 * Date Created: Mar 11, 2021
 * Date Modified: Mar 11, 2021
 * Version: 2021.2
 */
package actionToolbarItems;

import java.awt.Component;
import java.util.ArrayList;

import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import locatedObject.LocatedObject2D;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import undo.CombinedEdit;
import undo.UndoLayoutEdit;

/**Changes the border between panels for layouts selected objects*/
public class SetLayoutProperty extends BasicMultiSelectionOperator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double border=5;

	private static final int[] options2=new int[] { 4, 8, 10, 12, 16};
	
	
	
	public static BasicMultiSelectionOperator[] createManyBorders() {
		int[] options=options2;
		return createManyBorders(options);
	}
	


	/**
	 * @param options
	 * @return
	 */
	public static BasicMultiSelectionOperator[] createManyBorders(int[] options) {
		BasicMultiSelectionOperator[] output=new BasicMultiSelectionOperator[options.length] ;
		for(int i=0; i<options.length; i++) {
			output[i]=new SetLayoutProperty(options[i]);
		}
		return output;
	}
	
	public SetLayoutProperty(double a) {setBorder(a);}
	

	@Override
	public String getMenuCommand() {
		return getBorder()+" Degrees";
	}

	@Override
	public void run() {
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		CombinedEdit c1 = new CombinedEdit();
		for(LocatedObject2D a:all) {
			setLayoutBorderOf(c1, a);
		}
		addUndo(c1);
	}

	/**
	Sets the angle of the object, adds an undo to the list
	 */
	public void setLayoutBorderOf(CombinedEdit c1, LocatedObject2D a) {
		
		if (a instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic defaultLayoutGraphic = (DefaultLayoutGraphic) a;
			UndoLayoutEdit undo = new UndoLayoutEdit(defaultLayoutGraphic);
			defaultLayoutGraphic.getEditor().setVerticalBorder(defaultLayoutGraphic.getPanelLayout(), border);
			defaultLayoutGraphic.getEditor().setHorizontalBorder(defaultLayoutGraphic.getPanelLayout(), border);
			c1.addEditToList(undo);
		}
		
	}
	
	

	
	public double getBorder() {
		
		return border;
	}

	public void setBorder(double a) {
		this.border = a;
	}
	
	
	public Component getInputPanel() {
		return getPaddedPanel(getBorderInput() );
	}
	
	/**creates a JPanel for setting the border between panels*/
	protected NumberInputPanel getBorderInput() {
		
		NumberInputPanel panel = new NumberInputPanel("Set border", this.getBorder(), 2);
		panel.placeItems(panel, 0, 0);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				float value = (float) ne.getNumber();
				SetLayoutProperty runner = new SetLayoutProperty(value);
				runner.setSelector(selector);
				runner.run();
				selector.getWorksheet().updateDisplay();
				
			}
		});
		return panel;
	}

	
}

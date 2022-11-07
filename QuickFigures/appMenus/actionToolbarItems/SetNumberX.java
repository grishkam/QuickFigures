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
package actionToolbarItems;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.LocatedObject2D;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;

/**Sets a numeric value for a group of objects*/
public class SetNumberX extends BasicMultiSelectionOperator {


	/**
		 An object that implements the Value setter determines how this works
		 */
	public static interface ValueSetter {

		/**
		 * @param a
		 * @return 
		 */
		public AbstractUndoableEdit2 createUndo(Object a) ;

		/**
		 * @param a
		 * @param value2
		 */
		public void setValue(Object a, double value2) ;

		/**
		 * @param a
		 * @return
		 */
		public double getValue(Object a);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String name="Value ";
	private double value=45;

	private Object modelItem;

	private Icon icon=new NumberDisplayIcon(this);


	private ValueSetter setter;


	private String[] optionNames;
	
	
	


	/**Creates a list of number setters
	 * @param options
	 * @return
	 */
	public BasicMultiSelectionOperator[] createManyNumberSetters(int[] options) {
		BasicMultiSelectionOperator[] output=new BasicMultiSelectionOperator[options.length] ;
		for(int i=0; i<options.length; i++) {
			output[i]=new SetNumberX(options[i], setter, name, null);
		}
		return output;
	}
	
	/**Creates a list of number setters
	 * @param options
	 * @return
	 */
	public BasicMultiSelectionOperator[] createManyNumberSetters(double[] options) {
		BasicMultiSelectionOperator[] output=new BasicMultiSelectionOperator[options.length] ;
		for(int i=0; i<options.length; i++) {
			output[i]=new SetNumberX(options[i], setter, name, null);
		}
		return output;
	}
	
	/**Creates a list of number setters
	 * @param options
	 * @return
	 */
	public BasicMultiSelectionOperator[] createManyNumberSetters(String[] options) {
		BasicMultiSelectionOperator[] output=new BasicMultiSelectionOperator[options.length] ;
		for(int i=0; i<options.length; i++) {
			output[i]=new SetNumberX(i, setter, name, options);
		
		}
		return output;
	}
	
	public SetNumberX(double a, ValueSetter setter, String name, String[] optionnames) {
		setValue(a);
		this.setter=setter;
		this.name=name;
		this.optionNames=optionnames;
	}
	public SetNumberX(Object a, double start, ValueSetter setter, String name, String[] optionnames) {
		this.modelItem=a;
		this.setter=setter;
		this.setValue(a, setter.getValue(a));
		this.name=name;
		this.setValue(setter.getValue(a));
		this.optionNames=optionnames;
		}

	@Override
	public String getMenuCommand() {
		
		if(this.optionNames!=null)
			return optionNames[(int)getValue()];
		return "Set "+name+" to "+value;
	}

	@Override
	public void run() {
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		CombinedEdit c1 = new CombinedEdit();
		for(LocatedObject2D a:all) {
			setValueOf(c1, a);
		}
		addUndo(c1);
	}

	/**
	Sets the angle of the object, adds an undo to the list
	 */
	public void setValueOf(CombinedEdit c1, LocatedObject2D a) {
		AbstractUndoableEdit2 undo = createUndo(a);
		
		this.setValue(a, this.value);
		if(undo!=null) {
			undo.establishFinalState();
			c1.addEditToList(undo);
		}
	}
	
	/**
	 * @param a
	 * @param value2
	 */
	private void setValue(Object a, double value2) {
		setter.setValue(a, value2);
		
	}

	/**
	 * @param a
	 * @return
	 */
	private AbstractUndoableEdit2  createUndo(LocatedObject2D a) {
		return setter.createUndo(a);
	}

	public Icon getIcon() {
		return icon;
	}

	


	public void setValue(double a) {
		this.value = a;
	}
	
	public double getValue() {
		if(modelItem!=null) {
			value=setter.getValue(modelItem);
		}
		return value;
	}
	
	
	public Component getInputPanel() {
		return getPaddedPanel(getNumerInput() );
	}
	
	/**creates a JPanel for setting the angle*/
	protected NumberInputPanel getNumerInput() {
		
		int min=0;
		int max=50;
		
		NumberInputPanel panel = new NumberInputPanel(name, this.value, min, max);
		panel.placeItems(panel, 0, 0);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				
				SetNumberX runner = new SetNumberX(ne.getNumber(), setter, name, null);
				runner.setSelector(selector);
				runner.run();
				selector.getWorksheet().updateDisplay();
				
			}
		});
		return panel;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**An icon that depicts the angle*/
	public static class NumberDisplayIcon implements Icon {

		/**
		 * 
		 */
		
		private SetNumberX numberSetter;

		public NumberDisplayIcon(SetNumberX setAngle) {
			this.numberSetter=setAngle;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Font oldfont = g.getFont();
			if (g instanceof Graphics2D) {
				Graphics2D g2d=(Graphics2D) g;
				g2d.setStroke(new BasicStroke(1));
				
				
				
				/**Draws and arc with the given angle
				Rectangle rArc=new Rectangle(0,0, 10,10);
				RectangleEdges.setLocation(rArc, RectangleEdges.CENTER, x1, y1);
				Double arc1 = new Arc2D.Double(rArc, 0, angle.getAngle(), Arc2D.PIE);
				g2d.setColor(Color.red);
				g2d.draw(arc1);
				
				g.setColor(Color.black);
				g2d.drawLine(x1, y1, (int) (x1+length), y1);
				
				g.setColor(Color.green.darker());
				g2d.drawLine(x1, y1, (int) (x1+length*Math.cos(a)), (int) (y1-length*Math.sin(a)));
				*/
				
				TextGraphic.setAntialiasedText(g2d, true);
				g2d.setColor(Color.black);
				
				g2d.setFont(new Font("Arial", 0, 10));
				
				g2d.setColor(Color.black);
				String text = ""+numberSetter.getValue();
				if(numberSetter.optionNames!=null)
					text=numberSetter.optionNames[(int)numberSetter.getValue()];
				g2d.drawString(text, x, y+12);
				
			}
			g.setFont(oldfont);
		}

		
		@Override
		public int getIconWidth() {
			return ICON_SIZE;
		}

		@Override
		public int getIconHeight() {
			return ICON_SIZE;
		}

	}
}

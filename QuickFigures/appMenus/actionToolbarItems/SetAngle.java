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
package actionToolbarItems;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.BasicGraphicalObject;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import undo.CombinedEdit;
import undo.UndoScalingAndRotation;

/**Sets the angle for the selected objects*/
public class SetAngle extends BasicMultiSelectionOperator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double angle=45;

	private BasicGraphicalObject modelItem;
	private static final int[] options=new int[] {-45, -30, 0, 30, 45, 60, 90, 135, 180, 210, 270};
	
	public static BasicMultiSelectionOperator[] createManyAngles() {
		BasicMultiSelectionOperator[] output=new BasicMultiSelectionOperator[options.length] ;
		for(int i=0; i<options.length; i++) {
			output[i]=new SetAngle(options[i]);
		}
		return output;
	}
	
	public SetAngle(double a) {setAngle(a);}
	public SetAngle(BasicGraphicalObject a) {
		this.modelItem=a;
		}

	@Override
	public String getMenuCommand() {
		return getAngle()+" Degrees";
	}

	@Override
	public void run() {
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		CombinedEdit c1 = new CombinedEdit();
		for(LocatedObject2D a:all) {
			setAngleOf(c1, a);
		}
		addUndo(c1);
	}

	/**
	Sets the angle of the object, adds an undo to the list
	 */
	public void setAngleOf(CombinedEdit c1, LocatedObject2D a) {
		UndoScalingAndRotation undo = new UndoScalingAndRotation(a);
		if (a instanceof BasicGraphicalObject) {
			((BasicGraphicalObject) a).setAngle(getAngle()*Math.PI/180);
		}
		c1.addEditToList(undo);
	}
	
	public Icon getIcon() {
		return new AngleIcon(this);
	}

	
	public double getAngle() {
		if(this.modelItem!=null) {
			return modelItem.getAngle()*180/Math.PI;
		}
		return angle;
	}

	public void setAngle(double a) {
		this.angle = a;
	}
	
	
	public Component getInputPanel() {
		return getPaddedPanel(getAngleInput() );
	}
	
	/**creates a JPanel for setting the angle*/
	protected NumberInputPanel getAngleInput() {
		
		NumberInputPanel panel = new AngleInputPanel("Set Angle", this.getAngle()*Math.PI/180, true);
		panel.placeItems(panel, 0, 0);
		panel.addNumberInputListener(new NumberInputListener() {
			
			@Override
			public void numberChanged(NumberInputEvent ne) {
				float angle1 = (float) ne.getNumber();
				SetAngle runner = new SetAngle(angle1*180/Math.PI);
				runner.setSelector(selector);
				runner.run();
				selector.getWorksheet().updateDisplay();
				
			}
		});
		return panel;
	}

	/**An icon that depicts the angle*/
	public static class AngleIcon implements Icon {

		/**
		 * 
		 */
		
		private SetAngle angle;

		public AngleIcon(SetAngle setAngle) {
			this.angle=setAngle;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			
			if (g instanceof Graphics2D) {
				Graphics2D g2d=(Graphics2D) g;
				g2d.setStroke(new BasicStroke(1));
				
				double a = angle.getAngle()*Math.PI/180;
				int x1 = x+4;
				int y1 = y+15;
				double length=12;
				
				/**Draws and arc with the given angle*/
				Rectangle rArc=new Rectangle(0,0, 10,10);
				RectangleEdges.setLocation(rArc, RectangleEdges.CENTER, x1, y1);
				Double arc1 = new Arc2D.Double(rArc, 0, angle.getAngle(), Arc2D.PIE);
				g2d.setColor(Color.red);
				g2d.draw(arc1);
				
				/**Draws two lines separated by the given angle*/
				g.setColor(Color.black);
				g2d.drawLine(x1, y1, (int) (x1+length), y1);
				
				g.setColor(Color.green.darker());
				g2d.drawLine(x1, y1, (int) (x1+length*Math.cos(a)), (int) (y1-length*Math.sin(a)));
				g2d.setColor(Color.black);
				
			}
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

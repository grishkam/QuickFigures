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
 * Date Modified: Jan 5, 2021
 * Version: 2022.1
 */
package handles;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.CountParameter;
import locatedObject.LocatedObject2D;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartPopupJMenu;
import standardDialog.StandardDialog;
import undo.SimpleItemUndo;

/**A handle that allows a user to manipulate a count parameter by clicking on the handle*/
public class CountHandle extends SmartHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int ALL_IN_ONE=0, DISPLAY_COUNT=2, COUNT_UP=3, COUNT_DOWN=1;
	int form=ALL_IN_ONE;
	private LocatedObject2D polygon;
	private CountParameter count;
	int oX = 25;
	int oY = 24;
	private boolean loopToStart=false;
	private double widthStretch=2;
	String up = "+";
	String down = "-";
	
	

	public CountHandle(LocatedObject2D regularPolygonGraphic, CountParameter number, int handleNumber) {
		
		polygon=regularPolygonGraphic;
		this.count=number;
		this.setHandleNumber(handleNumber);
		message=count.getValueAsString();
		this.handlesize=10;
	}
	
	public CountHandle(LocatedObject2D r, CountParameter n, int handle, int dx, int dy, boolean h, double expandWidth) {
		this(r,n, handle);
		oX=dx;
		oY=dy;
		loopToStart=h;
		this.widthStretch=expandWidth;
	}
	
	public static CountHandle[] createTriad(LocatedObject2D r, CountParameter n, int handle) {
		CountHandle[] out = new CountHandle[3];
		for(int i=0; i<3; i++) {
			out[i]=new CountHandle(r, n, handle+i);
			out[i].form=COUNT_DOWN+i;
			if(out[i].form!=DISPLAY_COUNT) {
				out[i].widthStretch=0.7;
			}
					}
		
		return out;
	}
	
	public double getDrawnHandleWidth() {
		return (handleSize()*2*widthStretch);
	}
	
	public double getDrawnHandleHeight() {
		return handleSize()*2;
	}
	
	public Point2D getCordinateLocation() {
		
		double px = polygon.getBounds().getMaxX()+oX;
		double py = polygon.getBounds().getMinY()+oY;
		if(form==COUNT_UP) px+=25;
		if(form==COUNT_DOWN) px-=15;
		return new Point2D.Double(px, py);
	}
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		if (count.getNames()!=null) {
			new CountPopupMenu(canvasMouseEventWrapper).showForMouseEvent(canvasMouseEventWrapper);
			return;
		}
		
		SimpleItemUndo<CountParameter> undo = createUndo();
		boolean right=canvasMouseEventWrapper. getClickedXScreen()>lastDrawShape.getBounds().getCenterX();
		if(form!=ALL_IN_ONE) right=true;
		
		if((canvasMouseEventWrapper.clickCount()==2&&form==ALL_IN_ONE&&!right)||form==DISPLAY_COUNT) {
			double i = StandardDialog.getNumberFromUser("Input "+count.parameterName, count.getValue());
			 count.setValue((int)i);
		} else {
		
			double place = this.lastDrawShape.getBounds().getCenterY();
			boolean down=canvasMouseEventWrapper. getClickedYScreen()>place;
			
			if(form==COUNT_UP) down=false;
			if(form==COUNT_DOWN) down=true;
			if(!right)return;
			int nV=count.getValue()+(down?-1:1);
			if(nV<count.getMinValue() &&!loopToStart) return;
			
			if(nV<count.getMinValue() &&loopToStart)  count.setValue(count.getMaxValue());
				else
			if (nV>count.getMaxValue()) count.setValue(count.getMinValue());
				else
				count.setValue(nV);
		}
		undo.establishFinalState();
		canvasMouseEventWrapper.addUndo(undo);
		
		canvasMouseEventWrapper.getAsDisplay().updateDisplay();
	}

	public SimpleItemUndo<CountParameter> createUndo() {
		return new SimpleItemUndo<CountParameter> (count);
	}
	
	/**Draws the text of the handle*/
	protected void drawMessage(Graphics2D graphics, Shape s) {
		
			graphics.setColor(messageColor);
			graphics.setFont(getMessageFont());
			int y2 = (int)(s.getBounds().getCenterY()/2+s.getBounds().getMaxY()/2);
			int x2 = (int)s.getBounds().getX()+4;
			String valueAsString = count.getValueAsString();
			if(form==COUNT_DOWN) {valueAsString=down;}
			if(form==COUNT_UP) {valueAsString=up;}
			
			
			graphics.drawString(valueAsString, x2, y2);
			
			if(form==ALL_IN_ONE&&count.getNames()==null) {
				drawUpsAndDown(graphics, s, y2);
			}
	}

	/**
	draws lines and marks to create a + for count up and a - for count down
	in regions that the user may click
	 */
	public void drawUpsAndDown(Graphics2D graphics, Shape s, int y2) {
		float size2 = (float)(getMessageFont().getSize()/2);
		graphics.setFont(getMessageFont().deriveFont(size2));
		double tX =s.getBounds().getCenterX();// x2+12+end;
		graphics.drawLine((int)tX, (int)s.getBounds().getMinY(), (int)tX, (int)s.getBounds().getMaxY());
		graphics.drawLine((int)tX, (int)s.getBounds().getCenterY(), (int)s.getBounds().getMaxX(), (int)s.getBounds().getCenterY());
		
		tX+=5;
		graphics.drawString(up, (float) tX+3, y2-size2-2);
		graphics.drawString(down, (float) tX+4, y2+size2/2);
	}
	
	public boolean handlesOwnUndo() {
		return true;
	}
	
	/**A menu that gives the user a means to choose */
	public class CountPopupMenu extends SmartPopupJMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CountPopupMenu(CanvasMouseEvent canvasMouseEventWrapper) {
			for(int i=0; i<count.getNames().length; i++) {
				String name=count.getNames()[i];
				BasicSmartMenuItem m=new CountMenuItem (name, canvasMouseEventWrapper) ;
				add(m);
			}
		}

		/**
		 * 
		 */
		public void createItems() {
			
			
		}
	}
	
	/**A menu item for switching the count*/
	public class CountMenuItem extends BasicSmartMenuItem {
		public CountMenuItem(String string, CanvasMouseEvent canvasMouseEventWrapper) {
			super(string);
			this.setActionCommand(string);
			setLastMouseEvent(canvasMouseEventWrapper);
			this.setUndoManager(canvasMouseEventWrapper.getUndoManager());
			
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SimpleItemUndo<CountParameter> undo = createUndo();
					count.setValue(e.getActionCommand());
					undo.establishFinalState();
					getUndoManager().addEdit(undo);
				}
				
			});
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	
		
		
	
	}

	

}
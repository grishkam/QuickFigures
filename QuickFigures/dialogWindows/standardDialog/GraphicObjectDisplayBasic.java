/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package standardDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_BasicShapes.SimpleGraphicalObject;
import logging.IssueLog;

public class GraphicObjectDisplayBasic<Type extends SimpleGraphicalObject> extends JComponent implements Icon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Type currentDisplayObject;//=new TextGraphic();
	private double magnification=1;
	private Insets currentItemInsets=new Insets(2, 2, 2, 2);
	private Point locationInIcon=new Point(0,0);
	

private boolean relocatedForIcon=false;

public 	GraphicObjectDisplayBasic() {}
public 	GraphicObjectDisplayBasic(Type t) {
	this.setCurrentDisplayObject(t);
}

public double getMagnification() {
	return magnification;
}

public void setMagnification(double magnification) {
	this.magnification = magnification;
}
	
	@Override
	public void paintIcon(Component arg0, Graphics g, int arg2, int arg3) {
		Color oldcol = g.getColor();
		Font oldcon=g.getFont();
		if (currentDisplay()==null) return;
		if ( isRelocatedForIcon() &&currentDisplay()!=null) {
			
				currentDisplay().setLocationUpperLeft(arg2, arg3);
				currentDisplay().setLocationUpperLeft(getLocationInIcon().x, getLocationInIcon().y);
						}
		
		
		currentDisplay().draw((Graphics2D) g, new BasicCoordinateConverter(0-getCurrentItemInsets().left-arg2/this.getMagnification(),0-getCurrentItemInsets().top-arg3/this.getMagnification(),this.getMagnification()));
		
		g.setFont(oldcon);
		g.setColor(oldcol);
	}
	

	public SimpleGraphicalObject currentDisplay() {
		 return getCurrentDisplayObject();
	}
	
	
	
	public boolean isRelocatedForIcon() {
		return relocatedForIcon;
	}

	public void setRelocatedForIcon(boolean relocatedForIcon) {
		this.relocatedForIcon = relocatedForIcon;
	}
	
	
	
	public SimpleGraphicalObject getCurrentDisplayObject() {
		return currentDisplayObject;
	}

	public void setCurrentDisplayObject(Type currentDisplayObject) {
		this.currentDisplayObject = currentDisplayObject;
	}
	
	public Insets getCurrentItemInsets() {
		return currentItemInsets;
	}

	public void setCurrentItemInsets(Insets currentItemInsets) {
		this.currentItemInsets = currentItemInsets;
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return this.getHeight();
	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return this.getWidth();
	}


	public Point getLocationInIcon() {
		return locationInIcon;
	}

	public void setLocationInIcon(Point locationInIcon) {
		this.locationInIcon = locationInIcon;
	}
	
	Dimension getdimOfCurrent() {
		if (currentDisplay()==null) return new Dimension(0,0);
	
		Rectangle b = currentDisplay().getBounds();
		
		if (this.getMagnification()!=1) {
			b=currentMagConverter().getAffineTransform().createTransformedShape(b).getBounds();
		}
		return new Dimension(b.width+getCurrentItemInsets().left+getCurrentItemInsets().right, b.height+getCurrentItemInsets().top+getCurrentItemInsets().bottom);
	}
	
	
	BasicCoordinateConverter currentMagConverter() {
		return new BasicCoordinateConverter(0,0,getMagnification());
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getdimOfCurrent();
	
	}
	
	public int getHeight() {
		return  getPreferredSize().height;
	}
	
	
	public int getWidth() {
		return  getPreferredSize().width;
	}
	
	@Override
	public void paintComponent(Graphics g) {
	
		try {
			
			
		
			currentDisplay().draw((Graphics2D) g, new BasicCoordinateConverter());
			
			
		} catch (Exception e) {
	
			IssueLog.logT(e);
		}
		
	}
	


}

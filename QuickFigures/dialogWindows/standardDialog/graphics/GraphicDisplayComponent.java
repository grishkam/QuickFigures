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
 * Version: 2022.0
 */
package standardDialog.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.TextGraphic;
import logging.IssueLog;

/**A component object that contains a Graphic. Can be used as a Menu item, icon or other part of a
  GUI
  @see SimpleGraphicalObject
  */
public class GraphicDisplayComponent extends GraphicObjectDisplayBasic<SimpleGraphicalObject> implements DisplaysGraphicalObject, Icon{

	/**
	 * 
	 */
	static Color standardBG=new Color(42,91,214);//The color around the slected item
	public Color canvasColor = Color.white;//the color around all ther other items
	private static final long serialVersionUID = 1L;
	
	private boolean selected=false;
	
	
	 static Font defaultFont=new Font("SansSerif", 0, 12);
	
	private Icon icon=null;
	
	TextGraphic textRep=new TextGraphic();{textRep.setTextColor(Color.black);textRep.setFont(defaultFont); textRep.getBackGroundShape().setFilled(true);textRep.getBackGroundShape().setFillColor(standardBG);}
	
	private boolean hideText; 
	
	

	public SimpleGraphicalObject currentDisplay() {
		
		if (getCurrentDisplayObject()==null) setCurrentDisplayObject(new RectangularGraphic());
		 return getCurrentDisplayObject();
	}
	
	 
	 Dimension iconDim() {
		 if (getIcon()==null) return new Dimension(0,0);
		 return new Dimension(getIcon().getIconWidth(),getIcon().getIconHeight());
	 }
	 
	 Dimension textDim() {
		 if (hideText) return new Dimension(0,0);
		 return new Dimension(textRep.getBounds().width,textRep.getBounds().height);
	 }
	
	 
	 public GraphicDisplayComponent(String text, SimpleGraphicalObject simpleGraphicalObject, boolean selected) {
		if (text==null) hideText=true; else hideText=false;
		 this.setCurrentDisplayObject(simpleGraphicalObject);
		 textRep.setText(text);
		this.setSelected(selected);
		
	 }
	 
	 public GraphicDisplayComponent(SimpleGraphicalObject simpleGraphicalObject, double mag) { 
		 this(null,simpleGraphicalObject, false );
		 this.setMagnification(mag);
	 }
	 
	 public GraphicDisplayComponent(SimpleGraphicalObject simpleGraphicalObject) { 
		 this(simpleGraphicalObject, 1 );
		
	 }
	 
	public GraphicDisplayComponent(TextGraphic t, boolean selected) {
		super();
		try {
			
				
			this.setSelected(selected);
		
			
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
	}
	
	

	

	

	@Override
	public Dimension getPreferredSize() {
		/**The heights and widths of each part. */
		Dimension dim = getdimOfCurrent();//the dimensions of the currently displayed object
		Dimension dim2 = iconDim();
		Dimension dim3 = textDim();
		
		/**combined dimensions needed assuming the parts are arranged horozontally*/
		int width=dim.width+dim2.width+dim3.width+2;
		int height=Math.max(dim.height,dim2.height);
		 height=Math.max(dim3.height,height)+2;
		 
		//if (dim3.height>height) height=dim3.height;
		return  new Dimension(width,  height) ;
	}
	

	
	
	@Override
	public void paintComponent(Graphics g) {
	
		try {
			
				if (isSelected()) g.setColor(standardBG);
				else {
					
					g.setColor(canvasColor);
				}
				g.fillRect(0, 0, this.getWidth(), getHeight());
			
			
			
			if (!this.hideText) {
				this.textRep.setLocationUpperLeft(0, 0);
				textRep.draw((Graphics2D) g, new BasicCoordinateConverter(0,0,1));
			}
		
			currentDisplay().draw((Graphics2D) g, currentDisplayConverter());
			
			
		} catch (Exception e) {
	
			IssueLog.logT(e);
		}
		
	}
	
	
	public  BasicCoordinateConverter currentDisplayConverter() {
		return new BasicCoordinateConverter(this.currentDisplay().getLocationUpperLeft().getX()-textDim().width-getCurrentItemInsets().left,this.currentDisplay().getLocationUpperLeft().getY()-getCurrentItemInsets().top,getMagnification());
	}
	
	


	public boolean isSelected() {
		
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	
	}




	public Icon getIcon() {
		return icon;
	}


	public void setIcon(Icon icon) {
		this.icon = icon;
	}

public static void main(String[] args) {
	JFrame f = new JFrame();
	f.setLayout(new FlowLayout());
	RectangularGraphic r1 = new RectangularGraphic(100,100,50,10);
	r1.setStrokeColor(Color.blue);
	GraphicDisplayComponent gg = new GraphicDisplayComponent("Choice 1",r1, true);
	f.add(gg);
	RectangularGraphic r2 = new RectangularGraphic(8,80,50,80);
	GraphicDisplayComponent gg2 = new GraphicDisplayComponent("Choice 2", r2, false);
	f.add(gg2);
	PathGraphic pp = new PathGraphic(new Point(3,2)); pp.addPoint(new Point(16,20));
	pp.setStroke(new BasicStroke(5));
	pp.setName("path1");
	
	JLabel j1 = new JLabel("icon 1");
	r1.setFillColor(Color.orange);
	j1.setIcon(new GraphicObjectDisplayBasic<RectangularGraphic>(r1));
	f.add(j1);
	
	f.pack();
	f.setVisible(true);
}

public void setText(String text) {
	textRep.setText(text);
	
}


	
}

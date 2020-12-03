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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JFrame;

import graphicalObjects_BasicShapes.RectangularGraphic;
import utilityClassesForObjects.RectangleEdges;

public class OffsetBox extends GraphicComponent implements KeyListener, MouseMotionListener{

	
	Shape componentCord=null;
	int border=0;
	
	RectangularGraphic r=RectangularGraphic.filledRect(new Rectangle(border,border, 40,30)); {
		r.setLocationType(RectangleEdges.UPPER_LEFT);
		
		r.setFillColor(Color.CYAN.darker());
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	final NumericTextField textX=new NumericTextField(0) ;
	final NumericTextField textY=new NumericTextField(0) ;
	
	{
	textX.addKeyListener(this);
	textY.addKeyListener(this);
	this.addMouseMotionListener(this);
	}
	
	double xOff=0;
	double yOff=0;



	public OffsetBox() {
		
		}
	
	public OffsetBox(String string, double xi, double yi) {
		
		setNumbers(xi,yi);
		
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void setNumbers(double xi, double yi) {
			textX.setNumber(xi);
			textY.setNumber(yi);
		setValuesToNumberFields();
	}
	 
	

	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		OffsetBox sb = new OffsetBox("Box 1", 5,5);
		ff.add(sb.textX);
		ff.add(sb.textY);
		ff.add(sb);
		ff.pack();
		
		ff.setVisible(true);
	}



	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		setValuesToNumberFields();
		this.repaint();
	}

	public void setValuesToNumberFields(){
		xOff=textX.getNumberFromField();
		yOff=textY.getNumberFromField();
		r.setLocation(border+(int)xOff, border+ (int)yOff);
	} 
	
	public Dimension getPreferredSize() {
		
        return new Dimension(50,50);
    }
	
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		//getSnappingBehaviour().snapLocatedObjects(r2, r1);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		
		componentCord=getCord().getAffineTransform().createTransformedShape(r.getBounds());
		r.draw((Graphics2D) g, getCord());
		
		
	}
	
	
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		if (componentCord!=null&&componentCord.contains(arg0.getX(), arg0.getY())) 
		{}
			try {
				Point p = new Point(arg0.getX(), arg0.getY());
				Point2D p2=getCord().unTransformP(p);
				
				this.setNumbers(p2.getX(), p2.getY());
				this.repaint();
				
		}
		catch (Throwable t) {}
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}

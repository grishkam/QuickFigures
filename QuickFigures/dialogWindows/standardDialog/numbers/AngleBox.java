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
 * Version: 2021.2
 */
package standardDialog.numbers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JFrame;

import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_SpecialObjects.BarGraphic;
import standardDialog.graphics.GraphicComponent;

/**A component that allows a user to input an angle by dragging the mouse at the angles positions
  the graphic indicates the selected angle with lines*/
public class AngleBox extends GraphicComponent implements MouseListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double angle=0;
	
	Point center=new Point(95,95);
	BarGraphic handle=new BarGraphic(); {setBarProp(handle);}
	BarGraphic handle2=new BarGraphic(); {setBarProp(handle2);
	handle2.setFillColor(Color.LIGHT_GRAY);
	handle2.setStrokeColor(Color.lightGray);handle2.setStrokeWidth(4);}
			{	this.addMouseMotionListener(this);
				super.getCord().setMagnification(1);
			}
	

	/**sets the graphic to the right color and locaion to indicate the angle */
	private void setBarProp(BarGraphic r1) {
		r1.setLengthProjection(0);
		r1.setFillColor(Color.black);
		r1.setFilled(true);
		r1.setStrokeColor(Color.gray);
		r1.setShowText(false);
		r1.setLengthInUnits(80);
		r1.setBarStroke(8);
		r1.setLocation((int)center.getX(), (int)center.getY());
;
	}
	

	@Override
	public void mouseDragged(MouseEvent arg0) {
		Point2D p1 = this.getCord().unTransformP(new Point(arg0.getX(), arg0.getY()));
		setAngle(BasicGraphicalObject.distanceFromCenterOfRotationtoAngle(center, p1));
		handle.setAngle(getAngle());
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		
		
		handle2.draw((Graphics2D) g,getCord());
		handle.draw((Graphics2D) g, cords);
	}
	
	public int getHeight() {
		return 35;
		
	}
	
	public int getWidth() {
		return 35;
		
	}
	
	  public Dimension getPreferredSize() {
	        return new Dimension(getWidth(),getHeight());
	    }
	  
	

		public double getAngle() {
			return angle;
		}

		public void setAngle(double angle) {
			this.angle = angle;
			handle.setAngle(angle);
		}

	

}

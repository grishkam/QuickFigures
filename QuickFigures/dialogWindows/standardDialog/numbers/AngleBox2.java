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
 * Date Modified: Jan 11, 2021
 * Date Created: Jan 11, 2021
 * Version: 2021.1
 */
package standardDialog.numbers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import graphicalObjects.BasicGraphicalObject;
import locatedObject.RectangleEdges;

/**
 A component containing a drawing of an angle. Use mouse presses and drags will change the angle
 */
public class AngleBox2 extends JComponent implements MouseListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double angle=0;
	int x=18;
	int y=18;
	
	
	public AngleBox2(double a) {
		setAngleInDegrees(a);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		
		g.setColor(Color.white);
		g.fillRect(0,0, this.getWidth(), this.getHeight());
		int x1 =x;
		int y1=y;
		
		if (g instanceof Graphics2D) {
			Graphics2D g2d=(Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setStroke(new BasicStroke(2));
			
			double a = getAngleInDegress()*Math.PI/180;
			
			double length=16;
			
			/**Draws and arc with the given angle*/
			Rectangle rArc=new Rectangle(0,0, 10,10);
			RectangleEdges.setLocation(rArc, RectangleEdges.CENTER, x1, y1);
			Double arc1 = new Arc2D.Double(rArc, 0, getAngleInDegress(), Arc2D.PIE);
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
	public void mouseClicked(MouseEvent e) {
		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDragged(e);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D p1 = new Point(e.getX(), e.getY());
		setAngleInDegrees(BasicGraphicalObject.distanceFromCenterOfRotationtoAngle(new Point2D.Double(x, y), p1)*180/Math.PI);
		
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		
	}
	
	public int getHeight() {
		return 36;	
	}
	
	public int getWidth() {
		return 36;
	}
	
	  public Dimension getPreferredSize() {
	        return new Dimension(getWidth(),getHeight());
	    }

	public double getAngleInDegress() {
		return angle;
	}

	public void setAngleInDegrees(double angle) {
		this.angle = angle;
	}

	/**
	 * @return
	 */
	public double getAngle() {
		return angle*Math.PI/180;
	}

	/**
	 * @param number
	 */
	public void setAngle(double number) {
		angle=number/(Math.PI/180);
	}
	
	
}

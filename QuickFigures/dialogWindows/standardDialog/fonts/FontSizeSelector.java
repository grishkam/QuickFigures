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
 * Version: 2023.1
 */
package standardDialog.fonts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JFrame;

import graphicalObjects_SpecialObjects.TextGraphic;
import handles.SmartHandleForText;
import standardDialog.graphics.GraphicComponent;

/**A component that allows the user to resize a font by dragging */
public class FontSizeSelector  extends GraphicComponent implements MouseListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{this.addMouseListener(this); this.addMouseMotionListener(this);setMagnification(1);}
	
	private float fontSize=12;
	private int handleclick=-1;
	TextGraphic textItem=new TextGraphic("Text");
	 {textItem.select();textItem.setLocationUpperLeft(10, 40); textItem.setTextColor(Color.black);}
	public void setFont(Font f) {
		textItem.setFont(f);
		setFontSize(f.getSize());
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
		Point2D p =new Point(arg0.getX(), arg0.getY());
		handleclick=textItem.handleNumber((int)p.getX(), (int)p.getY());
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		Point2D p = this.getCord().unTransformClickPoint(arg0);//.getInverse().transformP(new Point(arg0.getX(), arg0.getY()));
		if(handleclick>0) {
			textItem.handleMove(handleclick, getMousePosition(), new Point( (int)p.getX(), (int)p.getY()));
			if (SmartHandleForText.TEXT_FONT_SIZE_HANDLE==handleclick) {
				double newsize = textItem.getBaseLineStart().getY()-p.getY();
				textItem.setFontSize((int) newsize);
			}
			
			setFontSize(textItem.getFont().getSize());
		
		}
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}

	@Override
	public void paintComponent(Graphics g) {
		
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(),  this.getHeight());
		
		
		textItem.draw((Graphics2D) g,getCord());
	
	}
	
	public int getHeight() {
		return 60;
		
	}
	
	public int getWidth() {
		return 150;
		
	}
	
	  public Dimension getPreferredSize() {
	        return new Dimension(getWidth(),getHeight());
	    }
	
	  
		public static void main(String[] args) {
			JFrame ff = new JFrame("frame");
			ff.setLayout(new FlowLayout());
			ff.add(new JButton("button"));
			FontSizeSelector sb = new FontSizeSelector();
			ff.add(sb);
			ff.pack();
			
			ff.setVisible(true);
		}

		public float getFontSize() {
			return fontSize;
		}

		public void setFontSize(float fontSize) {
			this.fontSize = fontSize;
		}

	
	
}

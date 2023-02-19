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
package standardDialog.colors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import graphicalObjects_Shapes.ArrowGraphic;
import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;
import standardDialog.graphics.GraphicDisplayComponent;


/**the color cell rendered for the color combo box. */
public class ColorCellRenderer extends BasicComboBoxRenderer implements ListCellRenderer<Object>{
	/**
	 * 
	 */
	int chosenWidth=60;
	int chosenHeight=20;
	Insets it=new Insets(2, 2, 2, 2);
	int drawWidthSelection=15;
	boolean includeArrow=true;
	int arrowSpace=20;
	boolean rainbow=false;
	
	private static final long serialVersionUID = 1L;
	 
	public Color currentColor=Color.black;
	

	private ColorListChoice colorComboBox;
	private boolean hasFocu;
	

	public ColorCellRenderer(ColorListChoice colorComboBox) {
		this.colorComboBox=colorComboBox;
	}
	
	public static ColorCellRenderer getPalleteRenderer(ColorListChoice colorComboBox) {
		ColorCellRenderer output = new ColorCellRenderer(colorComboBox);
		
		output.chosenWidth=30;
		output.chosenHeight=30;
		output.arrowSpace=0;
		output.includeArrow=false;
		
		return output;
	}

	public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	
		
		Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (out instanceof ColorCellRenderer && value!=null) {
			ColorCellRenderer cell=(ColorCellRenderer) out;
			if (isSelected) cell.selected=true; else cell.selected=false;
			
			if (index==colorComboBox.getRainbow())
				{rainbow=true;
				
				} else rainbow=false;
			
			out.setForeground(BasicMetaDataHandler.getColor(value.toString()));
			if (index<colorComboBox.getColors().size()&&index>-1) {
				Color c2=colorComboBox.getColors().get(index);
				out.setForeground(c2);
				
				cell.currentColor=c2;
				this.currentColor=c2;
			
			}
			
		
		}
		
		this.selected=isSelected;
		this.hasFocu=cellHasFocus;
		
		return this;
			}
	boolean selected=false;
	
	@Override
	public void paintComponent(Graphics g) {
	
		if (g instanceof Graphics2D) {

			Graphics2D g2d=(Graphics2D) g;
			
			//if (currentColor==null) currentColor=new Color(0,0,0,0);
		boolean isTransparent=currentColor==null||currentColor.getAlpha()==0;
		if (currentColor==null)IssueLog.log("Drawing null color");
			
		if (selected&&!isTransparent) {
				g.setColor(Color.darkGray);
				g.fillRect(30+it.left, it.top, drawWidthSelection, this.getHeight()-it.top-it.bottom);
			}
		
		Rectangle rr = new Rectangle(arrowSpace+it.left, it.top, this.getWidth()-it.left-it.right-arrowSpace,  this.getHeight()-it.top-it.bottom);
		
		//g.setColor(currentColor);
		if (currentColor!=null) g2d.setPaint(currentColor);
		
		
		if (rainbow) {
			g2d.setPaint(getRaindowGradient(rr));
			
		}
		g2d.fill(rr);//.fillRect(arrowSpace+it.left, it.top, this.getWidth()-it.left-it.right,  this.getHeight()-it.top-it.bottom);
		
		if (isTransparent) {
			g2d.setColor(Color.white);
			g2d.setPaint(Color.white);
			g2d.fill(rr);
			drawXAcrossREct(g2d, rr);
		}
		
		g.setColor(Color.black);
		
			g2d.setStroke(new BasicStroke(2));
			if (hasFocu)g2d.draw(rr);
		}
		
		if (selected&&includeArrow) {
		//	g.setColor(Color.darkGray);
			//g.fillRect(0, 0, 15, this.getHeight());
			ArrowGraphic ar = ArrowGraphic.createDefaltOutlineArrow(getForeground().brighter(), Color.black);
			ar.setStrokeWidth(4);
			ar.getHead().setArrowHeadSize(15);
			ar.setPoints(new Point(0,10), new Point(22,10));
			GraphicDisplayComponent i = new GraphicDisplayComponent(ar);
			i.paintIcon(this, g, 0, 0);
			
		}
	}
	
	public static Color[] standardRBColors=new Color[] {Color.red, Color.GREEN, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.white, Color.black};
	
	
	public static Paint getRaindowGradient(Rectangle rr) {
		float[] fracs=new float[standardRBColors.length]; fracs[0]=(float) (1.0/fracs.length); for(int i=1; i<fracs.length; i++) {fracs[i]=fracs[i-1]+(float) (1.0/fracs.length);}
		return new LinearGradientPaint(rr.x, rr.y, (float) rr.getMaxX(), (float) rr.getMaxY(),  fracs, standardRBColors);
	}

	public static void drawXAcrossREct(Graphics2D g2d, Rectangle rr) {
		g2d.setColor(Color.red);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setStroke(new BasicStroke(2));
		g2d.drawLine(rr.x, rr.y, rr.x+rr.width, rr.y+rr.height);
		g2d.drawLine(rr.x+rr.width, rr.y, rr.x, rr.y+rr.height);
	}
	
	 public Dimension getPreferredSize() {
	        return new Dimension(chosenWidth+it.left+it.right,chosenHeight+it.top+it.bottom);
	    }
}
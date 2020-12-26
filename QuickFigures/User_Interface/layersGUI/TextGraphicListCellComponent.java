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
package layersGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import logging.IssueLog;

/**Class for rendering tree cells of the Layers GUI*/
public class TextGraphicListCellComponent extends JComponent {

	/**
	 * 
	 */
	static Color standardBG=new Color(42,91,214);
	private static final long serialVersionUID = 1L;
	
	private int minimumWidth=0;
	private boolean selected=false;
	
	private Insets textInsets=new Insets(1, 1, 1, 1);
	 static Font defaultFont=new Font("SansSerif", 0, 12);
	
	private Icon icon=null;
	 private ComplexTextGraphic complexRepresentation=new ComplexTextGraphic();{innitialiseGraphicText(complexRepresentation);}
	 TextGraphic loc2=new TextGraphic(); {innitialiseGraphicText(loc2);}
	 boolean complex=false;
	private static int fontSize=12;
	
	public void innitialiseGraphicText(TextGraphic loc2) {
		{loc2.setTextColor(Color.black);loc2.setFont(defaultFont); loc2.getBackGroundShape().setFillColor(standardBG);loc2.setFillBackGround(true);}
	}
	
	 
	 Dimension iconDim() {
		 if (getIcon()==null) return new Dimension(0,0);
		 return new Dimension(getIcon().getIconWidth(),getIcon().getIconHeight());
	 }
	
	 
	 public TextGraphicListCellComponent(String text, boolean selected) {
		 this.setText(text);
		
		this.setSelected(selected);
		
	 }
	 
	public TextGraphicListCellComponent(TextGraphic t, boolean selected) {
		super();
		try {
			
				
			this.setSelected(selected);
			setToImmitate(t);
			
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
	}
	
	
	public void setToImmitate(TextGraphic t) {
	
			if (t instanceof ComplexTextGraphic ) {
				this.getComplexRepresentation().setParagraph(((ComplexTextGraphic)t).getParagraph().copy());
				complex=true;
				} else { complex=false;
				loc2.setText(t.getText());
				}
			this.currentDisplay().setFont(defaultFont);
		
			this.currentDisplay().setTextColor(t.getTextColor());
			currentDisplay().setFont( deriveFont(t.getFont()));
	}
	
	public Font deriveFont(Font f) {
		return	new Font(f.getFamily(), f.getStyle(), TextGraphicListCellComponent.fontSize);
	}
	
	public TextGraphic currentDisplay() {
		if (complex) return getComplexRepresentation();
		else return loc2;
	}
	
	Dimension getdimOfCurrent() {
		Rectangle b = currentDisplay().getBounds();
		return new Dimension(b.width+textInsets.left+textInsets.right, b.height+textInsets.top+textInsets.bottom);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = getdimOfCurrent();
		Dimension dim2 = iconDim();
		int width=dim.width+dim2.width+4;
		if (width<this.getMinimumWidth()) width=this.getMinimumWidth();
		int height=dim.height>dim2.height? dim.height:dim2.height;
		
		return  new Dimension(width,  height) ;
	}
	
	public int getHeight() {
		return  getPreferredSize().height;
	}
	
	
	public int getWidth() {
		return  getPreferredSize().width+4;
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
	
		try {
			this.currentDisplay().setFillBackGround(true);
			
			currentDisplay().getBackGroundShape().setFilled(true);
			currentDisplay().getBackGroundShape().setFilled(isSelected());
			
		if (this.getIcon()!=null) {
			getIcon().paintIcon(this, g, 0, (this.getHeight()-getIcon().getIconHeight())/2);
		}
			currentDisplay().draw((Graphics2D) g, new BasicCoordinateConverter(-this.iconDim().width-textInsets.left ,-this.currentDisplay().getFont().getSize()-textInsets.top,1));
			
			
		} catch (Exception e) {
	
			IssueLog.logT(e);
		}
		
	}

	public String getText() {
		return currentDisplay().getText();
	}

	public void setText(String text) {
	
		currentDisplay().setText(text);
	}

	public boolean isSelected() {
		
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		currentDisplay().setFillBackGround(selected);
	}

	public ComplexTextGraphic getComplexRepresentation() {
		if (this.complexRepresentation==null) this.complexRepresentation=new ComplexTextGraphic();
		return complexRepresentation;
	}

	public void setComplexRepresentation(ComplexTextGraphic complexRepresentation) {
		
		this.complexRepresentation = complexRepresentation;
	}
	
	void setSelectionColor(Color c) {
		this.currentDisplay().getBackGroundShape().setFillColor(c);
		
	}


	public Icon getIcon() {
		return icon;
	}


	public void setIcon(Icon icon) {
		this.icon = icon;
	}


	public int getMinimumWidth() {
		return minimumWidth;
	}


	public void setMinimumWidth(int minimumWidth) {
		this.minimumWidth = minimumWidth;
	}
	
	
}

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
package utilityClassesForObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;

public class TextDescriptor implements TextItem {
	public String label="";
	public Font font=new Font("Arial", 1, 20);
	public Color color=Color.black;
	public double angle=0;
	public int drawType=0;
	public int x=0;
	public int y=0;
	public int width=0;
	public int height=0;
	static BufferedImage fmImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	
	boolean multilin=false;
	boolean multiseg=false;
	private FontMetrics fontMetrics;
	
	public TextDescriptor(String label, Font font, Color c, Double a) {
		this.label=label;
		this.font=font;
		color=c;
		angle=a;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return label;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public Color getTextColor() {
		return color;
	}

	@Override
	public double getAngle() {
		return angle;
	}


	@Override
	public void setFont(Font font) {
		this.font=font;
	}

	@Override
	public void setTextColor(Color c) {
		this.color=c;
	}

	@Override
	public void setText(String st) {
		this.label=st;
		
	}
	
	
	public void parseAleration() {
		
	}

static public String multiplabelDelimiter = "///";
static public String multiplabelDelimeter2 = ".new";


/**the next few conditionals decode some commands that may be present in the text of the string.
   that can force modifications of the text properties. This is meant to be part of a way the
   user can override automatic parts of the code.
 */
public void parseAlteration() {

if (label.contains("Super[]") ) {
	font=new Font(font.getFamily(), font.getStyle(), font.getSize()/2);
	label=BasicMetaDataHandler.removeImpliedMethod(label, "Super");//label.replace("Super[]", "");
	setFont(font);
	}
if (label.contains("Color[")) {
	Color c = BasicMetaDataHandler .getColor(label);
	setTextColor(c);
	label=BasicMetaDataHandler .removeImpliedMethod(label, "Color");
	}

if (label.contains("Font[")) {
	Font f=getFont();
	if (f==null) IssueLog.log("null font");
	font =BasicMetaDataHandler .getFont(f, label);
	setFont(font);
	label=BasicMetaDataHandler .removeImpliedMethod(label, "Font");
}
}

@Override
public void cleanUpText() {
	parseAlteration();
	
}

@Override
public void storeFontMetrics(FontMetrics fontMetrics) {
	this.fontMetrics=fontMetrics;
}

@Override
public FontMetrics getStoredFontMetrics() {
	return fontMetrics;
}

@Override
public int getX() {
	// TODO Auto-generated method stub
	return x;
}

@Override
public int getY() {
	// TODO Auto-generated method stub
	return y;
}

@Override
public void setLocation(double x, double y) {
	this.x=(int) x;
	this.y=(int) y;
	
}

@Override
public int getTextWidth() {
	setUpBounds(null);
	// TODO Auto-generated method stub
	return width;
}



public FontMetrics defaultMetrics(Font font) {
	return	new java.awt.Canvas().getFontMetrics(font);
}

public void setUpBounds(Graphics g) {
	if (g==null) {
		g=fmImage.getGraphics();
	}
	Rectangle2D r=g.getFontMetrics(font).getStringBounds(label, g);
    width=(int)r.getWidth();
    height=(int)r.getHeight();
}

@Override
public void setAngle(double angle) {
	this.angle=angle;
	
}

@Override
public void rotate(double angle) {
	// TODO Auto-generated method stub
	
}

@Override
public boolean isRandians() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean isDegrees() {
	// TODO Auto-generated method stub
	return true;
}

public static void main(String[] arg) {
	FontMetrics f = fmImage.createGraphics().getFontMetrics();
	IssueLog.log(""+f.stringWidth("A"));
	IssueLog.log(""+f.stringWidth("b"));
	IssueLog.log(""+f.stringWidth("c"));
	IssueLog.log(""+f.stringWidth("d"));
	IssueLog.log(""+f.stringWidth("e"));
	IssueLog.log(""+f.stringWidth("Abcde"));
}

	
	
}

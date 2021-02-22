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
 * Version: 2021.1
 */
package export.pptx;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.RectangleEdges;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;

/**subclass of OfficeObjectMaker that creates a shape in powerpoint*/
public class TextGraphicImmitator implements OfficeObjectMaker {
	
	private TextGraphic t;

	public TextGraphicImmitator(TextGraphic t) {
		this.t=t;
	}

	@Override
	public XSLFTextBox addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		 XSLFTextBox shape = slide.createTextBox();
		

		    XSLFTextParagraph p = shape.addNewTextParagraph();

		    shape.setWordWrap(false);
		    
		    if (t instanceof ComplexTextGraphic) {
		    	TextParagraph paragraph = ((ComplexTextGraphic) t).getParagraph();
		    	boolean firstLine=true;
		    	
		    	for(TextLine line: paragraph) {
		    		
		    		if (!firstLine) p.addLineBreak();
		    		
		    		for(TextLineSegment seg: line) {
		    		
		    			  XSLFTextRun r1 = p.addNewTextRun();
		    			  r1.setText(seg.getText());
		    			  setTextRunFont(r1, seg.getFont(),t.getDimmedColor(seg.getTextColor()));
		    			  r1.setSubscript(seg.getScript()==TextLineSegment.SUPER_SCRIPT);//export appears to work for subscripts even though this makes no sense
		    			  r1.setSuperscript(seg.getScript()==TextLineSegment.SUPER_SCRIPT);
		    			  r1.setFontSize((double)seg.getParent().getFont().getSize());
		    			  r1.setUnderlined(seg.isUnderlined());
		    			  r1.setStrikethrough(seg.isStrikeThrough());
		    		}
		    		firstLine=false;
		    		 
		    	}
		    	
		    	 setTextAlign(paragraph.getJustification(),p );
		    	
		    } else 
		    
		    {
		    	XSLFTextRun r1 = p.addNewTextRun();
		    	r1.setText(t.getText());
		    	setTextRunFont(r1, t.getFont(), t.getDimmedColor(t.getTextColor()));
		    }
		   
		   shape.setAnchor(getRectForAnchor(t));
		   
		  
		   double angle=-Math.toDegrees(t.getAngle());
		   shape.setRotation(angle);
		   
		   return shape;

	}
	
	/**location adjustment values that are needed in order for the location in powerpoint to match the current location*/
	 double leftM=7;
	 double rightM=7;
	 
	 double topM=4.5;
	 double bottomM=4.5;
	
	 /**genrates an anchor rectangle*/
	public Rectangle2D getRectForAnchor(TextGraphic t) {
	    
		 //  Rectangle2D b = t.;//.getBounds();
		   
		   
		  	/**Because of differences in how rotation works, must set the right anchor rect*/
	
			   Rectangle2D.Double b2=new Rectangle2D.Double();
			   b2.setRect( t.getOutline().getBounds2D());
			   java.awt.geom.Point2D.Double p = new Point2D.Double(b2.getCenterX(), b2.getCenterY());
			
			   double versionShift=t.getFont().getSize();//added after switch from poi 3.12 to later version done on feb 22 2021
			  //creates teh anchor rect
			   Rectangle2D.Double b=new Rectangle2D.Double();
			   b.setRect( t.getBoundPriorToRotation());
			   RectangleEdges.setLocation(b, RectangleEdges.CENTER, p.getX(), p.getY()-versionShift);
			  
		
		  
		 
			//adds margins to the anchor rect so the text can be depicted normally  
			   
		   Double anchor = new Rectangle2D.Double(b.getX()-leftM, b.getY()-topM, b.getWidth(), b.getHeight());
		   anchor.width+=rightM+leftM;
		   anchor.height+=topM+bottomM;
		 //  anchor.width*=1.3;
		 //  anchor.height*=1.3;
		   ///**angle correction seems off. will need to fit it at some point*/
		   //if (!experimental)  anchor.y-=Math.sin(t.getAngle())*anchor.width/2;
		   
		   
		   
		   
		   /**
		   if (t instanceof ComplexTextGraphic ) {
			   
			   double wAugment=leftM+4+anchor.width*0.4;
			   double hAugment=topM+4+anchor.height*0.4;
			   
			   ComplexTextGraphic  ct=(ComplexTextGraphic) t;
			   			if (ct.getParagraph().getJustification()==TextParagraph.Justify_Right) {
				   
			   				
			   			   anchor = new Rectangle2D.Double(b.getX()+rightM, b.getY()-topM, b.getWidth(), b.getHeight());
			   			  
			   			   anchor.x-=wAugment;
			   			   anchor.width+=wAugment;
			   			   
			   			
			   			   anchor.height+=hAugment;
			   			   
			   				
			   		}
			   
			   
			   
		   }
		   */
		   
		  
		   
		return anchor;
		
		   
	}
	
	/**sets the alignments of the paragraph*/
	public void setTextAlign(int align, XSLFTextParagraph p ) {
		if (align==TextParagraph.JUSTIFY_LEFT) p.setTextAlign(TextAlign.LEFT);
		if (align==TextParagraph.JUSTIFY_RIGHT) p.setTextAlign(TextAlign.RIGHT);
		if (align==TextParagraph.JUSTIFY_CENTER) p.setTextAlign(TextAlign.CENTER);
	}
	
	/**sets the font of a particular text run*/
	void setTextRunFont(XSLFTextRun r1, Font f, Color c) {
		 r1.setFontColor(c);
		    r1.setFontSize((double)f.getSize());
		    r1.setFontFamily(f.getFamily());

		   r1.setBold(f.isBold());
		   r1.setItalic(f.isItalic());
	}

}

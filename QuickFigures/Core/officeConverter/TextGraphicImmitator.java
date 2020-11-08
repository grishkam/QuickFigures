package officeConverter;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import org.apache.poi.xslf.usermodel.TextAlign;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.TextLine;
import utilityClassesForObjects.TextLineSegment;
import utilityClassesForObjects.TextParagraph;

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
		    	boolean firstline=true;
		    	
		    	for(TextLine line: paragraph) {
		    		
		    		if (!firstline) p.addLineBreak();
		    		
		    		for(TextLineSegment seg: line) {
		    		
		    			  XSLFTextRun r1 = p.addNewTextRun();
		    			  r1.setText(seg.getText());
		    			  setTextRunFont(r1, seg.getFont(),t.getDimmedColor(seg.getTextColor()));
		    			  r1.setSubscript(seg.getScript()==1);
		    			  r1.setSuperscript(seg.getScript()==1);
		    			  r1.setFontSize(seg.getParent().getFont().getSize());
		    			  r1.setUnderline(seg.isUnderlined());
		    			  r1.setStrikethrough(seg.isStrikeThrough());
		    		}
		    		firstline=false;
		    		 
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
	
	 double leftM=7;
	 double rightM=7;
	 
	 double topM=4.5;
	 double bottomM=4.5;
	
	public Rectangle2D getRectForAnchor(TextGraphic t) {
	    
		 //  Rectangle2D b = t.;//.getBounds();
		   
		   
		  	/**Because of differences in how rotation works, must set the right anchor rect*/
		  // if (experimental) {
				//finds the correct x,y position for the anchor rect
			   Rectangle2D.Double b2=new Rectangle2D.Double();
			   b2.setRect( t.getOutline().getBounds2D());
			   java.awt.geom.Point2D.Double p = new Point2D.Double(b2.getCenterX(), b2.getCenterY());
			
			  //creates teh anchor rect
			   Rectangle2D.Double b=new Rectangle2D.Double();
			   b.setRect( t.getBoundPriorToRotation());
			   RectangleEdges.setLocation(b, RectangleEdges.CENTER, p.getX(), p.getY());
			  
		 //  }
		  
		 
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
	
	public void setTextAlign(int align, XSLFTextParagraph p ) {
		if (align==TextParagraph.Justify_Left) p.setTextAlign(TextAlign.LEFT);
		if (align==TextParagraph.Justify_Right) p.setTextAlign(TextAlign.RIGHT);
		if (align==TextParagraph.Justify_Center) p.setTextAlign(TextAlign.CENTER);
	}
	
	void setTextRunFont(XSLFTextRun r1, Font f, Color c) {
		 r1.setFontColor(c);
		    r1.setFontSize(f.getSize());
		    r1.setFontFamily(f.getFamily());

		   r1.setBold(f.isBold());
		   r1.setItalic(f.isItalic());
	}

}
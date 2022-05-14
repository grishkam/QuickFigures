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
 * Date Modified: April 26, 2022
 * Version: 2022.1
 */
package graphicalObjects_SpecialObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;

import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import icons.TreeIconWithText;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.TextFrame;
import illustratorScripts.TextRange;
import locatedObject.ColorDimmer;
import locatedObject.ShapesUtil;
import logging.IssueLog;
import messages.ShowMessage;
import objectDialogs.ComplexTextGraphicSwingDialog;
import standardDialog.StandardDialog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;
import undo.Edit;
import undo.UndoAbleEditForRemoveItem;

/**A subclass of text graphic that displays text consisting of many lines*/
public class ComplexTextGraphic extends TextGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int actioncount=1;
	
	private TextParagraph paragraph=new TextParagraph(this); {paragraph.addLine();}
	
	/**Creates text graphic with the text given*/
	public ComplexTextGraphic(String text) {
		super();
		this.getParagraph().get(0).get(0).setText(text);
	}
	
	/**Creates a multi-line text graphic with the lines of text given*/
	public ComplexTextGraphic(String... lines) {
		super();
		if(lines.length>0)
			this.getParagraph().get(0).get(0).setText(lines[0]);
			else return;
		
		if(lines.length>1) {
			for(int i=1; i<lines.length; i++)  {
				String line = lines[i];
				if(line!=null &&!line.equals(""))
					this.getParagraph().addLine(line);
				}
		}
	}
	
	/**Creates and empty text graphic */
	public ComplexTextGraphic() {
		super();
	}
	
	/**Creates a copy*/
	public ComplexTextGraphic copy() {
		
		
		ComplexTextGraphic output = createAnother();
		output.setTextColor(getTextColor());
		output.setParagraph(getParagraph().copy());
		output.setFillBackGround(isFillBackGround());
		output.copyAttributesFrom(this);
		output.copyBasicTraitsFrom(this);
		output.backGroundShape=getBackGroundShape().copy();
		if (getAttachmentPosition()!=null) output.setAttachmentPosition(getAttachmentPosition().copy());
		else output.setAttachmentPosition(null);
		
		return output;
	}

	/**returns another graphic of the same class*/
	public ComplexTextGraphic createAnother() {
		 return new ComplexTextGraphic();
		}
	
	/**Creates a text graphic with several segments of text in different colors*/
	public static ComplexTextGraphic createMultiSegment(ArrayList<String> texts, ArrayList<Color> c) {
		ComplexTextGraphic thi = new ComplexTextGraphic();
		int cindex=0;
		thi.setParagraph(new TextParagraph(thi));
		if (c==null) c=new ArrayList<Color>();
		TextLine line1 = thi.getParagraph().addLine();
		
		for(int i=0; i<texts.size(); i++, cindex++) {
			String text=texts.get(i);
			if (text==null) continue;
			Color color=Color.black;
			
			if (c.size()<=cindex) {
				cindex=0;
			}
			if (c.size()>cindex) {
				color=c.get(cindex);
			}
			
			line1.add(new TextLineSegment(text,color));
		}
		
		
		
		return thi;
		
	}
	
	/**Creates a text graphic with several colors*/
	public static ComplexTextGraphic createRainbow(String st, int[] arr, Color[] color) {
		ArrayList<String> sts = splitStringBasedOnArray(st, arr);
		ArrayList<Color> colors = ComplexTextGraphic.createColorArr(color);
		return createMultiSegment(sts, colors);
	}
	
	/**splits up the string st with segment lengths determined by the array given*/
	public static ArrayList<String> splitStringBasedOnArray(String st, int[] arr) {
		ArrayList<String> o=new ArrayList<String>();
		char[] arr2 = st.toCharArray();
		int i=0;
		int arr1index=0;
		while (i<arr2.length) {
			if (arr1index>=arr.length) arr1index=0;
			int lengtcheg =arr[arr1index];
			StringBuilder sb = new StringBuilder();
			for(int j=0; j<lengtcheg&&i+j<arr2.length; j++) {
				sb.append(arr2[i+j]);
			}
			o.add(sb.toString());
			i+=lengtcheg;
			arr1index++;
		}
		
		return o;
	}
	

	/**A hashmap used for keeping track of the bounding polygon for each text segment*/
	private transient HashMap<TextLineSegment, Polygon> rotatedSegmentBounds=new HashMap<TextLineSegment, Polygon>();
	private TextLineSegment cursorSegment;//which text segment currently contains the cursor
	private TextLineSegment highlightSegment ;
	private ArrayList<TextLineSegment> allSelectedSegments;//Add the highlighted segments; from the cursor location to the highlight location
	

	/**applies the rotation transformation to the segments*/
	public void setupSegOutlines() {
		
		for(TextLine lin: getParagraph()) {
			if (lin==null) continue;
			for(TextLineSegment seg:lin) {
				if (seg==null||seg.bounds==null) continue;
				if (seg.baseLine==null||seg.baseLineend==null) {
					IssueLog.log("text line segment baselines not set up");
					continue;}
				seg.transformedBounds=createAffline().createTransformedShape(seg.bounds);
				seg.transformedBaseLineStart=createAffline().transform(seg.baseLine, new Point2D.Double());
				seg.transformedBaseLineEnd=createAffline().transform(seg.baseLineend, new Point2D.Double());
				Polygon p = getGrahpicUtil().PolygonFromRect(seg.bounds, this.createAffline());
						getRotatedSegmentBounds().put(seg, p);
			}
			
			;
		}
	}
	
	
	/**Calculates the bounding box of the text item
	 */
	public void setUpBounds(Graphics g) { 
        textDimension =getParagraph().getDimensionsForAllLines(g, x,y);
        textDimension=ShapesUtil.addInsetsToRectangle((Rectangle2D.Double)textDimension, getInsets());
        //IssueLog.log("text dimension is set up to be "+textDimension);
        setUpOutlineFromParagraphiBounds(textDimension);
        width=textDimension.getWidth();
        height=textDimension.getHeight();
        boundsInnitial=true;
        setupSegOutlines();
	}
	
	/**Returns the segment at a given point*/
	public  TextLineSegment getSegmentAtPoint(Point2D p) {
		for(TextLineSegment s:getRotatedSegmentBounds().keySet()) {
			Polygon p2 = getRotatedSegmentBounds().get(s);
			if (p2.contains(p)) return s;
		}
		return null;
	}
	
	/**Returns the segment nearest to the given point*/
	public  TextLineSegment getNearestSegmentAtPoint(Point2D p) {
		double d=Integer.MAX_VALUE;
		TextLineSegment nearest = this.getParagraph().getAllSegments().get(0);
		for(TextLineSegment s:getRotatedSegmentBounds().keySet()) {
			Polygon p2 = getRotatedSegmentBounds().get(s);
			
			double newd = p.distance(p2.getBounds().getCenterX(), p2.getBounds().getCenterX());
			if(newd<d) {
				d=newd;
				nearest=s;
			}
		}
		return nearest;
	}
	
	
	@Override
	public void dropColor(Color ob, Point p) {
		TextLineSegment s = getSegmentAtPoint(p);
		if (s==null) this.setTextColor(ob); else
		s.setTextColor(ob);
		return ;
	}
	
	/**draws the given text onto a graphics 2d object*/
	 public void drawRotatedText(Graphics2D g, CordinateConverter cords) { 
		setAntialiasedText(g, true);
		setUpBounds(g);
		
		drawAllLinesAtLocation(g, cords,getParagraph());
	    
	}
	 
	 
	
	/**Draws many lines at the given locatioin*/
	public void drawAllLinesAtLocation(Graphics2D g, CordinateConverter cords, TextParagraph allLines) {
		if(this.isSelected()) {
			setUpSelectedSegmentList();
		}
		
		for (TextLine line1: allLines) {
			if(line1==null)
				continue;
		   drawLineAtLocation(line1,  (Graphics2D)g, cords);
		
	   }
	}



/**sets the cursor segment and highlighted segment list Fields, prior to use by another method*/
	private void setUpSelectedSegmentList() {
		TextLineSegment lastCursorSegment = cursorSegment;
		cursorSegment = findCursorSegment(getCursorPosition(), false);
		highlightSegment = findCursorSegment(this.getHighlightPosition(), true);
		if (lastCursorSegment!=null&&cursorSegment !=lastCursorSegment)
			lastCursorSegment.setCursorPosition(lastCursorSegment.getText().length());//added on may 13 2020 to fix bug. cursor should be moved out of the last cursor segment
		
		this.allSelectedSegments = this.getParagraph().getAllSegmentsInRange(highlightSegment, cursorSegment);
	
		/**if the cursor position object just has the cursur at the start,
		  it is not counted*/
		if(allSelectedSegments.size()==0) return;
		if (allSelectedSegments .get(allSelectedSegments .size()-1).getCursorposition()==0 &&allSelectedSegments .size()>1) {
			allSelectedSegments .remove(allSelectedSegments .get(allSelectedSegments.size()-1));
		}
		
	}
	
	/**draws a line of text. depending on whether the text is in edit mode, may draw differently*/
	public void drawLineAtLocation(TextLine line1, Graphics2D g, CordinateConverter cords) {
		  // line1.computeLineDimensions(g, x, y);
		  if (this.isEditMode())
				    for(TextLineSegment t: line1) { 
				    	
				    	if (t==null) continue;
				
				    	drawLineSegment(t,g,cords);
				 	    
				    }
		  else this.drawLine(line1, g, cords);
	}
	
	/**Dras a fragment of text with a cursor */
	public void drawLineSegment(TextLineSegment t, Graphics2D g, CordinateConverter cords) {
		if (t.baseLine==null) {
			IssueLog.log("failed to draw text segment "+t.getText()+" Its location had not been set up");
			return;
		}
		   Point2D d = this.createAffline().transform(t.baseLine, new Point2D.Double());
		   
		
			   getGrahpicUtil().drawString(g, cords, 
					   	t.getText(), 
					   	d, t.getFont(), getDimmedColor(t.getTextColor()), getAngle(), this.dontScaleText);
			  
			      double sx = cords.transformX(d.getX());
				   double sy = cords.transformY(d.getY());
			   
				 /**if not in editmode, dont draw cursors*/  
				   if(!isEditMode()) return;
				   
			   if (this.isSelected()&&t==cursorSegment) { 
				   g.rotate(-this.getAngle(), sx,sy);
				   super.drawCursor(g, cords,d.getX(),d.getY(), t.getText(), t.getCursorposition(), t.getFont());
				   g.rotate(this.getAngle(),  sx,sy);
			   }
			   
			   if (this.isSelected() && hasHighlightRegion() &&this.getAllSelectedSegments()!=null&&this.getAllSelectedSegments().contains(t)) {
				   g.rotate(-this.getAngle(), sx,sy);
				   drawAppropriateHighLight(t, g, cords, d);
				   g.rotate(this.getAngle(),  sx,sy);
			   }
	}
	
	
	/**draws a line of text*/
	public void drawLine(TextLine tLine, Graphics2D g, CordinateConverter cords) {
		TextLineSegment t = tLine.get(0);
		if (t.baseLine==null) {
			IssueLog.log("failed to draw text segment "+t.getText()+" Its location had not been set up");
			return;
		}
		   Point2D d = this.createAffline().transform(t.baseLine, new Point2D.Double());
		   
		
			   getGrahpicUtil().drawString(g, cords, this,
					   	tLine, 
					   	d,  getAngle());
			  
			    
	}



/**Depending on whether this segment is on the edges or inside of the highlight region, draws differently*/
	private void drawAppropriateHighLight(TextLineSegment t, Graphics2D g, CordinateConverter cords, Point2D d) {
		if (getAllSelectedSegments().size()==1) {
			   drawHighlight(g, cords,d.getX(),d.getY(), t.getText(), t.getHightLightPosition(), t.getCursorposition(), t.getFont());
		   } else
		   if (this.getAllSelectedSegments().indexOf(t)==0) {
			   drawHighlight(g, cords,d.getX(),d.getY(), t.getText(), t.getHightLightPosition(), t.getText().length(), t.getFont());  
		   } else if (this.getAllSelectedSegments().indexOf(t)==getAllSelectedSegments().size()-1) {//drawing it for the penultimate segment has been buggy
			  
			   if(this.getMaxNeededCursor()==this.getCursorPosition()) 
				   drawHighlight(g, cords,d.getX(),d.getY(), t.getText(), 0, t.getText().length(), t.getFont());
			   else  
			   drawHighlight(g, cords,d.getX(),d.getY(), t.getText(), 0, t.getCursorposition(), t.getFont());
			   
			   
		   
		   } else
		   drawHighlight(g, cords,d.getX(),d.getY(), t.getText(), 0, t.getText().length(), t.getFont());
	}
	
	
	/**returns a dimmed color*/
	public Color getDimmedColor(Color c) {
	
		if (this.isDimColor()&& getDimming()!=null) 
			return ColorDimmer.modifyColor( c, getDimming(), true);
		 	  
		 	    else return c;
	}
	
	public void drawHandlesAndOutline( Graphics2D g2d, CordinateConverter cords) {
		super.drawHandlesAndOutline(g2d, cords);
		for(TextLine lin: getParagraph()) {
			//Double[] rect = this.rotateRect();
			for(TextLineSegment seg:lin) {
				g2d.setColor(seg.getTextColor());
				if(this.isEditMode())
					getGrahpicUtil().drawPolygon(g2d, cords, this.rotateRect(seg.bounds), false);
			}
			
			;
		}
	}

	public TextParagraph getParagraph() {
		return paragraph;
	}

	public void setParagraph(TextParagraph paragraph) {
		if(paragraph==null) return;
		paragraph.setParent(this);
		this.paragraph = paragraph;
	}
	
	public void showOptionsDialog() {
		getOptionsDialog().showDialog();
	}
	
	@Override
	public StandardDialog getOptionsDialog() {
		ComplexTextGraphicSwingDialog dia = new ComplexTextGraphicSwingDialog(this);
		return dia;
	}
	

	/**returns an array of many texty graphics that this the broken version of this graphic*/
	public ArrayList<TextGraphic> breakdown() {
		
		
		ArrayList<TextGraphic> output = new ArrayList<TextGraphic>();
		for(TextLine line:this.getParagraph()) {
			for(TextLineSegment seg:line) { 
				TextGraphic t = new TextGraphic();
				t.setText(seg.getText());
				t.setFont(seg.getFont());
				t.setTextColor(this.getDimmedColor(seg.getTextColor()));
				t.setLocation(seg.transformedBaseLineStart);
				t.setAngle(this.getAngle());
				output.add(t);
			}
			
		}
		
		
		return output;
		
	}
	
	public GraphicLayerPane getBreakdownGroup() {
		GraphicLayerPane output = new GraphicLayerPane(this.getName());
		for(TextGraphic g: breakdown() )output.add(g);
		
		return output;
	}
	
	/**Flawed codereturns a point that is on a line defined by p1 and p2. 
	 * If d2 is above 1, that point will be beyond the second point */
	public Point2D extendPoint(Point2D p1, Point2D p2, double d2) {
		
	
		
		double d=1-d2;
		double nx = (p1.getX()*d+p2.getX()*d2);
		double ny = (p1.getY()*d+p2.getY()*d2);
		return new Point2D.Double(nx, ny);
		
		
		
	}
	
	/**Generates a similar text item in an adobe illustrator script*/
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		BackGroundToIllustrator(aref);
		for(TextLine line:this.getParagraph()) {
			TextFrame ti = new TextFrame();
			
			TextLineSegment seg2=line.get(0);
			
			
			double distanceChange=(line.getAllBaselineLengths()/seg2.baseLineDistance());
			
			Point2D p2 = extendPoint(seg2.transformedBaseLineStart, seg2.transformedBaseLineEnd, distanceChange);
			ti.createLinePathItem(aref, seg2.transformedBaseLineStart, p2);
			
		
			for(TextLineSegment seg:line) {
			
		
					TextRange tr = new TextRange();
					
					tr.createItem(ti, seg.getText());
					ti.wordSpacing();
					
					tr.createCharAttributesRef();
					tr.setContents2(seg.getText());
					tr.getCharAttributesRef().setfont(seg.getFont());
					tr.getCharAttributesRef().setFillColor(this.getDimmedColor(seg.getTextColor()));
					 tr.getCharAttributesRef().setUnderline(seg.isUnderlined());
					tr.getCharAttributesRef().setStrikeThrough(seg.isStrikeThrough());
					if (seg.isSuperscript()) {
						
						tr.getCharAttributesRef().setSuperScript();
						//tr.getCharAttributesRef().setfontSize(seg.getFont().getSize()*2);
					}
					else if (seg.isSubscript()) {
						tr.getCharAttributesRef().setSubScript();
						//tr.getCharAttributesRef().setfontSize(seg.getFont().getSize()*2);
					}
					else {
						tr.getCharAttributesRef().setNormalScript();
					}
		
					
					
				
			}
			ti.removeInsertionPointFromRange();
			
		
		}
		return null;
	}
	
	


	/**Keeps a hashmap of each rotsegment bounds*/
	private HashMap<TextLineSegment, Polygon> getRotatedSegmentBounds() {
		if (rotatedSegmentBounds==null)rotatedSegmentBounds=new HashMap<TextLineSegment, Polygon>();
		return rotatedSegmentBounds;
	}

	/**returns an array list of string with the given strings*/
	public static ArrayList<String> createStringArr(String... sts) {
		ArrayList<String> o=new ArrayList<String>();
		for(String st:sts) o.add(st);
		return o;
	}
	/**returns an array list of colors with the given colors*/
	public static ArrayList<Color> createColorArr(Color... sts) {
		ArrayList<Color> o=new ArrayList<Color>();
		for(Color st:sts) o.add(st);
		return o;
	}




	public String toString() {
		return this.getParagraph().getText();
	}
	
	@Override
	public Icon getTreeIcon() {
		return new TreeIconWithText(this.getFont(), "cd", Color.green.darker(), Color.red.darker());
	}
	
	public static Icon createImageIcon() {
		return new TreeIconWithText(new Font("Arial", Font.BOLD, 12) ,"cd", Color.green.darker(), Color.red.darker());
		
	}
	
	/**not yet implemented*/
	@Override
	public void handleKeyTypedEvent(KeyEvent arg0) {
		
	}
	
	/**sets which segment contains the cursor*/
	public void setCursorSegment(TextLineSegment segment) {
		
		ArrayList<TextLineSegment> segs = this.getParagraph().getAllSegments();
		if (segment==null||!segs.contains(segment)) return;
		int p=0;
		int count=0;
		for(int i=0; i<segs.size();i++) {
			TextLineSegment current = segs.get(i);
			if (segment==current) {
				if (i==0) {
					this.setCursorPosition(0);
					return;}
				setCursorPosition(p);
				return;
			} 
			
			/**if(p<=segs.get(i).getText().length()) {
				
				current.setCursorPosition(p);
				//return current;
				}*/
			p+=segs.get(i).getText().length()+1;
			count+=segs.get(i).getText().length()+1;
		}
		
		this.setCursorPosition(count);
	}
	
	
	/**Finds the segment where the cursor should be located. t*/
	private TextLineSegment findCursorSegment(int p, boolean highlight) {
		ArrayList<TextLineSegment> segs = this.getParagraph().getAllSegments();
		
		for(int i=0; i<segs.size();i++) {
			
			if(p<=segs.get(i).getText().length()) {
				TextLineSegment current = segs.get(i);
				
				if (!highlight)current.setCursorPosition(p);
				else current.setHighlightPosition(p);
				
				return current;
				}
			p-=segs.get(i).getText().length()+1;
			
		}
		
		this.setCursorPosition(this.getMaxNeededCursor());//this.setCursorPosition(count+1);
		
		
		if (segs.size()==0)return null;
		
		TextLineSegment outputcursorSegment = segs.get(segs.size()-1);
		
		return outputcursorSegment;
		}
	
	protected void afterSplitUp() {}

	/**Called when the user presses a key*/
	@Override
	public void handleKeyPressEvent(KeyEvent arg0) {
		
		if (handleNonLetterKey(arg0)) return;
		if(modifierKey(arg0)) return;
		arg0.consume();
		
		TextLineSegment lastSegment = getParagraph().getLastLine().getLastSegment();
		TextLineSegment thisSegment = lastSegment;
		if(cursorSegment!=null)  thisSegment=cursorSegment;
		TextLine thisLine = getParagraph().getLineWithSegment(thisSegment);//.getLastLine();
		
		int linIndex=this.getParagraph().indexOf(thisLine);
		
		if (arg0.getKeyCode()==KeyEvent.VK_UP) {
			if (linIndex>0) 
				this.setCursorPosition(this.getCursorPosition() - (this.getParagraph().get(linIndex-1).getText().length()+getParagraph().get(linIndex-1).size()));
			this.setHighlightPositionToCursor();//bug fix done on nov 24th 2021 
			return;
		}
		if (arg0.getKeyCode()==KeyEvent.VK_DOWN) {
			if (linIndex<this.getParagraph().size()-1)
				this.setCursorPosition(this.getCursorPosition() + this.getParagraph().get(linIndex).getText().length()+getParagraph().get(linIndex).size());
			this.setHighlightPositionToCursor();//bug fix done on nov 24th 2021 
			return;
		}
		
		if (arg0.getKeyCode()==KeyEvent.VK_ENTER) {
			thisLine.splitSegment(thisSegment, thisSegment.getCursorposition());
			getParagraph().splitLine(thisLine, thisLine.indexOf(thisSegment));
			this.setCursorPosition(this.getCursorPosition() + 1);
			return;
		}
		
		
		/**if (arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE &&!oneSegmentInline&&noTextInthisSegment&&thisLine.size()>1) {
			thisLine.remove( thisSegment);
			return;
		}*/
		boolean meta=arg0.isMetaDown();
		if (IssueLog.isWindows()) {meta=arg0.isControlDown();}
		
		
		
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_B) {
			emboldenSelectedRegion();
			return;
			}
		
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_U) {
			underlineSelectedRegion();
		return;
		}
		
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_C) {
			this.copySelectedRegion();
			return;
			}
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_C) {
			pasteIntoSelectedRegion();
			return;
			}
		
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_I) {
			italicizeSelectedRegion();
			return;
			}
	
		
		if (arg0.getKeyChar()=='+'&&meta&&arg0.isShiftDown()) {
			selectedRegionToSuperScript();
			return;
		}
		
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_A) {
			this.setSelectedRange(0, getMaxNeededCursor());
			return;
		}
		
		if ((arg0.getKeyCode()==KeyEvent.VK_MINUS&&meta&&arg0.isShiftDown())
				|| (arg0.getKeyChar()=='+'&&meta) ) {
			
			selectedRegionToSubscript();
			return;
		}
		
		if (arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE ||arg0.getKeyCode()==KeyEvent.VK_DELETE) {
			if (!hasHighlightRegion()&&arg0.getKeyCode()==KeyEvent.VK_DELETE) {
				this.setCursorPosition(getCursorPosition()+1);
			}
			onBackspace();
			return;
		}
		
		/**A list of segments in the highlight region that will be deleted when the user types a key*/
		ArrayList<TextLineSegment> segmentsToDelete = new ArrayList<TextLineSegment>();
		TextLineSegment segmentToDeleteForHighLight=null;
		Integer delIndex=null;
		
		/**work in progress. need to edit so that the selected region is removed when a highlight overlaps multiple segments*/
		if (this.hasHighlightRegion()) 
			{
			//need to determine what do do with the other segments
			this.setUpSelectedSegmentList();
			segmentsToDelete.addAll(allSelectedSegments);
			segmentsToDelete.remove(thisSegment);
			if (highlightSegment.getHightLightPosition()!=0&&thisSegment!=highlightSegment)
				{
				segmentsToDelete.remove(highlightSegment);
				segmentToDeleteForHighLight=highlightSegment;
				 delIndex=highlightSegment.getHightLightPosition();
				}
			
			if(highlightSegment==thisSegment)
				segmentsToDelete.remove(highlightSegment);
			}
		
		String st = handleKeyOnString(arg0,thisSegment .getText(),thisSegment.getCursorposition(), thisSegment.getHightLightPosition());
		String oldText=thisSegment.getText();
		thisSegment .setText(st);
		setCursorPosition(getCursorPosition()+thisSegment.getText().length()-oldText.length());
			
		
		/**removes some segment entirely if they are within the highlight region*/
		for(TextLineSegment segment: segmentsToDelete ) {
			this.getParagraph().removeSegment(segment);
			setCursorPosition(getCursorPosition()-segment.getText().length()-1);
		}
		
		if(segmentToDeleteForHighLight!=null) {
			
			String oldHText = segmentToDeleteForHighLight.getText();
			
			String newHText = oldHText.substring(0,delIndex);
			segmentToDeleteForHighLight.setText(newHText);
			setCursorPosition(getCursorPosition()-(oldHText.length()-newHText.length()));
		}
		
			
		String subscript="subscript";
		if ( thisSegment.getText().endsWith(subscript)) {
			thisSegment.setText(thisSegment.getText().substring(0, thisSegment.getText().length()-subscript.length()));
			TextLineSegment sg = thisLine.addSegment("subscript", thisSegment.getTextColor());
			sg.setScript(2);
			return;
		}
		subscript="superscript";
		if ( thisSegment.getText().endsWith(subscript)) {
			thisSegment.setText(thisSegment.getText().substring(0, thisSegment.getText().length()-subscript.length()));
			TextLineSegment sg = thisLine.addSegment("superscript", thisSegment.getTextColor());
			sg.setScript(1);
			return;
		}
		fuseIdenticalSegments();
		setHighlightPositionToCursor();
	}



	public void onBackspace() {
		handleBackSpaceForHighlightRegion();
		setHighlightPositionToCursor();
	}


	/**set the selected text to bold font style*/
	public void emboldenSelectedRegion() {
		if (!this.hasHighlightRegion()) return;
		splitHighLightedSegments();
		boolean state = isSelectionBold();
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			int fontS = embolden(seg1.getFont(), state).getStyle();
			seg1.setUniqueStyle(fontS+1);
			if (seg1.getFont().getStyle()==this.getFont().getStyle()) seg1.setUniqueStyle(0);
		}
		afterSplitUp();
	}

	/**returns true if the selected text is bold
	 * used to determine if the ital icon should be down*/
	public boolean isSelectionBold() {
		return getAllSelectedSegments().get(0).getFont().isBold();
	}



	/**set the selected text to italic font style*/
	public void italicizeSelectedRegion() {
		if (!this.hasHighlightRegion()) return;
		splitHighLightedSegments();
		boolean state = isSelectionItalic();
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			int fontS = italicize(seg1.getFont(), state).getStyle();
			seg1.setUniqueStyle(fontS+1);
			if (seg1.getFont().getStyle()==this.getFont().getStyle()) seg1.setUniqueStyle(0);
			
		}
		afterSplitUp();
	}

	/**returns true if the selected text is italic
	 * used to determine if the ital icon should be down*/
	public boolean isSelectionItalic() {
		return getAllSelectedSegments().get(0).getFont().isItalic();
	}
	
	/**set the selected text to underline*/
	public void underlineSelectedRegion() {
		if (!this.hasHighlightRegion()) return;
		splitHighLightedSegments();
		boolean state = isSelectionUnderlined();
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			seg1.setUnderlined(!state);
		}
		afterSplitUp();
	}
	
	/**not implemented, decided to implement a popup menu instead*/
	public String copySelectedRegion() {
		String out = getSelectedText();
		TextGraphic.lastCopy=out;
		afterSplitUp();
		return lastCopy;
	}

	/**returns the selected text as a string. used to copy that text*/
	public String getSelectedText() {
		if (!this.hasHighlightRegion()) return null;
		splitHighLightedSegments();
		String out="";
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			out+=seg1.getText();
		}
		return out;
	}
	
	/**not yet perfectly implemented. pastes string st into the cursors position*/
	public void handlePaste(String st) {
		if(this.hasHighlightRegion()) this.onBackspace();
		TextLineSegment lastSegment = getParagraph().getLastLine().getLastSegment();
		TextLineSegment thisSegment = lastSegment;
		if(cursorSegment!=null)  thisSegment=cursorSegment;
		String newT = handlePasteForString(thisSegment.getText(), st, thisSegment.getCursorposition());
			thisSegment.setText(newT);
			
	}
	
	/**not implemented, decided to implement a popup menu instead*/
	public void pasteIntoSelectedRegion() {
		if (!this.hasHighlightRegion()) return;
		splitHighLightedSegments();
		
		TextLineSegment selSeq = getAllSelectedSegments().get(0);
		
		if(selSeq!=null&&TextGraphic.lastCopy!=null) selSeq.setText(lastCopy);

		afterSplitUp();
	}

	/**returns true if selected text is underlines*/
	public boolean isSelectionUnderlined() {
		return getAllSelectedSegments().get(0).isUnderlined();
	}
	
	/**makes the selected part a strikethrough region*/
	public void strikeLineThroughSelectedRegion() {
		if (!this.hasHighlightRegion()) return;
		splitHighLightedSegments();
		boolean state = isSelectionStrikedThrough();
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			seg1.setStrikeThough(!state);
		}
		afterSplitUp();
	}

	public boolean isSelectionStrikedThrough() {
		return getAllSelectedSegments().get(0).isStrikeThrough();
	}
	public void colorSelectedRegion(Color c) {
		if (!this.hasHighlightRegion()) return;
		splitHighLightedSegments();
		
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			seg1.setTextColor(c);
		}
		afterSplitUp();
	}





	public void selectedRegionToSuperScript() {
		splitHighLightedSegments();
		boolean state=isSelectionASuperScript();
		if (this.getParagraph().getAllSegments().size()==1) {
			IssueLog.showMessage("Select A Region of Text First");
			return;
		}
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			if(state) {
				seg1.makeNormalScript();
			} else {
				seg1.makeSuperScript();
				
			}
		}
		afterSplitUp();
		/**
		if(thisSegment.isSuperscript()) {
			thisSegment.makeNormalScript();
		} else {
			TextLineSegment[] newseg = thisLine.splitSegment(thisSegment, thisSegment.getCursorposition());
			newseg[1].makeSuperScript();
			this.setCursorPosition(this.getCursorPosition() + 1);
		}*/
	}

	public boolean isSelectionASuperScript() {
		return getAllSelectedSegments().get(0).isSuperscript();
	}

	public void selectedRegionToSubscript() {
		splitHighLightedSegments();
		boolean state=isSelectionASubScript();
		
		if (this.getParagraph().getAllSegments().size()==1) {
			ShowMessage.showMessages("Select A Region of Text First");
			return;
		}
		
		for(TextLineSegment seg1: this.getAllSelectedSegments()) {
			
			if(state) {
				seg1.makeNormalScript();
			} else {
				seg1.makeSubScript();;
				
			}
			}
		afterSplitUp();
		/**
		if(thisSegment.isSubscript()) {
			thisSegment.makeNormalScript();
		} else {
			TextLineSegment[] newseg = thisLine.splitSegment(thisSegment, thisSegment.getCursorposition());
			newseg[1].makeSubScript();
			this.setCursorPosition(this.getCursorPosition() + 1);
		}*/
	}

	public boolean isSelectionASubScript() {
		return getAllSelectedSegments().get(0).isSubscript();
	}


	/**If the highlight begins or ends inside of a segment, splits the segments
	 * Line object should not be replaced*/
	public void splitHighLightedSegments() {
		if (!hasHighlightRegion()) return;
		
		ArrayList<TextLineSegment> segments = this.getAllSelectedSegments();
		if (segments.size()==1) {
			TextLineSegment thisSegment = segments.get(0);
			splitUpSingleHighLightSegment(thisSegment);
			
			actioncount++;
			return;
		}
		
		
		
		TextLineSegment firstSegment = segments.get(0);
		TextLineSegment lastSegment = segments.get(segments.size()-1);
		TextLine firstLine = this.getParagraph().getLineWithSegment(firstSegment);
		TextLine lastLine = this.getParagraph().getLineWithSegment(lastSegment);
		
		
		int cursor=getCursorPosition();
		int high=getHighlightPosition();
		
		int segHigh = firstSegment.getHightLightPosition();
		int segCursor = lastSegment.getCursorposition();
		
		if (segHigh<firstSegment.getText().length()-1&&segHigh>0) {
			 firstLine.splitSegment(firstSegment, firstSegment.getHightLightPosition());
			cursor++;
			high++;
			
		}
		
		if (segCursor<lastSegment.getText().length()-1&&segCursor>0)  {
			lastLine.splitSegment(lastSegment, segCursor);
			
			cursor++;
		}
		
		this.setSelectedRange(high, cursor);
		setUpSelectedSegmentList() ;
	}

	protected TextLineSegment[] splitUpSingleHighLightSegment(TextLineSegment thisSegment) {
		TextLine thisLine = this.getParagraph().getLineWithSegment(thisSegment);
		
		
		TextLineSegment[] newseg =null;
		int cursor=thisSegment.getCursorposition();
		int high=thisSegment.getHightLightPosition();
		
		int cursor2=getCursorPosition();
		int high2=getHighlightPosition();
		
		if (cursor<thisSegment.getText().length()) {
			newseg = thisLine.splitSegment(thisSegment, cursor);
			cursor2++;
			
			if (high>0) {
				TextLineSegment[] split2 = thisLine.splitSegment(newseg[0], high);
				newseg=new TextLineSegment[] {split2[0], split2[1], newseg[1]};
				high2++;
				}
			
		} else if (high>0) {
			newseg = thisLine.splitSegment(thisSegment, high);
			high2++;
			cursor2++;
			
		}
		
		
		this.setSelectedRange(high2, cursor2);
		setUpSelectedSegmentList() ;
		return newseg;
	}

	/**Handles backspace stroke for the hightlight region*/
	private void handleBackSpaceForHighlightRegion() {
		if (this.hasHighlightRegion()) {
			
			int start=this.getCursorPosition();
			int end=this.getHighlightPosition();
			/**repeats the backspace stroke*/
			for(int i=start; i>end; i--) {
				
				this.setHighlightPositionToCursor();
				setUpSelectedSegmentList();
				handleBackSpace();
			}
		}
		else 
			handleBackSpace();
	}



	/**called for each time the user hits the backspace key*/
	private void handleBackSpace() {
		
		TextLineSegment lastSegment = getParagraph().getLastLine().getLastSegment();
		TextLineSegment thisSegment = lastSegment;
		if(cursorSegment!=null)  thisSegment=cursorSegment;
		TextLine thisLine = getParagraph().getLineWithSegment(thisSegment);//.getLastLine();
		boolean oneSegmentInline=getParagraph().size()>1&&thisLine.size()==1;
		boolean noTextInthisSegment= thisSegment.getText().length()==0;
		int linIndex=this.getParagraph().indexOf(thisLine);
		
		/**if only one segment is in the highlight region, just backspace that segment*/
		if (oneSegmentInline&&noTextInthisSegment) {
			
			if (getParagraph().size()>1)this.getParagraph().remove(thisLine);
			
			this.setCursorPosition(this.getCursorPosition() - 1);
			return;
		}
		
		/**if cursor position is 0, then things can get deleted*/
		if (thisSegment.getCursorposition()==0)  {
						///IssueLog.log("back space on start of segment:"+thisSegment.getText()+"'"+thisSegment.getText().length());
						
						if (getParagraph().size()==1&&thisLine.size()==1&&thisSegment.getText().length()==0){
							if (this.getParentLayer()!=null) {
								UndoAbleEditForRemoveItem undo = Edit.removeItem(getParentLayer(), this);//removes the text item entirely
				
								
								/**TODO this undo does not work. cannot undo, will fix*/
								getUndoManager().addEdit(undo);
							return ;
							}
							}
						
						if(thisSegment.getText().length()==0) {
							if (this.getParagraph().size()==1&&thisLine.size()==1) return;
							thisLine.remove(thisSegment);
							this.setCursorPosition(this.getCursorPosition() - 1);
						}
						
						/**Case when there is text and a segment before this*/
						int segIndex = thisLine.indexOf(thisSegment);
						if(thisSegment.getText().length()>0&&segIndex>0) {
							///IssueLog.log("Segment should be removed");
							TextLineSegment previousSeg = thisLine.get(segIndex-1);
							
							thisLine.fuseSegments(previousSeg, thisSegment);
						
							this.setCursorPosition(this.getCursorPosition() - 1);
						}
						
						
						if(thisSegment.getText().length()>0&&segIndex==0&&linIndex>0) {
							if (getParagraph().size()>1)this.getParagraph().remove(thisLine);
							TextLine previousLine = getParagraph().get(linIndex-1);
							for(TextLineSegment seg: thisLine) {
								previousLine.add(seg);
							}
							this.setCursorPosition(this.getCursorPosition() - 1);
						}
						
						if(thisLine.size()==0) {
						//	IssueLog.log("Line should be removed");
							if (getParagraph().size()>1)this.getParagraph().remove(thisLine);
							
						}
						
						
						return;
		}
		
		String st = handleBackSpaceForString(thisSegment .getText(),thisSegment.getCursorposition());
		this.setCursorPosition(this.getCursorPosition()-1);
		thisSegment .setText(st);
	}
	
	protected int maxCursorPosition() {
		return 900;
	}




	public ArrayList<TextLineSegment> getAllSelectedSegments() {
		if (allSelectedSegments==null) setUpSelectedSegmentList();
		return allSelectedSegments;
	}


public int getMaxNeededCursor() { 
	ArrayList<TextLineSegment> all = getParagraph().getAllSegments();
	if (all.size()==1) return getParagraph().getText().length();
	int max = getParagraph().getText().length()+all.size()+2;//dont understand why I put +2 here
	
	return max;
	
}

/** combines any segments with identical properties*/
public void fuseIdenticalSegments() {
	for(TextLine line: this.getParagraph()) {
		for(int i=1; i<line.size(); i++) {
			TextLineSegment previous = line.get(i-1);
			TextLineSegment current = line.get(i);
			if (previous.isSimilarStyle(current)) {
				line.fuseSegments(previous, current);
				i--;
				this.setCursorPosition(this.getCursorPosition()-1);
			}
		}
	}
	this.setUpSelectedSegmentList();
}

/**returns a copy of the current paragraph*/
public TextParagraph copyParagraph() {
	return this.getParagraph().copy();
}


/**overrides the superclass method*/
@Override
public String getText() {
	return this.getParagraph().getText();
}

/***/
public void setContent(String st) {
	this.setText(st);
	getParagraph().get(0).get(0).setText(st);
}



}

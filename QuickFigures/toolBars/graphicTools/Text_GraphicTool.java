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
package graphicTools;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;

import applicationAdapters.ImageWrapper;
import externalToolBar.AbstractExternalToolset;
import genericMontageKit.BasicObjectListHandler;
import graphicalObjects.CursorFinder;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import icons.IconWrappingToolIcon;
import icons.TreeIconWrappingToolIcon;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ShapesUtil;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.TakesLockedItems;

/**A tool for adding text items to the image*/
public class Text_GraphicTool extends GraphicTool {
	
	private Cursor textCursor=new Cursor(Cursor.TEXT_CURSOR);
	{excludedClass=PanelLayoutGraphic.class;super.temporaryTool=true;}
	
	

	static boolean lowercase=false;
	TextGraphic model = new TextGraphic();
	{
		iconSet=TreeIconWrappingToolIcon.createIconSet(model);
	}
	Font font=new Font("Arial", Font.BOLD, 30);

	
	protected boolean editorOnly;
	private boolean startedDragCreation;
	private TextGraphic newCreation;

	
	
	public  Text_GraphicTool(boolean editorOnly) {
		this.editorOnly=editorOnly;
		
		if (editorOnly) {
			
			iconSet=IconWrappingToolIcon.createIconSet(new TextCursorIcon());
		}
	}
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		if (!(roi2 instanceof TextGraphic) && !createNewText()) super.onPress(gmp, roi2);
		handleTextObject(gmp, roi2);
		
		
	}
	
	public boolean createNewText() {
		return this.clickCount()==2;
	}
	
	
	public void handleTextObject(ImageWrapper gmp, LocatedObject2D roi2) {
		TextGraphic textob=null;
		lastUndo=null;
		int x = this.getClickedCordinateX();
		int y = this.getClickedCordinateY();
			
		boolean createNew=false;
		if (roi2 instanceof TextGraphic &&getSelectedHandleNumber()==-1 ) {
			
			textob=(TextGraphic) roi2;
			textob.setEditMode(true);
			mousePressOnTextCursor(textob);
			
			
		} else if (createNewText() &&getSelectedHandleNumber()==-1)
			createNew=true;
			
		/**in the annoying case that the user clicks just barely outside of the selected text item, this prevents a new item from being made*/
		if (createNew && lastText!=null&&lastText instanceof TextGraphic)
			{
			int insetDist=5;
			if (editorOnly) insetDist=25;
			Rectangle r2 = ShapesUtil.addInsetsToRectangle(lastText.getOutline().getBounds(), new Insets(insetDist,insetDist,insetDist,insetDist));
			if (r2.contains(new Point(x,y))) {
				textob=(TextGraphic) lastText;
				createNew=false;
				new CursorFinder().setCursorFor(textob, new Point(x,y));
				textob.setHighlightPositionToCursor();
				
			}
			this.setPrimarySelectedObject(textob);
			}
		
		
		
		if (createNew&&this.clickCount()==2)
			{ 
			textob= makeNewTextObject();
		
			
			textob.setLocationUpperLeft(getClickedCordinateX(), getClickedCordinateY());
			
			
			LocatedObject2D place = findPlaceToLockObject(textob);
			if (place instanceof KnowsParentLayer) {
				KnowsParentLayer layer=(KnowsParentLayer) place;
				layer.getParentLayer().add(textob);
			}
			else 
			{gmp.getTopLevelLayer().add(textob);
			addUndoerForAddItem(gmp, gmp.getTopLevelLayer().getSelectedContainer(), textob);
			}
			this.setPrimarySelectedObject(textob);
		}

		
		
		
		if (textob!=null) textob.updateDisplay();
		lastText=textob;
		
		if (lastText!=null)
		lastCursor=lastText.getCursorPosition();
	}

	
	@Override
	public void mouseReleased() {
		super.mouseReleased();
		
		startedDragCreation=false;
		newCreation=null;
	} 
	
	
	@Override
	public void mouseDragged() {
		
		if (this.shiftDown()||this.altKeyDown() ||this.getLastClickMouseEvent().isControlDown()||this.getLastClickMouseEvent().isMetaDown())
				{super.mouseDragged(); return;}
		
		if (lastText!=null)  {
			mouseDragForTextCursor();
			return;
		}
		
		
		double dist = pressY-this.getLastDragOrLastReleaseMouseEvent().getCoordinatePoint().getY();
		dist=Math.abs(dist);
		if (!startedDragCreation){
			
				if (dist>7) {
				
			startedDragCreation=true;
			newCreation= makeNewTextObject();
			
			ImageWrapper gmp = this.getImageClicked();
			gmp.getTopLevelLayer().add(newCreation);
			addUndoerForAddItem(gmp, gmp.getTopLevelLayer().getSelectedContainer(), newCreation);
			this.setPrimarySelectedObject(newCreation);
		}
		}
		
		if (startedDragCreation)
			{newCreation.setFontSize((int) dist);
			newCreation.setLocationUpperLeft(pressX, pressY);
			}
	}
	
	
	
	
	public LocatedObject2D findPlaceToLockObject(TextGraphic textob) {
		LocatedObject2D image = this.getObjecthandler().getClickedRoi(getImageClicked(), getClickedCordinateX(), getClickedCordinateY(), TakesLockedItems.class);
		int cx = getClickedCordinateX();
		int cy = getClickedCordinateY();
		lockAndSnap(image, textob, cx, cy);
		return image;
	}
	
	public static void lockAndSnap(LocatedObject2D image, TextGraphic textob, int cx, int cy) {
		if (image instanceof TakesLockedItems) {
			TakesLockedItems taker = (TakesLockedItems) image;
			
			taker.addLockedItem(textob);
			
		textob.setAttachmentPosition(createStartingDefaultSnap(image, textob, cx, cy));
		
		taker.snapLockedItems();	
		}
	}
	
	static AttachmentPosition createStartingDefaultSnap(LocatedObject2D image, TextGraphic textob , int cx,int cy) {
		AttachmentPosition output = AttachmentPosition.defaultPanelLabel();
		Rectangle2D snapBounds = image.getBounds();
		
		if (image instanceof PanelLayoutGraphic) { 
			PanelLayoutGraphic plg = (PanelLayoutGraphic) image;
			snapBounds = plg.getPanelLayout().getNearestPanel(textob.getBounds().getCenterX(), textob.getBounds().getCenterY());
			
			//sets the lettering to a, b, c… 
			if (snapBounds.contains(textob.getBounds())) {
				int i=(int) 'A';
				if (	lowercase) i=(int) 'a';
				 int index = plg.getPanelLayout().getNearestPanelIndex(textob.getBounds().getCenterX(), textob.getBounds().getCenterY());
				 String letter = (char)(i+index-1)+"";
				 textob.setText(letter);
				 
			}
			
			//textob.setText("");
		}
		
		
		
		output.setToNearestInternalSnap(textob.getBounds(),snapBounds, new Point(cx, cy));	
		textob.getAttachmentPosition().setToNearestExternalSnap(textob.getBounds(), snapBounds, new Point(cx, cy));	
		
		return output;
	}
	
	public TextGraphic makeNewTextObject() {
		TextGraphic textob = new TextGraphic();
		textob.copyAttributesFrom(model);
		textob.setTextColor(getForeGroundColor());
		
		return textob;
	}
	
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
	}
	
	@Override
	public boolean keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		if (this.getPrimarySelectedObject() instanceof TextGraphic) {
			
			keyPressOnSelectedTextItem(arg0);
			
		} else
		super.keyPressed(arg0);
	this.updateClickedDisplay();
	
	return true;
	}
	
	@Override
	public boolean keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		if (this.getPrimarySelectedObject() instanceof TextGraphic) {
			keyTypedOnTextItem(arg0);
		} else
		super.keyTyped(arg0);
	this.updateClickedDisplay();
	
	return true;
	}
	
	
	
	public void updateCursorIfOverhandle() { 
		
		LocatedObject2D roi2 =  getObjectAt(getImageClicked(), getClickedCordinateX(),getClickedCordinateY());
		if (roi2 instanceof TextGraphic) {
			getImageDisplayWrapperClick().setCursor(textCursor);
			
		} else
		
		super.updateCursorIfOverhandle();
		
		
	}

	
	
	@Override
	public String getToolName() {
			if (editorOnly) return "Text Edit Tool";
			return "Add Text Tool";
		}
	
	class TextCursorIcon implements Icon {

		@Override
		public int getIconHeight() {
			
			return AbstractExternalToolset.DEFAULT_ICONSIZE;
		}

		@Override
		public int getIconWidth() {
			return AbstractExternalToolset.DEFAULT_ICONSIZE;
		}

		@Override
		public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
			Font oFont = arg1.getFont();
			arg3+=2;
			arg1.setColor(Color.black);
			arg1.setFont(new Font("Arial", 1, 12));
			arg1.drawString("T", arg2+3, arg3+14);
			
			Graphics2D g2d=(Graphics2D) arg1;
			
			g2d.setStroke(new BasicStroke(1));
			
			int x=arg2+12;
			int y1=arg3+3;
			int y2=arg3+15;
			g2d.drawLine(x, y1, x, y2);
			
			g2d.drawLine(x-2, y1, x-1, y1);
			g2d.drawLine(x+1, y1, x+2, y1);
			
			g2d.drawLine(x-2, y2, x+2, y2);
			
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(x-12, y1-4, 18, 18);
			arg1.setFont(oFont);
		}}
	
	/**Method below was commented out on may 13 2020 to see effects. from appearances,
	 *  this method was meant to make it easier to click on the edge of a text item 
	 *  but it had the effect of making it impossible to click a text item with something else behind it */
/***/
	protected ArrayList<LocatedObject2D> getObjectsAtPressLocationWithoutFiltering(ImageWrapper click, int x, int y) {
		if (!this.editorOnly) return super.getObjectsAtPressLocationWithoutFiltering(click, x, y);
		ArrayList<LocatedObject2D> list = getObjecthandler().getOverlapOverlaypingOrContainedItems(new Rectangle(x-5, y-5, 10, 10), click, new BasicObjectListHandler.excluder(this.excludedClass));
		
		ArrayList<LocatedObject2D> list2 = new ArrayList<LocatedObject2D>();
		for(int i=list.size()-1; i>=0; i--) {
			list2.add(list.get(i));
		}
		
		return list2;
		//return getObjecthandler().getAllClickedRoi(click, x, y,this.onlySelectThoseOfClass);
	}
	
	
	
}

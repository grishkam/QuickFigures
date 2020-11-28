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
import externalToolBar.IconWrappingToolIcon;
import externalToolBar.TreeIconWrappingToolIcon;
import genericMontageKit.BasicObjectListHandler;
import graphicalObjects.CursorFinder;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ShapesUtil;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.TakesLockedItems;

public class Text_GraphicTool extends GraphicTool {
	
	private Cursor textCursor=new Cursor(Cursor.TEXT_CURSOR);
	{excludedClass=PanelLayoutGraphic.class;super.temporaryTool=true;}
	
	

	static boolean lowercase=false;
	TextGraphic model = new TextGraphic();// {model.setSnappingBehaviour(snappingBehaviour);}
	{//createIconSet("icons2/TextIcon.jpg","icons2/TextIconPressed.jpg","icons2/TextIcon.jpg");
	set=TreeIconWrappingToolIcon.createIconSet(model);

	}
	Font font=new Font("Arial", Font.BOLD, 30);

	
	protected boolean editorOnly;
	private boolean startedDragCreation;
	private TextGraphic newCreation;

	
	
	public  Text_GraphicTool(boolean editorOnly) {
		this.editorOnly=editorOnly;
		
		if (editorOnly) {
			
			set=IconWrappingToolIcon.createIconSet(new textCursorIcon());
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
		if (roi2 instanceof TextGraphic &&getPressedHandle()==-1 ) {
			
			textob=(TextGraphic) roi2;
			textob.setEditMode(true);
			mousePressOnTextCursor(textob);
			
			
		} else if (createNewText() &&getPressedHandle()==-1)
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
			this.setSelectedObject(textob);
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
			{gmp.getGraphicLayerSet().add(textob);
			addUndoerForAddItem(gmp, gmp.getGraphicLayerSet().getSelectedContainer(), textob);
			}
			this.setSelectedObject(textob);
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
		
		
		double dist = pressY-this.getLastDragOrRelMouseEvent().getCoordinatePoint().getY();
		dist=Math.abs(dist);
		if (!startedDragCreation){
			
				if (dist>7) {
				
			startedDragCreation=true;
			newCreation= makeNewTextObject();
			
			ImageWrapper gmp = this.getImageWrapperClick();
			gmp.getGraphicLayerSet().add(newCreation);
			addUndoerForAddItem(gmp, gmp.getGraphicLayerSet().getSelectedContainer(), newCreation);
			this.setSelectedObject(newCreation);
		}
		}
		
		if (startedDragCreation)
			{newCreation.setFontSize((int) dist);
			newCreation.setLocationUpperLeft(pressX, pressY);
			}
	}
	
	
	
	
	public LocatedObject2D findPlaceToLockObject(TextGraphic textob) {
		LocatedObject2D image = this.getObjecthandler().getClickedRoi(getImageWrapperClick(), getClickedCordinateX(), getClickedCordinateY(), TakesLockedItems.class);
		int cx = getClickedCordinateX();
		int cy = getClickedCordinateY();
		lockAndSnap(image, textob, cx, cy);
		return image;
	}
	
	public static void lockAndSnap(LocatedObject2D image, TextGraphic textob, int cx, int cy) {
		if (image instanceof TakesLockedItems) {
			TakesLockedItems taker = (TakesLockedItems) image;
			
			taker.addLockedItem(textob);
			
		textob.setSnapPosition(createStartingDefaultSnap(image, textob, cx, cy));
		
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
		textob.getSnapPosition().setToNearestExternalSnap(textob.getBounds(), snapBounds, new Point(cx, cy));	
		
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
		
		if (this.getSelectedObject() instanceof TextGraphic) {
			
			keyPressOnSelectedTextItem(arg0);
			
		} else
		super.keyPressed(arg0);
	this.updateClickedDisplay();
	
	return true;
	}
	
	@Override
	public boolean keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		if (this.getSelectedObject() instanceof TextGraphic) {
			keyTypedOnTextItem(arg0);
		} else
		super.keyTyped(arg0);
	this.updateClickedDisplay();
	
	return true;
	}
	
	
	
	public void updateCursorIfOverhandle() { 
		
		LocatedObject2D roi2 =  getObject(getImageWrapperClick(), getClickedCordinateX(),getClickedCordinateY());
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
	
	class textCursorIcon implements Icon {

		@Override
		public int getIconHeight() {
			// TODO Auto-generated method stub
			return 25;
		}

		@Override
		public int getIconWidth() {
			// TODO Auto-generated method stub
			return 25;
		}

		@Override
		public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
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

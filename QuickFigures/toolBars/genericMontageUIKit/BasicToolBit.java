package genericMontageUIKit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import externalToolBar.DragAndDropHandler;
import externalToolBar.IconSet;
import genericMontageKit.BasicOverlayHandler;
import gridLayout.BasicMontageLayout;
import imageDisplayApp.ImageAndDisplaySet;
import imageMenu.CanvasAutoResize;
import includedToolbars.ObjectToolset1;
import logging.IssueLog;
import undo.UndoManagerPlus;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;

public class BasicToolBit implements ToolBit {
	public static final String selectorToolName = "Object Selector/Mover";
	private ToolCore toolCore;
	private IconSet iconSet=null;
	private BasicOverlayHandler oh=new BasicOverlayHandler();
	private JButton button;
	
	

	protected Cursor normalCursor=new Cursor(Cursor.DEFAULT_CURSOR);

	@Override
	public void mousePressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void mouseExited() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setToolCore(ToolCore toolCore) {
		this.toolCore=toolCore;
		
	}
	
	
	public ToolCore getToolCore() {
		return toolCore;
		
	}

	

	@Override
	public IconSet getIconSet() {
		return iconSet;
	}
	
	public void setIconSet(IconSet icons) {
		iconSet=icons;
	}
	
	public int getMouseButtonClick() {
		return this.getToolCore().getMouseButtonClick();
	}
	
	
	public boolean shiftDown() {
		return this.getToolCore().shiftDown();
	}
	
	public boolean altKeyDown() {
		return this.getToolCore().altKeyDown();
	}

	
	public void setClickPointToDragReleasePoint() {
		getToolCore().setClickPointToDragReleasePoint();
		
	}
	
	public String getToolName() {
		return this.getClass().toString().replace('_', ' ');
	}
	
	public void createIconSet(String... sts) {
		IconSet output = new IconSet(sts);
		iconSet=output;
		
	}

	
	public ImageWrapper getImageWrapperClick() {
		// TODO Auto-generated method stub
		if (toolCore==null) IssueLog.log("no tool core!!!");
		return toolCore.getImageWrapperClick();
	}
	
	
	public DisplayedImageWrapper getImageDisplayWrapperClick() {
		// TODO Auto-generated method stub
		if (toolCore==null) IssueLog.log("no tool core!!!");
		return toolCore.getClickedImage();//.getImageWrapperClick();
	}
	
	public UndoManagerPlus getUndoManager() {
		return getImageDisplayWrapperClick() .getUndoManager();
	}
	
	public int clickCount() {
		return toolCore.clickCount();
	}
	
	public BasicOverlayHandler getObjecthandler() {
		return oh;
	}
	
	public int getXDisplaceMent() {
		return this.getToolCore().getXDisplaceMent();
	}
	
	
	public int getYDisplaceMent() {
		return this.getToolCore().getYDisplaceMent();
	}
	
	public int getClickedCordinateX() {
		return this.getToolCore().getClickedCordinateX();
	}
	
	public int getClickedCordinateY() {
		return this.getToolCore().getClickedCordinateY();
	}
	
	public int getDragCordinateX() {
		return this.getToolCore().getDragCordinateX();
	}
	public int getDragCordinateY() {
		return this.getToolCore().getDragCordinateY();
	}
	public Point getDragPoint() {
		return new Point(getDragCordinateX(), getDragCordinateY());
	}
	
	public int getReleaseCordinateY() {
		// TODO Auto-generated method stub
		return this.getToolCore().getReleaseCordinateY();
	}



	public int getReleaseCordinateX() {
		// TODO Auto-generated method stub
		return this.getToolCore().getReleaseCordinateX();
	}
	
	public int getMouseYClick() {
		return this.getToolCore(). getMouseYClick();
	}
	
	public int getMouseXClick() {
		return this.getToolCore(). getMouseXClick();
	}
	
	public int getColIndexClick() {
		return this.getToolCore(). getColIndexClick();
	}
	

	public int getRowIndexClick() {
		return this.getToolCore(). getRowIndexClick();
	}
	
	public int getColIndexDrag() {
		return this.getToolCore(). getColIndexDrag();
	}
	public int getRowIndexDrag() {
		return this.getToolCore(). getRowIndexDrag();
	}
	
	/**
	public BasicMontageLayout getClickedLayout() {
		return this.getToolCore().getClickedLayout();
	}*/

	@Override
	public void mouseReleased() {
		// TODO Auto-generated method stub
		
	}
	
	public CanvasMouseEventWrapper getLastClickMouseEvent() {
		return this.getToolCore(). getLastMouseEvent();
	}
	
	public CanvasMouseEventWrapper getLastDragOrRelMouseEvent() {
		return this.getToolCore(). getLastDragMouseEvent();
	}
	
	public Color getForeGroundColor() {
		return this.getToolCore().getForeGroundColor();
	}

	public int getMouseDisplacementY() {
		return this.getToolCore().getMouseDisplacementY();
	}
	
	public int getMouseDisplacementX() {
		return this.getToolCore().getMouseDisplacementX();
	}
	
	@Override
	public void mouseMoved() {
	
		
	}

	@Override
	public void mouseClicked() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOptionsDialog() {
		// TODO Auto-generated method stub
		
	}
	
	public int  getPanelIndexClick() {
		return getToolCore().getPanelIndexClick();
	}
	public int  getPanelIndexDrag() {
		return getToolCore().getPanelIndexDrag();
	}
	
	public int getMouseXdrag() {
		return getToolCore().getMouseXdrag();
	}
	public int getMouseYdrag() {
		return getToolCore().getMouseYdrag();
	}
	
	public ImageWrapper currentlyInFocusWindowImage() {
		return this.getToolCore().currentlyInFocusWindowImage();
	}
	/**
	public AbstractMontageLayout<?> getMainLayout() {
		return this.getToolCore().getMainLayout();
	}
	
	public AbstractMontageLayout<?> getLayoutForCurrentImage() {
		return this.getToolCore().getLayoutForCurrentImage();
	}*/

	/**
	public boolean doesClickedImageHAveMaintMontageLayout() {
		return this.getImageWrapperClick().createLayout()!=null;
	//	return this.getToolCore().doesClickedImageHAveMaintMontageLayout();
	}*/
	
	/**public void setCursorIcon(Image i) {
		this.getToolCore().setCursorIcon(i);
	}*/

	@Override
	public ArrayList<JMenuItem> getPopupMenuItems() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void updateClickedDisplay() {
		if(this.getImageWrapperClick()!=null)
		this.getImageWrapperClick() .updateDisplay();
	}
	
	protected Point clickedCord() {
		return new Point(this.getClickedCordinateX(), this.getClickedCordinateY());
	}
	
	protected Point draggedCord() {
		return new Point(this.getDragCordinateX(), this.getDragCordinateY());
	}

	public void setRowColClickForLayout(BasicMontageLayout lay) {
		this.getToolCore().setRowColClickForLayout(lay);
		
	}

	public void setMouseXClick(int mouseXClick) {
		getToolCore().setMouseXClick(mouseXClick);
		
	}

	public void setMouseYClick(int mouseYclick) {
		getToolCore().setMouseYClick(mouseYclick);
		
	}

	public void setRowColDragForLayout(BasicMontageLayout lay) {
		getToolCore().setRowColDragForLayout(lay);
		
	}

	

	public void setCursor(Cursor currentCursor) {
		//this.getImageDisplayWrapperClick().getWindow().setCursor(currentCursor);
		
	}

	public void resizeCanvas() {
		// TODO Auto-generated method stub
		resizeCanvas(getImageDisplayWrapperClick());
	}
	
	protected void resizeCanvas(DisplayedImageWrapper wrap) {
				new CanvasAutoResize().performActionDisplayedImageWrapper(wrap);
	}

	@Override
	public boolean keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleFileListDrop(ImageAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DragAndDropHandler getDragAndDropHandler() {
		return null;
		
	}

	@Override
	public String getToolTip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActionTool() {
		
		return false;
	}

	@Override
	public void performLoadAction() {
	
		
	}


	protected Object todefaultTool() {
		if(this.getClass().equals(Object_Mover.class)) {} else {
			return ObjectToolset1.setCurrentTool(selectorToolName);
		}
		return null;
	}

	@Override
	public boolean treeSetSelectedItem(Object o) {
		
		return false;
	}
	
	
	
	/**returns the object that may be clicked on at a given point*/
	public LocatedObject2D getObject(ImageWrapper click, int x, int y) {
		ArrayList<LocatedObject2D> therois = getObjecthandler().getAllClickedRoi(click, x, y, Object.class);
		ArraySorter.removehideableItems(therois);//removes hidden items
		return new ArraySorter<LocatedObject2D>().getFirstNonNull(therois);
		
	}

	/**Called when a tool is about to be switched away from (false) or switched to (true)*/
	@Override
	public void onToolChange(boolean b) {
		
		
	}

	@Override
	public String getToolSubMenuName() {
		return null;
	}

	
	

}
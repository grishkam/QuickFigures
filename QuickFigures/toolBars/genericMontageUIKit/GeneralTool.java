package genericMontageUIKit;
import appContext.CurrentAppContext;
import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import externalToolBar.*;
import genericMontageKit.*;
import graphicActionToombar.CurrentSetInformerBasic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.MontageEditorDialogs;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import utilityClassesForObjects.LocatedObject2D;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenuItem;

/**this class contains fields and methods useful for any tool that manipulates montages.
   It is used by multiple tools so any edits to it may interfere with the function of multiple other 
   classes. it has had a bit of rough editing.
   */
public class GeneralTool extends BlankTool<DisplayedImageWrapper> implements ActionListener, ToolCore{
	
	public BasicOverlayHandler oh=new BasicOverlayHandler();
	
	static Cursor defaultCursor=new Cursor(Cursor.DEFAULT_CURSOR);
	
	private ToolBit toolbit=null;
	
	private ArrayList<ToolBit> additionalBits;
	
	protected GeneralTool() {}

	public GeneralTool(
			ToolBit bit
			) {
		setUpToolBit(bit);
		
	}
	
	
	
	public GeneralTool(ArrayList<ToolBit> pathGraphicBits) {
		 additionalBits=pathGraphicBits;
		 this.setUpToolBit(pathGraphicBits.get(0));
	}
	
	public GeneralTool(ToolBit bit, ToolBit... bits2) {
		 additionalBits=new ArrayList<ToolBit>();
		 additionalBits.add(bit);
		 for(ToolBit bit2: bits2) {
			 additionalBits.add(bit2);
		 }
		 this.setUpToolBit(bit);
	}

	void setUpToolBit(ToolBit bit) {
		this.setToolbit(bit);
		bit.setToolCore(this);
	}
	
	//private int clickedCordinateX;
	private int clickedCordinateY;
	private int dragCordinateX;
	private int dragCordinateY;

	
	private int mouseDisplacementX;
	private int mouseDisplacementY;
	
	private Point releasePoint;
	private Point releasePointMouse;
	private int mouseXClick;
	private int mouseYClick;
	private int mouseXdrag;
	private int mouseYdrag;
	private DisplayedImageWrapper imageClick;
	private ImageWrapper imageWrapperClick;
	
	protected CanvasMouseEventWrapper event ;//the latest mouse event of any kind
	protected CanvasMouseEventWrapper eventClick ;
	protected CanvasMouseEventWrapper eventDrag ;
	protected CanvasMouseEventWrapper eventRelease ;
	boolean markEditedRegionsWithRoi=true;
	
	private int channelClick;
	private int channelDrag;
	private int frameClick;
	private int frameDrag;
	private int sliceClick;
	private int sliceDrag;
	public boolean isUpdaterImage=false;
	protected PanelListElement panelClick;
	protected PanelListElement panelDrag;
	//public AbstractMontageUpdater<DisplayedImageWrapper> updater;
	//private Image cursorIcon;
	private int panelIndexClick;
	private int panelIndexDrag;
	private int colIndexClick;
	private int colIndexDrag;
	private int rowIndexClick;
	private int rowIndexDrag;
	
	//Overlay o;
	//private Roi temp=null;
	//private AbstractMontageLayout<DisplayedImageWrapper> mainLayoutOfImageclick=null;//getAdapter().createLayout();
	private GenericMontageEditor montEditor=new GenericMontageEditor();//null;//getAdapter().createEditor();
	private int clickCount;
	private java.awt.event.MouseEvent lastME;
	private transient CanvasMouseEventWrapper lastDragMe;
	private Cursor currentCursor;
	
	private int releaseSlice;
	private int channelRelease;
	private int releaseFrame;
	private int panelIndRelease;
	private int rowRelease;
	private int colRelease;
	private int mouseButton;
	private DisplayedImageWrapper dispImageClick;
	private CanvasMouseEventWrapper lastDragOrReleaseEvent;
	private int clickedCordinateX;
	
	
	
	@Override
	public void mousePressed(DisplayedImageWrapper imp, CanvasMouseEventWrapper e) {
		 event =e;
		 eventClick=event;
		 
		//setClickPoint(imp,e);
		setClickPoint(imp,event);
		try {
			getToolbit().mousePressed();
			mousePressed();
			getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.log(e2);
		}
		
		}
	
	public void mousePressed() {
		// TODO Auto-generated method stub
		
	}
	
	public void setCursor(Cursor c, int i) {
		DisplayedImageWrapper disp = event.getAsDisplay();
		if (disp!=null)disp.setCursor(c);
	}


	
	@Override
	public void mouseEntered(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		try {
			//IssueLog.log("mouse entry heard");
			CanvasMouseEventWrapper event1 = e;
			event=e;
			setCursor(currentCursor, 0);
			setClickPoint(imp,event1);
			getToolbit().mouseEntered();
			mouseEntered();
			getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.log(e2);
		}
		}
	
	public void mouseEntered() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseMoved(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		
		
		try {
			
			CanvasMouseEventWrapper event1 = e;
			event=event1;
			//setCursor(currentCursor, 0);
			setClickPoint(imp,event1);
			if (getToolbit()!=null) getToolbit().mouseMoved();
			mouseMoved();
			if (getImageWrapperClick()==null) {
				 IssueLog.log("wrapper not innitialized");
			}
			//this display update was slowing down the program
			//getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.log(e2);
		}
		}
	
	
	
	public void mouseMoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		try {
			setReleaseOrDragPoint(imp, e);
			getToolbit().mouseExited();
			mouseExited();
			this.getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.log(e2);
		}
		}
	
	public void mouseExited() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		setReleaseOrDragPoint(imp,e);
	event=e;
			lastDragOrReleaseEvent =  e;
		
		
		 try {
			this.getToolbit().mouseDragged();
			mouseDragged();
			this.getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.log(e2);
		}
	}
	
	
	public void mouseDragged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		setReleasePoint(imp,e);
		event=e;
			lastDragOrReleaseEvent = e;
		
		getToolbit().mouseReleased();
		mouseReleased();
		this.getImageWrapperClick().updateDisplay();
	}
	
	

	public Color getForeGroundColor() {
		return CurrentAppContext.getGeneralContext().getForeGroundColor();
		
	}
	
	public void mouseReleased() {}
	
	/**
	public void updateFromSource() {
		if (isUpdaterImage) getAdapter().getCurrentUpdater().updateMontageFromSource();
	}
	*/
	
	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getEditor()
	
	@Override
	public GenericMontageEditor getEditor() {
		if (montEditor==null) montEditor=getAdapter().createEditor();
		return montEditor;
	} */
/**
	public BasicMontageLayout getMainLayout() {
		if (mainLayoutOfImageclick==null) {
			
			mainLayoutOfImageclick=getAdapter().createLayout();
			}
		return mainLayoutOfImageclick;
	}
	
	public AbstractMontageLayout<DisplayedImageWrapper> getLayoutForCurrentImage() {
		setImageClick(getAdapter().currentlyInFocusWindowImage());
		if (getImageClick()!=null) {
			mainLayoutOfImageclick=getAdapter().createLayout(getImageClick());
			}
		return mainLayoutOfImageclick;
	}*/
	
	

	/** Returns the difference in cordinates between the last mouse press
	  and the last drag (or release). Note, these are coordinates are
	  the Figure cordinates and NOT the JComponent coordinates of the MouseEvents.
	  
	 */
	@Override
	public int getXDisplaceMent() {
		setOScreen1();
		setOScreen2();
		return getDragCordinateX() -getClickedCordinateX() ;
	}
	@Override
	public int getYDisplaceMent() {
		setOScreen1();
		setOScreen2();	
		return getDragCordinateY()-getClickedCordinateY() ;
	}
	
	
	
	public void setXY1(DisplayedImageWrapper imp, CanvasMouseEventWrapper event) {
		
		setMouseXClick(event.getClickedXScreen());
		setMouseYClick(event.getClickedYScreen());
		setClickedCordinateX(event.getClickedXImage());
		setClickedCordinateY(event.getClickedYImage());
	
		setChannelClick(event.getClickedChannel());
		setFrameClick(event.getClickedFrame());
		setSliceClick(event.getClickedSlice());
	}
	
	
	
	
	
	public void setXY2(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		CanvasMouseEventWrapper event=e;
		
		setMouseXdrag(event.getClickedXScreen());
		setMouseYdrag(event.getClickedYScreen());
		setDragCordinateX(event.getClickedXImage());
		setDragCordinateY(event.getClickedYImage());

		setChannelDrag(event.getClickedChannel());
		setFrameDrag(event.getClickedFrame());
		setSliceDrag(event.getClickedSlice());
	}
	private void setXY3(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		CanvasMouseEventWrapper event=e;
		this.releasePointMouse=new Point(event.getClickedXScreen(), event.getClickedYScreen());
		this.releasePoint=new Point(event.getClickedXImage(), event.getClickedYImage());
		setChannelRelease(event.getClickedChannel());
		setFrameRelease(event.getClickedFrame());
		setSliceRelease(event.getClickedSlice());
	}
	/**
	public void setRowColReleaseForLayout(BasicMontageLayout ml) {
		setPanelIndexRelease(ml.getPanelIndex(getReleaseCordinateX() , getReleaseCordinateY()));
		int[] gridpos=ml.getGridCordAtIndex(getPanelIndexRelease());
		setColIndexRelease(gridpos[0]);
		setRowIndexRelease(gridpos[1]);
	}*/
	
	
	
	private void setFrameRelease(int clickedFrame) {
		releaseFrame=clickedFrame;
		
	}

	private void setSliceRelease(int clickedSlice) {
		releaseSlice=clickedSlice;
		
	}
	
	public int getSliceRelease() {
		return releaseSlice;
		
	}

	private void setChannelRelease(int clickedChannel) {
		channelRelease=clickedChannel;
		
	}

	public void setClickPoint(DisplayedImageWrapper imp, CanvasMouseEventWrapper event) {
		if (imp==null) return;
	
		eventClick=event;
		
		setImageClick(imp);
		
		mouseButton=event.mouseButton();
		
		setImageWrapperClick(event.getAsDisplay().getImageAsWrapper());
		setXY1(imp, event);
		if (event instanceof java.awt.event.MouseEvent ) {
			lastME = (java.awt.event.MouseEvent) event;
		}
		else lastME=eventClick.getAwtEvent();
		clickCount=event.clickCount();
		
		//mainLayoutOfImageclick=getAdapter().createLayout(imp);
		//setRowColClickForLayout(getMainLayout());
		
		/**AbstractMontageUpdater<DisplayedImageWrapper> up = getAdapter().getCurrentUpdater();
		
		if (up!=null&& up.images2()!=null&&this.getImageWrapperClick().isSameImage(up.images2().getMontage())) {
			
			if (up==null||up.getWorkingStack()==null) return;
			panelClick=up.getWorkingStack().getMontageIndexPanel(getPanelIndexClick());
			isUpdaterImage=true;
			updater=up;
		}
		else isUpdaterImage=false;*/
	}

	

	
	/**This takes converts the onscreen cordinates to off*/
	private void setOScreen1() {
		CanvasMouseEventWrapper canvasMouse =event;
		setClickedCordinateX(canvasMouse.convertClickedXImage(getMouseXClick()));
		setClickedCordinateY(canvasMouse.convertClickedYImage(getMouseYClick()));
		
	}
	private void setOScreen2() {
		CanvasMouseEventWrapper canvasMouse = event;
		setDragCordinateX(canvasMouse.convertClickedXImage(getMouseXdrag()));
		setDragCordinateY(canvasMouse.convertClickedYImage(getMouseYdrag()));
	}
	
	
	
	public void setReleaseOrDragPoint(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		this.setImageClick(imp);
		this.lastDragMe=e;
		this.event=e;
		this.eventDrag=event;
		lastDragOrReleaseEvent=e;
		this.setImageWrapperClick(event.getAsDisplay().getImageAsWrapper());
		setXY2(imp,e);
		
		/**mainLayoutOfImageclick=getAdapter().createLayout(imp);
		setRowColDragForLayout(getMainLayout());*/
		/**
		AbstractMontageUpdater<DisplayedImageWrapper> man = getAdapter().getUpdater(imp);
		
		
		if (man!=null&&man.images2().getMontage()==imp) {

			panelDrag=man.getWorkingStack().getMontageIndexPanel(getPanelIndexDrag());
			isUpdaterImage=true;
			updater=man;
			
		}
		else isUpdaterImage=false;*/
	}
	
	private void setReleasePoint(DisplayedImageWrapper imp, CanvasMouseEventWrapper e) {
		this.eventRelease=e;
		this.setImageClick(imp);
		this.setImageWrapperClick(event.getAsDisplay().getImageAsWrapper());
		setXY3(imp,e);
		//setRowColReleaseForLayout(getMainLayout());
		
		
	}
	
	

	private void setRowColReleaseForLayout(
			BasicMontageLayout ml) {
		if (ml==null) {IssueLog.log3("must have a layout to set a row and column release point"); return;}
		setPanelIndexRelease(ml.getPanelIndex(getReleaseCordinateX() , getReleaseCordinateY() ));
		int[] gridpos=ml.getGridCordAtIndex(getPanelIndexRelease());
		setColIndexRelease(gridpos[0]);
		setRowIndexRelease(gridpos[1]);
	}

	private void setRowIndexRelease(int i) {
		rowRelease=i;
		
	}

	private void setColIndexRelease(int i) {
		colRelease=i;
		
	}

	private int getPanelIndexRelease() {
		// TODO Auto-generated method stub
		return panelIndRelease;
	}

	private void setPanelIndexRelease(int panelIndex) {
		 panelIndRelease=panelIndex;
		
	}

	public void setRowColDragForLayout(BasicMontageLayout ml) {
		if (ml==null) {IssueLog.log3("must have a layout to set a row and col for drag");return;}
		setPanelIndexDrag(ml.getPanelIndex(getDragCordinateX() , getDragCordinateY() ));
		int[] gridpos=ml.getGridCordAtIndex(getPanelIndexDrag());
		setColIndexDrag(gridpos[0]);
		setRowIndexDrag(gridpos[1]);
	}
	
	public void setRowColClickForLayout(BasicMontageLayout ml) {
		if (ml==null) {IssueLog.log3("Do not have a layout when asked"); return;}
		setPanelIndexClick(ml.getPanelIndex(getClickedCordinateX() , getClickedCordinateY()));
		int[] gridpos=ml.getGridCordAtIndex(getPanelIndexClick());
		setColIndexClick(gridpos[0]);
		setRowIndexClick(gridpos[1]);
	}
	

	@Override
	public MontageEditorDialogs getMontageEditorDialogs() {return new MontageEditorDialogs();}

	

	@Override
	public void setMarkerRoi(int type) {
		//setMarkerRoi(type, getMainLayout());
	}
	
	public void setMarkerRoi(int type, BasicMontageLayout ml) {
		if (ml==null) {IssueLog.log3("must have a layout to set a marker roi");return;}
		setMarkerRoi(ml.getSelectedSpace(1, type));
	}


	@Override
	public void setMarkerRoi(int index, int type) {
		//setMarkerRoi(getMainLayout().getSelectedSpace(index, type));
	}
	
	@Override
	public void setMarkerRoi(Shape s) {
		this.getImageWrapperClick().getSelectionManagger().select(s, getStrokeWidth(), 0);
		//getObjectAdapter().select(getImageClick(), s, );
	}
	
	public void setMarkerRoi(Shape s, int strokeWidth) {
		getImageWrapperClick().getSelectionManagger().select( s, strokeWidth, 0);
	}
	
	public void removeSelection() {
		removeMarkerRoi() ;
	}
	
	public void removeMarkerRoi() {
		this.getImageWrapperClick().getSelectionManagger().removeSelections();
		//getObjectAdapter().deselect(getImageClick());
	//	imp.killRoi(); imp.updateAndDraw();
	}
	
	public int getStrokeWidth() {
		if (getImageClick()==null)return 8;
		
		return getImageWrapperClick().getPixelWrapper().width()/200;
		//if (imp!=null)return (int) (imp.getWidth()/200);
		//return 8;
	}
	
	/**These method is for showing and removing and overlay roi that surrounds the panel at the start of a
	 mouse drag
	public void makeDragMask(DisplayedImageWrapper imp) {
		//o=imp.getOverlay();if (o==null) {o=new Overlay();imp.setOverlay(o);}
		removeDragMask( imp);
		
		setTemporarySelection(getObjectAdapter().getSelectionObject(imp));//temp=imp.getRoi(); 

		
		getObjectAdapter().setSelectionStroke(getTemporarySelection(), getStrokeWidth(), Color.green.darker());
		
		getObjectAdapter().addRoiToImage(getTemporarySelection(), imp);
		//o.add(temp); 
		//temp.setStrokeWidth(getStrokeWidth());
	}
	
	public void removeDragMask(DisplayedImageWrapper imp) {
		getObjectAdapter().takeRoiFromImage(getTemporarySelection(), imp);
	}*/
	

	
	@Override
	public void actionPerformed(ActionEvent arg0) {}
	

	
	
	@Override
	public void createIconSet(String... sts) {
		IconSet output = new IconSet(sts);
		setIconSet(output);
		
	}
	
	/**
	public Rectangle getSelectionBounds(DisplayedImageWrapper imp) {
		return getObjectAdapter().getSelectionBounds(imp);
	}*/
	
	/**Returns a menu item with this object as an action listener.*/
	public MenuItem createMenuItem(String st) {
		MenuItem output = new MenuItem(st);
		output.addActionListener(this);
		return output;
	}
	/**Returns a menu item with this object as an action listener.*/
	public JMenuItem createJMenuItem(String st) {
		JMenuItem output = new JMenuItem(st);
		output.addActionListener(this);
		return output;
	}
	

	/**
	public boolean isMontage(DisplayedImageWrapper imp) {
		return this.getAdapter().hasMontageMetaData(imp);
	}*/
	
	public BasicOverlayHandler getObjecthandler() {
		return oh;
	}
	
	
	
	/**protected ObjectAdapter<DisplayedImageWrapper, Roi> getObjectAdapter() {
		return oa;
	}*/
	
	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#updateImageDisplay(DisplayedImageWrapper)
	
	@Override
	public void updateImageDisplay(DisplayedImageWrapper image) {
		if (image==null) return;
	getAdapter().updateImageDisplay(image);
	} */

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getImageClick()
	 */
	
	public DisplayedImageWrapper getImageClick() {
		return imageClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setImageClick(DisplayedImageWrapper)
	 */

	public void setImageClick(DisplayedImageWrapper imageClick) {
		this.imageClick = imageClick;
		if (event!=null)this.dispImageClick=event.getAsDisplay();
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getClickedCordinateX()
	 */
	@Override
	public int getClickedCordinateX() {
		return clickedCordinateX;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setClickedCordinateX(int)
	 */
	
	public void setClickedCordinateX(int clickedCordinateX) {
		this.clickedCordinateX = clickedCordinateX;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getClickedCordinateY()
	 */
	@Override
	public int getClickedCordinateY() {
		return clickedCordinateY;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setClickedCordinateY(int)
	 */
	@Override
	public void setClickedCordinateY(int clickedCordinateY) {
		this.clickedCordinateY = clickedCordinateY;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getDragCordinateX()
	 */
	@Override
	public int getDragCordinateX() {
		return dragCordinateX;
	}

	
	@Override
	public void setDragCordinateX(int dragCordinateX) {
		this.dragCordinateX = dragCordinateX;
	}

	
	@Override
	public int getDragCordinateY() {
		return dragCordinateY;
	}

	
	@Override
	public void setDragCordinateY(int dragCordinateY) {
		this.dragCordinateY = dragCordinateY;
	}

	
	@Override
	public int getMouseDisplacementX() {
		
		return mouseDisplacementX;
	}

	
	@Override
	public void setMouseDisplacementX(int mouseDisplacementX) {
		this.mouseDisplacementX = mouseDisplacementX;
	}

	
	@Override
	public int getMouseDisplacementY() {
		return mouseDisplacementY;
	}

	@Override
	public void setMouseDisplacementY(int mouseDisplacementY) {
		this.mouseDisplacementY = mouseDisplacementY;
	}

	
	@Override
	public int getMouseXClick() {
		return mouseXClick;
	}

	
	@Override
	public void setMouseXClick(int mouseXClick) {
		
		this.mouseXClick = mouseXClick;
	}

	
	@Override
	public int getMouseYClick() {
		return mouseYClick;
	}

	
	@Override
	public void setMouseYClick(int mouseYClick) {
		this.mouseYClick = mouseYClick;
	}

	@Override
	public int getMouseXdrag() {
		return mouseXdrag;
	}

	@Override
	public void setMouseXdrag(int mouseXdrag) {
		this.mouseXdrag = mouseXdrag;
	}

	
	@Override
	public int getMouseYdrag() {
		return mouseYdrag;
	}

	
	@Override
	public void setMouseYdrag(int mouseYdrag) {
		this.mouseYdrag = mouseYdrag;
	}

	
	@Override
	public int getChannelClick() {
		return channelClick;
	}

	@Override
	public void setChannelClick(int channelClick) {
		this.channelClick = channelClick;
	}


	@Override
	public int getChannelDrag() {
		return channelDrag;
	}

	@Override
	public void setChannelDrag(int channelDrag) {
		this.channelDrag = channelDrag;
	}


	@Override
	public int getFrameClick() {
		return frameClick;
	}


	@Override
	public void setFrameClick(int frameClick) {
		this.frameClick = frameClick;
	}

	@Override
	public int getFrameDrag() {
		return frameDrag;
	}

	@Override
	public void setFrameDrag(int frameDrag) {
		this.frameDrag = frameDrag;
	}

	@Override
	public int getSliceClick() {
		return sliceClick;
	}

	@Override
	public void setSliceClick(int sliceClick) {
		this.sliceClick = sliceClick;
	}

	@Override
	public int getSliceDrag() {
		return sliceDrag;
	}

	@Override
	public void setSliceDrag(int sliceDrag) {
		this.sliceDrag = sliceDrag;
	}


 


	
	


	@Override
	public int getPanelIndexClick() {
		return panelIndexClick;
	}

	@Override
	public void setPanelIndexClick(int panelIndexClick) {
		this.panelIndexClick = panelIndexClick;
	}

	@Override
	public int getPanelIndexDrag() {
		return panelIndexDrag;
	}


	@Override
	public void setPanelIndexDrag(int panelIndexDrag) {
		this.panelIndexDrag = panelIndexDrag;
	}

	
	@Override
	public int getColIndexClick() {
		return colIndexClick;
	}


	@Override
	public void setColIndexClick(int colIndexClick) {
		this.colIndexClick = colIndexClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getColIndexDrag()
	 */
	@Override
	public int getColIndexDrag() {
		return colIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setColIndexDrag(int)
	 */
	@Override
	public void setColIndexDrag(int colIndexDrag) {
		this.colIndexDrag = colIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getRowIndexClick()
	 */
	@Override
	public int getRowIndexClick() {
		return rowIndexClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setRowIndexClick(int)
	 */
	@Override
	public void setRowIndexClick(int rowIndexClick) {
		this.rowIndexClick = rowIndexClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getRowIndexDrag()
	 */
	@Override
	public int getRowIndexDrag() {
		return rowIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setRowIndexDrag(int)
	 */
	@Override
	public void setRowIndexDrag(int rowIndexDrag) {
		this.rowIndexDrag = rowIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getImageWrapperClick()
	 */
	@Override
	public ImageWrapper getImageWrapperClick() {
		return imageWrapperClick;
	}

	
	@Override
	public void setImageWrapperClick(ImageWrapper imageWrapperClick) {
		this.imageWrapperClick = imageWrapperClick;
	}

	public LocatedObject2D getTemporarySelection() {
		return this.getImageWrapperClick().getSelectionManagger().getSelection(1);
		
	}

	

	public ToolBit getToolbit() {
		if (toolbit==null) {toolbit=new BasicToolBit();
		toolbit.setToolCore((ToolCore) this) ;
		}
		return toolbit;
	}

	public void setToolbit(ToolBit toolbit) {
		toolbit.setToolCore(this);
		this.toolbit = toolbit;
	}

	public int clickCount() {
		// TODO Auto-generated method stub
		return clickCount;
	}

	public CanvasMouseEventWrapper getLastMouseEvent() {
		// TODO Auto-generated method stub
		return event;
	}
	
	

	public void setClickPointToDragReleasePoint() {
		CanvasMouseEventWrapper event1 = lastDragMe;//this.getAdapter().createCanvasMouseEventWrapper(getImageClick(), lastDragMe);
		//setCursor(currentCursor, 0);
		setClickPoint(getImageClick(),event1);
		
	}
	
	public IconSet getIconSet() {
		if (this.getToolbit().getIconSet()!=null) {
			if (this.additionalBits!=null && this.additionalBits.size()>1) {
				return CompoundSet(true, getToolbit().getIconSet());
			
			}
			
			return getToolbit().getIconSet();
		}
		
		return super.getIconSet();//iconSet;
	}
	
	public static IconSet CompoundSet(boolean b, IconSet i) {
		return new IconSet(
				new CompoundIcon(b, i.getIcon(0)),
				new CompoundIcon(b, i.getIcon(1)),
				new CompoundIcon(b, i.getIcon(2))
				);
				
	}
	
	public boolean shiftDown() {
		return event.shfitDown();
	}
	
	public boolean altKeyDown() {
		return event.altKeyDown();
	}

	@Override
	public BasicMontageLayout getClickedLayout() {
		// TODO Auto-generated method stub
		//return getMainLayout();
		return null;
	}

	
	@Override
	public void mouseClicked(DisplayedImageWrapper imp, CanvasMouseEventWrapper  e) {
		event=e;
		setReleaseOrDragPoint(imp,e);
		 try {
			this.getToolbit().mouseClicked();
			mouseClicked();
			this.getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.log(e2);
		}
	}
	
	public void mouseClicked() {
		// TODO Auto-generated method stub
		
	}

	public void setSelectedObject(LocatedObject2D lastRoi) {
		getImageWrapperClick().getSelectionManagger().setSelection(lastRoi, 0);
		//getObjectAdapter().setSelectedObject(this.getImageWrapperClick(),lastRoi);
		
		
	}

	/**
	public boolean doesClickedImageHAveMaintMontageLayout() {
		return this.isMontage(getImageClick());
	}*/

	public void setEditor(GenericMontageEditor montEditor) {
		this.montEditor = montEditor;
	}

	@Override
	public int getReleaseCordinateY() {
		// TODO Auto-generated method stub
		return releasePoint.y;
	}

	@Override
	public int getReleaseCordinateX() {
		// TODO Auto-generated method stub
		return  releasePoint.x;
	}
	@Override
	public void showOptionsDialog() {
		if (this.getToolbit()!=null) getToolbit().showOptionsDialog();
	}

	@Override
	public ImageWrapper currentlyInFocusWindowImage() {
		// TODO Auto-generated method stub
		return CurrentSetInformerBasic.getCurrentActiveDisplayGroup().getImageAsWrapper();
		//return getAdapter().currentlyInFocusWindowImage().getImageAsWrapper();
		//return this.getAdapter().createImageWrapper(getAdapter().currentlyInFocusWindowImage());
	}

	@Override
	public int getMouseXrelease() {
		if (releasePointMouse==null) return 0;
		return  releasePointMouse.x;
	}

	@Override
	public int getMouseYrelease() {
		if (releasePointMouse==null) return 0;
		return  releasePointMouse.y;
	}
	
	HashMap<String, SmartJMenu> subMenus=new HashMap<String, SmartJMenu> ();
	private SmartJMenu getSubMenu(String st, ArrayList<JMenuItem> pen) {
		SmartJMenu out = subMenus.get(st);
		if(out==null) {
			out=new SmartJMenu(st);
			subMenus.put(st, out);
			pen.add(out);
		}
		
		return out;
	}
	
	/**Get the items for a popup menu*/
	public ArrayList<JMenuItem> getPopupMenuItems() {	
		ArrayList<JMenuItem> pen = this.getToolbit().getPopupMenuItems() ;
		subMenus=new HashMap<String, SmartJMenu> ();
		if (pen==null&&additionalBits!=null&&additionalBits.size()>1) {
			pen=new ArrayList<JMenuItem>();
			for(ToolBit bit1:additionalBits ) {
				if(bit1==null) continue;
				if(bit1.getToolSubMenuName()!=null) {
					 getSubMenu(bit1.getToolSubMenuName(), pen).add(createJButtonForBit(bit1));
					 continue;
				}
				pen.add(createJButtonForBit(bit1));
			}
		}
		
		return pen;
	}
	
	private JMenuItem createJButtonForBit(ToolBit bit) {
		JMenuItem item=new JMenuItem();
		
		item.addActionListener(new bitSwitch(bit));
		bit.getIconSet().setItemIcons(item);
		item.setText(bit.getToolName());
		return item;
	}
	
	public String getToolName()  {
		return this.getToolbit().getToolName();
	}
	
	 class bitSwitch implements ActionListener {

		 ToolBit bit;
		bitSwitch(ToolBit b) {
			bit=b;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (toolbit!=null) toolbit.onToolChange(false);
			bit.onToolChange(true);
			setUpToolBit(bit);
			/**if (bit .getIconSet().getIcon(0) instanceof CompoundIcon) {} else {
				Icon icon1 = bit .getIconSet().getIcon(0);
				bit .getIconSet().setIcon(0, new CompoundIcon(icon1));
			}*/
			
			getIconSet().setItemIcons(getToolButton()) ;
			getToolButton().setIcon(getToolPressedImageIcon());
			if(bit.isActionTool())getToolButton().setIcon(getToolImageIcon());
			getToolButton().setRolloverEnabled(false);
			//getToolButton().repaint();
			
			getToolButton().setToolTipText(getToolTip());
			if (bit.isActionTool()) {
				bit.performLoadAction();
			} 
			else {
				
			}
			
		}}
	

	@Override
	public int getMouseButtonClick() {
		// TODO Auto-generated method stub
		return mouseButton;
	}

	@Override
	public DisplayedImageWrapper getClickedImage() {
		// TODO Auto-generated method stub
		if (dispImageClick!=null) return dispImageClick;
		if(event==null) return null;
		return event.getAsDisplay();
	}

	@Override
	public void setClickedImage(DisplayedImageWrapper d) {
		dispImageClick=d;
		
	}

	@Override
	public CanvasMouseEventWrapper getLastDragMouseEvent() {
		// TODO Auto-generated method stub
		return lastDragOrReleaseEvent;
	}
	
	@Override
	public boolean keyPressed(DisplayedImageWrapper imp, KeyEvent e) {
		setImageClick(imp);
		return this.getToolbit().keyPressed(e);
	}

	@Override
	public boolean keyReleased(DisplayedImageWrapper imp, KeyEvent e) {
		setImageClick(imp);
		return this.getToolbit().keyReleased(e);
	}

	@Override
	public boolean keyTyped(DisplayedImageWrapper imp, KeyEvent e) {
		setImageClick(imp);
		return this.getToolbit().keyTyped(e);
		
	}
	
	/**
	@Override
	public void handleFileListDrop(ImageAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file) {
		// TODO Auto-generated method stub
		
		this.getToolbit().handleFileListDrop( imageAndDisplaySet, location, file);
	}*/
	
	public DragAndDropHandler getDraghandler() {
		return this.getToolbit().getDragAndDropHandler();
	}
	
	
public void handleFileListDrop(Point location, ArrayList<File> file) {
		
	}

@Override
public String getToolTip() {
	return this.getToolbit().getToolTip();
}

@Override
public boolean isActionTool() {
	return this.getToolbit().isActionTool();
}

public void performLoadAction() {
	this.getToolbit().performLoadAction();
}

@Override
public boolean userSetSelectedItem(Object o) {
	
	return getToolbit().treeSetSelectedItem( o);
}

@Override
public void onToolChange(boolean b) {
	getToolbit().onToolChange(b);
	
}
	
}

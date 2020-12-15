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
package graphicalObjects_LayoutObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import genericMontageUIKitMenuItems.MontageEditCommandMenu;
import graphicalObjectHandles.AddRowHandle;
import graphicalObjectHandles.EditRowColNumberHandle;
import graphicalObjectHandles.LockedItemHandle;
import graphicalObjectHandles.MoveRowHandle;
import graphicalObjectHandles.RowLabelHandle;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import gridLayout.BasicMontageLayout;
import gridLayout.GridLayoutEditEvent;
import gridLayout.GridLayoutEditListener;
import gridLayout.MontageEditorDialogs;
import gridLayout.LayoutSpaces;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.MontageLayoutPanelMenu;
import standardDialog.StandardDialog;
import undo.UndoLayoutEdit;
import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.Scales;

public class MontageLayoutGraphic extends PanelLayoutGraphic implements GridLayoutEditListener, Scales {
	
	
	

	
	
	private static final long serialVersionUID = 1L;
	private BasicMontageLayout thelayout;
	private transient UndoLayoutEdit currentUndo;
	 
	public MontageLayoutGraphic() {}
	
	public MontageLayoutGraphic(BasicMontageLayout currentLayout) {
		if (currentLayout==null) {IssueLog.log("cannot make a layout graphic with no layout"); return;}
		thelayout=currentLayout;
		layout=currentLayout;
		thelayout.getListeners().add(this);
	}
	
	public MontageLayoutGraphic copy() {
		BasicMontageLayout dup = getPanelLayout().duplicate();
		
		MontageLayoutGraphic montageLayoutGraphic = new MontageLayoutGraphic(dup);
		if(this.isSelected()) montageLayoutGraphic.select();
		return montageLayoutGraphic;
	}


	@Override
	public BasicMontageLayout getPanelLayout() {
				if (thelayout==null) {
					if (this.layout instanceof BasicMontageLayout) thelayout=(BasicMontageLayout) layout;
						else {layout=null; thelayout=new BasicMontageLayout();}
					thelayout.getListeners().add(this);
				}
		return thelayout;
	}
	
	
	public void moveLayoutAndContents(double dx, double dy) {
		this.generateCurrentImageWrapper();
		
		this.getEditor().moveMontageLayout(this.getPanelLayout(), (int)dx,(int)dy);
		
	}
	
	/**Called on handle press. sets up the undoable edit*/
	@Override
	public void handlePress(int handlenum,  Point p2) {
		super.handlePress(handlenum, p2);
		currentUndo = new UndoLayoutEdit(this);
	}
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		
		int handleType = handlenum/PanelLayoutGraphic.handleIDFactor;
		
		onSmartHandleMove(handlenum, p1, p2);//call method in case any smart handle has a special task. otherwise, each smart handle is here
		//this.mapPanelLocationsOfLockedItems();
		this.generateCurrentImageWrapper();//generateStandardImageWrapper();
		
if (handlenum==LocationHandleID) {
			
			//getPanelLayout().move(p2.getX()-this.getBounds().getX(), p2.getY()-this.getBounds().getY());
			this.getEditor().moveMontageLayout(getPanelLayout(), p2.getX()-this.getBounds().getX(), p2.getY()-this.getBounds().getY());
		}

if(this.getPanelLayout() instanceof BasicMontageLayout) {
	BasicMontageLayout bml=(BasicMontageLayout) layout;
if (handlenum==RightHandleID) {
	double number = p2.getX()-bml.getBoundry().getBounds().getMaxX();	
	getEditor().addRightLabelSpace(bml, (int) number);	
} 
if (handlenum==BottomHandleID) {
	getEditor().addBottomLabelSpace(bml, (int) (p2.getY()-bml.getBoundry().getBounds().getMaxY()));	
} 
if (handlenum==LeftHandleID) {
	double number = bml.getBoundry().getBounds().getX()-p2.getX();	
	getEditor().addLeftLabelSpace(bml, (int) number);	
} 
if (handlenum==TopHandleID) {
	getEditor().addTopLabelSpace(bml, (int) (bml.getBoundry().getBounds().getY()-p2.getY()));	
} 


}


boolean adjustW =handleType==ROW_HEIGTH_HANDLE;// handlenum<this.getPanelLayout().getPanels().length;
boolean adjustH = handleType==COLUMN_WIDTH_HANDLE;//handlenum<(this.getPanelLayout().getPanels().length+handleIDFactor*2) && (handlenum>=handleIDFactor*2);

if (adjustW||adjustH) {
	int panelnum=handlenum+1;
	if(adjustH) panelnum=panelnum-handleIDFactor*2;
	Rectangle2D r = getPanelLayout().getPanel(panelnum);
	double increaseh=p2.getX()-r.getMaxX();
	double increasev= p2.getY()-r.getMaxY();
	
	int rowIndex=this.getPanelLayout().getRowAtIndex(panelnum);
	int colIndex=this.getPanelLayout().getColAtIndex(panelnum);
		if ( adjustH )getEditor().augmentPanelHeightOfRow(getPanelLayout(),  increasev, rowIndex);
	
		if ( adjustW )getEditor().augmentPanelWidthOfCol(getPanelLayout(), increaseh, colIndex);
	
}
int panelnum=handlenum+1-handleIDFactor*handleType;
int rowIndex=this.getPanelLayout().getRowAtIndex(panelnum);
int colIndex=this.getPanelLayout().getColAtIndex(panelnum);

boolean adjustH2 =handleType==ROW_HEIGHT_HANDLE_UNIFORM;
boolean adjustW2 = handleType==COLUMN_WIDTH_HANDLE_UNIFORM;
if (adjustW2||adjustH2) {
	
	Rectangle2D r = getPanelLayout().getPanel(panelnum);
	double increaseh=p2.getX()-r.getX()-r.getWidth();
	double increasev= p2.getY()-r.getY()-r.getHeight();
	int divh=0; for(int i=0; i<colIndex; i++) {if(!getPanelLayout().columnHasIndividualWidth(i+1)) divh++;}
	increaseh/=divh;
	
	int divv=0; for(int i=0; i<rowIndex; i++) {if(!getPanelLayout().rowHasIndividualHeight(i+1)) divv++;}
	increasev/=divv;
	//int rowIndex=this.getPanelLayout().getRowAtIndex(panelnum);
	//int colIndex=this.getPanelLayout().getColAtIndex(panelnum);
		if ( adjustH2 )getEditor().augmentPanelHeightold(getPanelLayout(),  increasev);
	
		if ( adjustW2 )getEditor().augmentPanelWidthold(getPanelLayout(), increaseh);
	
}


if (handleType==1/**handlenum-handleIDFactor>=0&&handlenum-handleIDFactor<this.getPanelLayout().getPanels().length*/) {
	//IssueLog.log("dragged mouse in handle "+handlenum);
	
	
	
	double increaseh= (p2.getX()-getPanelLayout().getPanel(panelnum).getCenterX());
	double increasev= (p2.getY()-getPanelLayout().getPanel(panelnum).getCenterY());
	//IssueLog.log("Dragging panel "+panelnum, "at row "+rowIndex," and col "+colIndex);
			if (colIndex>1){
				double expansion=increaseh/(colIndex-1);
				//IssueLog.log("Expanding panel spacing "+expansion);
				getEditor().expandBorderX2(getPanelLayout(),  expansion);
				if(getPanelLayout().BorderWidthLeftRight<1) {
					getEditor().expandBorderX2(getPanelLayout(), 1-getPanelLayout().BorderWidthLeftRight);
				}
				} 
			if (rowIndex>1)
			{
				double newHBorder =  (increasev/(rowIndex-1));
				getEditor().expandBorderY2(getPanelLayout(), newHBorder);
				if(getPanelLayout().BorderWidthBottomTop<1) {
					getEditor().expandBorderY2(getPanelLayout(), 1-getPanelLayout().BorderWidthBottomTop);
				}
				
				}
			if (rowIndex==1&&colIndex==1) {
				getEditor().moveMontageLayout(getPanelLayout(), (int)increaseh, (int)increasev);
			}
}

	
this.updateDisplay();


	}
	
	/**called on handle release. Adds the undoable edit to the list of edits*/
	public void handleRelease(int pressedHandle, Point point, Point point2) {
		super.handleRelease(pressedHandle, point, point2);
		if (currentUndo!=null) { currentUndo.establishFinalLocations();

		getUndoManager().addEdit(currentUndo);
		}
		
		
		}

	
	
	@Override
	public void handleMouseEvent(CanvasMouseEvent me, int handlenum, int button, int clickcount, int type,
			int... other) {
		
		if (clickcount<2) return;
		MontageEditorDialogs med = new  MontageEditorDialogs();
		generateStandardImageWrapper();
		int handleType = handlenum/handleIDFactor;
		UndoLayoutEdit undo = new UndoLayoutEdit(this);
		
		 if (handlenum==-1) {
				med.showDialogBasedOnLocation(getEditor(), thelayout, me.getCoordinatePoint());
			
		 }
		 else
		if (handleType==COLUMN_WIDTH_HANDLE)
			med.showUniqueDimensionDialog(this.getPanelLayout(), getEditor(), 1);
		else if (handleType==ROW_HEIGTH_HANDLE)
			med.showUniqueDimensionDialog(this.getPanelLayout(), getEditor(), 0);
		else if (handleType==PANEL_LOCATION_HANDLE)
			med.showBorderEditorDialog(new StandardDialog("Border Between Panels", true), getEditor(), getPanelLayout());
		else if (handleType==ROW_HEIGHT_HANDLE_UNIFORM||handleType==COLUMN_WIDTH_HANDLE_UNIFORM)
			{
			med.showColumnNumberEditorDialog(getEditor(), thelayout, 1,1);
			}
		else  med.showGeneralEditorDialog(getEditor(), getPanelLayout());
		 
		undo.establishFinalLocations();
		me.getAsDisplay().getUndoManager().addEdit(undo);
	}
	
	
	
	
	
	
	
	public void setParentLayer(GraphicLayer parent) {
		super.setParentLayer(parent);
		
	}

	public void showEditingOptions() {
			
			this.generateCurrentImageWrapper();
			new MontageEditorDialogs().showGeneralEditorDialog(getEditor(), getPanelLayout());
			
		
			//this.mapPanelLocationsOfLockedItems();
		
	}

	
	public PopupMenuSupplier getMenuSupplier(){
		return new  MontageLayoutPanelMenu(this);
	}
	
	@Override
	public Shape getOutline() {
		// TODO Auto-generated method stub
		return getPanelLayout().getBoundry();
	}

	@Override
	public void editWillOccur(GridLayoutEditEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editOccuring(GridLayoutEditEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editDone(GridLayoutEditEvent e) {
		this.mapPanelLocationsOfLockedItems();
		
		
	}


	void resizePanelsToFit(LocatedObject2D object) {
		generateCurrentImageWrapper();
		this.getEditor().resizePanels(getPanelLayout(), object.getBounds().getWidth(), object.getBounds().getHeight());
	}

	

	@Override
	public Rectangle2D getRectForSnap(int i, LocatedObject2D o) {
		if(o==null) return null;
		if(i<0) return null;
		Rectangle2D panel = getPanelLayout().getPanel(i);
		if (o.getAttachmentPosition()==null || o.getAttachmentPosition().getGridLayoutSnapType()==0)
							return panel;
		int rectSnapType = o.getAttachmentPosition().getGridSpaceCode();
		
		return	this.getPanelLayout().getSelectedSpace((int)panel.getCenterX(), (int)panel.getCenterY(), rectSnapType).getBounds();
	}
	
	
	public Rectangle2D getRectForValidityCheck(int i, LocatedObject2D o) {
		if(o==null) return null;
		if(i<0) return null;
		Rectangle2D panel = getPanelLayout().getPanel(i);
		if (o.getAttachmentPosition()==null || o.getAttachmentPosition().getGridLayoutSnapType()==0)
							return panel;
	
		int rectSnapType = o.getAttachmentPosition().getGridLayoutSnapType();
		
		return	this.getPanelLayout().getSelectedSpace((int)panel.getCenterX(), (int)panel.getCenterY(), rectSnapType).getBounds();
	}
	
	@Override
	protected boolean isPanelValid(int r, LocatedObject2D o) {
		
		Rectangle2D panel =  getRectForValidityCheck(r,o);
		
		if(panel==null) return false;
		if(!panel.intersects(o.getBounds())&&!panel.contains(o.getBounds()))
			return false;
	return true;
	}
	
	/**both resizes the panels and shifts the panel contents to fit better*/
public void resizeLayoutToFitContents() {
	try{
	this.generateCurrentImageWrapper();
	getPanelLayout().getEditor().shiftPanelContentsToEdge(getPanelLayout());
	this.generateCurrentImageWrapper();
	getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(getPanelLayout());
	getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(getPanelLayout());} catch (Throwable t) {
		t.printStackTrace();
	}
	
}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		
		BasicMontageLayout layout1 = this.getPanelLayout();
		
	
		double ox = layout1.specialSpaceWidthLeft;
		double oy = layout1.specialSpaceWidthTop;
		
		Point2D pnew = scaleAbout(new Point2D.Double(ox, oy), p, mag, mag);
		
		layout1.scale(mag);
		
		layout1.specialSpaceWidthLeft=pnew.getX();
		layout1.specialSpaceWidthTop= pnew.getY();
		layout1.resetPtsPanels();
	}
	
	protected void addFirstLayerHandles(SmartHandleList box) {
		for(int i=1; i<=this.getPanelLayout().nPanels(); i++) {
			box.add(new MoveRowHandle(this, LayoutSpaces.PANELS, false, i));
			
			}
	}
	
	protected void addAdditionalHandles(SmartHandleList box) {
		box.add(new AddRowHandle(this, LayoutSpaces.ROWS, false));
		box.add(new AddRowHandle(this, LayoutSpaces.COLS, false));
		box.add(new EditRowColNumberHandle(this));
		for(int i=1; i<=this.getPanelLayout().nColumns(); i++) {
			box.add(new MoveRowHandle(this, LayoutSpaces.COLS, false, i));
			}
		
		for(int i=1; i<=this.getPanelLayout().nRows(); i++) {
			box.add(new MoveRowHandle(this, LayoutSpaces.ROWS, false, i));
			
			}
		
		for(int i=1; i<=this.getPanelLayout().nColumns(); i++) {
			box.add(new RowLabelHandle(this, LayoutSpaces.COLS, i));
			}
		
		for(int i=1; i<=this.getPanelLayout().nRows(); i++) {
			box.add(new RowLabelHandle(this, LayoutSpaces.ROWS,  i));
			
			}
	
	}
	
	/**creates a locked item handle for use in this layout*/
	protected void generateHandleForText(LocatedObject2D l) {
		SmartHandleList list = this.getLocedItemHandleList();
		list.add(new LayoutLockedItemHandle(this, l, 1000000000+list.size()));
	}
	/**creates a locked item handle for use in this layout*/
	protected void generateHandleForImage(LocatedObject2D l) {
		SmartHandleList list = this.getLocedItemHandleList();
		list.add(new ImagePanelHandle(this, (ImagePanelGraphic) l, 1000000000+list.size()));
	}
	
	
	public void createHandlesForPanel(int w, Rectangle2D r,SmartHandleList handleBoxes2)  {
		super.createHandlesForPanel(w, r, handleBoxes2);
		
		SmartHandle r2;
		int nColumns = this.getPanelLayout().nColumns();
		boolean b = !getPanelLayout().doesPanelUseUniqueWidth(w+1);
		
		if (b&&getPanelLayout().getColAtIndex(w+1)<nColumns &&nColumns>1) {
			r2 = createHandle( RectangleEdges.getLocation(RectangleEdges.RIGHT, r.getBounds()), w+COLUMN_WIDTH_HANDLE_UNIFORM*handleIDFactor, 2);
			  handleBoxes2.add(r2);
		}
		
		int nRows = this.getPanelLayout().nRows();
		if (!getPanelLayout().doesPanelUseUniqueHeight(w+1) &&getPanelLayout().getRowAtIndex(w+1)<nRows&&nRows>1) {
			r2 = createHandle( RectangleEdges.getLocation(RectangleEdges.BOTTOM, r.getBounds()), w+ROW_HEIGHT_HANDLE_UNIFORM*handleIDFactor, 2);
			  handleBoxes2.add(r2);
		}
	}
	
	/**
	 A locked item handle with additional menus
	 */
	public class LayoutLockedItemHandle extends LockedItemHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int amount=1;
		
		private BasicMontageLayout myLayout;
		private int nOptions;
		boolean shifted=false;
		

		public LockedItemHandle copy() {
			LockedItemHandle output = new LayoutLockedItemHandle((MontageLayoutGraphic) taker, object, this.getHandleNumber()-100);
			
			return output;
		}
		
		public LayoutLockedItemHandle(MontageLayoutGraphic montageLayoutGraphic, LocatedObject2D l, int i) {
			super(montageLayoutGraphic,l,i);
			amount=1;
			boolean major = montageLayoutGraphic.getPanelLayout().rowmajor;
			if(!major &&l.getAttachmentPosition().getGridSpaceCode()==LayoutSpaces.COLUMN_OF_PANELS) {
				amount= montageLayoutGraphic.getPanelLayout().nRows();
			}
			if(major&&this.isRows()) {
				amount= montageLayoutGraphic.getPanelLayout().nColumns();
			}
			
			myLayout=montageLayoutGraphic.getPanelLayout();
			nOptions=myLayout.nPanels();
			if (this.isRows())nOptions=myLayout.nRows();
			if (this.isCols())nOptions=myLayout.nColumns();
		}
		
		String changeText() {
			if(isCols()) {
				return "Columns";
			}
			if(isRows()) {
				return "Rows";
			}
			return "Panels";
		}

		private boolean isRows() {
			return getObject().getAttachmentPosition().getGridSpaceCode()==LayoutSpaces.ROW_OF_PANELS;
		}

		private boolean isCols() {
			return getObject().getAttachmentPosition().getGridSpaceCode()==LayoutSpaces.COLUMN_OF_PANELS;
		}
		
		public JPopupMenu getJPopup() {
			JPopupMenu out = super.getJPopup();
			
			JMenu moveMenu = getRowSwithMenu();
			if(out==null) return null;
			out.add(moveMenu);
			return out;
		}

		private JMenu getRowSwithMenu() {
			JMenu moveMenu=new JMenu("Switch "+changeText());
			
			for(int i=1; i<=nOptions; i++) {
				moveMenu.add(new rowSwitchMenuItem(""+i,i));
			}
		
			return moveMenu;
		}
		
		void advanceLabel(int direction) {
			Integer newind = getPanelLocations().get(getObject())+amount*direction;
			
			if(newind<=0) return;
			getPanelLocations().put(getObject(), newind);
			snapLockedItem(getObject());
		}
		
		public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
	
			BasicMontageLayout rLayout = myLayout.makeAltered(getObject().getAttachmentPosition().getGridSpaceCode());
			Point2D p1 = super.getCordinateLocation();
			
			Point cordinatePoint = lastDragOrRelMouseEvent.getCoordinatePoint();
			double d = cordinatePoint.distance(p1);
			if(originalBounds!=null) d = cordinatePoint.distance(originalBounds.getCenterX(), originalBounds.getCenterY());
			if(d>rLayout.getPanelHeight(1)*0.5 &&!outOfRange(lastDragOrRelMouseEvent)) {
				shifted=true;
				getObject().getAttachmentPosition().copyPositionFrom(originalSnap);

					//int panel = getPanelForObject(new RectangularGraphic(cordinatePoint));
					Rectangle2D nearestPanel = rLayout.getNearestPanel(cordinatePoint);
					Rectangle r2 = getObject().getBounds();
					if (originalSnap!=null)originalSnap.snapRects(r2, nearestPanel); else
						getObject().getAttachmentPosition().snapRects(r2, nearestPanel);
					
					setDragMask(lastDragOrRelMouseEvent, r2);
			} else {
				shifted=false;
				lastDragOrRelMouseEvent.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelection(null, 0);
				super.handleDrag(lastDragOrRelMouseEvent);
			}

			
			
		}

		protected void setDragMask(CanvasMouseEvent lastDragOrRelMouseEvent, Rectangle2D r2) {
			if(r2==null) {lastDragOrRelMouseEvent.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelection(null, 0);
				return;
			}
			RectangularGraphic r = RectangularGraphic.blankRect(r2, Color.green, true, true);
			r.hideHandles(true);
			lastDragOrRelMouseEvent.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelection(r, 0);
		}
		
		
		public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
			
			if(releaseIt) {
				super.handleRelease(canvasMouseEventWrapper);
				releaseIt=false;
			}
			if (shifted) {
				getObject().setLocation(canvasMouseEventWrapper.getCoordinatePoint());
				getObject().getAttachmentPosition().copyPositionFrom(originalSnap);
				mapPanelLocation(getObject());
				shifted=false;
			} 
			originalSnap=null;
			originalBounds=null;
			
			
		}
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
				super.handlePress(canvasMouseEventWrapper);
				
		}
		
		
		
		class rowSwitchMenuItem extends JMenuItem implements ActionListener{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private int index;
			rowSwitchMenuItem(String t, int index) {
				super(t);
				this.index=index;
				this.addActionListener(this);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getPanelLocations().put(getObject(), index);
				if (myLayout.rowmajor&&isRows()) {
					getPanelLocations().put(getObject(), index*myLayout.nColumns());
				}
				snapLockedItem(getObject());
			}
		}
		
		
		
		

	}

	class ImagePanelHandle extends LayoutLockedItemHandle {

		private Rectangle2D panel;

		public ImagePanelHandle(MontageLayoutGraphic montageLayoutGraphic, ImagePanelGraphic l, int i) {
			super(montageLayoutGraphic, l, i);
			this.handlesize=0;
			this.setCordinateLocation(l.getLocationUpperLeft());
		}
		
		@Override
		public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
			panel = getPanelLayout().getNearestPanel(lastDragOrRelMouseEvent.getCoordinatePoint());
			super.setDragMask(lastDragOrRelMouseEvent, panel);
			
			if (!panel.contains(lastDragOrRelMouseEvent.getCoordinatePoint()))
			{
				panel=null;
			 showMessageForOutOfRange(lastDragOrRelMouseEvent);
			}
		}
		
		
		
		public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {

			if(releaseIt) {
				UndoTakeLockedItem undo = new UndoTakeLockedItem(taker, getObject() , true);
				taker.removeLockedItem(getObject());
				if(currentEdit!=null) currentEdit.addEditToList(undo);
				getObject().setLocation(canvasMouseEventWrapper.getCoordinatePoint());
			}
			
			/**moves the image panel to its new panel location*/
			if (panel!=null)getObject().setLocationUpperLeft(new Point2D.Double(panel.getX(), panel.getY()));
			mapPanelLocation(getObject());
			super.setDragMask(canvasMouseEventWrapper, null);
			
			if (shifted) {
				getObject().setLocation(canvasMouseEventWrapper.getCoordinatePoint());
				getObject().getAttachmentPosition().copyPositionFrom(originalSnap);
				mapPanelLocation(getObject());
			} 
			originalSnap=null;
			originalBounds=null;
			
			
		}
		
		public LockedItemHandle createDemiVersion() {
			suppressMenu=true;
			return  this;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}

	public String[] getScaleWarning() {
		//showEditingOptions();
		this.getEditor().roundUpAll(getPanelLayout());
	//	if (this.getPanelLayout().doPanelsUseUniqueHeights())new MontageEditorDialogs().showUniqueDimensionDialog(getPanelLayout(), MontageSpaces.ROWS);
		//if (this.getPanelLayout().doPanelsUseUniqueWidths())new MontageEditorDialogs().showUniqueDimensionDialog(getPanelLayout(), MontageSpaces.COLS);
	//	new MontageEditorDialogs().showColumnNumberEditorDialog(getEditor(), getPanelLayout(),1,1);
		new MontageEditCommandMenu(this.getPanelLayout()).handlePanelSizeFit();
		
		//new MontageEditorDialogs().showBorderEditorDialog(getEditor(), getPanelLayout());
		return new String[] {"Scaling figure can sometimes result in mismatches between Image Panel locations and layouts", "Layout measurements are rounded up after scaling", "Object positions/sizes may be rounded to integers during export"};
	}
	
	
}

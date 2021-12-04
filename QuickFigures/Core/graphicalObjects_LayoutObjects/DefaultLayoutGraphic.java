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
 * Date Modified: April 24, 2021
 * Version: 2021.2
 */
package graphicalObjects_LayoutObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import genericMontageUIKitMenuItems.LayoutEditCommandMenu;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.AttachmentPositionHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import handles.layoutHandles.AddRowHandle;
import handles.layoutHandles.RepackRowColoumnHandle;
import handles.layoutHandles.ScaleLayoutHandle;
import handles.layoutHandles.MoveRowHandle;
import handles.layoutHandles.AddLabelHandle;
import layout.RetrievableOption;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.GridLayoutEditEvent;
import layout.basicFigure.GridLayoutEditListener;
import layout.basicFigure.LayoutEditorDialogs;
import layout.basicFigure.LayoutSpaces;
import layout.basicFigure.TransformFigure;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import locatedObject.Scales;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;
import popupMenusForComplexObjects.LayoutMenu;
import standardDialog.StandardDialog;
import undo.UndoLayoutEdit;
import undo.UndoMoveItems;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoAddOrRemoveAttachedItem;
import undo.UndoAttachmentPositionChange;
import utilityClasses1.ArraySorter;

/**A layout graphic containing the standard layout for all figures*/
public class DefaultLayoutGraphic extends PanelLayoutGraphic implements GridLayoutEditListener, Scales {
	

	
	private static final long serialVersionUID = 1L;
	private BasicLayout thelayout;
	private transient UndoLayoutEdit currentUndo;
	
	@RetrievableOption(key = "Row labels on right side", label="Place new row labels on right")
	public boolean rowLabelsOnRight=false;
	@RetrievableOption(key = "Column labels at bottom", label="Place new column labels on bottom")
	public boolean columnLabelsBelow=false;
	public boolean hidePanelSwapHandles=false;
	public boolean hideRowColSwapHandles=false;
	
	 
	public DefaultLayoutGraphic() {}
	
	public DefaultLayoutGraphic(BasicLayout currentLayout) {
		if (currentLayout==null) {IssueLog.log("cannot make a layout graphic with no layout"); return;}
		thelayout=currentLayout;
		layout=currentLayout;
		thelayout.getListeners().add(this);
	}
	
	public DefaultLayoutGraphic copy() {
		BasicLayout dup = getPanelLayout().duplicate();
		
		DefaultLayoutGraphic montageLayoutGraphic = new DefaultLayoutGraphic(dup);
		if(this.isSelected()) montageLayoutGraphic.select();
		return montageLayoutGraphic;
	}

	/**returns the layout*/
	@Override
	public BasicLayout getPanelLayout() {
				if (thelayout==null) {
					if (this.layout instanceof BasicLayout) thelayout=(BasicLayout) layout;
						else {layout=null; thelayout=new BasicLayout();}
					thelayout.getListeners().add(this);
				}
		return thelayout;
	}
	
	/**moves the layout and all objects within it*/
	public void moveLayoutAndContents(double dx, double dy) {
		this.generateCurrentImageWrapper();
		
		this.getEditor().moveLayout(this.getPanelLayout(), (int)dx,(int)dy);
		
	}
	
	/**Called on handle press. sets up the undoable edit*/
	@Override
	public void handlePress(int handlenum,  Point p2) {
		super.handlePress(handlenum, p2);
		currentUndo = new UndoLayoutEdit(this);
		if (handlenum==SELECT_ALL_HANDLE) {
			ArraySorter.selectItems(this.getParentLayer().getAllGraphics());
			this.select();
		}
	}
	
	/**Handle drags for handles are implemented by this method*/
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		
		int handleType = handlenum/PanelLayoutGraphic.handleIDFactor;
		
		onSmartHandleMove(handlenum, p1, p2);//call method in case any smart handle has a special task. otherwise, each smart handle is implemented here
		
		this.generateCurrentImageWrapper();//generateStandardImageWrapper();
				
		if (handlenum==LAYOUT_LOCATION_HANDLE) {
					getEditor().moveLayout(getPanelLayout(), p2.getX()-this.getBounds().getX(), p2.getY()-this.getBounds().getY());
				}
		
		if(this.getPanelLayout() instanceof BasicLayout) {
			BasicLayout bml=(BasicLayout) layout;
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
				if ( adjustH2 )getEditor().augmentStandardPanelHeight(getPanelLayout(),  increasev);
			
				if ( adjustW2 )getEditor().augmentStandardPanelWidth(getPanelLayout(), increaseh);
			
		}
		
		
		if (handleType==PANEL_LOCATION_HANDLE) {
		
			
			
			double increaseh= (p2.getX()-getPanelLayout().getPanel(panelnum).getCenterX());
			double increasev= (p2.getY()-getPanelLayout().getPanel(panelnum).getCenterY());
			
					if (colIndex>1){
						double expansion=increaseh/(colIndex-1);
					
						getEditor().expandBorderX2(getPanelLayout(),  expansion);
						if(getPanelLayout().theBorderWidthLeftRight<1) {
							getEditor().expandBorderX2(getPanelLayout(), 1-getPanelLayout().theBorderWidthLeftRight);
						}
						} 
					if (rowIndex>1)
					{
						double newHBorder =  (increasev/(rowIndex-1));
						getEditor().expandBorderY2(getPanelLayout(), newHBorder);
						if(getPanelLayout().theBorderWidthBottomTop<1) {
							getEditor().expandBorderY2(getPanelLayout(), 1-getPanelLayout().theBorderWidthBottomTop);
						}
						
						}
					if (rowIndex==1&&colIndex==1) {
						getEditor().moveLayout(getPanelLayout(), (int)increaseh, (int)increasev);
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
		LayoutEditorDialogs med = new  LayoutEditorDialogs();
		this.generateCurrentImageWrapper();
		
		int handleType = handlenum/handleIDFactor;
		UndoLayoutEdit undo = new UndoLayoutEdit(this);
		 
		
		 if (handlenum==-1) {
			
			
				med.showDialogBasedOnLocation(getEditor(), getPanelLayout(), me.getCoordinatePoint());
				
						
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
	
	

	public void showEditingOptions() {
			
			this.generateCurrentImageWrapper();
			new LayoutEditorDialogs().showGeneralEditorDialog(getEditor(), getPanelLayout());
			
		
			//this.mapPanelLocationsOfLockedItems();
		
	}

	
	public PopupMenuSupplier getMenuSupplier(){
		return new  LayoutMenu(this);
	}
	
	/**returns the area a user may click on to select the layout
	 * is is somewhat larger than the layout*/
	@Override
	public Shape getOutline() {
		Shape boundry = getPanelLayout().getBoundry();
		Rectangle2D r = boundry.getBounds2D();
		int padding=10;
		return new Rectangle2D.Double(r.getX()-padding, r.getY()-padding, r.getWidth()+padding*2, r.getHeight()+padding*2);
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
	
	/**returns a transform object for this layout*/
	public TransformFigure transform() {
		return new TransformFigure(this);
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
		
		BasicLayout layout1 = this.getPanelLayout();
		
	
		double ox = layout1.specialSpaceWidthLeft;
		double oy = layout1.specialSpaceWidthTop;
		
		Point2D pnew = scalePointAbout(new Point2D.Double(ox, oy), p, mag, mag);
		
		layout1.scale(mag);
		
		layout1.specialSpaceWidthLeft=pnew.getX();
		layout1.specialSpaceWidthTop= pnew.getY();
		layout1.resetPtsPanels();
	}
	
	/**Adds the panel swap handles*/
	protected void addFirstLayerHandles(SmartHandleList box) {
		if(!hidePanelSwapHandles)
		for(int i=1; i<=this.getPanelLayout().nPanels(); i++) {
			box.add(new MoveRowHandle(this, LayoutSpaces.PANELS, false, i));
			
			}
	}
	
	/**Adds several handles to the list for label addition*/
	protected void addAdditionalHandles(SmartHandleList box) {
		box.add(new AddRowHandle(this, LayoutSpaces.ROWS));
		box.add(new AddRowHandle(this, LayoutSpaces.COLS));
		
		box.add(new RepackRowColoumnHandle(this));
		box.add(new ScaleLayoutHandle(this));
		
		if(!hideRowColSwapHandles)
		for(int i=1; i<=this.getPanelLayout().nColumns(); i++) {
			box.add(new MoveRowHandle(this, LayoutSpaces.COLS, false, i));
			}
		if(!hideRowColSwapHandles)
		for(int i=1; i<=this.getPanelLayout().nRows(); i++) {
			box.add(new MoveRowHandle(this, LayoutSpaces.ROWS, false, i));
			
			}
		
		for(int i=1; i<=this.getPanelLayout().nColumns(); i++) {
			box.add(new AddLabelHandle(this, LayoutSpaces.COLS, i, false));
			}
		
		for(int i=1; i<=this.getPanelLayout().nRows(); i++) {
			box.add(new AddLabelHandle(this, LayoutSpaces.ROWS,  i, false));
			if(rowLabelsOnRight)
				box.add(new AddLabelHandle(this, LayoutSpaces.ROWS,  i, true));
			}
	
	}
	
	/**creates a locked item handle for use in this layout*/
	@Override
	protected void generateHandleForText(LocatedObject2D l) {
		SmartHandleList list = this.getAttachedItemHandleList();
		list.add(new LayoutLockedItemHandle(this, l, 1000000000+list.size()));
	}
	/**creates a locked item handle for use in this layout*/
	protected void generateHandleForImage(LocatedObject2D l) {
		SmartHandleList list = this.getAttachedItemHandleList();
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
	public class LayoutLockedItemHandle extends AttachmentPositionHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int amount=1;
		
		private BasicLayout myLayout;
		//private int nOptions;
		boolean shifted=false;
		

		public AttachmentPositionHandle copy() {
			AttachmentPositionHandle output = new LayoutLockedItemHandle((DefaultLayoutGraphic) attachmentSite, object, this.getHandleNumber()-100);
			
			return output;
		}
		
		public LayoutLockedItemHandle(DefaultLayoutGraphic montageLayoutGraphic, LocatedObject2D l, int i) {
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
			
		}
		
		/**returns how many possible locations the item attached to this can have (depending on whether it is attached to rows cols or panels))*/
		public int nOptions() {
			int nOptions = myLayout.nPanels();
			if (this.isRows())nOptions=myLayout.nRows();
			if (this.isCols())nOptions=myLayout.nColumns();
			return nOptions;
		}
		
		/**returns which type of attachment position this item has*/
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
			return getObject().getAttachmentPosition().isRowAttachment();
		}

		private boolean isCols() {
			return getObject().getAttachmentPosition().isColumnAttachment();
		}
		
		public JPopupMenu getJPopup() {
			JPopupMenu out = super.getJPopup();
			
			
			if(out==null) return null;
			
			out.add( getRowSwithMenu(false));
			out.add( getRowSwithMenu(true));
			return out;
		}
		
		String allSubsequent="Next several";

		private JMenu getRowSwithMenu(boolean copy) {
			String baseText = "Switch ";
			if(copy)
				baseText="Copy to ";
			JMenu moveMenu=new SmartJMenu(baseText+changeText());
			
			 for(int i=1; i<=nOptions(); i++) {
				moveMenu.add(new RowSwitchMenuItem(""+i,i, copy));
			}
			 if (copy)
			moveMenu.add(new RowSwitchMenuItem(allSubsequent,getPanelLocations().get(getObject()), copy));
		
			return moveMenu;
		}
		
		/**Changes the position of the label*/
		void advanceLabel(int direction) {
			Integer newind = getPanelLocations().get(getObject())+amount*direction;
			
			if(newind<=0) return;
			getPanelLocations().put(getObject(), newind);
			snapLockedItem(getObject());
		}
		
		public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
	
			BasicLayout rLayout = myLayout.makeAltered(getObject().getAttachmentPosition().getGridSpaceCode());
			Point2D p1 = super.getCordinateLocation();
			
			Point cordinatePoint = lastDragOrRelMouseEvent.getCoordinatePoint();
			double d = cordinatePoint.distance(p1);
			if(originalBounds!=null) d = cordinatePoint.distance(originalBounds.getCenterX(), originalBounds.getCenterY());
			
			/**determine whether the object is being dragged out of range of its hone row of column*/
			boolean outsideOfHomeRow = d>rLayout.getPanelHeight(1)*0.5 &&!outOfRange(lastDragOrRelMouseEvent) &&super.object.getTagHashMap().get("Index")==null;
			if(outsideOfHomeRow&&!super.isInFineControlMode()) {
				shifted=true;
				getObject().getAttachmentPosition().copyPositionFrom(originalSnap);

					Rectangle2D nearestPanel = rLayout.getNearestPanel(cordinatePoint);
					Rectangle r2 = getObject().getBounds();
					if (originalSnap!=null)originalSnap.snapRects(r2, nearestPanel); else
						getObject().getAttachmentPosition().snapRects(r2, nearestPanel);
					
					setDragMask(lastDragOrRelMouseEvent, r2);//displays the new destination row to the user
			} else {
				shifted=false;
				lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelection(null, 0);
				super.handleDrag(lastDragOrRelMouseEvent);
			}

			
			
		}

		protected void setDragMask(CanvasMouseEvent lastDragOrRelMouseEvent, Rectangle2D r2) {
			if(r2==null) {lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelection(null, 0);
				return;
			}
			RectangularGraphic r = RectangularGraphic.blankRect(r2, Color.green, true, true);
			r.hideHandles(true);
			lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelection(r, 0);
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
		
		
		/**A menu item that allows the user to move a row/column label to a
		 * new location*/
		class RowSwitchMenuItem extends BasicSmartMenuItem {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private int index;
			private boolean copy;
			private boolean all;
			RowSwitchMenuItem(String t, int index, boolean copy) {
				super(t);
				this.index=index;
				this.copy=copy;
				all=t.equals(allSubsequent);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				LocatedObject2D target1 = getObject();
				
				CombinedEdit undo;
				if (all) {
					undo=new CombinedEdit();
					for(int i=index+1;i<=nOptions(); i++) {
						undo.addEditToList(new RowSwitchMenuItem(""+i,i, copy).performTask(target1));
					}
				}
				else 
				undo= performTask(target1);
				
				
				
				this.addUndo(undo);
				me.getAsDisplay().updateDisplay();
				
			}

			/**
			 * @param target1
			 * @return
			 */
			public CombinedEdit performTask(LocatedObject2D target1) {
				CombinedEdit undo = new CombinedEdit();
				if(copy) {
					LocatedObject2D target2 = target1.copy();
					if(target1 instanceof ZoomableGraphic && target2 instanceof ZoomableGraphic)
						undo.addEditToList(Edit.addItem(((ZoomableGraphic) target1).getParentLayer(), (ZoomableGraphic) target2));
					target2.setAttachmentPosition(target1.getAttachmentPosition());
					target1=target2;
					attachmentSite.addLockedItem(target1);
					 undo.addEditToList(new UndoAddOrRemoveAttachedItem(attachmentSite, target1, false));
					 
				}
				
				undo.addEdit(new UndoMoveItems(target1));
				undo.addEditToList(new UndoAttachmentPositionChange(target1, (PanelLayoutGraphic) attachmentSite));
				undo.addEdit(new UndoMoveItems(target1));
				
				getPanelLocations().put(target1, index);
				if (myLayout.rowmajor&&isRows()) {
					getPanelLocations().put(target1, index*myLayout.nColumns());
					
					
				}
				
				/**If the index is fixed, makes it possible to change it*/
				if (getObject().getTagHashMap().containsKey("Index"))
					getObject().getTagHashMap().put("Index", index);
				
				snapLockedItem(target1);
				
				undo.establishFinalState();
				return undo;
			}
		}
		
		
		
		

	}

	/**A handle for attached image panels*/
	class ImagePanelHandle extends LayoutLockedItemHandle {

		private Rectangle2D panel;

		public ImagePanelHandle(DefaultLayoutGraphic montageLayoutGraphic, ImagePanelGraphic l, int i) {
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
				UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(attachmentSite, getObject() , true);
				attachmentSite.removeLockedItem(getObject());
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
		
		public AttachmentPositionHandle createDemiVersion() {
			suppressMenu=true;
			return  this;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}

	public String[] getScaleWarning() {
		//showEditingOptions();
	
	//	if (this.getPanelLayout().doPanelsUseUniqueHeights())new MontageEditorDialogs().showUniqueDimensionDialog(getPanelLayout(), MontageSpaces.ROWS);
		//if (this.getPanelLayout().doPanelsUseUniqueWidths())new MontageEditorDialogs().showUniqueDimensionDialog(getPanelLayout(), MontageSpaces.COLS);
	//	new MontageEditorDialogs().showColumnNumberEditorDialog(getEditor(), getPanelLayout(),1,1);
		this.generateCurrentImageWrapper();
		this.getEditor().roundUpAll(getPanelLayout());
		
		new LayoutEditCommandMenu(this).handlePanelSizeFit();
		
		return new String[] {"Layout panels were resized after scaling","Layout measurements are rounded up after scaling"};
		/**this.getEditor().roundUpAll(getPanelLayout());
		new LayoutEditCommandMenu(this.getPanelLayout()).handlePanelSizeFit();
		
		//new MontageEditorDialogs().showBorderEditorDialog(getEditor(), getPanelLayout());
		
	*/}
	
	
}

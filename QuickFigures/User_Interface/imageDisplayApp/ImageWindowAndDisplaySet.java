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
package imageDisplayApp;


import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.BasicCoordinateConverter;
import layersGUI.GraphicTreeUI;

/**
Stores everything related to a particular figure image including the
display window for figures, the layer set inside, the undo manager and what selections are made
Also used for windows that display multichannel images
 */
public class ImageWindowAndDisplaySet implements DisplayedImage {
	public static GraphicTreeUI exampletree;
	private GraphicSetDisplayWindow theWindow=null;
	private GraphicDisplayCanvas theCanvas=null;
	private GraphicContainingImage theFigure=null;
	private MiniToolBarPanel sidePanel;
	
	/**The time frames for animations*/
	private int currentFrame=0;
	private int endFrame=200;
	private transient Selectable selectedItem=null;
	
	/**This undo manager stores the undos for this image*/
	transient UndoManagerPlus undoManager=null;
	
	/**Generates a Display of the given graphic set*/
	public ImageWindowAndDisplaySet(GraphicContainingImage graphicSet) {
		this.setTheSet( graphicSet);
		GraphicDisplayCanvas canvas = new GraphicDisplayCanvas();
		this.setTheCanvas(canvas);
		//updateCanvasDims();//
		
		this.setTheWindow(new GraphicSetDisplayWindow(this,canvas));
		this.getTheWindow().reSetCanvasAndWindowSizes() ;
		centreWindow(this.getWindow());
		ensureAllLinked();
	}
	
	/**called to let the window know which set of objects it is displaying*/
	void ensureAllLinked() {
		this.getTheWindow().setDisplaySet(this);
	}
	
	/**returns the component that all the edited object are drawn onto*/
	public JComponent getTheCanvas() {
		return theCanvas;
	}
	
	/**sets the component that all the edited object are drawn onto*/
	public void setTheCanvas(GraphicDisplayCanvas theCanvas) {
		
		this.theCanvas = theCanvas;
		
	}
	
	/**getter methow for the window*/
	public GraphicSetDisplayWindow getTheWindow() {
		return theWindow;
	}
	public void setTheWindow(GraphicSetDisplayWindow theWindow) {
		this.theWindow = theWindow;
		if (theWindow!=null) {
			theWindow.setDisplaySet(this);
		}
	}
	
	/**getter method for the 'image' containing all the objects and layers*/
	public GraphicContainingImage getTheSet() {
		return theFigure;
	}
	public void setTheSet(GraphicContainingImage theSet) {
		this.theFigure = theSet;
		theSet.undoManager=this.getUndoManager();
		if (theSet!=null)	theSet.setDisplayGroup(this);
	}
	
	
	int count =0;//keeps a count of all the updates to the display windows
	
	public void updateDisplay() {
		
		if (this.getTheCanvas()==null) return;
		
		theCanvas.repaint();
		if (this.sidePanel!=null) sidePanel.repaint();
		 count++;
	}
	
	public BasicCoordinateConverter getConverter() {
		if (theWindow==null) {
			//IssueLog.log("Problem: Cordinate conversion factor requested despite no window being set");
		return new BasicCoordinateConverter();}
		return theWindow.getZoomer().getConverter();
	}
	
	@Override
	public ImageWrapper getImageAsWrapper() {
		return theFigure;
	}
	@Override
	public Window getWindow() {
		return this.theWindow;
	}
	
	
	/**Creates a new blank image*/
	public static ImageWindowAndDisplaySet createAndShowNew(String title, int width, int height) {
		GraphicContainingImage gs = new GraphicContainingImage();
		gs.setTitle(title);
		gs.getBasics().setWidth(width);
		gs.getBasics().setHeight(height);
		return  show(gs);
	}
	
	/**creates the window an user interface elements needed to display the image*/
	public static ImageWindowAndDisplaySet  show(GraphicContainingImage gs) {
		ImageWindowAndDisplaySet set = new ImageWindowAndDisplaySet(gs);
		Window win = set.getWindow();
		win.pack();
		return set;
		
	}
	
	
	/**returns the undo manager*/
	public UndoManagerPlus getUndoManager() {
		if ( undoManager==null) {
			undoManager=new UndoManagerPlus();
			if (theFigure!=null)theFigure.undoManager=undoManager;
		}
		
		return undoManager;
	}
	
	/**sets the cursor being used*/
	@Override
	public void setCursor(Cursor c) {
		if (c==null)return;
		if (theCanvas.getCursor().equals(c)) return;
		theCanvas.setCursor(c);
	
	}

	
	/**resets the window size*/
	@Override
	public void updateWindowSize() {
		this.getTheWindow().reSetCanvasAndWindowSizes();
		
	}
	
	/**places the window at the center of the screen*/
	public static void centreWindow(Window window1) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - window1.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - window1.getHeight()) / 2);
	    window1.setLocation(x, y);
	}

	

	/**chooses a zoom level automatically*/
	public void autoZoom() {
		this.getTheWindow().comfortZoom();
		
	}

	/**zooms out until the entire image is of a size that is comfortable visible*/
	@Override
	public void zoomOutToDisplayEntireCanvas() {
		getTheWindow().shrinktoFit();
		
	}


	@Override
	public void zoom(String actionCommand) {
		getTheWindow().zoom(actionCommand);
	}
	
	@Override
	public void setZoomLevel(double st) {
		getTheWindow().getZoomer().setZoom(st);
	}


	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	@Override
	public void scrollPane(double dx, double dy) {
		theWindow.scrollPane(dx, dy);
		
	}
	
	@Override
	public void setScrollCenter(double dx, double dy) {
		theWindow.centerZoom(new Point2D.Double(dx, dy));
		
	}

	@Override
	public void closeWindowButKeepObjects() {
		this.getTheWindow().closeGroupWithoutObjectDeath();
		
	}
	
	public String toString() {
		return this.getTheSet().getTitle();
	}

	public Selectable getSelectedItem() {
		if (selectedItem!=null&&!selectedItem.isSelected()) return null;
		return selectedItem;
	}

	public void setSelectedItem(Selectable selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**returns the zoom level (100% is no zoom)*/
	@Override
	public double getZoomLevel() {
		return 100*getTheWindow().getZoomer().getZoomMagnification();
	}


	public void setSidePanel(MiniToolBarPanel miniToolBarPanel) {
		sidePanel=miniToolBarPanel;
		
	}

	/**a handle used for resizing the canvas*/
	class CanvasResizeHandle extends SmartHandle {

		public CanvasResizeHandle(ImageWindowAndDisplaySet s) {
			
			this.setHandleNumber(999910044);
			this.setHandleColor(Color.DARK_GRAY.darker());
			
		}
		
		/**the location at the bottom right corner of the canvas*/
		public Point2D getCordinateLocation() {
			Dimension d = theFigure.getCanvasDims();
			return new Point2D.Double(d.getWidth(), d.getHeight());
			}
		
		/**performs the change in canvas size*/
		public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
			Point p = lastDragOrRelMouseEvent.getCoordinatePoint();
			theFigure.getBasics().setWidth(p.x);
			theFigure.getBasics().setHeight(p.y);
			//updateDisplay();
			theWindow.resetCanvasDisplayObjectSize();
			
		}
		
		public void handleRelease(CanvasMouseEvent lastDragOrRelMouseEvent) {
			if (theWindow.usesBuiltInSidePanel())
				theWindow.reSetCanvasAndWindowSizes();
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}

	/**The handle list for the canvas size changing handle*/
	transient SmartHandleList canvasHandleList;
	@Override
	public SmartHandleList getCanvasHandles() {
		if (canvasHandleList==null) canvasHandleList = SmartHandleList.createList(new CanvasResizeHandle(this));
		return canvasHandleList;
	}
	
}
